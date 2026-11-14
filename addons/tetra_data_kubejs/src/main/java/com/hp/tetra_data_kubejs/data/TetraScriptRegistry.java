/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  dev.latvian.mods.kubejs.generator.KubeAssetGenerator
 *  dev.latvian.mods.kubejs.script.data.VirtualDataPack
 *  dev.latvian.mods.kubejs.util.JsonIO
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.packs.resources.ResourceManager
 */
package com.hp.tetra_data_kubejs.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hp.tetra_data_kubejs.TetraDataKubejs;
import com.hp.tetra_data_kubejs.data.JsonMergeUtil;
import com.hp.tetra_data_kubejs.data.ScriptedTetraEntry;
import com.hp.tetra_data_kubejs.data.TetraDataLoadDiagnostics;
import com.hp.tetra_data_kubejs.data.TetraDataUtil;
import com.hp.tetra_data_kubejs.data.TetraGeneratedJsonExporter;
import com.hp.tetra_data_kubejs.data.TetraTypingsGenerator;
import dev.latvian.mods.kubejs.generator.KubeAssetGenerator;

import dev.latvian.mods.kubejs.script.data.VirtualDataPack;
import dev.latvian.mods.kubejs.util.JsonIO;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

public final class TetraScriptRegistry {
    private static final RegistryState STARTUP = new RegistryState();
    private static final RegistryState SERVER_ACTIVE = new RegistryState();
    private static final RegistryState SERVER_STAGING = new RegistryState();
    private static final Map<String, SortedSet<String>> KNOWN_KEYS = TetraScriptRegistry.createKnownKeysMap();
    private static final SortedSet<String> KNOWN_EFFECT_KEYS = new TreeSet<String>();
    private static final SortedSet<String> KNOWN_ATTRIBUTE_KEYS = new TreeSet<String>();
    private static final SortedSet<String> KNOWN_MODULE_VARIANT_KEYS = new TreeSet<String>();
    private static final Map<String, SortedSet<String>> KNOWN_MODULE_VARIANTS_BY_MODULE = new LinkedHashMap<String, SortedSet<String>>();
    private static final SortedSet<String> KNOWN_IMPROVEMENT_GROUPS = new TreeSet<String>();
    private static final SortedSet<String> KNOWN_MATERIAL_IMPROVEMENT_KEYS = new TreeSet<String>();
    private static final Map<String, String> IMPROVEMENT_PATH_KEY_ALIASES = new LinkedHashMap<String, String>();
    private static Map<String, LinkedHashMap<String, String>> TRANSLATION_CACHE = null;
    private static Runnable ON_TRANSLATIONS_CHANGED = null;

    private TetraScriptRegistry() {
    }

    public static synchronized void clearStartup() {
        STARTUP.clear();
        TetraScriptRegistry.invalidateCaches();
    }

    public static synchronized void beginServerStaging() {
        SERVER_STAGING.clear();
    }

    public static synchronized void commitServerStaging() {
        SERVER_ACTIVE.copyFrom(SERVER_STAGING);
        TetraScriptRegistry.invalidateCaches();
        TetraScriptRegistry.notifyTranslationsChanged();
    }

    public static synchronized ScriptedTetraEntry entry(Scope scope, String directory, String key) {
        TetraScriptRegistry.invalidateCaches();
        return TetraScriptRegistry.state(scope).entry(directory, key);
    }

    public static synchronized void addTranslation(Scope scope, String language, String fullKey, String value) {
        TetraScriptRegistry.state(scope).addTranslation(language, fullKey, value);
        TetraScriptRegistry.invalidateCaches();
    }

    public static synchronized void remapTranslation(Scope scope, String oldFullKey, String newFullKey) {
        TetraScriptRegistry.state(scope).remapTranslation(oldFullKey, newFullKey);
        TetraScriptRegistry.invalidateCaches();
    }

    public static synchronized void remapEntryKey(Scope scope, ScriptedTetraEntry entry, String oldKey, String newKey) {
        TetraScriptRegistry.state(scope).remapEntryKey(entry, oldKey, newKey);
        TetraScriptRegistry.invalidateCaches();
    }

    public static synchronized boolean hasTranslation(Scope scope, String language, String fullKey) {
        return TetraScriptRegistry.state(scope).hasTranslation(language, fullKey);
    }

    public static synchronized void setDebugOutput(Scope scope, boolean enabled) {
        TetraScriptRegistry.state(scope).setDebugOutput(enabled);
    }

    public static synchronized String findTranslation(String language, String fullKey) {
        String normalizedLanguage = TetraScriptRegistry.normalizeLanguage(language);
        LinkedHashMap<String, String> translations = TetraScriptRegistry.cachedTranslations().get(normalizedLanguage);
        return translations == null ? null : translations.get(fullKey);
    }

    public static synchronized Map<String, Map<String, String>> getAllTranslations() {
        Map<String, LinkedHashMap<String, String>> cache = TetraScriptRegistry.cachedTranslations();
        LinkedHashMap<String, Map<String, String>> result = new LinkedHashMap<String, Map<String, String>>();
        for (Map.Entry<String, LinkedHashMap<String, String>> entry : cache.entrySet()) {
            result.put(entry.getKey(), new LinkedHashMap(entry.getValue()));
        }
        return result;
    }

