package net.yiran.extraholopage.compat;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.yiran.extraholopage.api.TooltipRegistries;
import net.yiran.extraholopage.gui.ComponentHelper;
import net.yiran.extraholopage.gui.MaterialTooltipHelper;
import net.yiran.extraholopage.gui.TooltipHandler;
import net.yiran.tmo.ContextData;
import net.yiran.tmo.core.IMaterialData;
import se.mickelus.tetra.effect.ItemEffect;
import se.mickelus.tetra.module.data.EffectData;
import se.mickelus.tetra.module.data.MaterialData;

public class OverhaulCompat {
    public static void addAttributeTooltip(List<Component> toolTip, MaterialData data) {
        if (OverhaulCompat.getContextData(data).size() == 1) {
            TooltipHandler.INSTANCE.get().addAttributeTooltip(toolTip, data.attributes);
        } else {
            if (OverhaulCompat.getContextData(data).values().stream().map(contextData -> contextData.attributes).filter(Objects::nonNull).mapToInt(Multimap::size).sum() == 0) {
                return;
            }
            toolTip.add(MaterialTooltipHelper.addPrefix(ComponentHelper.translatable("tetra.holo.craft.materials.stat.attributes", MaterialTooltipHelper.color1)));
            LinkedHashMap<MutableComponent, String> tooltipShowMap = new LinkedHashMap<MutableComponent, String>();
            for (Map.Entry<String, ContextData> contextDataEntry : OverhaulCompat.getContextData(data).entrySet()) {
                if (contextDataEntry.getValue().attributes == null) continue;
                for (Map.Entry<Attribute, AttributeModifier> entry : contextDataEntry.getValue().attributes.entries()) {
                    OverhaulCompat.computeGroup(tooltipShowMap, MaterialTooltipHelper.getValue(ComponentHelper.translatable(entry.getKey().getDescriptionId(), MaterialTooltipHelper.color1), TooltipHandler.INSTANCE.get().getAttributeModifierValue(entry.getValue())), contextDataEntry.getKey());
                }
            }
            OverhaulCompat.toAddTooltip(toolTip, tooltipShowMap);
        }
    }

    public static void addEffectsTooltip(List<Component> toolTip, MaterialData data) {
        if (OverhaulCompat.getContextData(data).size() == 1) {
            TooltipHandler.INSTANCE.get().addEffectsTooltip(toolTip, data.effects);
        } else {
            if (OverhaulCompat.getContextData(data).values().stream().mapToInt(contextData -> contextData.effects.getValues().size()).sum() == 0) {
                return;
            }
            toolTip.add(MaterialTooltipHelper.addPrefix(ComponentHelper.translatable("tetra.holo.craft.materials.stat.effects", MaterialTooltipHelper.color1)));
            LinkedHashMap<MutableComponent, String> tooltipShowMap = new LinkedHashMap<MutableComponent, String>();
            for (Map.Entry<String, ContextData> contextDataEntry : OverhaulCompat.getContextData(data).entrySet()) {
                EffectData effectData = contextDataEntry.getValue().effects;
                for (ItemEffect itemEffect : effectData.getValues()) {
                    OverhaulCompat.computeGroup(tooltipShowMap, TooltipRegistries.getTooltip(itemEffect).get(itemEffect, effectData.getLevel(itemEffect), effectData.getEfficiency(itemEffect)), contextDataEntry.getKey());
                }
            }
            OverhaulCompat.toAddTooltip(toolTip, tooltipShowMap);
        }
    }

    public static void toAddTooltip(List<Component> toolTip, Map<MutableComponent, String> tooltipShowMap) {
        int index = tooltipShowMap.size();
        for (Map.Entry<MutableComponent, String> entry : tooltipShowMap.entrySet()) {
            toolTip.add(MaterialTooltipHelper.addHeadTooltip(--index, entry.getKey().append(ComponentHelper.literal(I18n.get("overhaul.format", entry.getValue()), MaterialTooltipHelper.color2))));
        }
    }

    public static void computeGroup(Map<MutableComponent, String> tooltipShowMap, MutableComponent tooltip, String newGroup) {
        tooltipShowMap.compute(tooltip, (component, string) -> OverhaulCompat.mergeGroup(string, newGroup));
    }

    public static String mergeGroup(String oldGroup, String newGroup) {
        String string = I18n.exists("overhaul." + newGroup) ? I18n.get("overhaul." + newGroup) : newGroup;
        if (oldGroup == null) {
            return string;
        }
        return I18n.get("overhaul.merge.format", oldGroup, string);
    }

    public static Map<String, ContextData> getContextData(MaterialData data) {
        return ((IMaterialData)data).getContextData();
    }

    public static Multimap<ItemEffect, String> getContextItemEffects(MaterialData data) {
        HashMultimap<ItemEffect, String> result = HashMultimap.create();
        for (Map.Entry<String, ContextData> entry : ((IMaterialData)data).getContextData().entrySet()) {
            for (ItemEffect effect : entry.getValue().effects.getValues()) {
                result.put(effect, entry.getKey());
            }
        }
        return result;
    }
}
