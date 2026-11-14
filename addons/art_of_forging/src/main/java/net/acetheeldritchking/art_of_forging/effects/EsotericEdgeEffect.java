package net.acetheeldritchking.art_of_forging.effects;

import net.acetheeldritchking.art_of_forging.effects.gui.EffectGuiStats;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import se.mickelus.tetra.items.modular.ModularItem;

public class EsotericEdgeEffect {
    @SubscribeEvent
    public void onLivingAttackEvent(LivingIncomingDamageEvent event) {
        Entity attackingEntity = event.getSource().getEntity();
        LivingEntity target = event.getEntity();
        if (attackingEntity instanceof LivingEntity attacker) {
            ItemStack heldStack = attacker.getMainHandItem();
            Item item = heldStack.getItem();
            if (item instanceof ModularItem item2) {
                float level = item2.getEffectLevel(heldStack, EffectGuiStats.esotericEdgeEffect);
                float baseAmount = event.getAmount();
                float magicBonusDamage = EffectGuiStats.getDecimalPercentage(level, baseAmount);
                if (level > 0.0f) {
                    target.hurt(target.damageSources().magic(), magicBonusDamage);
                }
            }
        }
    }

    @SubscribeEvent
    public void onLivingDamageEvent(LivingDamageEvent.Pre event) {
        Entity attackingEntity = event.getSource().getEntity();
        if (attackingEntity instanceof LivingEntity attacker) {
            ItemStack heldStack = attacker.getMainHandItem();
            Item item = heldStack.getItem();
            if (item instanceof ModularItem item2) {
                float level = item2.getEffectLevel(heldStack, EffectGuiStats.esotericEdgeEffect);
                float baseAmount = event.getNewDamage();
                if (level > 0.0f) {
                    event.setNewDamage(baseAmount);
                }
            }
        }
    }
}
