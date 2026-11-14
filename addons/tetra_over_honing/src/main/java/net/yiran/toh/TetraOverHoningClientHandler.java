package net.yiran.toh;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import se.mickelus.tetra.blocks.workbench.gui.WorkbenchStatsGui;
import se.mickelus.tetra.gui.stats.bar.GuiStatBar;
import se.mickelus.tetra.gui.stats.getter.IStatGetter;
import se.mickelus.tetra.gui.stats.getter.LabelGetterBasic;
import se.mickelus.tetra.gui.stats.getter.StatGetterEffectLevel;
import se.mickelus.tetra.gui.stats.getter.TooltipGetterInteger;
import se.mickelus.tetra.items.modular.impl.holo.gui.craft.HoloStatsGui;

public class TetraOverHoningClientHandler {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        StatGetterEffectLevel getter = new StatGetterEffectLevel(OverHoningUtil.Conversion);
        GuiStatBar guiStatBar = new GuiStatBar(0, 0, 59, "tetra.stats.over_honing:conversion", 0.0, 50.0, false,
                (IStatGetter) getter, LabelGetterBasic.integerLabel, new TooltipGetterInteger("tetra.stats.over_honing:conversion.tooltip", getter));
        event.enqueueWork(() -> {
            WorkbenchStatsGui.addBar(guiStatBar);
            HoloStatsGui.addBar(guiStatBar);
        });
    }
}
