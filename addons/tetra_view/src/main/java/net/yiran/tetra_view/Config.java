package net.yiran.tetra_view;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    public static final ModConfigSpec SPEC;
    private static final ModConfigSpec.Builder BUILDER;
    public static final ModConfigSpec.ConfigValue<Boolean> EnableTooltip;

    static {
        BUILDER = new ModConfigSpec.Builder();
        EnableTooltip = BUILDER.define("enableTooltip", true);
        SPEC = BUILDER.build();
    }
}
