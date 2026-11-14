package net.acetheeldritchking.art_of_forging.effects;

import net.acetheeldritchking.art_of_forging.capabilities.conquer.PlayerConquer;
import net.acetheeldritchking.art_of_forging.capabilities.conquer.PlayerConquerProvider;
import net.acetheeldritchking.art_of_forging.effects.gui.EffectGuiStats;
import net.acetheeldritchking.art_of_forging.effects.potion.PotionEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import se.mickelus.tetra.items.modular.ModularItem;

public class ConqueringEffect {
    @SubscribeEvent
    public void onLivingDeathEvent(LivingDeathEvent event) {
        Entity attackingEntity = event.getSource().getEntity();
        if (attackingEntity instanceof LivingEntity attacker) {
            ItemStack heldStack = attacker.getMainHandItem();
            Item item2 = heldStack.getItem();
            if (item2 instanceof ModularItem item) {
                int level = item.getEffectLevel(heldStack, EffectGuiStats.conqueringEffect);
                if (level > 0 && !attacker.level().isClientSide && attacker instanceof Player player) {
                    player.getData(PlayerConquerProvider.PLAYER_CONQUER).addConquer(1);
                }
            }
        }
    }

    @SubscribeEvent
    public void onLivingAttackEvent(LivingDamageEvent.Pre event) {
        Entity attackingEntity = event.getSource().getEntity();
        LivingEntity target = event.getEntity();
        if (attackingEntity instanceof LivingEntity attacker) {
            ItemStack heldStack = attacker.getMainHandItem();
            Item item = heldStack.getItem();
            if (item instanceof ModularItem item2) {
                int level = item2.getEffectLevel(heldStack, EffectGuiStats.conqueringEffect);
                int eff = (int) item2.getEffectEfficiency(heldStack, EffectGuiStats.conqueringEffect);
                if (level > 0) {
                    target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, eff * 20, 0, true, true, true));
                    if (!attacker.level().isClientSide && attacker instanceof Player player) {
                        PlayerConquer conquer = player.getData(PlayerConquerProvider.PLAYER_CONQUER);
                        if (conquer.getConquer() == 5) {
                            target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, eff * 20, 1, true, true, true));
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onPlayerTickEvent(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        ItemStack heldStack = player.getMainHandItem();
        Item item = heldStack.getItem();
        if (player.tickCount % 20 == 0 && item instanceof ModularItem item2) {
            int level = item2.getEffectLevel(heldStack, EffectGuiStats.conqueringEffect);
            int eff = (int) item2.getEffectEfficiency(heldStack, EffectGuiStats.conqueringEffect);
            if (level > 0 && !player.level().isClientSide) {
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, eff * 20, 0, true, true, true));
                PlayerConquer conquer = player.getData(PlayerConquerProvider.PLAYER_CONQUER);
                if (conquer.getConquer() == 5) {
                    player.addEffect(new MobEffectInstance(PotionEffects.MORTAL_WOUNDS, 20, level, false, false, false));
                    player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, eff * 20, 1, true, true, true));
                }
            }
        }
    }
}
