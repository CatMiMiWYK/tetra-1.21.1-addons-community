package com.sswltzz.tetracompat.mixin.tetra;

import com.sswltzz.tetracompat.integration.ExDeorumUtil;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbility;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import se.mickelus.tetra.TetraItemAbilities;
import se.mickelus.tetra.util.ItemAbilityHelper;

@Mixin(value = ItemAbilityHelper.class, remap = false)
public abstract class ToolActionHelperMixin {
    @Inject(method = "isEffectiveOn(Lnet/neoforged/neoforge/common/ItemAbility;Lnet/minecraft/world/level/block/state/BlockState;)Z", at = @At("RETURN"), cancellable = true, remap = false)
    private static void isEffectiveOnMixin(ItemAbility action, BlockState state, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValueZ() && action == TetraItemAbilities.hammer && ExDeorumUtil.getHammerRecipe(state.getBlock().asItem()) != null) {
            cir.setReturnValue(true);
        }
    }
}
