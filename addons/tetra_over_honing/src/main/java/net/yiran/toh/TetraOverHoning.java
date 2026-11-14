package net.yiran.toh;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLEnvironment;

@Mod(TetraOverHoning.MODID)
public class TetraOverHoning {
    public static final String MODID = "tetra_over_honing";
    public static final String NBT_KEY = "over_honing";

    public TetraOverHoning(IEventBus modEventBus, ModContainer modContainer) {
        if (FMLEnvironment.dist.isClient()) {
            modEventBus.register(TetraOverHoningClientHandler.class);
        }
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }
}
