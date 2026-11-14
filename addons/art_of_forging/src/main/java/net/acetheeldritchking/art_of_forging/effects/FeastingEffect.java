package net.acetheeldritchking.art_of_forging.effects;

import net.acetheeldritchking.art_of_forging.effects.gui.EffectGuiStats;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import se.mickelus.tetra.items.modular.ModularItem;

public class FeastingEffect {
    @SubscribeEvent
    public void onLivingAttackEvent(LivingDamageEvent.Pre event) {
        LivingEntity target = event.getEntity();
        Entity attackingEntity = event.getSource().getEntity();
        if (attackingEntity instanceof LivingEntity attacker) {
            ItemStack heldStack = attacker.getMainHandItem();
            Item item = heldStack.getItem();
            if (item instanceof ModularItem item2) {
                int level = item2.getEffectLevel(heldStack, EffectGuiStats.feastingEffect);
                float eff = item2.getEffectEfficiency(heldStack, EffectGuiStats.feastingEffect);
                if (level > 0 && eff > target.getRandom().nextFloat() * 100.0f && attacker instanceof Player player) {
                    FoodData playerFood = player.getFoodData();
                    player.getFoodData().setFoodLevel(Math.min(playerFood.getFoodLevel() + level, playerFood.getFoodLevel()));
                    if (target instanceof Player targetPlayer) {
                        FoodData targetFood = targetPlayer.getFoodData();
                        targetPlayer.getFoodData().setFoodLevel(Math.max(targetFood.getFoodLevel() - level, 0));
                    }
                }
            }
        }
    }
}
