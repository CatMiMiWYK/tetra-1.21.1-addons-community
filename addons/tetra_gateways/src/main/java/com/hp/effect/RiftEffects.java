package com.hp.effect;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import se.mickelus.tetra.effect.ItemEffect;

import java.util.HashMap;
import java.util.Map;

public class RiftEffects {
    public static final ItemEffect RIFT_ATTRIBUTES = ItemEffect.get("rift_attributes");
    public static final ItemEffect RIFT_DAMAGE_BONUS = ItemEffect.get("rift_damage_bonus");
    public static final ItemEffect RIFT_INFO = ItemEffect.get("rift_info");
    public static final ItemEffect RIFT_REWARD_COUNT = ItemEffect.get("rift_reward_count");
    public static final ItemEffect RIFT_DUAL = ItemEffect.get("rift_dual");
    public static final ItemEffect RIFT_LOCAL_SPAWN_MODE = ItemEffect.get("rift_local_spawn_mode");
    public static final ItemEffect RIFT_CHALLENGE_TIME = ItemEffect.get("rift_challenge_time");
    public static final ItemEffect RIFT_WAVE_INTERVAL = ItemEffect.get("rift_wave_interval");
    public static final ItemEffect RIFT_WAVE_COUNT = ItemEffect.get("rift_wave_count");
    public static final ItemEffect RIFT_POOL_A = ItemEffect.get("rift_pool_a");
    public static final ItemEffect RIFT_POOL_B = ItemEffect.get("rift_pool_b");
    public static final ItemEffect RIFT_POOL_C = ItemEffect.get("rift_pool_c");
    public static final ItemEffect RIFT_MOB_BASE_HEALTH = ItemEffect.get("rift_mob_base_health");
    public static final ItemEffect RIFT_MOB_SPAWN = ItemEffect.get("rift_mob_spawn");
    private static final Map<Attribute, ItemEffect> ATTRIBUTE_EFFECTS = new HashMap<>();

    public static ItemEffect attributeEffect(Attribute attribute) {
        return ATTRIBUTE_EFFECTS.computeIfAbsent(attribute, value -> ItemEffect.get("rift_attributes_" + attributeName(value)));
    }

    public static String attributeName(Attribute attribute) {
        ResourceLocation id = BuiltInRegistries.ATTRIBUTE.getKey(attribute);
        if (id == null) {
            return "unknown";
        }
        return id.getNamespace() + "_" + id.getPath().replace('/', '_').replace('.', '_');
    }

    private RiftEffects() {
    }
}
