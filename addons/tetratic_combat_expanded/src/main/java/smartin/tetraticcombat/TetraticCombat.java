package smartin.tetraticcombat;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import smartin.tetraticcombat.ItemResolver.ReloadListener;

@Mod(TetraticCombat.MODID)
public class TetraticCombat {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "tetratic_combat_expanded";

    public TetraticCombat(ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, ForgeConfigHolder.COMMON_SPEC, "tetratic_combat_expanded-common.toml");
        if (FMLEnvironment.dist == Dist.CLIENT) {
            clientSetup();
        }
        NeoForge.EVENT_BUS.register(this);
    }

    @OnlyIn(Dist.CLIENT)
    private void clientSetup() {
        NeoForge.EVENT_BUS.register(ClientEventHandler.class);
    }

    @SubscribeEvent
    public void addReloadListener(AddReloadListenerEvent event) {
        event.addListener(new ReloadListener());
    }
}
