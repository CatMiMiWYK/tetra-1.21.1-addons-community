package net.yiran.toh.core.mixins;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.yiran.toh.OverHoningUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import se.mickelus.tetra.blocks.workbench.WorkbenchTile;
import se.mickelus.tetra.items.modular.IModularItem;

@Mixin(value = WorkbenchTile.class, remap = false)
public abstract class WorkbenchTileMixin {
    @Shadow
    public abstract ItemStack getTargetItemStack();

    @Inject(method = "craft", at = @At(value = "RETURN"))
    private void t$craft(Player player, CallbackInfo ci) {
        ItemStack target = this.getTargetItemStack();
        if (target.getItem() instanceof IModularItem item) {
            double conversion = OverHoningUtil.getConversion(target);
            if (conversion > 0.0) {
                int overHoning = OverHoningUtil.getOverHoning(target);
                int amount = Math.min(item.getHoningProgress(target), (int) (overHoning * conversion));
                item.tickHoningProgression(player, target, amount);
                OverHoningUtil.addOverHoning(target, -((int) (amount / conversion)));
            }
        }
    }
}
