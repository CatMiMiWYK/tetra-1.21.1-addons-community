package net.yiran.tie.core.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.yiran.tie.TetraInheritEnchant;
import se.mickelus.tetra.blocks.workbench.WorkbenchTile;

@Mixin(value = WorkbenchTile.class, remap = false)
public class WorkbenchTileMixin {
    @Shadow
    private String currentSlot;

    @WrapOperation(method = {"craft"}, at = @At(value = "INVOKE", target = "Lse/mickelus/tetra/aspect/TetraEnchantmentHelper;removeEnchantments(Lnet/minecraft/world/item/ItemStack;Ljava/lang/String;)V"))
    private void t$removeEnchantments(ItemStack itemStack, String slot, Operation<Void> original) {
        // 取消原版整槽附魔移除，改为只移除新模块不接受的附魔（见 t$doRemoveEnchantments）
    }

    @Inject(method = {"craft"}, at = @At(value = "INVOKE", target = "Lse/mickelus/tetra/module/schematic/UpgradeSchematic;getExperienceCost(Lnet/minecraft/world/item/ItemStack;[Lnet/minecraft/world/item/ItemStack;Ljava/lang/String;)I"))
    private void t$doRemoveEnchantments(Player player, CallbackInfo ci, @Local boolean willReplace, @Local(ordinal = 1) ItemStack upgradedStack) {
        if (willReplace) {
            TetraInheritEnchant.removeUnacceptedEnchantments(upgradedStack, this.currentSlot);
        }
    }
}
