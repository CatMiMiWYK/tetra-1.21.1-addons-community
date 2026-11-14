/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.packs.resources.Resource
 *  net.minecraft.server.packs.resources.ResourceManager
 *  net.minecraft.core.registries.BuiltInRegistries
 *  se.mickelus.tetra.effect.ItemEffect
 *  se.mickelus.tetra.module.ItemModule
 *  se.mickelus.tetra.module.ModuleRegistry
 */
package com.hp.tetra_data_kubejs.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hp.tetra_data_kubejs.TetraDataKubejs;
import java.io.BufferedReader;
import java.io.Reader;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.core.registries.BuiltInRegistries;
import se.mickelus.tetra.effect.ItemEffect;
import se.mickelus.tetra.module.ItemModule;
import se.mickelus.tetra.module.ModuleRegistry;

public final class TetraDataUtil {
    public static final String TETRA_NAMESPACE = "tetra";
    public static final String DEFAULT_LANGUAGE = "zh_cn";
    public static final List<String> ALL_DIRECTORIES = List.of("tiers", "tweaks", "materials", "improvements", "modules", "repairs", "enchantments", "synergies", "replacements", "schematics", "crafting_effects", "actions", "destabilization", "unlocks", "archetypes", "item_effects");
    public static final List<String> COMPLETION_DIRECTORIES = List.of("materials", "modules", "improvements", "schematics");
    public static final List<String> MATERIAL_EFFECT_KEYS = List.of("workable", "unstable", "howling", "bleeding", "backstab", "armorPenetration", "crushing", "skewering", "sweeping", "truesweep", "strikingAxe", "strikingPickaxe", "strikingCut", "strikingShovel", "strikingHoe", "sweepingStrike", "sweepingFocus", "planarSweep", "extraction", "unboundExtraction", "extractionMedialLimit", "extractionLateralLimit", "extractionAxialLimit", "extractionAxialAmplify", "extractionPlanarAmplify", "unbreaking", "blocking", "blockingReflect", "bashing", "ricochet", "piercing", "piercingHarvest", "jab", "counterweight", "quickStrike", "softStrike", "severing", "stun", "abilityDefensive", "abilityOvercharge", "abilityMomentum", "abilityCombo", "abilityRevenge", "abilityOverextend", "abilityExhilaration", "abilitySpeed", "abilityEcho", "execute", "lunge", "slam", "puncture", "pry", "overpower", "reap", "denailing", "fierySelf", "enderReverb", "sculkTaint", "haunted", "stabilizing", "criticalStrike", "intuit", "earthbind", "reaching", "janking", "throwable", "shieldbreaker", "booster", "quickSlot", "storageSlot", "potionSlot", "quiverSlot", "quickAccess", "cellSocket", "suspendSelf", "releaseLatch", "flow", "overbowed", "multishot", "ammoCapacity", "zoom", "spread", "focus", "focusEcho", "velocity", "suspend", "rangeCritical", "sweeperRange", "sweeperHorizontalSpread", "sweeperVerticalSpread", "percussionScanner");
    public static final List<String> ATTRIBUTE_KEYS = List.of("generic.max_health", "generic.follow_range", "generic.knockback_resistance", "generic.movement_speed", "generic.flying_speed", "generic.attack_damage", "generic.attack_speed", "generic.armor", "generic.armor_toughness", "generic.attack_knockback", "generic.luck", "tetra:draw_strength", "tetra:draw_speed", "tetra:ability_cooldown", "tetra:ability_damage");
    public static final Set<String> MATERIAL_CATEGORIES = Set.of("wood", "metal", "bone", "rod", "stone", "gem", "fibre", "fabric", "wool", "skin", "scale", "misc", "socket");

    private TetraDataUtil() {
    }

    public static SortedSet<String> baseKnownEffectKeys() {
        TreeSet<String> keys = new TreeSet<String>(MATERIAL_EFFECT_KEYS);
        keys.addAll(TetraDataUtil.runtimeEffectKeys());
        return keys;
    }

    public static SortedSet<String> baseKnownAttributeKeys() {
        TreeSet<String> keys = new TreeSet<String>(ATTRIBUTE_KEYS);
        try {
            for (ResourceLocation location : BuiltInRegistries.ATTRIBUTE.keySet()) {
                String fullKey = location.toString();
                keys.add(fullKey);
                if (!"minecraft".equals(location.getNamespace())) continue;
                keys.add(location.getPath());
            }
        }
        catch (Exception exception) {
            TetraDataKubejs.LOGGER.debug("Failed to read attribute registry for completions", (Throwable)exception);
        }
        return keys;
    }

