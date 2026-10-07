package com.maneatingroblox.nightfall.entity;

import com.maneatingroblox.nightfall.NightfallConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;

/** A quiet watcher that only becomes openly hostile when it is cornered or provoked. */
public class TheHollowEntity extends Zombie {
    private static final int WATCHING = 0;
    private static final int STALKING = 1;
    private static final int HUNTING = 2;
    private static final EntityDataAccessor<Integer> BEHAVIOR =
            SynchedEntityData.defineId(TheHollowEntity.class, EntityDataSerializers.INT);

    private int behaviorTicks;
    private int watchedTicks;

    public TheHollowEntity(EntityType<? extends Zombie> type, Level level) {
        super(type, level);
        this.xpReward = 12;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(BEHAVIOR, WATCHING);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.45D, false));
        this.goalSelector.addGoal(2, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(0, new HurtByTargetGoal(this));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Zombie.createAttributes()
                .add(Attributes.MAX_HEALTH, 36.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.18D)
                .add(Attributes.ATTACK_DAMAGE, 7.0D)
                .add(Attributes.FOLLOW_RANGE, 96.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.4D);
    }

    public static boolean checkSpawnRules(EntityType<TheHollowEntity> type, ServerLevelAccessor level,
                                          MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        long day = Math.max(0L, level.getLevel().getDayTime() / 24_000L);
        if (day < NightfallConfig.FIRST_HOLLOW_DAY.get()
                || level.getLevel().getDifficulty() == Difficulty.PEACEFUL
                || !Monster.checkMonsterSpawnRules(type, level, spawnType, pos, random)) {
            return false;
        }

        long timeOfDay = Math.floorMod(level.getLevel().getDayTime(), 24_000L);
        boolean night = timeOfDay >= 13_000L && timeOfDay < 23_000L;
        return night || !level.canSeeSky(pos);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide && this.tickCount % 10 == 0) {
            this.updateBehavior();
        }
    }

    private void updateBehavior() {
        Player nearest = this.findNearestPlayer();
        Player target = this.getTarget() instanceof Player current && current.isAlive()
                && !current.isCreative() && !current.isSpectator() ? current : nearest;
        if (target == null) {
            this.getNavigation().stop();
            this.setTarget(null);
            if (this.getBehavior() != WATCHING) {
                this.setBehavior(WATCHING);
            }
            return;
        }

        this.behaviorTicks += 10;
        double distanceSqr = this.distanceToSqr(target);
        int behavior = this.getBehavior();

        if (behavior == HUNTING) {
            if (distanceSqr > 2_500.0D || this.behaviorTicks >= 240) {
                this.discard();
                return;
            }
            if (this.getTarget() != target) {
                this.setTarget(target);
            }
            if (this.getNavigation().isDone()) {
                this.getNavigation().moveTo(target, 1.45D);
            }
            return;
        }

        this.setTarget(null);
        this.getLookControl().setLookAt(target, 35.0F, 35.0F);
        boolean watched = this.isBeingWatchedBy(target);
        if (watched) {
            this.watchedTicks += 10;
            this.getNavigation().stop();
            if (this.watchedTicks >= 60) {
                this.discard();
            }
            return;
        }
        this.watchedTicks = 0;

        if (behavior == WATCHING) {
            this.getNavigation().stop();
            if (distanceSqr <= 25.0D) {
                this.beginHunt(target);
            } else if (this.behaviorTicks >= 200) {
                this.setBehavior(STALKING);
            }
            return;
        }

        if (distanceSqr <= 36.0D) {
            this.beginHunt(target);
        } else if (this.behaviorTicks >= 600) {
            this.setBehavior(WATCHING);
        } else if (this.getNavigation().isDone() || this.tickCount % 40 == 0) {
            this.getNavigation().moveTo(target, 0.62D);
        }
    }

    private Player findNearestPlayer() {
        return this.level().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(96.0D),
                        player -> player.isAlive() && !player.isCreative() && !player.isSpectator())
                .stream()
                .min(Comparator.comparingDouble((Player player) -> this.distanceToSqr(player)))
                .orElse(null);
    }

    private boolean isBeingWatchedBy(Player player) {
        Vec3 towardMe = this.getEyePosition().subtract(player.getEyePosition()).normalize();
        if (player.getViewVector(1.0F).dot(towardMe) < 0.93D) {
            return false;
        }

        BlockHitResult hit = this.level().clip(new ClipContext(
                player.getEyePosition(), this.getEyePosition(),
                ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
        return hit.getType() == HitResult.Type.MISS;
    }

    private void beginHunt(Player player) {
        this.setBehavior(HUNTING);
        this.setTarget(player);
        this.getNavigation().moveTo(player, 1.45D);
    }

    private int getBehavior() {
        return this.entityData.get(BEHAVIOR);
    }

    private void setBehavior(int behavior) {
        if (this.getBehavior() == behavior) {
            return;
        }

        this.entityData.set(BEHAVIOR, behavior);
        this.behaviorTicks = 0;
        this.watchedTicks = 0;
        this.getNavigation().stop();
        AttributeInstance speed = this.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed != null) {
            speed.setBaseValue(behavior == HUNTING ? 0.42D : behavior == STALKING ? 0.24D : 0.18D);
        }
        if (behavior != HUNTING) {
            this.setTarget(null);
        } else if (!this.level().isClientSide) {
            this.level().playSound(null, this.blockPosition(), SoundEvents.AMBIENT_CAVE.value(),
                    SoundSource.HOSTILE, 0.8F, 0.58F);
        }
    }

    public void repelFrom(Player player) {
        if (this.level().isClientSide || !this.isAlive()) {
            return;
        }
        this.setBehavior(WATCHING);
        this.behaviorTicks = 0;
        this.watchedTicks = 0;
        this.setTarget(null);
        this.getNavigation().stop();
        this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 3));
        this.knockback(1.7D, player.getX() - this.getX(), player.getZ() - this.getZ());
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean hurt = super.hurt(source, amount);
        if (hurt && this.isAlive() && !this.level().isClientSide && source.getEntity() instanceof Player player) {
            this.beginHunt(player);
        }
        return hurt;
    }

    @Override
    public boolean doHurtTarget(net.minecraft.world.entity.Entity target) {
        boolean hit = super.doHurtTarget(target);
        if (hit && target instanceof Player player) {
            player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 40, 0, true, false, false));
        }
        return hit;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return this.getBehavior() == HUNTING ? SoundEvents.ZOMBIE_AMBIENT : null;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.ZOMBIE_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ZOMBIE_DEATH;
    }

    @Override
    public float getVoicePitch() {
        return 0.58F + this.random.nextFloat() * 0.12F;
    }
}
