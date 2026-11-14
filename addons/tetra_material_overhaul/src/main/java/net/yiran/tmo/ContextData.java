/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Multimap
 *  com.google.common.reflect.TypeToken
 *  net.minecraft.world.entity.ai.attributes.Attribute
 *  net.minecraft.world.entity.ai.attributes.AttributeModifier
 *  se.mickelus.tetra.module.data.EffectData
 *  se.mickelus.tetra.module.data.MaterialData
 *  se.mickelus.tetra.module.data.MaterialMultiplier
 *  se.mickelus.tetra.module.data.VariantData
 *  se.mickelus.tetra.properties.AttributeHelper
 */
package net.yiran.tmo;

import com.google.common.collect.Multimap;
import com.google.common.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.yiran.tmo.core.IMaterialData;
import net.yiran.tmo.core.IMaterialMultiplier;
import se.mickelus.tetra.module.data.EffectData;
import se.mickelus.tetra.module.data.MaterialData;
import se.mickelus.tetra.module.data.MaterialMultiplier;
import se.mickelus.tetra.module.data.VariantData;
import se.mickelus.tetra.properties.AttributeHelper;

public class ContextData {
    public static Type TYPE = new TypeToken<Map<String, ContextData>>(){}.getType();
    public Multimap<Attribute, AttributeModifier> attributes;
    public EffectData effects = new EffectData();

    public static ContextData buildDefaultContextData(MaterialData data) {
        ContextData defaultContext = new ContextData();
        defaultContext.attributes = data.attributes;
        defaultContext.effects = data.effects;
        return defaultContext;
    }

    public static void combineImprovementData(VariantData THIS, VariantData result, List<String> contexts, MaterialMultiplier extract, MaterialData material) {
        ArrayList<Multimap<Attribute, AttributeModifier>> attributeList = new ArrayList<>();
        attributeList.add(THIS.attributes);
        attributeList.add(AttributeHelper.multiplyModifiers((Multimap)extract.primaryAttributes, (double)material.primary.floatValue()));
        attributeList.add(AttributeHelper.multiplyModifiers((Multimap)extract.secondaryAttributes, (double)material.secondary.floatValue()));
        attributeList.add(AttributeHelper.multiplyModifiers((Multimap)extract.tertiaryAttributes, (double)material.tertiary.floatValue()));
        attributeList.add(AttributeHelper.multiplyModifiers(((IMaterialMultiplier)extract).getMagicAttributes(), (double)material.magicCapacity));
        ArrayList<EffectData> effectList = new ArrayList<EffectData>();
        effectList.add(THIS.effects);
        effectList.add(EffectData.multiply((EffectData)extract.primaryEffects, (float)material.primary.floatValue(), (float)material.primary.floatValue()));
        effectList.add(EffectData.multiply((EffectData)extract.secondaryEffects, (float)material.secondary.floatValue(), (float)material.secondary.floatValue()));
        effectList.add(EffectData.multiply((EffectData)extract.tertiaryEffects, (float)material.tertiary.floatValue(), (float)material.tertiary.floatValue()));
        effectList.add(EffectData.multiply((EffectData)((IMaterialMultiplier)extract).getMagicEffects(), (float)material.magicCapacity, (float)material.magicCapacity));
        if (((IMaterialData)material).getContextData() != null) {
            Map<String, ContextData> contextMap = ((IMaterialData)material).getContextData();
            for (String context : contexts) {
                if (!contextMap.containsKey(context)) continue;
                ContextData contextData = contextMap.get(context);
                attributeList.add(contextData.attributes);
                effectList.add(contextData.effects);
            }
        }
        result.attributes = AttributeHelper.collapseRound((Multimap)AttributeHelper.merge(attributeList));
        result.effects = EffectData.merge(effectList);
    }
}

