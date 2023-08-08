package com.teampotato.modifiers.common.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ReforgeConfig {
    public static ForgeConfigSpec CONFIG;
    public static ForgeConfigSpec.ConfigValue<String> UNIVERSAL_REFORGE_ITEM;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.push("Remodified");
        UNIVERSAL_REFORGE_ITEM = builder.define("Universal Reforge Item", "minecraft:diamond");
        builder.pop();
        CONFIG = builder.build();
    }
}