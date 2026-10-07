package com.maneatingroblox.nightfall;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
        if (chance > 0 && player.getRandom().nextInt(100) < chance) {
            playWhisperFromBehind(player);
            if (player.getRandom().nextBoolean()) {
                whisper(player, "message.nightfall.whisper_" + (player.getRandom().nextInt(4) + 1));
            }
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
            playWhisperFromBehind(player);
            whisper(player, "message.nightfall.woke_up");
        }
    }

    private static void playWhisperFromBehind(ServerPlayer player) {
        Vec3 behind = player.position().subtract(player.getLookAngle().normalize().scale(7.0D));
        player.level().playSound(null, behind.x, behind.y + 1.0D, behind.z,
                SoundEvents.AMBIENT_CAVE.value(), SoundSource.AMBIENT, 0.85F,
                0.55F + player.getRandom().nextFloat() * 0.15F);
    }

    private static void whisper(ServerPlayer player, String translationKey) {
        player.displayClientMessage(Component.translatable(translationKey)
                .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC), false);
    }
}
