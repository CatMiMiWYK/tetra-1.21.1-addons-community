package net.tetra.modify.core.mixins.toolbelt;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import se.mickelus.mutil.gui.DisabledSlot;
import se.mickelus.tetra.items.modular.impl.toolbelt.ToolbeltContainer;
import se.mickelus.tetra.items.modular.impl.toolbelt.gui.screen.ToolbeltScreen;

@Mixin(value = {ToolbeltScreen.class})
public abstract class ToolbeltScreenMixin extends AbstractContainerScreen<ToolbeltContainer> {
    public ToolbeltScreenMixin(ToolbeltContainer menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Inject(method = {"slotClicked"}, at = {@At(value = "HEAD")}, cancellable = true)
    private void tmc$slotClicked(Slot slot, int slotIndex, int barIndex, ClickType clickType, CallbackInfo ci) {
        if (!(slot instanceof DisabledSlot) && (clickType != ClickType.SWAP || (barIndex != 40 && !(this.getMenu().getSlotAt(barIndex, Inventory.class) instanceof DisabledSlot)))) {
            super.slotClicked(slot, slotIndex, barIndex, clickType);
        }
        ci.cancel();
    }
}
