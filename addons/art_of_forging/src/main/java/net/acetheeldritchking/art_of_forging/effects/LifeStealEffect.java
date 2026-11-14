package net.acetheeldritchking.art_of_forging.effects;

import net.acetheeldritchking.art_of_forging.effects.gui.EffectGuiStats;
import net.acetheeldritchking.art_of_forging.networking.AoFPackets;
import net.acetheeldritchking.art_of_forging.networking.packet.LifeStealParticlesS2CPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import se.mickelus.tetra.items.modular.ModularItem;

public class LifeStealEffect {
    @SubscribeEvent
    public void onLivingAttackEvent(LivingDamageEvent.Pre event) {
        LivingEntity target = event.getEntity();
        Entity attackingEntity = event.getSource().getEntity();
        if (attackingEntity instanceof LivingEntity attacker) {
            ItemStack heldStack = attacker.getMainHandItem();
            Item item = heldStack.getItem();
            if (item instanceof ModularItem item2) {
                float level = item2.getEffectLevel(heldStack, EffectGuiStats.lifeSteal);
                float eff = item2.getEffectEfficiency(heldStack, EffectGuiStats.lifeSteal);
                if (level > 0.0f && eff > target.getRandom().nextFloat() * 100.0f) {
                    attacker.heal(level);
                    target.playSound(SoundEvents.PLAYER_ATTACK_CRIT, 0.2f, 1.0f);
                    AoFPackets.sendToEntity(new LifeStealParticlesS2CPacket(target.getX(), target.getY(0.5), target.getZ()), target);
                }
            }
        }
    }
}
