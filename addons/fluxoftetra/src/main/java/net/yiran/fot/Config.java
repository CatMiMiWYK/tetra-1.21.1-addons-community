package net.yiran.fot;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    public static final ModConfigSpec SPEC;
    private static final ModConfigSpec.Builder BUILDER;
    public static final ModConfigSpec.ConfigValue<Integer> ForgeEnergy2Durability;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> NotEnergyItems;

    static {
        BUILDER = new ModConfigSpec.Builder();
        ForgeEnergy2Durability = BUILDER.comment("电量抵扣耐久比例").comment("default : 320").define("ForgeEnergy2Durability", 320);
        NotEnergyItems = BUILDER.comment("不附加电力的物品id").defineList("NotEnergyItems", ObjectArrayList::new, o -> true);
        SPEC = BUILDER.build();
    }
}
