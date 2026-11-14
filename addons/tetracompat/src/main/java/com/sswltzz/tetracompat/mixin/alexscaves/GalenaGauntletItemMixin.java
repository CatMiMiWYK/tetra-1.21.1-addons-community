package com.sswltzz.tetracompat.mixin.alexscaves;

import com.github.alexmodguy.alexscaves.server.enchantment.ACEnchantmentRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.item.MagneticWeaponEntity;
import com.github.alexmodguy.alexscaves.server.item.GalenaGauntletItem;
import com.llamalad7.mixinextras.sugar.Local;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import se.mickelus.tetra.items.modular.ModularItem;

@Mixin(value = GalenaGauntletItem.class, remap = false)
public class GalenaGauntletItemMixin {
    private static final String MAGNETIZABLE = "tetracompat:magnetizable";
    private static final String CRYSTAL_MAGNETIZABLE = "tetracompat:crystal_magnetizable";

    @Inject(method = "use", at = @At("HEAD"), cancellable = true, remap = false)
    public void useMixin(Level level, Player player, InteractionHand interactionHand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        ItemStack itemstack = player.getItemInHand(interactionHand);
        ItemStack otherHand = interactionHand == InteractionHand.MAIN_HAND
                ? player.getItemInHand(InteractionHand.OFF_HAND)
                : player.getItemInHand(InteractionHand.MAIN_HAND);
        if (otherHand.getItem() instanceof ModularItem modularItem) {
            boolean crystallization = itemstack.getEnchantmentLevel(getCrystallization(player)) > 0;
            boolean magnetizable = modularItem.getEffects(otherHand).stream().anyMatch(itemEffect -> itemEffect.getKey().equals(MAGNETIZABLE));
            boolean crystal = modularItem.getEffects(otherHand).stream().anyMatch(itemEffect -> itemEffect.getKey().equals(CRYSTAL_MAGNETIZABLE));
            if (magnetizable || crystallization && crystal) {
                if (!player.isCreative()) {
                    itemstack.hurtAndBreak(1, player,
                            interactionHand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
                }
                player.startUsingItem(interactionHand);
                cir.setReturnValue(InteractionResultHolder.consume(itemstack));
            } else {
                cir.setReturnValue(InteractionResultHolder.fail(itemstack));
            }
        }
    }

    @Inject(method = "onUseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getEnchantmentLevel(Lnet/minecraft/core/Holder;)I", shift = At.Shift.AFTER), remap = false)
    public void useTickMixin(Level level, LivingEntity living, ItemStack stack, int timeUsing, CallbackInfo ci, @Local InteractionHand otherHand) {
        ItemStack otherStack = living.getItemInHand(otherHand);
        boolean otherMagneticWeaponsInUse = false;
        if (otherStack.getItem() instanceof ModularItem modularItem) {
            boolean crystallization = stack.getEnchantmentLevel(getCrystallization(living)) > 0;
            boolean magnetizable = modularItem.getEffects(otherStack).stream().anyMatch(itemEffect -> itemEffect.getKey().equals(MAGNETIZABLE));
            boolean crystal = modularItem.getEffects(otherStack).stream().anyMatch(itemEffect -> itemEffect.getKey().equals(CRYSTAL_MAGNETIZABLE));
            if (magnetizable || crystallization && crystal) {
                List<MagneticWeaponEntity> magneticWeapons = level.getEntitiesOfClass(MagneticWeaponEntity.class,
                        living.getBoundingBox().inflate(64.0, 64.0, 64.0));
                for (MagneticWeaponEntity magneticWeapon : magneticWeapons) {
                    Entity controller = magneticWeapon.getController();
                    if (controller == null || !controller.is(living)) {
                        continue;
                    }
                    otherMagneticWeaponsInUse = true;
                    break;
                }
                if (!otherMagneticWeaponsInUse) {
                    ItemStack copy = otherStack.copy();
                    otherStack.setCount(0);
                    MagneticWeaponEntity magneticWeapon = ACEntityRegistry.MAGNETIC_WEAPON.get().create(level);
                    if (magneticWeapon != null) {
                        magneticWeapon.setItemStack(copy);
                        magneticWeapon.setPos(living.position().add(0.0, 1.0, 0.0));
                        magneticWeapon.setControllerUUID(living.getUUID());
                        level.addFreshEntity(magneticWeapon);
                    }
                }
            } else if (!otherStack.isEmpty()) {
                living.stopUsingItem();
            }
        }
    }

    private static Holder<Enchantment> getCrystallization(LivingEntity entity) {
        return entity.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(ACEnchantmentRegistry.CRYSTALLIZATION);
    }
}
