package net.tetra.modify.core.mixins.client;

import net.minecraft.world.item.ItemStack;
import net.tetra.modify.client.TemporaryHoloItemGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import se.mickelus.tetra.items.modular.IModularItem;
import se.mickelus.tetra.items.modular.impl.holo.gui.craft.HoloCraftRootGui;
import se.mickelus.tetra.module.schematic.UpgradeSchematic;

import javax.annotation.Nullable;

@Mixin(value = {HoloCraftRootGui.class}, remap = false)
public class HoloCraftRootGuiMixin {
    @Inject(method = {"openFromWorkbench"}, at = {@At(value = "HEAD")})
    private void tmc$captureWorkbenchStack(IModularItem item, ItemStack itemStack, @Nullable String slot, @Nullable UpgradeSchematic schematic, CallbackInfo ci) {
        TemporaryHoloItemGui.setWorkbenchStack(itemStack);
    }
}
