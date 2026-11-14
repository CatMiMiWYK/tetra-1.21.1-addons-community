package se.mickelus.tetracelium.mixin;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import se.mickelus.tetra.items.modular.IModularItem;
import vectorwing.farmersdelight.common.block.entity.CuttingBoardBlockEntity;

@ParametersAreNonnullByDefault
@Mixin(value = {CuttingBoardBlockEntity.class}, remap = false)
public class CuttingBoardBlockEntityMixin {
    @Inject(at = {@At(value = "RETURN")}, method = {"processStoredItemUsingTool(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/player/Player;)Z"})
    private void addEnchantment(ItemStack toolStack, @Nullable Player player, CallbackInfoReturnable<Boolean> cir) {
        Item item;
        if (cir.getReturnValue().booleanValue() && player != null && (item = toolStack.getItem()) instanceof IModularItem) {
            IModularItem modularItem = (IModularItem) item;
            modularItem.applyUsageEffects((LivingEntity) player, toolStack, 2.0);
        }
    }
}
