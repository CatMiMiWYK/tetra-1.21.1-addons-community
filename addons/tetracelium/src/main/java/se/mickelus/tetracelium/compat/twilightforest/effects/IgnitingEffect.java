package se.mickelus.tetracelium.compat.twilightforest.effects;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import se.mickelus.tetra.blocks.workbench.gui.WorkbenchStatsGui;
import se.mickelus.tetra.effect.ApplyHitTargetEffectsEvent;
import se.mickelus.tetra.effect.ItemEffect;
import se.mickelus.tetra.gui.stats.bar.GuiStatBar;
import se.mickelus.tetra.gui.stats.getter.LabelGetterBasic;
import se.mickelus.tetra.gui.stats.getter.StatGetterEffectLevel;
import se.mickelus.tetra.gui.stats.getter.TooltipGetterInteger;
import se.mickelus.tetra.items.modular.IModularItem;
import se.mickelus.tetra.items.modular.impl.holo.gui.craft.HoloStatsGui;

public class IgnitingEffect {
    public static final ItemEffect ignitingEffect = ItemEffect.get("igniting");

    @SubscribeEvent
    public static void onApplyHitTargetEffects(ApplyHitTargetEffectsEvent event) {
        ItemStack weaponStack;
        if (!event.getTarget().isOnFire() && !(weaponStack = event.getUsedItemStack()).isEmpty() && weaponStack.getItem() instanceof IModularItem item) {
            int ignitingLevel = item.getEffectLevel(weaponStack, ignitingEffect);
            event.getAttacker().igniteForSeconds(ignitingLevel);
        }
    }

    @OnlyIn(value = Dist.CLIENT)
    public static void clientInit() {
        StatGetterEffectLevel statGetter = new StatGetterEffectLevel(ignitingEffect, 1.0);
        GuiStatBar statBar = new GuiStatBar(0, 0, 59, "tetra.stats.igniting", 0.0, 10.0, false, false, false, statGetter, LabelGetterBasic.integerLabel, new TooltipGetterInteger("tetra.stats.igniting.tooltip", statGetter));
        WorkbenchStatsGui.addBar(statBar);
        HoloStatsGui.addBar(statBar);
    }
}
