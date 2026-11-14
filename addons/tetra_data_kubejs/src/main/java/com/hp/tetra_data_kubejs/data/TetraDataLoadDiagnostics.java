/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  net.minecraft.resources.ResourceLocation
 */
package com.hp.tetra_data_kubejs.data;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hp.tetra_data_kubejs.TetraDataKubejs;
import com.hp.tetra_data_kubejs.data.ScriptedTetraEntry;
import com.hp.tetra_data_kubejs.data.TetraDataUtil;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;

public final class TetraDataLoadDiagnostics {
    private static final Set<String> SUPPORTED_DIRECTORIES = Set.of("schematics", "modules", "materials", "improvements");

    private TetraDataLoadDiagnostics() {
    }

    public static boolean validateScriptedEntry(ScriptedTetraEntry entry, JsonElement json) {
        if (entry == null || json == null || json.isJsonNull()) {
            return true;
        }
        String directory = TetraDataUtil.normalizeDirectory(entry.getDirectory());
        if (!SUPPORTED_DIRECTORIES.contains(directory)) {
            return true;
        }
        List<Problem> problems = TetraDataLoadDiagnostics.validateStructure(directory, json);
        if (problems.isEmpty()) {
            return true;
        }
        TetraDataLoadDiagnostics.logProblems("\u8df3\u8fc7\u65e0\u6548\u7684 Tetra KubeJS \u6570\u636e", directory, entry.getResourceId(), "tetra_data_kubejs \u865a\u62df\u6570\u636e\u5305", problems, null);
        return false;
    }

    public static void inspectDataStore(String directory, Map<ResourceLocation, JsonElement> data, Gson gson, Class<?> dataClass) {
        if (directory == null || data == null || gson == null || dataClass == null) {
            return;
        }
        String normalizedDirectory = TetraDataUtil.normalizeDirectory(directory);
        if (!SUPPORTED_DIRECTORIES.contains(normalizedDirectory)) {
            return;
        }
        for (Map.Entry<ResourceLocation, JsonElement> entry : data.entrySet()) {
            JsonElement json = entry.getValue();
            List<Problem> problems = TetraDataLoadDiagnostics.validateStructure(normalizedDirectory, json);
            if (!problems.isEmpty()) {
                TetraDataLoadDiagnostics.logProblems("\u53d1\u73b0\u53ef\u80fd\u5bfc\u81f4 Tetra reload \u5931\u8d25\u7684\u6570\u636e", normalizedDirectory, entry.getKey(), TetraDataLoadDiagnostics.sourcesOf(json), problems, null);
            }
            TetraDataLoadDiagnostics.inspectDeserialization(normalizedDirectory, entry.getKey(), json, gson, dataClass);
        }
    }

    private static void inspectDeserialization(String directory, ResourceLocation id, JsonElement json, Gson gson, Class<?> dataClass) {
        try {
            gson.fromJson(json, dataClass);
            return;
        }
        catch (Throwable exception) {
            TetraDataLoadDiagnostics.logProblems("Tetra \u6570\u636e\u53cd\u5e8f\u5217\u5316\u5931\u8d25", directory, id, TetraDataLoadDiagnostics.sourcesOf(json), List.of(new Problem("$", TetraDataLoadDiagnostics.describe(exception))), null);
            if (!dataClass.isArray() || json == null || !json.isJsonArray()) {
                return;
            }
            Class<?> componentClass = dataClass.getComponentType();
            JsonArray array = json.getAsJsonArray();
            for (int index = 0; index < array.size(); ++index) {
                JsonElement element = array.get(index);
                try {
                    gson.fromJson(element, componentClass);
                    continue;
                }
                catch (Throwable exception2) {
                    TetraDataLoadDiagnostics.logProblems("Tetra \u6570\u636e\u6570\u7ec4\u5143\u7d20\u53cd\u5e8f\u5217\u5316\u5931\u8d25", directory, id, TetraDataLoadDiagnostics.sourcesOf(element), List.of(new Problem("$[" + index + "]", TetraDataLoadDiagnostics.describe(exception2))), null);
                }
            }
            return;
        }
    }

    private static List<Problem> validateStructure(String directory, JsonElement json) {
        ArrayList<Problem> problems = new ArrayList<Problem>();
        TetraDataLoadDiagnostics.validateByDirectory(directory, json, "$", problems);
        return problems;
    }

    private static void validateByDirectory(String directory, JsonElement json, String path, List<Problem> problems) {
        if (json == null || json.isJsonNull()) {
            return;
        }
        if (json.isJsonArray()) {
            JsonArray array = json.getAsJsonArray();
            for (int index = 0; index < array.size(); ++index) {
                TetraDataLoadDiagnostics.validateByDirectory(directory, array.get(index), path + "[" + index + "]", problems);
            }
            return;
        }
        JsonObject object = TetraDataUtil.jsonObject(json);
        if (object == null) {
            return;
        }
        switch (directory) {
            case "schematics": {
                TetraDataLoadDiagnostics.validateSchematic(object, path, problems);
                break;
            }
            case "modules": {
                TetraDataLoadDiagnostics.validateModule(object, path, problems);
                break;
            }
            case "materials": {
                TetraDataLoadDiagnostics.validateMaterial(object, path, problems);
                break;
            }
            case "improvements": {
                TetraDataLoadDiagnostics.validateImprovement(object, path, problems);
                break;
            }
        }
    }

