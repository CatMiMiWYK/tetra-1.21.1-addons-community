package net.acetheeldritchking.art_of_forging.effects;

import net.acetheeldritchking.art_of_forging.effects.gui.EffectGuiStats;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.phys.Vec3;
import se.mickelus.tetra.effect.AbilityUseResult;
import se.mickelus.tetra.effect.ChargedAbilityEffect;
import se.mickelus.tetra.items.modular.ItemModularHandheld;

public class SonicShockEffect
        extends ChargedAbilityEffect {
    public static final SonicShockEffect instance = new SonicShockEffect();

    public SonicShockEffect() {
        super(60, 0.15, 450, 1.0, EffectGuiStats.sonicShockEffect, ChargedAbilityEffect.TargetRequirement.either, UseAnim.SPEAR, "raised");
    }

    public void perform(Player attacker, InteractionHand hand, ItemModularHandheld item, ItemStack itemStack, LivingEntity target, Vec3 hitVec, int chargedTicks) {
        if (!target.level().isClientSide) {
            int level = item.getEffectLevel(itemStack, EffectGuiStats.sonicShockEffect);
            this.doSonicBoomAttack(level, attacker, item, itemStack, target);
            attacker.swing(hand, false);
            attacker.getCooldowns().addCooldown(item, this.getCooldown(item, itemStack));
            item.tickProgression(attacker, itemStack, 1);
            item.applyDamage(10, itemStack, attacker);
        }
    }

    private AbilityUseResult doSonicBoomAttack(float damage, Player attacker, ItemModularHandheld item, ItemStack itemStack, LivingEntity target) {
        AbilityUseResult result = item.hitEntity(itemStack, attacker, target, 0.0, 1.0, 1.0f, 1.0f);
        ServerLevel world = (ServerLevel) attacker.level();
        if (result != AbilityUseResult.fail) {
            Vec3 vec3 = attacker.position().add(0.0, 1.6f, 0.0);
            Vec3 vec31 = target.getEyePosition().subtract(vec3);
            Vec3 vec32 = vec31.normalize();
            for (int i = 1; i < Mth.floor(vec31.length()) + 7; ++i) {
                Vec3 vec33 = vec3.add(vec32.scale(i));
                world.sendParticles(ParticleTypes.SONIC_BOOM, vec33.x, vec33.y, vec33.z, 1, 0.0, 0.0, 0.0, 0.0);
            }
            attacker.level().playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(), SoundEvents.WARDEN_SONIC_BOOM, SoundSource.PLAYERS, 3.0f, 1.0f);
            target.hurt(target.damageSources().mobAttack(attacker), damage);
            double d1 = 0.5 * (1.0 - target.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
            double d0 = 2.5 * (1.0 - target.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
            target.knockback(vec32.x * d0, vec32.y * d1, vec32.z * d0);
        }
        return result;
    }
}
