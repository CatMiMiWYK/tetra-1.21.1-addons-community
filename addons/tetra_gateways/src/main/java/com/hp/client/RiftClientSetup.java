package com.hp.client;

import com.hp.effect.RiftEffects;
import com.hp.rift.RiftConfigBridge;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import se.mickelus.tetra.blocks.workbench.gui.WorkbenchStatsGui;
import se.mickelus.tetra.effect.ItemEffect;
import se.mickelus.tetra.gui.stats.StatsHelper;
import se.mickelus.tetra.gui.stats.bar.GuiStatBar;
import se.mickelus.tetra.gui.stats.bar.GuiStatBase;
import se.mickelus.tetra.gui.stats.bar.GuiStatIndicator;
import se.mickelus.tetra.gui.stats.getter.IStatFormat;
import se.mickelus.tetra.gui.stats.getter.IStatGetter;
import se.mickelus.tetra.gui.stats.getter.ITooltipGetter;
import se.mickelus.tetra.gui.stats.getter.LabelGetterBasic;
import se.mickelus.tetra.gui.stats.getter.StatFormat;
import se.mickelus.tetra.gui.stats.getter.StatGetterEffectEfficiency;
import se.mickelus.tetra.gui.stats.getter.StatGetterEffectLevel;
import se.mickelus.tetra.gui.stats.getter.TooltipGetterMultiValue;
import se.mickelus.tetra.gui.stats.getter.TooltipGetterNone;
import se.mickelus.tetra.items.modular.impl.holo.gui.craft.HoloStatsGui;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class RiftClientSetup {
    private static final int BAR_MIN = 0;
    private static final int BAR_MAX = 100;

    public static void init(IEventBus modEventBus) {
        modEventBus.register(RiftClientSetup.class);
    }

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(RiftClientSetup::registerStats);
    }

    private static void registerStats() {
        GuiStatBar attributes = createAttributeBar();
        GuiStatBar info = createInfoBar();
        HoloStatsGui.addBar(attributes);
        HoloStatsGui.addBar(info);
        HoloStatsGui.addBar(createPoolBar("tetra.stats.rift_pool_a", RiftEffects.RIFT_POOL_A));
        HoloStatsGui.addBar(createPoolBar("tetra.stats.rift_pool_b", RiftEffects.RIFT_POOL_B));
        HoloStatsGui.addBar(createPoolBar("tetra.stats.rift_pool_c", RiftEffects.RIFT_POOL_C));
        WorkbenchStatsGui.addBar(createAttributeBar());
        WorkbenchStatsGui.addBar(createInfoBar());
        WorkbenchStatsGui.addBar(createPoolBar("tetra.stats.rift_pool_a", RiftEffects.RIFT_POOL_A));
        WorkbenchStatsGui.addBar(createPoolBar("tetra.stats.rift_pool_b", RiftEffects.RIFT_POOL_B));
        WorkbenchStatsGui.addBar(createPoolBar("tetra.stats.rift_pool_c", RiftEffects.RIFT_POOL_C));
    }

    private static GuiStatBar createAttributeBar() {
        return new GuiStatBar(0, 0, 59, "tetra.stats.rift_attributes", 0.0, 100.0, false,
                new StatGetterEffectLevel(RiftEffects.RIFT_ATTRIBUTES, 1.0),
                LabelGetterBasic.noLabel,
                new TooltipGetterNone("tetra.stats.rift_attributes.tooltip"))
                .setIndicators(attributeIndicators());
    }

    private static GuiStatIndicator[] attributeIndicators() {
        List<GuiStatIndicator> indicators = new ArrayList<>();
        int icon = 0;
        for (Attribute attribute : BuiltInRegistries.ATTRIBUTE) {
            ResourceLocation id = BuiltInRegistries.ATTRIBUTE.getKey(attribute);
            if (RiftConfigBridge.isAttributeBlocked(id)) {
                continue;
            }
            String effectName = "rift_attributes_" + RiftEffects.attributeName(attribute);
            indicators.add(indicator("tetra.stats." + effectName, icon, RiftEffects.attributeEffect(attribute),
                    StatFormat.oneDecimal, StatFormat.oneDecimal, 100.0));
            icon = (icon + 1) % 24;
        }
        indicators.add(indicator("tetra.stats.rift_damage_bonus", icon, RiftEffects.RIFT_DAMAGE_BONUS,
                StatFormat.oneDecimal, StatFormat.oneDecimal, 100.0));
        return indicators.toArray(GuiStatIndicator[]::new);
    }

    private static GuiStatBar createInfoBar() {
        return new GuiStatBar(0, 0, 59, "tetra.stats.rift_info", 0.0, 100.0, false,
                new StatGetterEffectLevel(RiftEffects.RIFT_INFO, 1.0),
                LabelGetterBasic.noLabel,
                new TooltipGetterNone("tetra.stats.rift_info.tooltip"))
                .setIndicators(
                        indicator("tetra.stats.rift_reward_count", 0, new StatGetterEffectLevel(RiftEffects.RIFT_REWARD_COUNT, 1.0), StatFormat.noDecimal),
                        indicator("tetra.stats.rift_dual", 1, RiftEffects.RIFT_DUAL, StatFormat.noDecimal, StatFormat.noDecimal, 100.0),
                        indicator("tetra.stats.rift_local_spawn_mode", 3, new StatGetterEffectLevel(RiftEffects.RIFT_LOCAL_SPAWN_MODE, 1.0), StatFormat.noDecimal),
                        indicator("tetra.stats.rift_challenge_time", 4, new StatGetterEffectLevel(RiftEffects.RIFT_CHALLENGE_TIME, 1.0), StatFormat.noDecimal),
                        indicator("tetra.stats.rift_wave_interval", 5, new StatGetterEffectLevel(RiftEffects.RIFT_WAVE_INTERVAL, 1.0), StatFormat.noDecimal),
                        indicator("tetra.stats.rift_wave_count", 6, new StatGetterEffectLevel(RiftEffects.RIFT_WAVE_COUNT, 1.0), StatFormat.noDecimal),
                        indicator("tetra.stats.rift_mob_base_health", 9, new StatGetterEffectLevel(RiftEffects.RIFT_MOB_BASE_HEALTH, 1.0), StatFormat.noDecimal),
                        indicator("tetra.stats.rift_mob_spawn", 10, RiftEffects.RIFT_MOB_SPAWN, StatFormat.noDecimal, StatFormat.noDecimal, 1.0));
    }

    private static GuiStatBar createPoolBar(String labelKey, ItemEffect effect) {
        StatGetterEffectLevel getter = new StatGetterEffectLevel(effect, 1.0);
        return new GuiStatBar(0, 0, 59, labelKey, 0.0, 100.0, false,
                getter,
                LabelGetterBasic.integerLabel,
                new TooltipGetterMultiValue(labelKey + ".tooltip",
                        StatsHelper.withStats(getter),
                        StatsHelper.withFormat(StatFormat.noDecimal)));
    }

    private static GuiStatIndicator indicator(String labelKey, int icon, IStatGetter getter, IStatFormat format) {
        return new GuiStatIndicator(0, 0, labelKey, icon, getter,
                new TooltipGetterMultiValue(labelKey + ".tooltip",
                        StatsHelper.withStats(getter),
                        StatsHelper.withFormat(format)));
    }

    private static GuiStatIndicator indicator(String labelKey, int icon, ItemEffect effect, IStatFormat levelFormat,
            IStatFormat efficiencyFormat, double efficiencyMultiplier) {
        StatGetterEffectLevel levelGetter = new StatGetterEffectLevel(effect, 1.0);
        StatGetterEffectEfficiency efficiencyGetter = new StatGetterEffectEfficiency(effect, efficiencyMultiplier);
        return new GuiStatIndicator(0, 0, labelKey, icon, levelGetter,
                new TooltipGetterMultiValue(labelKey + ".tooltip",
                        StatsHelper.withStats(levelGetter, efficiencyGetter),
                        StatsHelper.withFormat(levelFormat, efficiencyFormat)));
    }

    private RiftClientSetup() {
    }
}
