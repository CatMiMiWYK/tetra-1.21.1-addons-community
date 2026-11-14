package net.acetheeldritchking.art_of_forging.effects.potion;

import net.minecraft.core.particles.DustColorTransitionOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Vector3f;

public class DevouringPotionEffect
        extends MobEffect {
    public DevouringPotionEffect() {
        super(MobEffectCategory.HARMFUL, 13041721);
    }

    public boolean applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {
        pLivingEntity.hurt(pLivingEntity.damageSources().magic(), (float) pAmplifier);
        if (!pLivingEntity.level().isClientSide) {
            ServerLevel world = (ServerLevel) pLivingEntity.level();
            world.sendParticles(new DustColorTransitionOptions(new Vector3f(0.5f, 0.18f, 0.18f), new Vector3f(0.95f, 0.1f, 0.1f), 1.5f), pLivingEntity.getX(), pLivingEntity.getY(0.5), pLivingEntity.getZ(), 25, 0.5, 0.3, 0.5, 0.0);
        }
        return true;
    }

    public boolean shouldApplyEffectTickThisTick(int pDuration, int pAmplifier) {
        return pDuration % 10 == 0;
    }
}
