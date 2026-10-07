package com.maneatingroblox.nightfall.item;

import com.maneatingroblox.nightfall.entity.TheHollowEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class EchoBellItem extends Item {
    private static final int COOLDOWN_TICKS = 20 * 45;
    private static final double RADIUS = 12.0D;

    public EchoBellItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.nightfall.echo_bell"));
        tooltip.add(Component.translatable("tooltip.nightfall.echo_bell.cooldown"));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResultHolder.pass(stack);
        }

        if (!level.isClientSide) {
            player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
            level.playSound(null, player.blockPosition(), SoundEvents.BELL_BLOCK, SoundSource.PLAYERS, 1.4F, 0.72F);

            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.END_ROD,
                        player.getX(), player.getY() + 1.0D, player.getZ(),
                        24, 1.8D, 0.8D, 1.8D, 0.025D);
            }

            AABB pulseArea = player.getBoundingBox().inflate(RADIUS);
            for (TheHollowEntity hollow : level.getEntitiesOfClass(TheHollowEntity.class, pulseArea)) {
                hollow.hurt(level.damageSources().magic(), 5.0F);
                if (hollow.isAlive()) {
                    hollow.repelFrom(player);
                }
            }

            player.displayClientMessage(Component.translatable("message.nightfall.echo_bell"), true);
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
}
