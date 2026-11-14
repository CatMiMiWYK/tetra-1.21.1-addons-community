package net.yiran.extraholopage.gui;

import com.google.common.base.Suppliers;
import com.google.common.collect.Multimap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.yiran.extraholopage.Config;
import net.yiran.extraholopage.KeyMappingHandler;
import net.yiran.extraholopage.api.TooltipRegistries;
import net.yiran.extraholopage.compat.CompatHandler;
import net.yiran.extraholopage.compat.OverhaulCompat;
import net.yiran.extraholopage.util.KeyMappingUtil;
import se.mickelus.tetra.aspect.ItemAspect;
import se.mickelus.tetra.data.DataManager;
import se.mickelus.tetra.effect.ItemEffect;
import se.mickelus.tetra.module.data.AspectData;
import se.mickelus.tetra.module.data.EffectData;
import se.mickelus.tetra.module.data.MaterialData;
import se.mickelus.tetra.module.data.ToolData;

public class TooltipHandler {
    public static Supplier<TooltipHandler> INSTANCE = Suppliers.memoize(TooltipHandler::new);
    public int size = 0;
    public int index = 0;
    public int scrollDelta = 0;
    public int nowProgress = 0;
    public int progressLength = Config.PROGRESS_SHOW_TICK.get();
    public int bar = Config.PROGRESS_BAR_SIZE.get();
    public String style = Config.PROGRESS_STYLE.get().substring(0, 1);
    public boolean isSelected = false;
    public int lastScrolledTick = 0;
    public boolean showNeedAdvancedTooltip = Config.SHOW_NEED_ADVANCED_TOOLTIP.get();

    @SubscribeEvent
    public void onTick(ClientTickEvent.Pre event) {
        --this.lastScrolledTick;
        if (this.isSelected) {
            this.isSelected = false;
        }
        if (this.size < 2) {
            return;
        }
        if (!KeyMappingUtil.isKeyMappingPressed(KeyMappingHandler.showTooltip)) {
            this.nowProgress = 0;
        } else if (this.lastScrolledTick > 1) {
            this.updateProgress();
        } else {
            this.nowProgress = this.reduce(this.nowProgress);
        }
    }

    public void updateProgress() {
        int progress = this.scrollDelta / 6;
        this.scrollDelta -= progress;
        this.nowProgress += progress;
        if (this.nowProgress >= this.progressLength) {
            this.toNextPage();
        }
    }

    public void toNextPage() {
        this.nowProgress = 0;
        ++this.index;
        this.scrollDelta = 0;
    }

    public int reduce(int number) {
        return --number < 0 ? 0 : number;
    }

    // 1.21.1 的 ScreenEvent.MouseScrolled 是抽象基类，必须监听 Pre/Post 子类。
    @SubscribeEvent
    public void onMouseScrolled(ScreenEvent.MouseScrolled.Post event) {
        if (!this.isSelected) {
            return;
        }
        this.lastScrolledTick = 20;
        this.scrollDelta -= Math.min(0, (int)(event.getScrollDeltaY() * 8.0));
    }

