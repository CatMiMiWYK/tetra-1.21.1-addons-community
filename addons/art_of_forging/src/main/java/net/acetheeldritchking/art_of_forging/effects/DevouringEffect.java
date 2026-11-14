package net.acetheeldritchking.art_of_forging.effects;

import net.acetheeldritchking.art_of_forging.capabilities.devouring.PlayerDevouring;
import net.acetheeldritchking.art_of_forging.capabilities.devouring.PlayerDevouringProvider;
import net.acetheeldritchking.art_of_forging.effects.gui.EffectGuiStats;
import net.acetheeldritchking.art_of_forging.effects.potion.PotionEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import se.mickelus.tetra.items.modular.ModularItem;

public class DevouringEffect {
    @SubscribeEvent
    public void onLivingAttackEvent(LivingDamageEvent.Pre event) {
        Entity attackingEntity = event.getSource().getEntity();
        if (attackingEntity instanceof LivingEntity attacker) {
            ItemStack heldStack = attacker.getMainHandItem();
            Item item2 = heldStack.getItem();
            if (item2 instanceof ModularItem item) {
                int level = item.getEffectLevel(heldStack, EffectGuiStats.devouringEffect);
                if (level > 0 && !attacker.level().isClientSide && attacker instanceof Player player) {
                    player.getData(PlayerDevouringProvider.PLAYER_DEVOURING).resetDevour();
                    player.removeEffect(PotionEffects.DEVOURING);
                }
            }
        }
    }

    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        ItemStack heldStack = player.getMainHandItem();
        Item item = heldStack.getItem();
        if (player.tickCount % 100 == 0 && item instanceof ModularItem item2) {
            int level = item2.getEffectLevel(heldStack, EffectGuiStats.devouringEffect);
            int eff = (int) item2.getEffectEfficiency(heldStack, EffectGuiStats.devouringEffect);
            if (level > 0 && !player.hasEffect(PotionEffects.DEVOURING) && !player.level().isClientSide) {
                PlayerDevouring devouring = player.getData(PlayerDevouringProvider.PLAYER_DEVOURING);
                devouring.addDevour(1);
                if (devouring.getDevour() >= 30) {
                    player.addEffect(new MobEffectInstance(PotionEffects.DEVOURING, eff * 20, level, true, true, true));
                }
            } else if (heldStack.getItem() != item2 && player.hasEffect(PotionEffects.DEVOURING) && !player.level().isClientSide) {
                player.getData(PlayerDevouringProvider.PLAYER_DEVOURING).resetDevour();
                player.removeEffect(PotionEffects.DEVOURING);
            }
        }
    }
}