    public static synchronized void setOnTranslationsChangedCallback(Runnable callback) {
        ON_TRANSLATIONS_CHANGED = callback;
    }

    private static void notifyTranslationsChanged() {
        if (ON_TRANSLATIONS_CHANGED != null) {
            ON_TRANSLATIONS_CHANGED.run();
        }
    }

    public static InjectStats inject(VirtualDataPack pack, ResourceManager resourceManager, boolean writeGeneratedJson) {
        long refreshKnownKeysStart = System.nanoTime();
        TetraScriptRegistry.refreshKnownKeys(resourceManager);
        long refreshKnownKeysNanos = System.nanoTime() - refreshKnownKeysStart;
        Collection<ScriptedTetraEntry> entries = TetraScriptRegistry.snapshotEntries();
        long prepareExportNanos = 0L;
        if (writeGeneratedJson) {
            long prepareExportStart = System.nanoTime();
            TetraGeneratedJsonExporter.prepareExports(entries);
            prepareExportNanos = System.nanoTime() - prepareExportStart;
        }
        boolean debugOutput = TetraScriptRegistry.isDebugOutputEnabled();
        long normalizeAndValidateNanos = 0L;
        long addDataNanos = 0L;
        long writeGeneratedNanos = 0L;
        int injectedEntries = 0;
        int skippedEntries = 0;
        int validationFailedEntries = 0;
        for (ScriptedTetraEntry entry : entries) {
            long normalizeStart = System.nanoTime();
            JsonElement json = entry.createInjectedData(resourceManager);
            if ("materials".equals(entry.getDirectory())) {
                json = TetraScriptRegistry.normalizeMaterialImprovementAliases(json);
            } else if ("modules".equals(entry.getDirectory())) {
                json = TetraScriptRegistry.normalizeMultiModuleSlotSuffixes(json);
            } else if ("improvements".equals(entry.getDirectory())) {
                json = TetraScriptRegistry.normalizeImprovementLevelBases(json);
            }
            normalizeAndValidateNanos += System.nanoTime() - normalizeStart;
            if (json != null && !json.isJsonNull()) {
                long validateStart = System.nanoTime();
                if (!TetraDataLoadDiagnostics.validateScriptedEntry(entry, json)) {
                    normalizeAndValidateNanos += System.nanoTime() - validateStart;
                    ++validationFailedEntries;
                    continue;
                }
                normalizeAndValidateNanos += System.nanoTime() - validateStart;
                if (debugOutput) {
                    TetraDataKubejs.LOGGER.info("[tetra_data_kubejs] inject {} -> {} {}", new Object[]{entry.getDirectory(), entry.getResourceId(), JsonIO.toString((JsonElement)json)});
                }
                long addDataStart = System.nanoTime();
                pack.json(entry.getResourceId(), json);
                addDataNanos += System.nanoTime() - addDataStart;
                ++injectedEntries;
                if (!writeGeneratedJson) continue;
                long writeGeneratedStart = System.nanoTime();
                TetraGeneratedJsonExporter.writeEntry(entry, json);
                writeGeneratedNanos += System.nanoTime() - writeGeneratedStart;
                continue;
            }
            ++skippedEntries;
        }
        return new InjectStats(entries.size(), injectedEntries, skippedEntries, validationFailedEntries, refreshKnownKeysNanos, prepareExportNanos, normalizeAndValidateNanos, addDataNanos, writeGeneratedNanos, writeGeneratedJson);
    }

    public static InjectStats exportGeneratedJson(ResourceManager resourceManager) {
        Collection<ScriptedTetraEntry> entries = TetraScriptRegistry.snapshotEntries();
        long prepareExportStart = System.nanoTime();
        TetraGeneratedJsonExporter.prepareExports(entries);
        long prepareExportNanos = System.nanoTime() - prepareExportStart;
        long refreshKnownKeysStart = System.nanoTime();
        TetraScriptRegistry.refreshKnownKeys(resourceManager);
        long refreshKnownKeysNanos = System.nanoTime() - refreshKnownKeysStart;
        long normalizeAndValidateNanos = 0L;
        long writeGeneratedNanos = 0L;
        int exportedEntries = 0;
        int skippedEntries = 0;
        int validationFailedEntries = 0;
        for (ScriptedTetraEntry entry : entries) {
            long normalizeStart = System.nanoTime();
            JsonElement json = entry.createInjectedData(resourceManager);
            if ("materials".equals(entry.getDirectory())) {
                json = TetraScriptRegistry.normalizeMaterialImprovementAliases(json);
            } else if ("modules".equals(entry.getDirectory())) {
                json = TetraScriptRegistry.normalizeMultiModuleSlotSuffixes(json);
            } else if ("improvements".equals(entry.getDirectory())) {
                json = TetraScriptRegistry.normalizeImprovementLevelBases(json);
            }
            normalizeAndValidateNanos += System.nanoTime() - normalizeStart;
            if (json == null || json.isJsonNull()) {
                ++skippedEntries;
                continue;
            }
            long validateStart = System.nanoTime();
            if (!TetraDataLoadDiagnostics.validateScriptedEntry(entry, json)) {
                normalizeAndValidateNanos += System.nanoTime() - validateStart;
                ++validationFailedEntries;
                continue;
            }
            normalizeAndValidateNanos += System.nanoTime() - validateStart;
            long writeGeneratedStart = System.nanoTime();
            TetraGeneratedJsonExporter.writeEntry(entry, json);
            writeGeneratedNanos += System.nanoTime() - writeGeneratedStart;
            ++exportedEntries;
        }
        return new InjectStats(entries.size(), exportedEntries, skippedEntries, validationFailedEntries, refreshKnownKeysNanos, prepareExportNanos, normalizeAndValidateNanos, 0L, writeGeneratedNanos, true);
    }

