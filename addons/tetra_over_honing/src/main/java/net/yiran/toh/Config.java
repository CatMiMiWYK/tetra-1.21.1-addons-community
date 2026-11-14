package net.yiran.toh;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.ConfigValue<Double> Conversion;
    public static final ModConfigSpec.ConfigValue<Double> MaxConversion;

    static {
        Conversion = BUILDER.comment("过量打磨转换为打磨值的默认转换率")
                .comment("Default conversion rate for converting excess polish to polish value")
                .define("OverHoning default conversion rate", 0.7);
        MaxConversion = BUILDER.comment("过量打磨的最大转化率，可以通过添加\"over_honing:conversion\"的effect提高转化率")
                .comment("The maximum conversion rate of over-honing can be increased by adding the effect of \"over_honing:conversion\"")
                .define("OverHoning max conversion rate", 1.0);
        SPEC = BUILDER.build();
    }
}
