package se.mickelus.tetracelium.compat.twilightforest.effects;

import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import se.mickelus.tetra.blocks.workbench.gui.WorkbenchStatsGui;
import se.mickelus.tetra.effect.EffectHelper;
import se.mickelus.tetra.effect.ItemEffect;
import se.mickelus.tetra.event.ModularLooseProjectilesEvent;
import se.mickelus.tetra.gui.stats.bar.GuiStatBar;
import se.mickelus.tetra.gui.stats.getter.LabelGetterBasic;
import se.mickelus.tetra.gui.stats.getter.StatGetterEffectLevel;
import se.mickelus.tetra.gui.stats.getter.TooltipGetterNone;
import se.mickelus.tetra.items.modular.impl.holo.gui.craft.HoloStatsGui;
import twilightforest.init.TFItems;
import twilightforest.item.SeekerBowItem;

public class SeekerProjectilesEffect {
    public static final ItemEffect seekerProjectilesEffect = ItemEffect.get("seekerProjectiles");

    @SubscribeEvent
    public static void onModularLooseProjectiles(ModularLooseProjectilesEvent event) {
        if (EffectHelper.getEffectLevel(event.getFiringStack(), seekerProjectilesEffect) > 0) {
            SeekerBowItem bow = (SeekerBowItem) TFItems.SEEKER_BOW.get();
            event.addProjectileRemapper(arrow -> bow.customArrow(arrow, event.getFiringStack(), event.getAmmoStack()));
        }
    }

    @OnlyIn(value = Dist.CLIENT)
    public static void clientInit() {
        StatGetterEffectLevel countGetter = new StatGetterEffectLevel(seekerProjectilesEffect, 1.0);
        GuiStatBar statBar = new GuiStatBar(0, 0, 59, "tetra.stats.seekerProjectiles", 0.0, 1.0, false, false, false, countGetter, LabelGetterBasic.noLabel, new TooltipGetterNone("tetra.stats.seekerProjectiles.tooltip"));
        WorkbenchStatsGui.addBar(statBar);
        HoloStatsGui.addBar(statBar);
    }
}
