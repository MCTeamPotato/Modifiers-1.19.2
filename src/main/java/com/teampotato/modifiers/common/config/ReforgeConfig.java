package com.teampotato.modifiers.common.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ReforgeConfig {
    public static ForgeConfigSpec CONFIG;
    public static ForgeConfigSpec.ConfigValue<String> UNIVERSAL_REFORGE_ITEM;
    public static ForgeConfigSpec.BooleanValue DISABLE_REPAIR_REFORGED;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.push("Remodified");
        UNIVERSAL_REFORGE_ITEM = builder.define("Universal Reforge Item", "minecraft:diamond");
        DISABLE_REPAIR_REFORGED = builder.define("If you turn this on, item with modifier cannot be reforged on the smithing table by its repair item.", false);
        builder.pop();
        CONFIG = builder.build();
    }
}