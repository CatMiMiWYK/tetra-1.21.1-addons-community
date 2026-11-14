package net.tetra.modify.core.mixins.modular;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.tetra.modify.api.event.ModularGetterProjectileEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import se.mickelus.tetra.items.modular.IModularItem;
import se.mickelus.tetra.items.modular.impl.bow.ModularBowItem;

@Mixin(value = {ModularBowItem.class}, remap = false)
public class ModularBowItemMixin {
    @WrapOperation(method = {"fireArrow"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getProjectile(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/item/ItemStack;")})
    private ItemStack tmc$fireArrow$getProjectile(Player instance, ItemStack bowStack, Operation<ItemStack> original) {
        return ModularGetterProjectileEvent.onGetter((IModularItem) (Object) this, instance, bowStack, bowStack);
    }

    @WrapOperation(method = {"use"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getProjectile(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/item/ItemStack;")})
    private ItemStack tmc$use$getProjectile(Player instance, ItemStack bowStack, Operation<ItemStack> original) {
        return ModularGetterProjectileEvent.onGetter((IModularItem) (Object) this, instance, bowStack, bowStack);
    }
}
