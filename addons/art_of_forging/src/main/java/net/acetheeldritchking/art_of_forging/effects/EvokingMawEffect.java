package net.acetheeldritchking.art_of_forging.effects;

import net.acetheeldritchking.art_of_forging.effects.gui.EffectGuiStats;
import net.acetheeldritchking.art_of_forging.effects.potion.PotionEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import se.mickelus.tetra.items.modular.ModularItem;

public class EvokingMawEffect {
    @SubscribeEvent
    public void onLivingAttackEvent(LivingDamageEvent.Pre event) {
        LivingEntity target = event.getEntity();
        Entity eAttacker = event.getSource().getEntity();
        if (eAttacker instanceof LivingEntity attacker) {
            ItemStack heldStack = attacker.getMainHandItem();
            Item item = heldStack.getItem();
            if (item instanceof ModularItem item2) {
                int level = item2.getEffectLevel(heldStack, EffectGuiStats.evokingMaw);
                int eff = (int) item2.getEffectEfficiency(heldStack, EffectGuiStats.evokingMaw);
                if (level > 0 && !attacker.level().isClientSide) {
                    target.addEffect(new MobEffectInstance(PotionEffects.EVOKING_MAW, eff * 20, level, false, false, false));
                }
            }
        }
    }
}