    @SubscribeEvent(priority=EventPriority.HIGHEST)
    public void onTooltip(ItemTooltipEvent event) {
        if (this.showNeedAdvancedTooltip && !event.getFlags().isAdvanced()) {
            return;
        }
        List<Component> originToolTip = event.getToolTip();
        ArrayList<Component> toolTip = new ArrayList<Component>();
        Map<MaterialData, ResourceLocation> map = DataManager.instance.materialData.getData().entrySet().stream().filter(this::filterHide).filter(d -> d.getValue().material.getPredicate() != null && d.getValue().material.getPredicate().test(event.getItemStack())).collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
        List<MaterialData> pMaterialData = map.keySet().stream().toList();
        this.size = pMaterialData.size();
        if (this.size == 0) {
            return;
        }
        String keyMappingName = KeyMappingUtil.getKeyMappingName(KeyMappingHandler.showTooltip);
        if (!KeyMappingUtil.isKeyMappingPressed(KeyMappingHandler.showTooltip)) {
            toolTip.add(Component.literal("\u00a77[\u00a78 " + keyMappingName + " \u00a77]\u00a78 +"));
        } else {
            toolTip.add(Component.literal("\u00a78[\u00a7f " + keyMappingName + " \u00a78]\u00a7f +"));
            if (this.index >= this.size) {
                this.index = 0;
            }
            if (this.size > 1) {
                this.addProgressBar(toolTip);
                this.isSelected = true;
            }
            MaterialData materialData = pMaterialData.get(this.index);
            if (Config.SHOW_SOURCE.get()) {
                toolTip.add(MaterialTooltipHelper.addPrefix(ComponentHelper.translatable("tetra.holo.craft.materials.stat.source", MaterialTooltipHelper.color1), ComponentHelper.literal(map.get(materialData).toString(), MaterialTooltipHelper.color1)));
            }
            if (Config.SHOW_CATEGORY.get()) {
                toolTip.add(MaterialTooltipHelper.addPrefix(ComponentHelper.translatable("tetra.holo.craft.materials.stat.category", MaterialTooltipHelper.color1), ComponentHelper.translatable("tetra.variant_category." + materialData.category + ".label", MaterialTooltipHelper.color1)));
            }
            if (Config.SHOW_DURABILITY.get()) {
                this.addIfNotZero(toolTip, "durability", Float.valueOf(materialData.durability));
            }
            if (Config.SHOW_BASIC.get()) {
                this.addIfNotZero(toolTip, "primary", materialData.primary);
                this.addIfNotZero(toolTip, "secondary", materialData.secondary);
                this.addIfNotZero(toolTip, "tertiary", materialData.tertiary);
            }
            if (Config.SHOW_TOOL_LEVEL.get()) {
                this.addIfNotZero(toolTip, "toolLevel", materialData.toolLevel);
                this.addIfNotZero(toolTip, "toolEfficiency", Float.valueOf(materialData.toolEfficiency));
            }
            if (Config.SHOW_XP_COST.get()) {
                this.addIfNotZero(toolTip, "experienceCost", Float.valueOf(materialData.experienceCost));
            }
            if (Config.SHOW_INTEGRITY_INFO.get()) {
                this.addIfNotZero(toolTip, "integrityGain", Float.valueOf(materialData.integrityGain));
                this.addIfNotZero(toolTip, "integrityCost", Float.valueOf(materialData.integrityCost));
            }
            if (Config.SHOW_XP_COST.get()) {
                this.addIfNotZero(toolTip, "magicCapacity", materialData.magicCapacity);
            }
            if (Config.SHOW_REQUIRED_TOOL.get()) {
                this.addRequiredToolTooltip(toolTip, materialData.requiredTools);
            }
            if (Config.SHOW_ATTRIBUTES.get()) {
                if (CompatHandler.OverhaulIsLoaded) {
                    OverhaulCompat.addAttributeTooltip(toolTip, materialData);
                } else {
                    this.addAttributeTooltip(toolTip, materialData.attributes);
                }
            }
            if (Config.SHOW_EFFECTS.get()) {
                if (CompatHandler.OverhaulIsLoaded) {
                    OverhaulCompat.addEffectsTooltip(toolTip, materialData);
                } else {
                    this.addEffectsTooltip(toolTip, materialData.effects);
                }
            }
            if (Config.SHOW_ASPECTS.get()) {
                this.addAspectsTooltip(toolTip, materialData.aspects);
            }
            if (Config.SHOW_IMPROVEMENTS.get()) {
                this.addImprovementsTooltip(toolTip, materialData.improvements);
            }
        }
        originToolTip.addAll(1, toolTip);
    }

    public boolean filterHide(Map.Entry<ResourceLocation, MaterialData> entry) {
        return !Config.HIDE_MATERIAL_KEYS.get().contains(entry.getValue().key);
    }

    public void addImprovementsTooltip(List<Component> toolTip, Map<String, Integer> improvements) {
        if (improvements == null) {
            return;
        }
        if (improvements.isEmpty()) {
            return;
        }
        int index = improvements.size();
        toolTip.add(MaterialTooltipHelper.addPrefix(ComponentHelper.translatable("tetra.holo.craft.materials.stat.improvements", MaterialTooltipHelper.color1)));
        for (Map.Entry<String, Integer> entry : improvements.entrySet()) {
            toolTip.add(MaterialTooltipHelper.addHeadTooltip(--index, ComponentHelper.translatable("tetra.improvement." + entry.getKey() + ".name", MaterialTooltipHelper.color1), ComponentHelper.literal(String.valueOf(entry.getValue()), MaterialTooltipHelper.color1)));
        }
    }

