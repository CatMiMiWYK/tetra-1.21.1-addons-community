package net.acetheeldritchking.art_of_forging.effects;

import net.acetheeldritchking.art_of_forging.effects.gui.EffectGuiStats;
import net.acetheeldritchking.art_of_forging.effects.potion.PotionEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import se.mickelus.tetra.items.modular.ModularItem;

public class DisorientingEffect {
    @SubscribeEvent
    public void onLivingAttackEvent(LivingDamageEvent.Pre event) {
        LivingEntity target = event.getEntity();
        Entity attackingEntity = event.getSource().getEntity();
        if (attackingEntity instanceof LivingEntity attacker) {
            ItemStack heldStack = attacker.getMainHandItem();
            Item item = heldStack.getItem();
            if (item instanceof ModularItem item2) {
                int level = item2.getEffectLevel(heldStack, EffectGuiStats.disorientingEffect);
                int eff = (int) item2.getEffectEfficiency(heldStack, EffectGuiStats.disorientingEffect);
                if (level > 0 && !target.isInvertedHealAndHarm()) {
                    if (target instanceof Player) {
                        this.applyEffects(level, eff, target);
                        target.addEffect(new MobEffectInstance(MobEffects.CONFUSION, eff * 20, level, true, true, true));
                    } else if (target instanceof Creeper creeper) {
                        this.applyEffects(level, eff, creeper);
                        creeper.addEffect(new MobEffectInstance(PotionEffects.DEFUSE_CREEPER, eff * 100, 2, false, false, false));
                    } else {
                        this.applyEffects(level, eff, target);
                    }
                }
            }
        }
    }

    private void applyEffects(int level, int duration, LivingEntity target) {
        target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, duration * 20, level, true, true, true));
        target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, duration * 20, level, true, true, true));
    }
}
