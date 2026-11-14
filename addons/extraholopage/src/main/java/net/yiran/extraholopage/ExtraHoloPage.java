package net.yiran.extraholopage;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.yiran.extraholopage.compat.CompatHandler;
import net.yiran.extraholopage.gui.ComponentHelper;
import net.yiran.extraholopage.gui.MaterialTooltipHelper;
import net.yiran.extraholopage.gui.ScrollTooltipHandler;
import net.yiran.extraholopage.gui.SpecialMaterialTooltipHandler;
import net.yiran.extraholopage.gui.TooltipHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(value=ExtraHoloPage.MODID, dist=Dist.CLIENT)
public class ExtraHoloPage {
    public static final String MODID = "extraholopage";
    public static final String CLIENT_CONFIG = "extraholopage-client.toml";
    public static Logger LOGGER = LogManager.getLogger();

    public ExtraHoloPage(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(CompatHandler::onClientSetup);
        modEventBus.addListener(this::onCommonSetup);
        modEventBus.addListener(KeyMappingHandler::onRegisterKeyMapping);
        NeoForge.EVENT_BUS.addListener(KeyMappingHandler::onClientTick);
        modContainer.registerConfig(ModConfig.Type.CLIENT, Config.SPEC, CLIENT_CONFIG);
    }

    public void onCommonSetup(FMLCommonSetupEvent event) {
        if (Config.ENABLE_SCROLL_TOOLTIP.get()) {
            NeoForge.EVENT_BUS.register(ScrollTooltipHandler.INSTANCE.get());
            ScrollTooltipHandler.init();
        }
        if (Config.ENABLE_SPECIAL_MATERIAL_TOOLTIP.get()) {
            NeoForge.EVENT_BUS.register(SpecialMaterialTooltipHandler.INSTANCE.get());
            SpecialMaterialTooltipHandler.INSTANCE.get().init();
        }
        if (Config.ENABLE_MATERIAL_TOOLTIP.get()) {
            TooltipHandler tooltipHandler = TooltipHandler.INSTANCE.get();
            NeoForge.EVENT_BUS.register(tooltipHandler);
            if (Config.COLOR_1.get() != 0) {
                MaterialTooltipHelper.color1 = Config.COLOR_1.get();
            }
            if (Config.COLOR_2.get() != 0) {
                MaterialTooltipHelper.color2 = Config.COLOR_2.get();
                MaterialTooltipHelper.MIDDLE = ComponentHelper.literal("  \u251c ", MaterialTooltipHelper.color2);
                MaterialTooltipHelper.END = ComponentHelper.literal("  \u2514 ", MaterialTooltipHelper.color2);
            }
            if (Config.COLOR_3.get() != 0) {
                MaterialTooltipHelper.color3 = Config.COLOR_3.get();
                MaterialTooltipHelper.PREFIX = ComponentHelper.literal("\u00bb ", MaterialTooltipHelper.color3);
            }
            if (Config.COLOR_GOOD.get() != 0) {
                MaterialTooltipHelper.color_good = Config.COLOR_GOOD.get();
            }
            if (Config.COLOR_BAD.get() != 0) {
                MaterialTooltipHelper.color_bad = Config.COLOR_BAD.get();
            }
        }
    }
}