    public static SortedSet<String> baseKnownSlotKeys() {
        TreeSet<String> keys = new TreeSet<String>();
        try {
            ModuleRegistry registry = ModuleRegistry.instance;
            if (registry != null) {
                for (ItemModule module : registry.getAllModules()) {
                    String slot;
                    if (module == null || (slot = module.getSlot()) == null || (slot = slot.trim()).isEmpty()) continue;
                    keys.add(slot);
                }
            }
        }
        catch (Exception exception) {
            TetraDataKubejs.LOGGER.debug("Failed to read Tetra ModuleRegistry slots for completions", (Throwable)exception);
        }
        return keys;
    }

    public static String normalizeDirectory(String directory) {
        String normalized = TetraDataUtil.stripSlashes(directory).toLowerCase(Locale.ROOT);
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("Tetra directory can't be empty");
        }
        return normalized;
    }

    public static String normalizeKey(String key) {
        String normalized = key.trim().replace('\\', '/');
        if (normalized.startsWith("tetra:")) {
            normalized = normalized.substring(TETRA_NAMESPACE.length() + 1);
        }
        if ((normalized = TetraDataUtil.stripSlashes(normalized)).isEmpty()) {
            throw new IllegalArgumentException("Tetra key can't be empty");
        }
        ResourceLocation.fromNamespaceAndPath(TETRA_NAMESPACE, normalized);
        return normalized;
    }

    public static String normalizeVariantKey(String key) {
        String normalized = key.trim().replace('\\', '/');
        if (normalized.startsWith("tetra:")) {
            normalized = normalized.substring(TETRA_NAMESPACE.length() + 1);
        }
        while (normalized.startsWith("/")) {
            normalized = normalized.substring(1);
        }
        while (normalized.contains("//")) {
            normalized = normalized.replace("//", "/");
        }
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("Tetra variant key can't be empty");
        }
        return normalized;
    }

    public static String normalizeFolderKey(String key) {
        String normalized = TetraDataUtil.normalizeVariantKey(key);
        return normalized.endsWith("/") ? normalized : normalized + "/";
    }

    public static String normalizeImprovementKey(String key) {
        String normalized = key.trim().replace('\\', '/');
        if (normalized.startsWith("tetra:")) {
            normalized = normalized.substring(TETRA_NAMESPACE.length() + 1);
        }
        while (normalized.startsWith("/")) {
            normalized = normalized.substring(1);
        }
        while (normalized.contains("//")) {
            normalized = normalized.replace("//", "/");
        }
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("Tetra improvement key can't be empty");
        }
        return normalized;
    }

    public static ResourceLocation dataId(String directory, String key) {
        return ResourceLocation.fromNamespaceAndPath(TETRA_NAMESPACE, TetraDataUtil.normalizeDirectory(directory) + "/" + TetraDataUtil.normalizeKey(key) + ".json");
    }

    public static String extractKeyFromPath(String directory, String path) {
        String normalizedDirectory = TetraDataUtil.normalizeDirectory(directory);
        String prefix = normalizedDirectory + "/";
        if (!path.startsWith(prefix) || !path.endsWith(".json")) {
            throw new IllegalArgumentException("Path " + path + " is not a tetra " + normalizedDirectory + " json");
        }
        return path.substring(prefix.length(), path.length() - ".json".length());
    }

    public static JsonElement readJson(ResourceManager resourceManager, ResourceLocation resourceId) {
        if (resourceManager == null) {
            return null;
        }
        Optional<Resource> optional = resourceManager.getResource(resourceId);
        if (optional.isEmpty()) {
            return null;
        }
        try (BufferedReader reader = optional.get().openAsReader()) {
            return JsonParser.parseReader((Reader)reader);
        }
        catch (Exception exception) {
            TetraDataKubejs.LOGGER.warn("Failed to read tetra json {} for scripted merge", (Object)resourceId, (Object)exception);
            return null;
        }
    }

    public static String materialNameKey(String key) {
        return "tetra.material." + TetraDataUtil.materialKeySuffix(key);
    }

    public static String materialPrefixKey(String key) {
        return "tetra.material." + TetraDataUtil.materialKeySuffix(key) + ".prefix";
    }

    public static String materialKeySuffix(String key) {
        String normalized = TetraDataUtil.normalizeKey(key);
        int separatorIndex = normalized.lastIndexOf(47);
        return separatorIndex >= 0 ? normalized.substring(separatorIndex + 1) : normalized;
    }

    public static String materialCategory(String key) {
        String normalized = TetraDataUtil.normalizeKey(key);
        int separatorIndex = normalized.lastIndexOf(47);
        return separatorIndex >= 0 ? normalized.substring(0, separatorIndex) : "";
    }

    public static boolean isMaterialKey(String key) {
        return MATERIAL_CATEGORIES.contains(TetraDataUtil.materialCategory(key));
    }

    public static String variantNameKey(String key) {
        return "tetra.variant." + TetraDataUtil.normalizeVariantKey(key);
    }

    public static String variantPrefixKey(String key) {
        return "tetra.variant." + TetraDataUtil.normalizeVariantKey(key) + ".prefix";
    }

    public static String variantItemNameKey(String key) {
        return "tetra.variant." + TetraDataUtil.normalizeVariantKey(key) + ".item_name";
    }

    public static String variantDescriptionKey(String key) {
        return "tetra.variant." + TetraDataUtil.normalizeVariantKey(key) + ".description";
    }

    public static JsonObject jsonObject(JsonElement json) {
        return json != null && json.isJsonObject() ? json.getAsJsonObject() : null;
    }

    public static JsonArray jsonArray(JsonObject json, String key) {
        JsonElement element = json == null ? null : json.get(key);
        return element != null && element.isJsonArray() ? element.getAsJsonArray() : null;
    }

    public static String moduleNameKey(String key) {
        return "tetra.module." + TetraDataUtil.normalizeKey(key) + ".name";
    }

    public static String moduleMaterialNameKey(String key) {
        return "tetra.module." + TetraDataUtil.normalizeKey(key) + ".material_name";
    }

    public static String moduleItemNameKey(String key) {
        return "tetra.module." + TetraDataUtil.normalizeKey(key) + ".item_name";
    }

    public static String moduleDescriptionKey(String key) {
        return "tetra.module." + TetraDataUtil.normalizeKey(key) + ".description";
    }

    public static String modulePrefixKey(String key) {
        return "tetra.module." + TetraDataUtil.normalizeKey(key) + ".prefix";
    }

    public static String improvementNameKey(String key) {
        return "tetra.improvement." + TetraDataUtil.normalizeKey(key) + ".name";
    }

    public static String improvementDescriptionKey(String key) {
        return "tetra.improvement." + TetraDataUtil.normalizeKey(key) + ".description";
    }

    public static String schematicNameKey(String key) {
        return "tetra/schematic/" + TetraDataUtil.normalizeKey(key) + ".name";
    }

    public static String schematicDescriptionKey(String key) {
        return "tetra/schematic/" + TetraDataUtil.normalizeKey(key) + ".description";
    }

    public static String schematicSlotKey(String key, int slotNumber) {
        if (slotNumber < 1) {
            throw new IllegalArgumentException("Schematic slot number must be 1 or greater");
        }
        return "tetra/schematic/" + TetraDataUtil.normalizeKey(key) + ".slot" + slotNumber;
    }

    private static String stripSlashes(String value) {
        String normalized = value.trim().replace('\\', '/');
        while (normalized.startsWith("/")) {
            normalized = normalized.substring(1);
        }
        while (normalized.endsWith("/")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        return normalized;
    }

    private static Set<String> runtimeEffectKeys() {
        try {
            Field field = ItemEffect.class.getDeclaredField("effectMap");
            field.setAccessible(true);
            Object value = field.get(null);
            if (value instanceof Map) {
                Map map = (Map)value;
                TreeSet<String> keys = new TreeSet<String>();
                for (Object key : map.keySet()) {
                    String stringKey;
                    if (!(key instanceof String) || (stringKey = (String)key).isBlank()) continue;
                    keys.add(stringKey);
                }
                return keys;
            }
        }
        catch (Exception exception) {
            TetraDataKubejs.LOGGER.debug("Failed to read Tetra ItemEffect effectMap for completions", (Throwable)exception);
        }
        return Set.of();
    }
}