    public static void generateAssetJsons(KubeAssetGenerator generator) {
        for (Map.Entry<String, LinkedHashMap<String, String>> entry : TetraScriptRegistry.cachedTranslations().entrySet()) {
            JsonObject json = new JsonObject();
            entry.getValue().forEach((arg_0, arg_1) -> ((JsonObject)json).addProperty(arg_0, arg_1));
            generator.json(ResourceLocation.fromNamespaceAndPath("tetra", "lang/" + entry.getKey()), (JsonElement)json);
        }
    }

    public static void updateTypings() {
        Collection<ScriptedTetraEntry> entries = TetraScriptRegistry.snapshotEntries();
        Map<String, SortedSet<String>> knownKeysByDirectory = TetraScriptRegistry.snapshotKnownKeys();
        SortedSet<String> knownEffectKeys = TetraScriptRegistry.snapshotKnownEffectKeys();
        SortedSet<String> knownAttributeKeys = TetraScriptRegistry.snapshotKnownAttributeKeys();
        SortedSet<String> knownModuleVariantKeys = TetraScriptRegistry.snapshotKnownModuleVariantKeys();
        Map<String, SortedSet<String>> knownModuleVariantsByModule = TetraScriptRegistry.snapshotKnownModuleVariantsByModule();
        SortedSet<String> knownImprovementGroups = TetraScriptRegistry.snapshotKnownImprovementGroups();
        SortedSet<String> knownSlotKeys = TetraScriptRegistry.snapshotKnownSlotKeys();
        SortedSet<String> knownMaterialImprovementKeys = TetraScriptRegistry.snapshotKnownMaterialImprovementKeys();
        TetraTypingsGenerator.write(entries, knownKeysByDirectory, knownEffectKeys, knownAttributeKeys, knownModuleVariantKeys, knownModuleVariantsByModule, knownImprovementGroups, knownSlotKeys, knownMaterialImprovementKeys);
    }

    public static void updateTypings(ResourceManager resourceManager) {
        if (resourceManager != null) {
            TetraScriptRegistry.refreshKnownKeys(resourceManager);
        }
        TetraScriptRegistry.updateTypings();
    }

    private static synchronized Collection<ScriptedTetraEntry> snapshotEntries() {
        LinkedHashMap<String, ScriptedTetraEntry> merged = new LinkedHashMap<String, ScriptedTetraEntry>();
        TetraScriptRegistry.mergeEntriesInto(merged, TetraScriptRegistry.STARTUP.entries);
        TetraScriptRegistry.mergeEntriesInto(merged, TetraScriptRegistry.SERVER_ACTIVE.entries);
        ArrayList<ScriptedTetraEntry> snapshot = new ArrayList<ScriptedTetraEntry>(merged.size());
        merged.values().forEach(entry -> snapshot.add(new ScriptedTetraEntry((ScriptedTetraEntry)entry)));
        return snapshot;
    }

    private static synchronized Map<String, LinkedHashMap<String, String>> snapshotTranslations() {
        LinkedHashMap<String, LinkedHashMap<String, String>> snapshot = new LinkedHashMap<String, LinkedHashMap<String, String>>();
        TetraScriptRegistry.mergeTranslationsInto(snapshot, TetraScriptRegistry.STARTUP.translations);
        TetraScriptRegistry.mergeTranslationsInto(snapshot, TetraScriptRegistry.SERVER_ACTIVE.translations);
        return snapshot;
    }

    private static synchronized Map<String, SortedSet<String>> snapshotKnownKeys() {
        Map<String, SortedSet<String>> snapshot = TetraScriptRegistry.createKnownKeysMap();
        for (Map.Entry<String, SortedSet<String>> entry : KNOWN_KEYS.entrySet()) {
            snapshot.get(entry.getKey()).addAll((Collection<String>)entry.getValue());
        }
        return snapshot;
    }

    private static synchronized SortedSet<String> snapshotKnownEffectKeys() {
        SortedSet<String> snapshot = TetraDataUtil.baseKnownEffectKeys();
        snapshot.addAll(KNOWN_EFFECT_KEYS);
        for (ScriptedTetraEntry entry : TetraScriptRegistry.snapshotEntries()) {
            TetraScriptRegistry.collectEffectKeys(entry, snapshot);
        }
        return snapshot;
    }

