package net.acetheeldritchking.art_of_forging.effects;

import net.acetheeldritchking.art_of_forging.effects.gui.EffectGuiStats;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import se.mickelus.tetra.items.modular.ModularItem;

public class DismountingEffect {
    @SubscribeEvent
    public void onLivingAttackEvent(LivingDamageEvent.Pre event) {
        LivingEntity target = event.getEntity();
        Entity attackingEntity = event.getSource().getEntity();
        if (attackingEntity instanceof LivingEntity attacker) {
            ItemStack heldStack = attacker.getMainHandItem();
            Item item = heldStack.getItem();
            if (item instanceof ModularItem item2) {
                int level = item2.getEffectLevel(heldStack, EffectGuiStats.dismountingEffect);
                float baseAmount = event.getNewDamage();
                float bonusDamage = EffectGuiStats.getDecimalPercentage(level, baseAmount);
                double eff = item2.getEffectEfficiency(heldStack, EffectGuiStats.dismountingEffect);
                if (level > 0) {
                    if (target.isPassenger()) {
                        event.setNewDamage(EffectGuiStats.getExactPercentage(baseAmount, bonusDamage));
                        if (eff > (double) (target.getRandom().nextFloat() * 100.0f)) {
                            target.stopRiding();
                        }
                    }
                    if (target.isVehicle() && eff > (double) (target.getRandom().nextFloat() * 100.0f)) {
                        target.ejectPassengers();
                    }
                }
            }
        }
    }
}
