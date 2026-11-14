package net.yiran.morerequirement;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    public static final ModConfigSpec SPEC;
    private static final ModConfigSpec.Builder BUILDER;
    public static final ModConfigSpec.ConfigValue<Boolean> CreateSingleCreativeTab;

    static {
        BUILDER = new ModConfigSpec.Builder();
        CreateSingleCreativeTab = BUILDER.define("CreateSingleCreativeTab", true);
        SPEC = BUILDER.build();
    }
}