    private static synchronized SortedSet<String> snapshotKnownAttributeKeys() {
        SortedSet<String> snapshot = TetraDataUtil.baseKnownAttributeKeys();
        snapshot.addAll(KNOWN_ATTRIBUTE_KEYS);
        for (ScriptedTetraEntry entry : TetraScriptRegistry.snapshotEntries()) {
            TetraScriptRegistry.collectAttributeKeys(entry, snapshot);
        }
        return snapshot;
    }

    private static synchronized SortedSet<String> snapshotKnownSlotKeys() {
        return TetraDataUtil.baseKnownSlotKeys();
    }

    private static synchronized SortedSet<String> snapshotKnownModuleVariantKeys() {
        TreeSet<String> snapshot = new TreeSet<String>(KNOWN_MODULE_VARIANT_KEYS);
        for (ScriptedTetraEntry entry : TetraScriptRegistry.snapshotEntries()) {
            if (!"modules".equals(entry.getDirectory())) continue;
            TetraScriptRegistry.collectModuleVariantKeys(entry.getData(), snapshot);
        }
        return snapshot;
    }

    private static synchronized Map<String, SortedSet<String>> snapshotKnownModuleVariantsByModule() {
        LinkedHashMap<String, SortedSet<String>> snapshot = new LinkedHashMap<String, SortedSet<String>>();
        KNOWN_MODULE_VARIANTS_BY_MODULE.forEach((moduleKey, variants) -> snapshot.put((String)moduleKey, new TreeSet(variants)));
        for (ScriptedTetraEntry entry : TetraScriptRegistry.snapshotEntries()) {
            if (!"modules".equals(entry.getDirectory())) continue;
            TetraScriptRegistry.collectModuleVariantKeys(entry.getKey(), entry.getData(), snapshot);
        }
        return snapshot;
    }

    private static synchronized SortedSet<String> snapshotKnownMaterialImprovementKeys() {
        TreeSet<String> snapshot = new TreeSet<String>(KNOWN_MATERIAL_IMPROVEMENT_KEYS);
        for (ScriptedTetraEntry entry : TetraScriptRegistry.snapshotEntries()) {
            if (!"improvements".equals(entry.getDirectory())) continue;
            TetraScriptRegistry.collectImprovementDataKeys(entry.getData(), snapshot);
        }
        return snapshot;
    }

    private static synchronized SortedSet<String> snapshotKnownImprovementGroups() {
        TreeSet<String> snapshot = new TreeSet<String>(KNOWN_IMPROVEMENT_GROUPS);
        for (ScriptedTetraEntry entry : TetraScriptRegistry.snapshotEntries()) {
            if (!"improvements".equals(entry.getDirectory())) continue;
            TetraScriptRegistry.collectImprovementGroups(entry.getData(), snapshot);
        }
        return snapshot;
    }

    private static synchronized void refreshKnownKeys(ResourceManager resourceManager) {
        if (resourceManager == null) {
            return;
        }
        Map<String, SortedSet<String>> refreshed = TetraScriptRegistry.createKnownKeysMap();
        SortedSet<String> refreshedEffectKeys = TetraDataUtil.baseKnownEffectKeys();
        SortedSet<String> refreshedAttributeKeys = TetraDataUtil.baseKnownAttributeKeys();
        TreeSet<String> refreshedModuleVariantKeys = new TreeSet<String>();
        LinkedHashMap<String, SortedSet<String>> refreshedModuleVariantsByModule = new LinkedHashMap<String, SortedSet<String>>();
        TreeSet<String> refreshedImprovementGroups = new TreeSet<String>();
        TreeSet<String> refreshedMaterialImprovementKeys = new TreeSet<String>();
        LinkedHashMap<String, String> refreshedImprovementAliases = new LinkedHashMap<String, String>();
        for (String directory : TetraDataUtil.COMPLETION_DIRECTORIES) {
            resourceManager.listResources(directory, location -> "tetra".equals(location.getNamespace()) && location.getPath().endsWith(".json")).keySet().stream().map(ResourceLocation::getPath).map(path -> TetraDataUtil.extractKeyFromPath(directory, path)).forEach(refreshed.get(directory)::add);
        }
        for (String directory : TetraDataUtil.ALL_DIRECTORIES) {
            resourceManager.listResources(directory, location -> "tetra".equals(location.getNamespace()) && location.getPath().endsWith(".json")).keySet().forEach(location -> {
                JsonElement json = TetraDataUtil.readJson(resourceManager, location);
                TetraScriptRegistry.collectEffectKeys(directory, json, refreshedEffectKeys);
                TetraScriptRegistry.collectAttributeKeys(directory, json, refreshedAttributeKeys);
                if ("modules".equals(directory)) {
                    TetraScriptRegistry.collectModuleVariantKeys(json, refreshedModuleVariantKeys);
                    TetraScriptRegistry.collectModuleVariantKeys(TetraDataUtil.extractKeyFromPath(directory, location.getPath()), json, refreshedModuleVariantsByModule);
                }
                if ("improvements".equals(directory)) {
                    SortedSet<String> keys = TetraScriptRegistry.extractImprovementDataKeys(json);
                    refreshedMaterialImprovementKeys.addAll(keys);
                    TetraScriptRegistry.collectImprovementGroups(json, refreshedImprovementGroups);
                    if (keys.size() == 1) {
                        refreshedImprovementAliases.put(TetraDataUtil.extractKeyFromPath(directory, location.getPath()), keys.first());
                    }
                }
            });
        }
        KNOWN_KEYS.clear();
        KNOWN_KEYS.putAll(refreshed);
        KNOWN_EFFECT_KEYS.clear();
        KNOWN_EFFECT_KEYS.addAll(refreshedEffectKeys);
        KNOWN_ATTRIBUTE_KEYS.clear();
        KNOWN_ATTRIBUTE_KEYS.addAll(refreshedAttributeKeys);
        KNOWN_MODULE_VARIANT_KEYS.clear();
        KNOWN_MODULE_VARIANT_KEYS.addAll(refreshedModuleVariantKeys);
        KNOWN_MODULE_VARIANTS_BY_MODULE.clear();
        refreshedModuleVariantsByModule.forEach((moduleKey, variants) -> KNOWN_MODULE_VARIANTS_BY_MODULE.put((String)moduleKey, new TreeSet(variants)));
        KNOWN_IMPROVEMENT_GROUPS.clear();
        KNOWN_IMPROVEMENT_GROUPS.addAll(refreshedImprovementGroups);
        KNOWN_MATERIAL_IMPROVEMENT_KEYS.clear();
        KNOWN_MATERIAL_IMPROVEMENT_KEYS.addAll(refreshedMaterialImprovementKeys);
        IMPROVEMENT_PATH_KEY_ALIASES.clear();
        IMPROVEMENT_PATH_KEY_ALIASES.putAll(refreshedImprovementAliases);
    }

