package net.acetheeldritchking.art_of_forging.effects;

import net.acetheeldritchking.art_of_forging.effects.gui.EffectGuiStats;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import se.mickelus.tetra.items.modular.ModularItem;

public class HubrisEffect {
    @SubscribeEvent
    public void onLivingAttackEvent(LivingDamageEvent.Pre event) {
        Entity attackingEntity = event.getSource().getEntity();
        if (attackingEntity instanceof LivingEntity attacker) {
            ItemStack heldStack = attacker.getMainHandItem();
            Item item = heldStack.getItem();
            if (item instanceof ModularItem item2) {
                float level = item2.getEffectLevel(heldStack, EffectGuiStats.hubrisEffect);
                float baseAmount = event.getNewDamage();
                float bonusDamage = EffectGuiStats.getDecimalPercentage(level, baseAmount);
                float BASE_HEALTH = attacker.getMaxHealth();
                float baseHealth = attacker.getHealth();
                double percent = baseHealth / BASE_HEALTH * 100.0f;
                if (level > 0.0f) {
                    if (percent <= 30.0) {
                        double totalDamage = this.calculateReducedDamageAmount(baseAmount, 2.0);
                        if (Double.isInfinite(totalDamage) || Double.isNaN(totalDamage) || totalDamage <= 0.0) {
                            event.setNewDamage(baseAmount);
                        } else {
                            event.setNewDamage((float) totalDamage);
                        }
                    } else if (percent <= 40.0) {
                        double totalDamage = this.calculateReducedDamageAmount(baseAmount, 1.5);
                        if (Double.isInfinite(totalDamage) || Double.isNaN(totalDamage) || totalDamage <= 0.0) {
                            event.setNewDamage(baseAmount);
                        } else {
                            event.setNewDamage((float) totalDamage);
                        }
                    } else if (percent <= 60.0) {
                        event.setNewDamage(baseAmount);
                    } else if (percent <= 70.0) {
                        double totalDamage = this.calculateBonusDamageAmount(baseAmount, bonusDamage, 1.5);
                        if (Double.isInfinite(totalDamage) || Double.isNaN(totalDamage) || totalDamage <= 0.0) {
                            event.setNewDamage(baseAmount);
                        } else {
                            event.setNewDamage((float) totalDamage);
                        }
                    } else if (percent <= 80.0) {
                        double totalDamage = this.calculateBonusDamageAmount(baseAmount, bonusDamage, 1.0);
                        event.setNewDamage((float) totalDamage);
                    } else {
                        double totalDamage = this.calculateBonusDamageAmount(baseAmount, bonusDamage, 1.0);
                        event.setNewDamage((float) totalDamage);
                    }
                }
            }
        }
    }

    private double calculateBonusDamageAmount(float baseAmount, float bonus, double factor) {
        float totalDamage = (float) ((double) baseAmount + (double) bonus / factor);
        return Math.floor(totalDamage);
    }

    private double calculateReducedDamageAmount(float baseAmount, double factor) {
        float totalDamage = (float) ((double) baseAmount / factor);
        return Math.floor(totalDamage);
    }
}
