package com.tetrawhetstone;

import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.yiran.tetra_addition.WorkbenchTileCraftEvent;
import se.mickelus.tetra.items.modular.IModularItem;
import se.mickelus.tetra.module.ItemModule;
import se.mickelus.tetra.module.ItemModuleMajor;
import se.mickelus.tetra.util.ItemStackTagHelper;

public class OverWhetstoneUse {
    @SubscribeEvent
    public void onWhetstoneCraft(WorkbenchTileCraftEvent event) {
        IModularItem modularItem;
        ItemModule module;
        if (event.upgradedStack.getItem() instanceof IModularItem
                && (module = (modularItem = (IModularItem) event.upgradedStack.getItem()).getModuleFromSlot(event.upgradedStack, event.currentSlot)) instanceof ItemModuleMajor) {
            ItemModuleMajor majorModule = (ItemModuleMajor) module;
            if (event.currentSchematic.getKey().equals("shared/overhpsettled")) {
                int settledLevel = majorModule.getImprovementLevel(event.upgradedStack, "settled");
                if (settledLevel <= 1) {
                    ItemModuleMajor.addImprovement(event.upgradedStack, event.currentSlot, "settled", 2);
                } else {
                    ItemStack overwhetstone = new ItemStack(TetraWhetstone.OVERWHETSTONE.get(), 2);
                    if (!event.player.getInventory().add(overwhetstone)) {
                        event.player.drop(overwhetstone, false);
                    }
                }
            }
            if (event.currentSchematic.getKey().equals("shared/overwhetstone")) {
                modularItem.setHoningProgress(event.upgradedStack, 0);
                ItemStackTagHelper.mutate(event.upgradedStack, tag -> tag.putInt("over_hone", 9));
            }
            if (!ItemStackTagHelper.getOrCreateTag(event.upgradedStack).contains("honing_available")
                    && ItemStackTagHelper.getOrCreateTag(event.upgradedStack).getInt("over_hone") > 0) {
                modularItem.setHoningProgress(event.upgradedStack, 0);
                ItemStackTagHelper.mutate(event.upgradedStack, tag -> tag.putInt("over_hone", tag.getInt("over_hone") - 1));
            }
            ItemModuleMajor.removeImprovement(event.upgradedStack, event.currentSlot, "whetstone");
            IModularItem.updateIdentifier(event.upgradedStack);
        }
    }
}