    private static RegistryState state(Scope scope) {
        return switch (scope) {
            default -> throw new IncompatibleClassChangeError();
            case Scope.STARTUP -> STARTUP;
            case Scope.SERVER_STAGING -> SERVER_STAGING;
        };
    }

    private static String normalizeLanguage(String language) {
        return language == null ? "zh_cn" : language.trim().toLowerCase(Locale.ROOT);
    }

    private static Map<String, SortedSet<String>> createKnownKeysMap() {
        LinkedHashMap<String, SortedSet<String>> keysByDirectory = new LinkedHashMap<String, SortedSet<String>>();
        for (String directory : TetraDataUtil.COMPLETION_DIRECTORIES) {
            keysByDirectory.put(directory, new TreeSet());
        }
        return keysByDirectory;
    }

    private static void mergeEntriesInto(Map<String, ScriptedTetraEntry> target, Map<String, ScriptedTetraEntry> source) {
        for (Map.Entry<String, ScriptedTetraEntry> sourceEntry : source.entrySet()) {
            ScriptedTetraEntry existing = target.get(sourceEntry.getKey());
            if (existing == null) {
                target.put(sourceEntry.getKey(), new ScriptedTetraEntry(sourceEntry.getValue()));
                continue;
            }
            if (sourceEntry.getValue().shouldReplace()) {
                target.put(sourceEntry.getKey(), new ScriptedTetraEntry(sourceEntry.getValue()));
                continue;
            }
            if (sourceEntry.getValue().getData() != null) {
                existing.merge(sourceEntry.getValue().getData());
            }
            if (sourceEntry.getValue().shouldMergeExisting()) {
                existing.setMergeExisting(true);
            }
            if (sourceEntry.getValue().getCopyFromKey() == null) continue;
            existing.setCopyFromKey(sourceEntry.getValue().getCopyFromKey());
        }
    }

    private static void mergeTranslationsInto(Map<String, LinkedHashMap<String, String>> target, Map<String, LinkedHashMap<String, String>> source) {
        for (Map.Entry<String, LinkedHashMap<String, String>> entry : source.entrySet()) {
            target.computeIfAbsent(entry.getKey(), ignored -> new LinkedHashMap()).putAll((Map)entry.getValue());
        }
    }

    private static synchronized JsonElement normalizeMaterialImprovementAliases(JsonElement json) {
        JsonObject improvements;
        JsonObject jsonObject = TetraDataUtil.jsonObject(json);
        JsonObject jsonObject2 = improvements = jsonObject == null ? null : TetraDataUtil.jsonObject(jsonObject.get("improvements"));
        if (jsonObject == null || improvements == null || improvements.size() == 0) {
            return json;
        }
        Map<String, String> aliases = TetraScriptRegistry.snapshotImprovementPathAliases();
        JsonObject normalized = new JsonObject();
        boolean changed = false;
        for (Map.Entry entry : improvements.entrySet()) {
            String normalizedKey = TetraDataUtil.normalizeKey((String)entry.getKey());
            String resolvedKey = aliases.getOrDefault(normalizedKey, normalizedKey);
            changed |= !resolvedKey.equals(normalizedKey);
            JsonElement existing = normalized.get(resolvedKey);
            JsonElement value = (JsonElement)entry.getValue();
            if (existing != null && existing.isJsonPrimitive() && value.isJsonPrimitive() && existing.getAsJsonPrimitive().isNumber() && value.getAsJsonPrimitive().isNumber()) {
                normalized.addProperty(resolvedKey, (Number)Math.max(existing.getAsInt(), value.getAsInt()));
                continue;
            }
            normalized.add(resolvedKey, value.deepCopy());
        }
        if (!changed) {
            return json;
        }
        JsonObject copy = jsonObject.deepCopy();
        copy.add("improvements", (JsonElement)normalized);
        return copy;
    }

