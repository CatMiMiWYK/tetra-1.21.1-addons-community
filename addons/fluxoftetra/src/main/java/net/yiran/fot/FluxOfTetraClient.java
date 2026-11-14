package net.yiran.fot;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.yiran.fot.client.FEItemEffectBars;
import net.yiran.fot.client.ItemRenderHandler;

public class FluxOfTetraClient {
    public static void init(IEventBus modBus) {
        modBus.addListener(ItemRenderHandler::onRegisterItemDecorations);
        NeoForge.EVENT_BUS.addListener(ItemRenderHandler::onItemTooltip);
        modBus.addListener(FEItemEffectBars::onClientSetup);
    }
}