    private static void validateSchematic(JsonObject object, String path, List<Problem> problems) {
        JsonArray outcomes;
        JsonElement requirement = object.get("requirement");
        if (requirement != null && !requirement.isJsonNull()) {
            TetraDataLoadDiagnostics.validateRequirement(requirement, path + ".requirement", problems);
        }
        if ((outcomes = TetraDataUtil.jsonArray(object, "outcomes")) != null) {
            for (int index = 0; index < outcomes.size(); ++index) {
                TetraDataLoadDiagnostics.validateOutcome(outcomes.get(index), path + ".outcomes[" + index + "]", problems);
            }
        }
    }

    private static void validateRequirement(JsonElement json, String path, List<Problem> problems) {
        JsonObject object = TetraDataUtil.jsonObject(json);
        if (object == null) {
            problems.add(new Problem(path, "requirement \u5fc5\u987b\u662f\u5bf9\u8c61"));
            return;
        }
        JsonElement type = object.get("type");
        if (type == null || type.isJsonNull()) {
            problems.add(new Problem(path, "requirement \u7f3a\u5c11 type \u5b57\u6bb5"));
            return;
        }
        if (!type.isJsonPrimitive() || !type.getAsJsonPrimitive().isString()) {
            problems.add(new Problem(path + ".type", "requirement \u7684 type \u5fc5\u987b\u662f\u5b57\u7b26\u4e32"));
            return;
        }
        String typeValue = type.getAsString();
        if ("tetra:and".equals(typeValue) || "tetra:or".equals(typeValue)) {
            JsonArray children = TetraDataUtil.jsonArray(object, "requirements");
            if (children == null) {
                return;
            }
            for (int index = 0; index < children.size(); ++index) {
                TetraDataLoadDiagnostics.validateRequirement(children.get(index), path + ".requirements[" + index + "]", problems);
            }
            return;
        }
        if ("tetra:not".equals(typeValue) && object.has("requirement")) {
            TetraDataLoadDiagnostics.validateRequirement(object.get("requirement"), path + ".requirement", problems);
        }
    }

    private static void validateOutcome(JsonElement json, String path, List<Problem> problems) {
        JsonObject object = TetraDataUtil.jsonObject(json);
        if (object == null) {
            return;
        }
        JsonElement material = object.get("material");
        if (material != null && !material.isJsonNull()) {
            TetraDataLoadDiagnostics.validateOutcomeMaterial(material, path + ".material", problems);
        }
        TetraDataLoadDiagnostics.validateResourceLocationArray(object.get("materials"), path + ".materials", problems);
    }

    private static void validateModule(JsonObject object, String path, List<Problem> problems) {
        JsonArray variants;
        JsonArray slots = TetraDataUtil.jsonArray(object, "slots");
        JsonArray slotSuffixes = TetraDataUtil.jsonArray(object, "slotSuffixes");
        if (slotSuffixes != null && slotSuffixes.size() > 0 && slots != null && slotSuffixes.size() < slots.size()) {
            problems.add(new Problem(path + ".slotSuffixes", "slotSuffixes \u6570\u91cf\u4e0d\u80fd\u5c11\u4e8e slots \u6570\u91cf"));
        }
        if ((variants = TetraDataUtil.jsonArray(object, "variants")) == null) {
            return;
        }
        for (int index = 0; index < variants.size(); ++index) {
            JsonObject variant = TetraDataUtil.jsonObject(variants.get(index));
            if (variant == null) continue;
            JsonElement key = variant.get("key");
            if (key == null || key.isJsonNull() || !key.isJsonPrimitive() || !key.getAsJsonPrimitive().isString() || key.getAsString().isBlank()) {
                problems.add(new Problem(path + ".variants[" + index + "]", "module variant \u7f3a\u5c11 key \u5b57\u6bb5"));
            }
            TetraDataLoadDiagnostics.validateResourceLocationArray(variant.get("materials"), path + ".variants[" + index + "].materials", problems);
        }
    }

    private static void validateMaterial(JsonObject object, String path, List<Problem> problems) {
        JsonElement material = object.get("material");
        if (material != null && !material.isJsonNull()) {
            TetraDataLoadDiagnostics.validateOutcomeMaterial(material, path + ".material", problems);
        }
    }

    private static void validateImprovement(JsonObject object, String path, List<Problem> problems) {
        JsonElement key = object.get("key");
        if (key == null || key.isJsonNull() || !key.isJsonPrimitive() || !key.getAsJsonPrimitive().isString() || key.getAsString().isBlank()) {
            problems.add(new Problem(path, "improvement \u7f3a\u5c11 key \u5b57\u6bb5"));
        }
        TetraDataLoadDiagnostics.validateResourceLocationArray(object.get("materials"), path + ".materials", problems);
    }

