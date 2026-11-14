package net.yiran.tetrajs.kubejs;

import dev.latvian.mods.kubejs.event.EventGroupRegistry;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.registry.BuilderTypeRegistry;
import dev.latvian.mods.kubejs.script.BindingRegistry;
import dev.latvian.mods.kubejs.script.TypeWrapperRegistry;
import net.minecraft.core.registries.Registries;
import net.yiran.tetrajs.api.DynamicModularHelper;
import net.yiran.tetrajs.api.LangUtils;
import net.yiran.tetrajs.api.SchematicUtils;
import net.yiran.tetrajs.api.ShowModelHelper;
import net.yiran.tetrajs.api.StatBarHelper;
import net.yiran.tetrajs.api.TetraJSUtils;
import net.yiran.tetrajs.compat.CompatManager;
import net.yiran.tetrajs.kubejs.builders.items.BaseModularItemBuilder;
import net.yiran.tetrajs.kubejs.builders.items.BowModularItemBuilder;
import net.yiran.tetrajs.kubejs.builders.items.CrossBowModularItemBuilder;
import net.yiran.tetrajs.kubejs.builders.items.DynamicModularItemBuilder;
import net.yiran.tetrajs.kubejs.builders.items.EquipModularItemBuilder;
import net.yiran.tetrajs.kubejs.builders.items.ShieldModularItemBuilder;
import net.yiran.tetrajs.kubejs.builders.items.TwoHandedModularItemBuilder;
import net.yiran.tetrajs.kubejs.events.TetraJSEvents;
import net.yiran.tetrajs.util.NbtSlotData;
import se.mickelus.tetra.aspect.ItemAspect;
import se.mickelus.tetra.data.DataManager;
import se.mickelus.tetra.effect.ItemEffect;

public class TetraJSPlugin implements KubeJSPlugin {
    @Override
    public void registerBuilderTypes(BuilderTypeRegistry registry) {
        registry.of(Registries.ITEM, types -> {
            types.add("tetrajs:base_modular_item", BaseModularItemBuilder.class, BaseModularItemBuilder::new);
            types.add("tetrajs:two_handed_modular_item", TwoHandedModularItemBuilder.class, TwoHandedModularItemBuilder::new);
            types.add("tetrajs:dynamic_modular_item", DynamicModularItemBuilder.class, DynamicModularItemBuilder::new);
            types.add("tetrajs:bow_modular_item", BowModularItemBuilder.class, BowModularItemBuilder::new);
            types.add("tetrajs:shield_modular_item", ShieldModularItemBuilder.class, ShieldModularItemBuilder::new);
            types.add("tetrajs:crossbow_modular_item", CrossBowModularItemBuilder.class, CrossBowModularItemBuilder::new);
            types.add("tetrajs:equip_modular_item", EquipModularItemBuilder.class, EquipModularItemBuilder::new);
            CompatManager.registerCompatItemBuilder(types);
        });
    }

    @Override
    public void registerTypeWrappers(TypeWrapperRegistry typeWrappers) {
        typeWrappers.register(ItemEffect.class, (dev.latvian.mods.kubejs.script.TypeWrapperRegistry.ContextFromFunction<ItemEffect>) (context, o) -> TetraJSWrappers.toItemEffect(o));
        // ItemAbility 的 TypeWrapper 已由 KubeJS 2101.7.2 内置注册，重复注册会抛
        // "Wrapper for class ... ItemAbility already exists!" 导致 KubeJS 构造失败。
        typeWrappers.registerCodec(NbtSlotData.class, NbtSlotData.CODEC);
        typeWrappers.register(ItemAspect.class, (dev.latvian.mods.kubejs.script.TypeWrapperRegistry.ContextFromFunction<ItemAspect>) (context, o) -> TetraJSWrappers.toItemAspect(o));
    }

    @Override
    public void registerBindings(BindingRegistry event) {
        event.add("DynamicModularHelper", DynamicModularHelper.INSTANCE);
        event.add("StatBarHelper", StatBarHelper.INSTANCE);
        event.add("TetraDataManager", DataManager.instance);
        event.add("TetraJSUtils", TetraJSUtils.INSTANCE);
        event.add("ShowModelHelper", ShowModelHelper.INSTANCE);
        addTetraJSBinding(event, "DynamicModularHelper", DynamicModularHelper.INSTANCE);
        addTetraJSBinding(event, "StatBarHelper", StatBarHelper.INSTANCE);
        addTetraJSBinding(event, "DataManager", DataManager.instance);
        addTetraJSBinding(event, "TetraJSUtils", TetraJSUtils.INSTANCE);
        addTetraJSBinding(event, "ShowModelHelper", ShowModelHelper.INSTANCE);
        addTetraJSBinding(event, "LangUtils", LangUtils.INSTANCE);
        addTetraJSBinding(event, "SchematicUtils", SchematicUtils.INSTANCE);
        CompatManager.registerCompatBindings((name, value) -> addTetraJSBinding(event, name, value));
    }

    public static void addTetraJSBinding(BindingRegistry event, String name, Object value) {
        event.add("TetraJS$" + name, value);
    }

    @Override
    public void registerEvents(EventGroupRegistry registry) {
        registry.register(TetraJSEvents.GROUP);
    }
}
