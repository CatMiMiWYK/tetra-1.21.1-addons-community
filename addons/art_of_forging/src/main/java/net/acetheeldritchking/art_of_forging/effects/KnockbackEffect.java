package net.acetheeldritchking.art_of_forging.effects;

import net.acetheeldritchking.art_of_forging.effects.gui.EffectGuiStats;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import se.mickelus.tetra.items.modular.ModularItem;

public class KnockbackEffect {
    @SubscribeEvent
    public void onLivingAttackEvent(LivingDamageEvent.Pre event) {
        LivingEntity target = event.getEntity();
        Entity attackingEntity = event.getSource().getEntity();
        if (attackingEntity instanceof LivingEntity attacker) {
            ItemStack heldStack = attacker.getMainHandItem();
            Item item = heldStack.getItem();
            if (item instanceof ModularItem item2) {
                float level = item2.getEffectLevel(heldStack, EffectGuiStats.knockbackEffect);
                int eff = (int) item2.getEffectEfficiency(heldStack, EffectGuiStats.knockbackEffect);
                int xTarget = (int) target.getX();
                int zTarget = (int) target.getZ();
                int xAttacker = (int) attacker.getX();
                int zAttacker = (int) attacker.getZ();
                Vec3 vec3 = new Vec3(xAttacker, 0.0, zAttacker).subtract(xTarget, 0.0, zTarget).normalize();
                Vec3 vec3r = new Vec3(xTarget, 0.0, zTarget).subtract(xAttacker, 0.0, zAttacker).normalize();
                if (level > 0.0f) {
                    target.knockback(level, eff == 1 ? vec3r.x : vec3.x, eff == 1 ? vec3r.z : vec3.z);
                }
            }
        }
    }
}
