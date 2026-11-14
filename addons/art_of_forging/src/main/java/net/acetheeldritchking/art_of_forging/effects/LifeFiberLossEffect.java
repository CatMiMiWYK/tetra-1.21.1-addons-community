package net.acetheeldritchking.art_of_forging.effects;

import net.acetheeldritchking.art_of_forging.effects.gui.EffectGuiStats;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import se.mickelus.tetra.items.modular.ModularItem;

public class LifeFiberLossEffect {
    @SubscribeEvent
    public void onLivingDeathEvent(LivingDeathEvent event) {
        Entity attackingEntity = event.getSource().getEntity();
        if (attackingEntity instanceof LivingEntity attacker) {
            ItemStack heldStack = attacker.getMainHandItem();
            Item item = heldStack.getItem();
            if (item instanceof ModularItem item2) {
                int level = item2.getEffectLevel(heldStack, EffectGuiStats.lifeFiberLoss);
                int eff = (int) item2.getEffectEfficiency(heldStack, EffectGuiStats.lifeFiberLoss);
                if (level > 0) {
                    this.grantEffects(attacker, eff, level);
                }
            }
        }
    }

    private void grantEffects(LivingEntity target, int duration, int level) {
        target.addEffect(new MobEffectInstance(MobEffects.REGENERATION, duration * 20, level - 1, true, true, true));
        target.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, duration * 20, level - 1, true, true, true));
        target.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, duration * 20, level - 1, true, true, true));
        target.addEffect(new MobEffectInstance(MobEffects.GLOWING, duration * 20, 0, true, true, true));
    }
}