    private static synchronized Map<String, LinkedHashMap<String, String>> cachedTranslations() {
        if (TRANSLATION_CACHE == null) {
            TRANSLATION_CACHE = TetraScriptRegistry.snapshotTranslations();
        }
        return TRANSLATION_CACHE;
    }

    private static void invalidateCaches() {
        TRANSLATION_CACHE = null;
    }

    private static synchronized Map<String, String> snapshotImprovementPathAliases() {
        LinkedHashMap<String, String> snapshot = new LinkedHashMap<String, String>(IMPROVEMENT_PATH_KEY_ALIASES);
        for (ScriptedTetraEntry entry : TetraScriptRegistry.snapshotEntries()) {
            SortedSet<String> keys;
            if (!"improvements".equals(entry.getDirectory()) || (keys = TetraScriptRegistry.extractImprovementDataKeys(entry.getData())).size() != 1) continue;
            snapshot.put(entry.getKey(), keys.first());
        }
        return snapshot;
    }

    private static synchronized boolean isDebugOutputEnabled() {
        if (TetraScriptRegistry.SERVER_ACTIVE.debugOutput != null) {
            return TetraScriptRegistry.SERVER_ACTIVE.debugOutput;
        }
        if (TetraScriptRegistry.STARTUP.debugOutput != null) {
            return TetraScriptRegistry.STARTUP.debugOutput;
        }
        return false;
    }

    private static JsonElement normalizeImprovementLevelBases(JsonElement json) {
        if (json == null || json.isJsonNull() || !json.isJsonArray()) {
            return json;
        }
        JsonArray array = json.getAsJsonArray();
        if (array.size() <= 1) {
            return json;
        }
        JsonObject base = TetraDataUtil.jsonObject(array.get(0));
        if (base == null || base.has("level")) {
            return json;
        }
        boolean hasLevelEntries = false;
        JsonArray expanded = new JsonArray();
        for (int i = 1; i < array.size(); ++i) {
            JsonObject levelObject = TetraDataUtil.jsonObject(array.get(i));
            if (levelObject == null || !levelObject.has("level")) {
                return json;
            }
            hasLevelEntries = true;
            expanded.add((JsonElement)JsonMergeUtil.merge((JsonElement)base, (JsonElement)levelObject).getAsJsonObject());
        }
        return hasLevelEntries ? expanded : json;
    }

    private static JsonElement normalizeMultiModuleSlotSuffixes(JsonElement json) {
        JsonObject jsonObject = TetraDataUtil.jsonObject(json);
        if (jsonObject == null) {
            return json;
        }
        JsonElement typeElement = jsonObject.get("type");
        if (typeElement == null || !typeElement.isJsonPrimitive() || !typeElement.getAsJsonPrimitive().isString()) {
            return json;
        }
        String type = typeElement.getAsString();
        if (!"tetra:multi_module".equals(type) && !"tetra:multi_major_module".equals(type)) {
            return json;
        }
        JsonArray slots = TetraDataUtil.jsonArray(jsonObject, "slots");
        if (slots == null || slots.size() == 0) {
            return json;
        }
        JsonArray slotSuffixes = TetraDataUtil.jsonArray(jsonObject, "slotSuffixes");
        if (slotSuffixes == null) {
            slotSuffixes = new JsonArray();
            jsonObject.add("slotSuffixes", (JsonElement)slotSuffixes);
        }
        while (slotSuffixes.size() < slots.size()) {
            slotSuffixes.add("");
        }
        return jsonObject;
    }

    private static void collectEffectKeys(ScriptedTetraEntry entry, SortedSet<String> target) {
        TetraScriptRegistry.collectEffectKeys(entry.getDirectory(), entry.getData(), target);
    }

    private static void collectAttributeKeys(ScriptedTetraEntry entry, SortedSet<String> target) {
        TetraScriptRegistry.collectAttributeKeys(entry.getDirectory(), entry.getData(), target);
    }

    private static void collectEffectKeys(String directory, JsonElement json, SortedSet<String> target) {
        if (json == null || json.isJsonNull()) {
            return;
        }
        if ("item_effects".equals(directory)) {
            String effectKey;
            JsonElement effectElement;
            JsonObject jsonObject = TetraDataUtil.jsonObject(json);
            JsonElement jsonElement = effectElement = jsonObject == null ? null : jsonObject.get("effect");
            if (effectElement != null && effectElement.isJsonPrimitive() && !(effectKey = effectElement.getAsString().trim()).isEmpty()) {
                target.add(effectKey);
            }
        }
        TetraScriptRegistry.collectNestedEffectKeys(json, target);
    }

    private static void collectAttributeKeys(String directory, JsonElement json, SortedSet<String> target) {
        if (json == null || json.isJsonNull()) {
            return;
        }
        TetraScriptRegistry.collectNestedAttributeKeys(json, target);
    }