    public void addAspectsTooltip(List<Component> toolTip, AspectData aspects) {
        if (aspects == null) {
            return;
        }
        Map<ItemAspect, Float> levelMap = aspects.levelMap;
        if (levelMap.isEmpty()) {
            return;
        }
        int index = levelMap.size();
        toolTip.add(MaterialTooltipHelper.addPrefix(ComponentHelper.translatable("tetra.holo.craft.materials.stat.aspects", MaterialTooltipHelper.color1)));
        for (Map.Entry<ItemAspect, Float> entry : levelMap.entrySet()) {
            toolTip.add(MaterialTooltipHelper.addHeadTooltip(--index, ComponentHelper.setColor(entry.getKey().getLabel(), MaterialTooltipHelper.color1), ComponentHelper.literal(String.valueOf(entry.getValue().intValue()), MaterialTooltipHelper.color1)));
        }
    }

    public void addAttributeTooltip(List<Component> toolTip, Multimap<Attribute, AttributeModifier> attributes) {
        if (attributes == null) {
            return;
        }
        if (attributes.isEmpty()) {
            return;
        }
        toolTip.add(MaterialTooltipHelper.addPrefix(ComponentHelper.translatable("tetra.holo.craft.materials.stat.attributes", MaterialTooltipHelper.color1)));
        int index = attributes.values().size();
        for (Map.Entry<Attribute, AttributeModifier> entry : attributes.entries()) {
            toolTip.add(MaterialTooltipHelper.addHeadTooltip(--index, ComponentHelper.translatable(entry.getKey().getDescriptionId(), MaterialTooltipHelper.color1), this.getAttributeModifierValue(entry.getValue())));
        }
    }

    public MutableComponent getAttributeModifierValue(AttributeModifier modifier) {
        int color = modifier.amount() > 0.0 ? MaterialTooltipHelper.color_good : MaterialTooltipHelper.color_bad;
        if (modifier.operation().id() == 0) {
            return ComponentHelper.literal(String.valueOf(modifier.amount()), color);
        }
        return ComponentHelper.literal(modifier.amount() * 100.0 + "%", color);
    }

    public void addRequiredToolTooltip(List<Component> toolTip, ToolData toolData) {
        if (toolData == null) {
            return;
        }
        Map<net.neoforged.neoforge.common.ItemAbility, Float> levelMap = toolData.levelMap;
        if (levelMap.isEmpty()) {
            return;
        }
        toolTip.add(MaterialTooltipHelper.addPrefix(ComponentHelper.translatable("tetra.holo.craft.materials.stat.requiredTools", MaterialTooltipHelper.color1)));
        int index = levelMap.size();
        for (Map.Entry<net.neoforged.neoforge.common.ItemAbility, Float> entry : toolData.levelMap.entrySet()) {
            toolTip.add(MaterialTooltipHelper.addHeadTooltip(--index, ComponentHelper.translatable("tetra.tool." + entry.getKey().name(), MaterialTooltipHelper.color1), ComponentHelper.literal(entry.getValue().toString(), MaterialTooltipHelper.color1)));
        }
    }

    public void addEffectsTooltip(List<Component> toolTip, EffectData effectData) {
        if (effectData == null) {
            return;
        }
        Set<ItemEffect> effects = effectData.getValues();
        if (effects.isEmpty()) {
            return;
        }
        toolTip.add(MaterialTooltipHelper.addPrefix(ComponentHelper.translatable("tetra.holo.craft.materials.stat.effects", MaterialTooltipHelper.color1)));
        int index = effects.size();
        for (ItemEffect value : effects) {
            toolTip.add(MaterialTooltipHelper.addHeadTooltip(--index, TooltipRegistries.getTooltip(value).get(value, effectData.getLevel(value), effectData.getEfficiency(value))));
        }
    }

    public void addIfNotZero(List<Component> toolTip, String key, Number value) {
        if (value.intValue() != 0) {
            toolTip.add(MaterialTooltipHelper.addPrefix(ComponentHelper.translatable("tetra.holo.craft.materials.stat." + key, MaterialTooltipHelper.color1), ComponentHelper.literal(value.toString(), MaterialTooltipHelper.color1)));
        }
    }

    public void addProgressBar(List<Component> tooltips) {
        int arg0 = this.nowProgress * this.bar / this.progressLength;
        StringBuilder builder = new StringBuilder();
        builder.append("\u00a77");
        for (int i = 0; i < this.bar; ++i) {
            if (i == arg0) {
                builder.append("\u00a78");
            }
            builder.append(this.style);
        }
        tooltips.add(ComponentHelper.append(ComponentHelper.fromStrings(MaterialTooltipHelper.color1, "[", this.index + 1 + "/" + this.size, "]"), ComponentHelper.literal(builder.toString())));
    }

    public boolean isShift() {
        return Screen.hasShiftDown();
    }

    public boolean isCtrl() {
        return Screen.hasControlDown();
    }

    public boolean isAlt() {
        return Screen.hasAltDown();
    }
}
