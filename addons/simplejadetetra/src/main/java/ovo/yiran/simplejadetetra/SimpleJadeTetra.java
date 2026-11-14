package ovo.yiran.simplejadetetra;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import snownee.jade.addon.harvest.HarvestToolProvider;

@Mod(SimpleJadeTetra.MODID)
public class SimpleJadeTetra {
    public static final String MODID = "simplejadetetra";
    private static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    public SimpleJadeTetra(IEventBus modEventBus) {
        modEventBus.addListener(this::onCommonSetup);
    }

    public void onCommonSetup(FMLCommonSetupEvent event) {
        HarvestToolProvider.registerHandler(new TetraHammerToolHandler());
    }
}
