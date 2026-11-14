package net.acetheeldritchking.art_of_forging.effects;

import net.acetheeldritchking.art_of_forging.effects.gui.EffectGuiStats;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import se.mickelus.tetra.items.modular.ModularItem;

public class RetributionEffect {
    @SubscribeEvent
    public void onLivingHurtEvent(LivingIncomingDamageEvent event) {
        Entity attackingEntity = event.getSource().getEntity();
        LivingEntity target = event.getEntity();
        if (attackingEntity instanceof LivingEntity attacker) {
            ItemStack heldStack = target.getMainHandItem();
            Item item = heldStack.getItem();
            if (item instanceof ModularItem item2) {
                int level = item2.getEffectLevel(heldStack, EffectGuiStats.retributionEffect);
                float eff = item2.getEffectEfficiency(heldStack, EffectGuiStats.retributionEffect);
                if (level > 0 && eff > attacker.getRandom().nextFloat() * 100.0f) {
                    attacker.hurt(attacker.damageSources().thorns(attacker), (float) level);
                    target.level().playSound(null, attacker.getY(), attacker.getY(), attacker.getZ(), SoundEvents.PLAYER_HURT, SoundSource.PLAYERS, 0.5f, 1.0f);
                }
            }
        }
    }
}