    private static void collectNestedEffectKeys(JsonElement json, SortedSet<String> target) {
        if (json == null || json.isJsonNull()) {
            return;
        }
        if (json.isJsonArray()) {
            json.getAsJsonArray().forEach(element -> TetraScriptRegistry.collectNestedEffectKeys(element, target));
            return;
        }
        if (!json.isJsonObject()) {
            return;
        }
        JsonObject jsonObject = json.getAsJsonObject();
        JsonObject effectsObject = TetraDataUtil.jsonObject(jsonObject.get("effects"));
        if (effectsObject != null) {
            effectsObject.entrySet().stream().map(Map.Entry::getKey).filter(key -> !key.isBlank()).forEach(target::add);
        }
        jsonObject.entrySet().forEach(entry -> {
            if (!"effects".equals(entry.getKey())) {
                TetraScriptRegistry.collectNestedEffectKeys((JsonElement)entry.getValue(), target);
            }
        });
    }

    private static void collectNestedAttributeKeys(JsonElement json, SortedSet<String> target) {
        if (json == null || json.isJsonNull()) {
            return;
        }
        if (json.isJsonArray()) {
            json.getAsJsonArray().forEach(element -> TetraScriptRegistry.collectNestedAttributeKeys(element, target));
            return;
        }
        if (!json.isJsonObject()) {
            return;
        }
        JsonObject jsonObject = json.getAsJsonObject();
        JsonObject attributesObject = TetraDataUtil.jsonObject(jsonObject.get("attributes"));
        if (attributesObject != null) {
            attributesObject.entrySet().stream().map(Map.Entry::getKey).filter(key -> !key.isBlank()).forEach(target::add);
        }
        jsonObject.entrySet().forEach(entry -> {
            if (!"attributes".equals(entry.getKey())) {
                TetraScriptRegistry.collectNestedAttributeKeys((JsonElement)entry.getValue(), target);
            }
        });
    }

    private static void collectModuleVariantKeys(JsonElement json, SortedSet<String> target) {
        if (json == null || json.isJsonNull()) {
            return;
        }
        JsonObject jsonObject = TetraDataUtil.jsonObject(json);
        if (jsonObject == null) {
            return;
        }
        JsonArray variants = TetraDataUtil.jsonArray(jsonObject, "variants");
        if (variants == null) {
            return;
        }
        variants.forEach(variant -> {
            String key;
            JsonObject variantObject = TetraDataUtil.jsonObject(variant);
            if (variantObject == null) {
                return;
            }
            JsonElement keyElement = variantObject.get("key");
            if (keyElement != null && keyElement.isJsonPrimitive() && keyElement.getAsJsonPrimitive().isString() && !(key = keyElement.getAsString().trim()).isEmpty()) {
                target.add(TetraDataUtil.normalizeVariantKey(key));
            }
        });
    }

    private static void collectModuleVariantKeys(String moduleKey, JsonElement json, Map<String, SortedSet<String>> target) {
        if (moduleKey == null || moduleKey.isBlank() || json == null || json.isJsonNull()) {
            return;
        }
        JsonObject jsonObject = TetraDataUtil.jsonObject(json);
        if (jsonObject == null) {
            return;
        }
        JsonArray variants = TetraDataUtil.jsonArray(jsonObject, "variants");
        if (variants == null) {
            return;
        }
        String normalizedModuleKey = TetraDataUtil.normalizeKey(moduleKey);
        SortedSet<String> keys = target.computeIfAbsent(normalizedModuleKey, ignored -> new TreeSet<String>());
        TetraScriptRegistry.collectModuleVariantKeys(json, keys);
    }

    private static SortedSet<String> extractImprovementDataKeys(JsonElement json) {
        TreeSet<String> keys = new TreeSet<String>();
        TetraScriptRegistry.collectImprovementDataKeys(json, keys);
        return keys;
    }

    private static void collectImprovementDataKeys(JsonElement json, SortedSet<String> target) {
        String key;
        if (json == null || json.isJsonNull()) {
            return;
        }
        if (json.isJsonArray()) {
            json.getAsJsonArray().forEach(element -> TetraScriptRegistry.collectImprovementDataKeys(element, target));
            return;
        }
        JsonObject jsonObject = TetraDataUtil.jsonObject(json);
        if (jsonObject == null) {
            return;
        }
        JsonElement keyElement = jsonObject.get("key");
        if (keyElement != null && keyElement.isJsonPrimitive() && keyElement.getAsJsonPrimitive().isString() && !(key = keyElement.getAsString().trim()).isEmpty()) {
            target.add(TetraDataUtil.normalizeKey(key));
        }
    }

    private static void collectImprovementGroups(JsonElement json, SortedSet<String> target) {
        String group;
        if (json == null || json.isJsonNull()) {
            return;
        }
        if (json.isJsonArray()) {
            json.getAsJsonArray().forEach(element -> TetraScriptRegistry.collectImprovementGroups(element, target));
            return;
        }
        JsonObject jsonObject = TetraDataUtil.jsonObject(json);
        if (jsonObject == null) {
            return;
        }
        JsonElement groupElement = jsonObject.get("group");
        if (groupElement != null && groupElement.isJsonPrimitive() && groupElement.getAsJsonPrimitive().isString() && !(group = groupElement.getAsString().trim()).isEmpty()) {
            target.add(group);
        }
    }

