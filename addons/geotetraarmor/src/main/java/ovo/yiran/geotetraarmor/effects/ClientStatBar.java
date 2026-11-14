package ovo.yiran.geotetraarmor.effects;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import se.mickelus.tetra.blocks.workbench.gui.WorkbenchStatsGui;
import se.mickelus.tetra.gui.stats.bar.GuiStatBar;
import se.mickelus.tetra.gui.stats.getter.LabelGetterBasic;
import se.mickelus.tetra.gui.stats.getter.StatGetterEffectLevel;
import se.mickelus.tetra.gui.stats.getter.TooltipGetterNone;
import se.mickelus.tetra.items.modular.impl.holo.gui.craft.HoloStatsGui;

@EventBusSubscriber(modid = "geotetraarmor", bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientStatBar {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> addStatBar(new GuiStatBar(0, 0, 59, "tetra.stats." + FlyEffect.FLY.getKey(), 0.0, 1.0, false, false, false,
                new StatGetterEffectLevel(FlyEffect.FLY), LabelGetterBasic.integerLabel,
                new TooltipGetterNone("tetra.stats." + FlyEffect.FLY.getKey() + ".tooltip"))));
    }

    private static void addStatBar(GuiStatBar bar) {
        WorkbenchStatsGui.addBar(bar);
        HoloStatsGui.addBar(bar);
    }
}
