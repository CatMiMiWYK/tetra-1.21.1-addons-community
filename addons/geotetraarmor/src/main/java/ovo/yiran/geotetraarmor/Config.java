package ovo.yiran.geotetraarmor;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    public static final ModConfigSpec SPEC;
    private static final ModConfigSpec.Builder BUILDER;
    public static final ModConfigSpec.ConfigValue<Integer> HeadHoneBase;
    public static final ModConfigSpec.ConfigValue<Integer> HeadHoneMultiplier;
    public static final ModConfigSpec.ConfigValue<Integer> ChestHoneBase;
    public static final ModConfigSpec.ConfigValue<Integer> ChestHoneMultiplier;
    public static final ModConfigSpec.ConfigValue<Integer> LegsHoneBase;
    public static final ModConfigSpec.ConfigValue<Integer> LegsHoneMultiplier;
    public static final ModConfigSpec.ConfigValue<Integer> FeetHoneBase;
    public static final ModConfigSpec.ConfigValue<Integer> FeetHoneMultiplier;

    static {
        BUILDER = new ModConfigSpec.Builder();
        BUILDER.push("HoningConfig");
        HeadHoneBase = BUILDER.define("HeadHoneBase", 450);
        HeadHoneMultiplier = BUILDER.define("HeadHoneMultiplier", 200);
        ChestHoneBase = BUILDER.define("ChestHoneBase", 450);
        ChestHoneMultiplier = BUILDER.define("ChestHoneMultiplier", 200);
        LegsHoneBase = BUILDER.define("LegsHoneBase", 450);
        LegsHoneMultiplier = BUILDER.define("LegsHoneMultiplier", 200);
        FeetHoneBase = BUILDER.define("FeetHoneBase", 450);
        FeetHoneMultiplier = BUILDER.define("FeetHoneMultiplier", 200);
        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