    private static final class RegistryState {
        private final Map<String, ScriptedTetraEntry> entries = new LinkedHashMap<String, ScriptedTetraEntry>();
        private final Map<String, LinkedHashMap<String, String>> translations = new LinkedHashMap<String, LinkedHashMap<String, String>>();
        private Boolean debugOutput;

        private RegistryState() {
        }

        private void clear() {
            this.entries.clear();
            this.translations.clear();
            this.debugOutput = null;
        }

        private ScriptedTetraEntry entry(String directory, String key) {
            String normalizedDirectory = TetraDataUtil.normalizeDirectory(directory);
            String normalizedKey = TetraDataUtil.normalizeKey(key);
            String registryKey = normalizedDirectory + "|" + normalizedKey;
            return this.entries.computeIfAbsent(registryKey, ignored -> new ScriptedTetraEntry(normalizedDirectory, normalizedKey));
        }

        private void addTranslation(String language, String fullKey, String value) {
            String normalizedLanguage = TetraScriptRegistry.normalizeLanguage(language);
            this.translations.computeIfAbsent(normalizedLanguage, ignored -> new LinkedHashMap()).put(fullKey, value);
        }

        private boolean hasTranslation(String language, String fullKey) {
            String normalizedLanguage = TetraScriptRegistry.normalizeLanguage(language);
            LinkedHashMap<String, String> values = this.translations.get(normalizedLanguage);
            return values != null && values.containsKey(fullKey);
        }

        private void remapTranslation(String oldFullKey, String newFullKey) {
            if (oldFullKey == null || newFullKey == null || oldFullKey.equals(newFullKey)) {
                return;
            }
            for (LinkedHashMap<String, String> values : this.translations.values()) {
                if (!values.containsKey(oldFullKey)) continue;
                String value = (String)values.remove(oldFullKey);
                values.putIfAbsent(newFullKey, value);
            }
        }

        private void setDebugOutput(boolean enabled) {
            this.debugOutput = enabled;
        }

        private void remapEntryKey(ScriptedTetraEntry entry, String oldKey, String newKey) {
            String newRegistryKey;
            if (entry == null || oldKey == null || newKey == null) {
                return;
            }
            String normalizedDirectory = TetraDataUtil.normalizeDirectory(entry.getDirectory());
            String normalizedOldKey = TetraDataUtil.normalizeKey(oldKey);
            String normalizedNewKey = TetraDataUtil.normalizeKey(newKey);
            String oldRegistryKey = normalizedDirectory + "|" + normalizedOldKey;
            if (oldRegistryKey.equals(newRegistryKey = normalizedDirectory + "|" + normalizedNewKey)) {
                entry.setKey(normalizedNewKey);
                return;
            }
            ScriptedTetraEntry removed = this.entries.remove(oldRegistryKey);
            if (removed != null && removed != entry) {
                this.mergeEntries(entry, removed);
            }
            this.entries.entrySet().removeIf(mapEntry -> mapEntry.getValue() == entry);
            ScriptedTetraEntry existing = this.entries.remove(newRegistryKey);
            if (existing != null && existing != entry) {
                this.mergeEntries(entry, existing);
            }
            entry.setKey(normalizedNewKey);
            this.entries.put(newRegistryKey, entry);
        }

        private void mergeEntries(ScriptedTetraEntry target, ScriptedTetraEntry source) {
            if (target == null || source == null || target == source) {
                return;
            }
            JsonElement sourceData = source.getData();
            JsonElement targetData = target.getData();
            if (sourceData != null && !sourceData.isJsonNull()) {
                target.setData(targetData == null || targetData.isJsonNull() ? sourceData : JsonMergeUtil.merge(sourceData, targetData));
            }
            if (source.shouldMergeExisting()) {
                target.setMergeExisting(true);
            }
            if (target.getCopyFromKey() == null && source.getCopyFromKey() != null) {
                target.setCopyFromKey(source.getCopyFromKey());
            }
        }

        private void copyFrom(RegistryState other) {
            this.clear();
            for (Map.Entry<String, ScriptedTetraEntry> entry : other.entries.entrySet()) {
                this.entries.put(entry.getKey(), new ScriptedTetraEntry(entry.getValue()));
            }
            for (Map.Entry<String, LinkedHashMap<String, String>> entry : other.translations.entrySet()) {
                this.translations.put(entry.getKey(), new LinkedHashMap<String, String>(entry.getValue()));
            }
            this.debugOutput = other.debugOutput;
        }
    }

    public static enum Scope {
        STARTUP,
        SERVER_STAGING;

    }

    public record InjectStats(int totalEntries, int injectedEntries, int skippedEntries, int validationFailedEntries, long refreshKnownKeysNanos, long prepareExportNanos, long normalizeAndValidateNanos, long addDataNanos, long writeGeneratedNanos, boolean generatedExportWritten) {
        public long totalNanos() {
            return this.refreshKnownKeysNanos + this.prepareExportNanos + this.normalizeAndValidateNanos + this.addDataNanos + this.writeGeneratedNanos;
        }
    }
}

