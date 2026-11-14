/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Multimap
 *  net.minecraft.world.entity.ai.attributes.Attribute
 *  net.minecraft.world.entity.ai.attributes.AttributeModifier
 *  se.mickelus.tetra.module.data.EffectData
 */
package net.yiran.tmo.core;

import com.google.common.collect.Multimap;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import se.mickelus.tetra.module.data.EffectData;

public interface IMaterialMultiplier {
    public Multimap<Attribute, AttributeModifier> getMagicAttributes();

    public EffectData getMagicEffects();
}

