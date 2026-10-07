package com.maneatingroblox.nightfall;

import net.minecraftforge.common.ForgeConfigSpec;

public final class NightfallConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.BooleanValue ANOMALIES_ENABLED = BUILDER
            .comment("Enable the rare ambient and sleep anomalies.")
            .define("anomaliesEnabled", true);

    public static final ForgeConfigSpec.IntValue FIRST_HOLLOW_DAY = BUILDER
            .comment("Earliest Overworld day The Hollow can naturally spawn. Early days stay quiet.")
            .defineInRange("firstHollowDay", 5, 0, 365);

    public static final ForgeConfigSpec.IntValue DAILY_ANOMALY_CHANCE = BUILDER
            .comment("Percent chance of one subtle anomaly per player per day after day 1. Higher values mean more frequent anomalies.")
            .defineInRange("dailyAnomalyChance", 14, 0, 100);

    public static final ForgeConfigSpec.IntValue SLEEP_ANOMALY_CHANCE = BUILDER
            .comment("Percent chance of a brief anomaly each time a player wakes from sleep.")
            .defineInRange("sleepAnomalyChance", 5, 0, 100);

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    private NightfallConfig() {
    }
}
