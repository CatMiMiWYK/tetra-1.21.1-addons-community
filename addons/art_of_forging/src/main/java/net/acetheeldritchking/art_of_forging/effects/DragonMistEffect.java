package net.acetheeldritchking.art_of_forging.effects;

import net.acetheeldritchking.art_of_forging.effects.gui.EffectGuiStats;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import se.mickelus.tetra.items.modular.ModularItem;

public class DragonMistEffect {
    @SubscribeEvent
    public void onLivingAttackEvent(LivingDamageEvent.Pre event) {
        LivingEntity target = event.getEntity();
        Entity attackingEntity = event.getSource().getEntity();
        if (attackingEntity instanceof LivingEntity attacker) {
            ItemStack heldStack = attacker.getMainHandItem();
            Item item = heldStack.getItem();
            if (item instanceof ModularItem item2) {
                int level = item2.getEffectLevel(heldStack, EffectGuiStats.dragonMist);
                float eff = item2.getEffectEfficiency(heldStack, EffectGuiStats.dragonMist);
                if (level > 0 && !attacker.level().isClientSide && eff > target.getRandom().nextFloat() * 100.0f) {
                    ServerLevel world = (ServerLevel) attacker.level();
                    double posX = target.getX();
                    double posY = target.getY();
                    double posZ = target.getZ();
                    AreaEffectCloud aoeCloud = new AreaEffectCloud(world, posX, posY, posZ);
                    aoeCloud.setOwner(attacker);
                    aoeCloud.setParticle(ParticleTypes.DRAGON_BREATH);
                    aoeCloud.setRadius(0.5f);
                    aoeCloud.setDuration(level);
                    aoeCloud.setRadiusPerTick((5.0f - aoeCloud.getRadius()) / (float) aoeCloud.getDuration());
                    aoeCloud.addEffect(new MobEffectInstance(MobEffects.HARM, 1, 1));
                    world.addFreshEntity(aoeCloud);
                }
            }
        }
    }
}
