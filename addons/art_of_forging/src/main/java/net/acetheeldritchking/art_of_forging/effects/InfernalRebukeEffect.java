package net.acetheeldritchking.art_of_forging.effects;

import net.acetheeldritchking.art_of_forging.effects.gui.EffectGuiStats;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import se.mickelus.tetra.items.modular.ModularItem;

public class InfernalRebukeEffect {
    @SubscribeEvent
    public void onLivingHurtEvent(LivingDamageEvent.Pre event) {
        Entity attackingEntity = event.getSource().getEntity();
        LivingEntity target = event.getEntity();
        if (attackingEntity instanceof LivingEntity attacker) {
            ItemStack heldStack = target.getMainHandItem();
            Item item = heldStack.getItem();
            if (item instanceof ModularItem item2) {
                int level = item2.getEffectLevel(heldStack, EffectGuiStats.infernalRebukeEffect);
                float eff = item2.getEffectEfficiency(heldStack, EffectGuiStats.infernalRebukeEffect);
                double xTarget = (int) target.getX();
                double zTarget = (int) target.getZ();
                int xAttacker = (int) attacker.getX();
                int zAttacker = (int) attacker.getZ();
                Vec3 vec3 = new Vec3(xTarget, 0.0, zTarget).subtract(xAttacker, 0.0, zAttacker).normalize();
                if (level > 0 && eff > attacker.getRandom().nextFloat() * 100.0f) {
                    attacker.knockback(0.4, vec3.x, vec3.z);
                    attacker.setRemainingFireTicks(level);
                    target.level().playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, 0.5f, 1.0f);
                }
            }
        }
    }
}
