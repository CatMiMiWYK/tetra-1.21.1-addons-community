package net.yiran.fot;

import com.mojang.logging.LogUtils;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.yiran.fot.common.AttachCapability;
import net.yiran.fot.common.DamageHook;
import org.slf4j.Logger;

@Mod(value = FluxOfTetra.MODID)
public class FluxOfTetra {
    public static final String MODID = "fluxoftetra";
    private static final Logger LOGGER = LogUtils.getLogger();

    public FluxOfTetra(IEventBus modBus, ModContainer modContainer) {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            FluxOfTetraClient.init(modBus);
        }
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        modBus.addListener(AttachCapability::attach);
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, DamageHook::onModularDamage);
    }
}
