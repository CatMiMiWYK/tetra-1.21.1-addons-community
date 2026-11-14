/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Multimap
 *  net.minecraft.world.entity.ai.attributes.Attribute
 *  net.minecraft.world.entity.ai.attributes.AttributeModifier
 *  org.spongepowered.asm.mixin.Mixin
 *  se.mickelus.tetra.module.data.EffectData
 *  se.mickelus.tetra.module.data.MaterialMultiplier
 */
package net.yiran.tmo.core.mixins;

import com.google.common.collect.Multimap;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.yiran.tmo.core.IMaterialMultiplier;
import org.spongepowered.asm.mixin.Mixin;
import se.mickelus.tetra.module.data.EffectData;
import se.mickelus.tetra.module.data.MaterialMultiplier;

@Mixin(value={MaterialMultiplier.class})
public class MaterialMultiplierMixin
implements IMaterialMultiplier {
    public EffectData magicEffects;
    public Multimap<Attribute, AttributeModifier> magicAttributes;

    @Override
    public Multimap<Attribute, AttributeModifier> getMagicAttributes() {
        return this.magicAttributes;
    }

    @Override
    public EffectData getMagicEffects() {
        return this.magicEffects;
    }
}

