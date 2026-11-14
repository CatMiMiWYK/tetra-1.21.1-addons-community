package net.yiran.tie.core.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.item.ItemStack;
import net.yiran.tie.TetraInheritEnchant;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import se.mickelus.tetra.blocks.workbench.gui.WorkbenchScreen;
import se.mickelus.tetra.module.schematic.UpgradeSchematic;

@Mixin(value = WorkbenchScreen.class, remap = false)
public class WorkbenchScreenMixin {
    @WrapOperation(method = {"buildPreviewStack"}, at = @At(value = "INVOKE", target = "Lse/mickelus/tetra/aspect/TetraEnchantmentHelper;removeEnchantments(Lnet/minecraft/world/item/ItemStack;Ljava/lang/String;)V"))
    private void t$removeEnchantments(ItemStack itemStack, String slot, Operation<Void> original) {
        // 取消原版整槽附魔移除，改为只移除新模块不接受的附魔（见 t$doRemoveEnchantments）
    }

    @Inject(method = {"buildPreviewStack"}, at = @At(value = "INVOKE", target = "Lse/mickelus/tetra/blocks/workbench/WorkbenchTile;applyCraftingBonusEffects(Lnet/minecraft/world/item/ItemStack;Ljava/lang/String;ZLnet/minecraft/world/entity/player/Player;[Lnet/minecraft/world/item/ItemStack;[Lnet/minecraft/world/item/ItemStack;Ljava/util/Map;Lse/mickelus/tetra/module/schematic/UpgradeSchematic;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;ZF)Lnet/minecraft/world/item/ItemStack;"))
    private void t$doRemoveEnchantments(UpgradeSchematic schematic, ItemStack targetStack, String slot, ItemStack[] materials,
            CallbackInfoReturnable<ItemStack> cir, @Local boolean willReplace, @Local(ordinal = 1) ItemStack upgradedStack) {
        if (willReplace) {
            TetraInheritEnchant.removeUnacceptedEnchantments(upgradedStack, slot);
        }
    }
}
