package net.acetheeldritchking.art_of_forging.effects.potion;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class TargetedPotionEffect
        extends MobEffect {
    protected TargetedPotionEffect() {
        super(MobEffectCategory.HARMFUL, 0xE0E0E0);
    }
}
