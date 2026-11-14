package com.hp.mixin;

import com.hp.rift.RiftWorkbenchHooks;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import se.mickelus.tetra.blocks.workbench.WorkbenchTile;
import se.mickelus.tetra.module.schematic.UpgradeSchematic;

@Mixin(value = WorkbenchTile.class, remap = false)
public abstract class WorkbenchTileMixin {
    @Unique
    private ItemStack[] tetra_gateways$materials = new ItemStack[0];

    @Shadow
    private UpgradeSchematic currentSchematic;

    @Shadow
    private String currentSlot;

    @Shadow
    public abstract ItemStack[] getMaterials();

    @Inject(method = "craft", at = @At("HEAD"))
    private void tetra_gateways$captureRiftMaterials(Player player, CallbackInfo ci) {
        this.tetra_gateways$materials = this.getMaterials();
    }

    @Inject(method = "craft", at = @At(value = "INVOKE",
            target = "Lse/mickelus/tetra/blocks/workbench/WorkbenchTile;clearSchematic()V"))
    private void tetra_gateways$storeRiftCoreReward(Player player, CallbackInfo ci) {
        RiftWorkbenchHooks.afterCraft((WorkbenchTile) (Object) this, player, this.currentSlot,
                this.currentSchematic, this.tetra_gateways$materials);
    }
}