    private static void validateOutcomeMaterial(JsonElement json, String path, List<Problem> problems) {
        JsonElement tag;
        JsonObject object = TetraDataUtil.jsonObject(json);
        if (object == null) {
            problems.add(new Problem(path, "material \u5fc5\u987b\u662f\u5bf9\u8c61"));
            return;
        }
        JsonElement items = object.get("items");
        if (items != null && !items.isJsonNull()) {
            TetraDataLoadDiagnostics.validateResourceLocationArray(items, path + ".items", problems);
        }
        if ((tag = object.get("tag")) != null && !tag.isJsonNull()) {
            TetraDataLoadDiagnostics.validateResourceLocationString(tag, path + ".tag", problems);
        }
    }

    private static void validateResourceLocationArray(JsonElement json, String path, List<Problem> problems) {
        if (json == null || json.isJsonNull()) {
            return;
        }
        if (!json.isJsonArray()) {
            problems.add(new Problem(path, "\u8fd9\u91cc\u5fc5\u987b\u662f\u5b57\u7b26\u4e32\u6570\u7ec4"));
            return;
        }
        JsonArray array = json.getAsJsonArray();
        for (int index = 0; index < array.size(); ++index) {
            TetraDataLoadDiagnostics.validateResourceLocationString(array.get(index), path + "[" + index + "]", problems);
        }
    }

    private static void validateResourceLocationString(JsonElement json, String path, List<Problem> problems) {
        if (json == null || json.isJsonNull()) {
            problems.add(new Problem(path, "\u8fd9\u91cc\u5fc5\u987b\u662f\u8d44\u6e90 ID \u5b57\u7b26\u4e32"));
            return;
        }
        if (!json.isJsonPrimitive() || !json.getAsJsonPrimitive().isString()) {
            problems.add(new Problem(path, "\u8fd9\u91cc\u5fc5\u987b\u662f\u8d44\u6e90 ID \u5b57\u7b26\u4e32\uff0c\u4e0d\u80fd\u662f\u5bf9\u8c61\u6216\u6570\u7ec4"));
            return;
        }
        String value = json.getAsString();
        if (ResourceLocation.tryParse((String)(value.startsWith("#") ? value.substring(1) : value)) == null) {
            problems.add(new Problem(path, "\u8d44\u6e90 ID \u4e0d\u5408\u6cd5: " + value));
        }
    }

    private static void logProblems(String title, String directory, ResourceLocation id, String sources, List<Problem> problems, Throwable exception) {
        String dataPath = TetraDataLoadDiagnostics.dataPath(directory, id);
        for (Problem problem : problems) {
            if (exception == null) {
                TetraDataKubejs.LOGGER.error("[tetra_data_kubejs] {}: {}, \u6765\u6e90: {}, \u4f4d\u7f6e: {}, \u539f\u56e0: {}", new Object[]{title, dataPath, sources, problem.path(), problem.reason()});
                continue;
            }
            TetraDataKubejs.LOGGER.error("[tetra_data_kubejs] {}: {}, \u6765\u6e90: {}, \u4f4d\u7f6e: {}, \u539f\u56e0: {}", new Object[]{title, dataPath, sources, problem.path(), problem.reason(), exception});
        }
    }

    private static String dataPath(String directory, ResourceLocation id) {
        if (id == null) {
            return "data/tetra/" + directory + "/<unknown>.json";
        }
        String path = id.getPath();
        if (path.startsWith(directory + "/") && path.endsWith(".json")) {
            return "data/" + id.getNamespace() + "/" + path;
        }
        return "data/" + id.getNamespace() + "/" + directory + "/" + path + ".json";
    }

    private static String sourcesOf(JsonElement json) {
        LinkedHashSet<String> sources = new LinkedHashSet<String>();
        TetraDataLoadDiagnostics.collectSources(json, sources);
        if (sources.isEmpty()) {
            return "\u672a\u77e5\u6570\u636e\u5305";
        }
        return String.join((CharSequence)", ", sources);
    }

    private static void collectSources(JsonElement json, LinkedHashSet<String> sources) {
        if (json == null || json.isJsonNull()) {
            return;
        }
        if (json.isJsonArray()) {
            json.getAsJsonArray().forEach(element -> TetraDataLoadDiagnostics.collectSources(element, sources));
            return;
        }
        JsonObject object = TetraDataUtil.jsonObject(json);
        if (object == null) {
            return;
        }
        JsonArray sourceArray = TetraDataUtil.jsonArray(object, "sources");
        if (sourceArray == null) {
            return;
        }
        sourceArray.forEach(source -> {
            if (source != null && source.isJsonPrimitive() && source.getAsJsonPrimitive().isString()) {
                sources.add(source.getAsString());
            }
        });
    }

    private static String describe(Throwable throwable) {
        Throwable root = throwable;
        while (root.getCause() != null) {
            root = root.getCause();
        }
        String message = root.getMessage();
        if (message == null || message.isBlank()) {
            return root.getClass().getName();
        }
        return root.getClass().getName() + ": " + message;
    }

    private record Problem(String path, String reason) {
    }
}

