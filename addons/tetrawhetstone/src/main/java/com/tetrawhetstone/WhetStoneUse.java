package com.tetrawhetstone;

import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.yiran.tetra_addition.WorkbenchTileCraftEvent;
import se.mickelus.tetra.items.modular.IModularItem;
import se.mickelus.tetra.module.ItemModule;
import se.mickelus.tetra.module.ItemModuleMajor;

public class WhetStoneUse {
    @SubscribeEvent
    public void onWhetstoneCraft(WorkbenchTileCraftEvent event) {
        IModularItem modularItem;
        ItemModule module;
        if (event.upgradedStack.getItem() instanceof IModularItem
                && (module = (modularItem = (IModularItem) event.upgradedStack.getItem()).getModuleFromSlot(event.upgradedStack, event.currentSlot)) instanceof ItemModuleMajor) {
            ItemModuleMajor majorModule = (ItemModuleMajor) module;
            if (event.currentSchematic.getKey().equals("shared/hpsettled")) {
                int settledLevel = majorModule.getImprovementLevel(event.upgradedStack, "settled");
                if (settledLevel <= 0) {
                    ItemModuleMajor.addImprovement(event.upgradedStack, event.currentSlot, "settled", 1);
                } else {
                    ItemStack whetstone = new ItemStack(TetraWhetstone.WHETSTONE.get(), 3);
                    if (!event.player.getInventory().add(whetstone)) {
                        event.player.drop(whetstone, false);
                    }
                }
            }
            if (event.currentSchematic.getKey().equals("shared/whetstone")) {
                modularItem.setHoningProgress(event.upgradedStack, 0);
            }
            ItemModuleMajor.removeImprovement(event.upgradedStack, event.currentSlot, "whetstone");
            IModularItem.updateIdentifier(event.upgradedStack);
        }
    }
}
