package ovo.yiran.geotetraarmor.core.mixins;

import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForge;
import ovo.yiran.geotetraarmor.items.ModularArmorItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import se.mickelus.tetra.event.ModularItemDamageEvent;

@Mixin(value = Player.class, remap = false)
public class PlayerMixin {
    @Inject(method = "hurtArmor", at = @At(value = "HEAD"))
    private void t$hurtArmor(DamageSource source, float amount, CallbackInfo ci) {
        LivingEntity livingEntity = (LivingEntity) (Object) this;
        for (ItemStack itemStack : livingEntity.getArmorSlots()) {
            if (itemStack.getItem() instanceof ModularArmorItem armorItem) {
                if (!(source.is(DamageTypeTags.IS_FIRE) && itemStack.getDamageValue() > 0 && itemStack.has(DataComponents.FIRE_RESISTANT))) {
                    ModularItemDamageEvent event = new ModularItemDamageEvent(livingEntity, itemStack, 1);
                    NeoForge.EVENT_BUS.post(event);
                    armorItem.applyDamage(event.getAmount(), itemStack, livingEntity);
                }
            }
        }
    }
}
