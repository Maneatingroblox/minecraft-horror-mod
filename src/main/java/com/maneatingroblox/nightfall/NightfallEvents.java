package com.maneatingroblox.nightfall;

import com.maneatingroblox.nightfall.entity.ModEntities;
import com.maneatingroblox.nightfall.entity.TheHollowEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Difficulty;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Nightfall.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class NightfallEvents {
    private static final String LAST_ANOMALY_DAY = Nightfall.MOD_ID + ":last_anomaly_day";

    private NightfallEvents() {
    }

    @SubscribeEvent
    public static void rollDailyAnomaly(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.player instanceof ServerPlayer player)
                || player.tickCount % 20 != 0 || !player.level().dimension().equals(Level.OVERWORLD)
                || !NightfallConfig.ANOMALIES_ENABLED.get()) {
            return;
        }

        long day = Math.max(0L, player.level().getDayTime() / 24_000L);
        if (day < 1 || player.getPersistentData().getLong(LAST_ANOMALY_DAY) >= day) {
            return;
        }

        player.getPersistentData().putLong(LAST_ANOMALY_DAY, day);
        int chance = NightfallConfig.DAILY_ANOMALY_CHANCE.get();
        if (chance > 0 && player.getRandom().nextInt(100) < chance
                && (day < NightfallConfig.FIRST_HOLLOW_DAY.get() || !spawnDistantWatcher(player))) {
            playDistantSoundBehind(player);
        }
    }

    @SubscribeEvent
    public static void onWake(PlayerWakeUpEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)
                || !player.level().dimension().equals(Level.OVERWORLD)
                || !NightfallConfig.ANOMALIES_ENABLED.get()) {
            return;
        }

        int chance = NightfallConfig.SLEEP_ANOMALY_CHANCE.get();
        if (chance > 0 && player.getRandom().nextInt(100) < chance) {
            playDistantSoundBehind(player);
        }
    }

    /** Stages a watcher just outside the player's current view in a dark patch, if one exists nearby. */
    private static boolean spawnDistantWatcher(ServerPlayer player) {
        if (!(player.level() instanceof ServerLevel level) || player.isCreative() || player.isSpectator()
                || level.getDifficulty() == Difficulty.PEACEFUL) {
            return false;
        }

        if (!level.getEntitiesOfClass(TheHollowEntity.class, player.getBoundingBox().inflate(36.0D)).isEmpty()) {
            return false;
        }

        Vec3 look = player.getLookAngle();
        Vec3 horizontalLook = new Vec3(look.x, 0.0D, look.z);
        if (horizontalLook.lengthSqr() < 1.0E-6D) {
            return false;
        }
        horizontalLook = horizontalLook.normalize();
        Vec3 side = new Vec3(-horizontalLook.z, 0.0D, horizontalLook.x);

        for (int attempt = 0; attempt < 8; attempt++) {
            double distance = 18.0D + player.getRandom().nextInt(9);
            double sideOffset = player.getRandom().nextBoolean() ? 4.0D : -4.0D;
            Vec3 point = player.position()
                    .subtract(horizontalLook.scale(distance))
                    .add(side.scale(sideOffset));
            BlockPos searchPos = BlockPos.containing(point.x, player.getY(), point.z);
            if (!level.hasChunkAt(searchPos)) {
                continue;
            }

            BlockPos spawnPos = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, searchPos);
            BlockPos floorPos = spawnPos.below();
            if (spawnPos.getY() >= level.getMaxBuildHeight() - 1
                    || !level.isEmptyBlock(spawnPos)
                    || !level.isEmptyBlock(spawnPos.above())
                    || level.getBlockState(floorPos).getCollisionShape(level, floorPos).isEmpty()
                    || level.getMaxLocalRawBrightness(spawnPos) > 8) {
                continue;
            }

            TheHollowEntity watcher = ModEntities.THE_HOLLOW.get().create(level);
            if (watcher == null) {
                return false;
            }
            watcher.moveTo(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D,
                    player.getRandom().nextFloat() * 360.0F, 0.0F);
            return level.addFreshEntity(watcher);
        }
        return false;
    }

    private static void playDistantSoundBehind(ServerPlayer player) {
        Vec3 behind = player.position().subtract(player.getLookAngle().normalize().scale(7.0D));
        player.level().playSound(null, behind.x, behind.y + 1.0D, behind.z,
                SoundEvents.AMBIENT_CAVE.value(), SoundSource.AMBIENT, 0.72F,
                0.52F + player.getRandom().nextFloat() * 0.12F);
    }
}
