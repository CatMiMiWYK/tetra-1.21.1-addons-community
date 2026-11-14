package net.acetheeldritchking.art_of_forging.effects;

import net.acetheeldritchking.art_of_forging.effects.gui.EffectGuiStats;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import se.mickelus.tetra.items.modular.ModularItem;

public class GoliathSlayerEffect {
    @SubscribeEvent
    public void onLivingAttackEvent(LivingDamageEvent.Pre event) {
        Entity attackingEntity = event.getSource().getEntity();
        LivingEntity target = event.getEntity();
        if (attackingEntity instanceof LivingEntity attacker) {
            ItemStack heldStack = attacker.getMainHandItem();
            Item item = heldStack.getItem();
            if (item instanceof ModularItem item2) {
                double level = item2.getEffectLevel(heldStack, EffectGuiStats.goliathSlayerEffect);
                float baseAmount = event.getNewDamage();
                float bonusDamage = EffectGuiStats.getDecimalPercentage((float) level, baseAmount);
                if (level > 0.0 && EffectGuiStats.isBossEntity(target.getType())) {
                    event.setNewDamage(EffectGuiStats.getExactPercentage(baseAmount, bonusDamage));
                }
            }
        }
    }
}
