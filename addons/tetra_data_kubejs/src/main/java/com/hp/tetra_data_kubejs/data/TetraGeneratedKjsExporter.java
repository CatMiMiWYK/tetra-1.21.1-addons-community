/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  dev.latvian.mods.kubejs.KubeJSPaths
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.packs.resources.Resource
 *  net.minecraft.server.packs.resources.ResourceManager
 *  net.neoforged.api.distmarker.Dist
 *  net.minecraftforge.fml.DistExecutor
 */
package com.hp.tetra_data_kubejs.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hp.tetra_data_kubejs.TetraDataKubejs;
import com.hp.tetra_data_kubejs.data.TetraDataUtil;
import dev.latvian.mods.kubejs.KubeJSPaths;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;

public final class TetraGeneratedKjsExporter {
    private static final Gson PRETTY_GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final List<String> EXPORT_DIRECTORIES = List.of("schematics", "modules", "materials", "improvements");
    private static final Path ROOT = KubeJSPaths.DIRECTORY.resolve("server").resolve("exported").resolve("tetra_data_kubejs_kjs_dump");
    private static final Path GROUPED_ROOT = KubeJSPaths.DIRECTORY.resolve("server").resolve("exported").resolve("tetra_data_kubejs_grouped_kjs_dump");

    private TetraGeneratedKjsExporter() {
    }

    public static ExportSummary export(ResourceManager resourceManager) throws IOException {
        TetraGeneratedKjsExporter.clearDirectory(ROOT);
        Files.createDirectories(ROOT, new FileAttribute[0]);
        ExportContext context = new ExportContext(resourceManager, TetraGeneratedKjsExporter.loadTranslations(resourceManager));
        ExportSummary summary = new ExportSummary(ROOT);
        for (String directory : EXPORT_DIRECTORIES) {
            TetraGeneratedKjsExporter.exportDirectory(context, directory, summary);
        }
        return summary;
    }

    public static ExportSummary exportGroupedBySchematicOutcome(ResourceManager resourceManager) throws IOException {
        TetraGeneratedKjsExporter.clearDirectory(GROUPED_ROOT);
        Files.createDirectories(GROUPED_ROOT, new FileAttribute[0]);
        ExportContext context = new ExportContext(resourceManager, TetraGeneratedKjsExporter.loadTranslations(resourceManager));
        Map<String, JsonElement> modules = TetraGeneratedKjsExporter.loadDirectoryEntries(context.resourceManager, "modules");
        Map<String, JsonElement> improvements = TetraGeneratedKjsExporter.loadImprovementEntries(context.resourceManager);
        Map<String, JsonElement> schematics = TetraGeneratedKjsExporter.loadDirectoryEntries(context.resourceManager, "schematics");
        ExportSummary summary = new ExportSummary(GROUPED_ROOT);
        ArrayList<String> schematicKeys = new ArrayList<String>(schematics.keySet());
        schematicKeys.sort(String::compareTo);
        for (String schematicKey : schematicKeys) {
            GroupedOutcome groupedOutcome;
            JsonElement schematicJson = schematics.get(schematicKey);
            if (schematicJson == null || !schematicJson.isJsonObject() || !(groupedOutcome = TetraGeneratedKjsExporter.classifySchematicOutcome(schematicJson.getAsJsonObject())).hasExportableTarget()) continue;
            String folder = groupedOutcome.hasModule() ? "module" : "improvement";
            Path output = GROUPED_ROOT.resolve(folder).resolve(TetraDataUtil.normalizeKey(schematicKey) + ".js");
            Files.createDirectories(output.getParent(), new FileAttribute[0]);
            ArrayList<String> lines = new ArrayList<String>();
            lines.add("hp_TetraDataEvents.queue(event => {");
            if (groupedOutcome.hasModule()) {
                for (String moduleKey : groupedOutcome.moduleKeys()) {
                    JsonElement moduleJson = modules.get(moduleKey);
                    if (moduleJson == null || moduleJson.isJsonNull()) continue;
                    TetraGeneratedKjsExporter.appendExportBody(lines, TetraGeneratedKjsExporter.toModuleKjs(context, moduleKey, moduleJson));
                }
            }
            for (String improvementKey : groupedOutcome.improvementKeys()) {
                JsonElement improvementJson = improvements.get(improvementKey);
                if (improvementJson == null || improvementJson.isJsonNull()) continue;
                TetraGeneratedKjsExporter.appendExportBody(lines, TetraGeneratedKjsExporter.toImprovementKjs(context, improvementKey, improvementJson));
            }
            TetraGeneratedKjsExporter.appendExportBody(lines, TetraGeneratedKjsExporter.toSchematicKjs(context, schematicKey, schematicJson));
            lines.add("})");
            Files.writeString(output, (CharSequence)(String.join((CharSequence)"\n", lines) + "\n"), StandardCharsets.UTF_8, new OpenOption[0]);
            summary.record(folder);
        }
        return summary;
    }

    private static void exportDirectory(ExportContext context, String directory, ExportSummary summary) throws IOException {
        String normalizedDirectory = TetraDataUtil.normalizeDirectory(directory);
        Map<ResourceLocation, Resource> resources = context.resourceManager.listResources(normalizedDirectory, location -> location.getPath().endsWith(".json"));
        ArrayList<ResourceLocation> ids = new ArrayList<ResourceLocation>();
        for (ResourceLocation resourceId : resources.keySet()) {
            if (!"tetra".equals(resourceId.getNamespace()) || !resourceId.getPath().startsWith(normalizedDirectory + "/")) continue;
            ids.add(resourceId);
        }
        ids.sort(Comparator.comparing(ResourceLocation::toString));
        for (ResourceLocation resourceId : ids) {
            JsonElement json = TetraDataUtil.readJson(context.resourceManager, resourceId);
            if (json == null || json.isJsonNull()) continue;
            String key = TetraDataUtil.extractKeyFromPath(normalizedDirectory, resourceId.getPath());
            Path output = ROOT.resolve(resourceId.getNamespace()).resolve(normalizedDirectory).resolve(TetraDataUtil.normalizeKey(key) + ".js");
            Files.createDirectories(output.getParent(), new FileAttribute[0]);
            Files.writeString(output, (CharSequence)TetraGeneratedKjsExporter.toKjs(context, directory, key, json), StandardCharsets.UTF_8, new OpenOption[0]);
            summary.record(directory);
        }
    }

    private static String toKjs(ExportContext context, String directory, String key, JsonElement json) {
        return switch (TetraDataUtil.normalizeDirectory(directory)) {
            case "materials" -> TetraGeneratedKjsExporter.toMaterialKjs(context, key, json);
            case "modules" -> TetraGeneratedKjsExporter.toModuleKjs(context, key, json);
            case "improvements" -> TetraGeneratedKjsExporter.toImprovementKjs(context, key, json);
            case "schematics" -> TetraGeneratedKjsExporter.toSchematicKjs(context, key, json);
            default -> TetraGeneratedKjsExporter.toRawDataKjs(directory, key, json);
        };
    }

    private static String toMaterialKjs(ExportContext context, String key, JsonElement json) {
        JsonObject data = json != null && json.isJsonObject() ? json.getAsJsonObject().deepCopy() : new JsonObject();
        ArrayList<String> lines = new ArrayList<String>();
        lines.add("hp_TetraDataEvents.queue(event => {");
        lines.add("    " + TetraGeneratedKjsExporter.materialBuilderStart(key));
        TetraGeneratedKjsExporter.emitBooleanCall(lines, data, "replace", "replace", 8, false);
        TetraGeneratedKjsExporter.emitTranslationCalls(lines, "name", context.translationsFor(TetraDataUtil.materialNameKey(key)), 8);
        TetraGeneratedKjsExporter.emitTranslationCalls(lines, "prefix", context.translationsFor(TetraDataUtil.materialPrefixKey(key)), 8);
        String category = TetraDataUtil.isMaterialKey(key) ? TetraDataUtil.materialCategory(key) : null;
        String dataKey = TetraGeneratedKjsExporter.removeString(data, "key");
        String inferredDataKey = TetraDataUtil.materialKeySuffix(key);
        if (dataKey != null && !dataKey.equals(inferredDataKey)) {
            data.addProperty("key", dataKey);
        }
        if (category != null && category.equals(TetraGeneratedKjsExporter.readString(data, "category"))) {
            data.remove("category");
        }
        TetraGeneratedKjsExporter.emitStringCall(lines, data, "category", "category", 8);
        TetraGeneratedKjsExporter.emitMaterialHiddenCall(lines, data, 8);
        TetraGeneratedKjsExporter.emitNumberCall(lines, data, "durability", "durability", 8);
        TetraGeneratedKjsExporter.emitMaterialIntegrityCall(lines, data, 8);
        TetraGeneratedKjsExporter.emitMaterialStatsCall(lines, data, 8);
        TetraGeneratedKjsExporter.emitIntegerCall(lines, data, "magicCapacity", "magicCapacity", 8);
        TetraGeneratedKjsExporter.emitMaterialToolCall(lines, data, 8);
        TetraGeneratedKjsExporter.emitJsonCall(lines, data, "effects", "effects", 8);
        TetraGeneratedKjsExporter.emitJsonCall(lines, data, "attributes", "attributes", 8);
        TetraGeneratedKjsExporter.emitJsonCall(lines, data, "aspects", "aspects", 8);
        TetraGeneratedKjsExporter.emitJsonCall(lines, data, "improvements", "improvements", 8);
        TetraGeneratedKjsExporter.emitMaterialIngredientCall(lines, data, 8);
        TetraGeneratedKjsExporter.emitJsonCall(lines, data, "requiredTools", "requires", 8);
        TetraGeneratedKjsExporter.emitJsonCall(lines, data, "tags", "tags", 8);
        TetraGeneratedKjsExporter.emitMaterialTintsCall(lines, data, 8);
        TetraGeneratedKjsExporter.emitJsonCall(lines, data, "textures", "textures", 8);
        TetraGeneratedKjsExporter.emitJsonCall(lines, data, "textureOverrides", "textureOverrides", 8);
        TetraGeneratedKjsExporter.emitBooleanCall(lines, data, "tintOverrides", "tintOverrides", 8, false);
        TetraGeneratedKjsExporter.emitNumberCall(lines, data, "experienceCost", "experienceCost", 8);
        TetraGeneratedKjsExporter.emitJsonCall(lines, data, "features", "features", 8);
        TetraGeneratedKjsExporter.emitMergeCall(lines, data, 8, null);
        lines.add("})");
        return String.join((CharSequence)"\n", lines) + "\n";
    }

    private static String toModuleKjs(ExportContext context, String key, JsonElement json) {
        JsonObject data = json != null && json.isJsonObject() ? json.getAsJsonObject().deepCopy() : new JsonObject();
        ArrayList<String> lines = new ArrayList<String>();
        lines.add("hp_TetraDataEvents.queue(event => {");
        lines.add("    event.module(" + TetraGeneratedKjsExporter.toJsString(key) + ")");
        TetraGeneratedKjsExporter.emitBooleanCall(lines, data, "replace", "replace", 8, false);
        TetraGeneratedKjsExporter.emitTranslationCalls(lines, "name", context.translationsFor(TetraDataUtil.moduleNameKey(key)), 8);
        TetraGeneratedKjsExporter.emitTranslationCalls(lines, "materialName", context.translationsFor(TetraDataUtil.moduleMaterialNameKey(key)), 8);
        TetraGeneratedKjsExporter.emitTranslationCalls(lines, "itemName", context.translationsFor(TetraDataUtil.moduleItemNameKey(key)), 8);
        TetraGeneratedKjsExporter.emitTranslationCalls(lines, "description", context.translationsFor(TetraDataUtil.moduleDescriptionKey(key)), 8);
        TetraGeneratedKjsExporter.emitTranslationCalls(lines, "prefix", context.translationsFor(TetraDataUtil.modulePrefixKey(key)), 8);
        TetraGeneratedKjsExporter.emitJsonCall(lines, data, "slots", "slots", 8);
        TetraGeneratedKjsExporter.emitJsonCall(lines, data, "slotSuffixes", "slotSuffixes", 8);
        TetraGeneratedKjsExporter.emitStringCall(lines, data, "type", "type", 8);
        TetraGeneratedKjsExporter.emitJsonCall(lines, data, "improvements", "improvements", 8);
        TetraGeneratedKjsExporter.emitStringCall(lines, data, "renderLayer", "renderLayer", 8);
        TetraGeneratedKjsExporter.emitStringCall(lines, data, "namePriority", "namePriority", 8);
        TetraGeneratedKjsExporter.emitStringCall(lines, data, "prefixPriority", "prefixPriority", 8);
        TetraGeneratedKjsExporter.emitStringCall(lines, data, "tweakKey", "tweakKey", 8);
        TetraGeneratedKjsExporter.emitModuleVariantCalls(lines, data, context, 8);
        TetraGeneratedKjsExporter.emitMergeCall(lines, data, 8, null);
        lines.add("})");
        return String.join((CharSequence)"\n", lines) + "\n";
    }

    private static void emitMaterialHiddenCall(List<String> lines, JsonObject data, int indent) {
        JsonElement hidden = data.remove("hidden");
        JsonElement hiddenOutcomes = data.remove("hiddenOutcomes");
        if (hidden == null || !hidden.isJsonPrimitive() || !hidden.getAsJsonPrimitive().isBoolean()) {
            if (hidden != null) {
                data.add("hidden", hidden);
            }
            if (hiddenOutcomes != null) {
                data.add("hiddenOutcomes", hiddenOutcomes);
            }
            return;
        }
        boolean hiddenValue = hidden.getAsBoolean();
        if (hiddenOutcomes != null && hiddenOutcomes.isJsonPrimitive() && hiddenOutcomes.getAsJsonPrimitive().isBoolean()) {
            boolean hiddenOutcomesValue = hiddenOutcomes.getAsBoolean();
            if (hiddenValue == hiddenOutcomesValue) {
                lines.add(TetraGeneratedKjsExporter.spaces(indent) + ".hidden(" + hiddenValue + ")");
            } else {
                lines.add(TetraGeneratedKjsExporter.spaces(indent) + ".hidden(" + hiddenValue + ", " + hiddenOutcomesValue + ")");
            }
            return;
        }
        if (hiddenOutcomes != null) {
            data.add("hiddenOutcomes", hiddenOutcomes);
        }
        lines.add(TetraGeneratedKjsExporter.spaces(indent) + ".hidden(" + hiddenValue + ")");
    }

    private static void emitMaterialIntegrityCall(List<String> lines, JsonObject data, int indent) {
        JsonElement gain = data.remove("integrityGain");
        JsonElement cost = data.remove("integrityCost");
        if (gain != null && cost != null && gain.isJsonPrimitive() && gain.getAsJsonPrimitive().isNumber() && cost.isJsonPrimitive() && cost.getAsJsonPrimitive().isNumber()) {
            lines.add(TetraGeneratedKjsExporter.spaces(indent) + ".integrity(" + TetraGeneratedKjsExporter.numberLiteral(gain.getAsNumber()) + ", " + TetraGeneratedKjsExporter.numberLiteral(cost.getAsNumber()) + ")");
            return;
        }
        if (gain != null) {
            data.add("integrityGain", gain);
        }
        if (cost != null) {
            data.add("integrityCost", cost);
        }
    }

    private static void emitMaterialStatsCall(List<String> lines, JsonObject data, int indent) {
        JsonElement primary = data.remove("primary");
        JsonElement secondary = data.remove("secondary");
        JsonElement tertiary = data.remove("tertiary");
        if (primary != null && secondary != null && tertiary != null && primary.isJsonPrimitive() && primary.getAsJsonPrimitive().isNumber() && secondary.isJsonPrimitive() && secondary.getAsJsonPrimitive().isNumber() && tertiary.isJsonPrimitive() && tertiary.getAsJsonPrimitive().isNumber()) {
            lines.add(TetraGeneratedKjsExporter.spaces(indent) + ".stats(" + TetraGeneratedKjsExporter.numberLiteral(primary.getAsNumber()) + ", " + TetraGeneratedKjsExporter.numberLiteral(secondary.getAsNumber()) + ", " + TetraGeneratedKjsExporter.numberLiteral(tertiary.getAsNumber()) + ")");
            return;
        }
        if (primary != null) {
            data.add("primary", primary);
        }
        if (secondary != null) {
            data.add("secondary", secondary);
        }
        if (tertiary != null) {
            data.add("tertiary", tertiary);
        }
    }

    private static void emitMaterialToolCall(List<String> lines, JsonObject data, int indent) {
        JsonElement level = data.remove("toolLevel");
        JsonElement efficiency = data.remove("toolEfficiency");
        if (level != null && efficiency != null && efficiency.isJsonPrimitive() && efficiency.getAsJsonPrimitive().isNumber()) {
            lines.add(TetraGeneratedKjsExporter.spaces(indent) + ".tool(" + TetraGeneratedKjsExporter.inlineJson(level) + ", " + TetraGeneratedKjsExporter.numberLiteral(efficiency.getAsNumber()) + ")");
            return;
        }
        if (level != null) {
            data.add("toolLevel", level);
        }
        if (efficiency != null) {
            data.add("toolEfficiency", efficiency);
        }
    }

    private static void emitMaterialIngredientCall(List<String> lines, JsonObject data, int indent) {
        JsonElement material = data.remove("material");
        if (material == null || material.isJsonNull()) {
            return;
        }
        TetraGeneratedKjsExporter.emitIngredientCall(lines, material, indent, "");
    }

    private static void emitMaterialTintsCall(List<String> lines, JsonObject data, int indent) {
        JsonObject tints = TetraGeneratedKjsExporter.removeObject(data, "tints");
        if (tints == null) {
            return;
        }
        String glyph = TetraGeneratedKjsExporter.readString(tints, "glyph");
        String texture = TetraGeneratedKjsExporter.readString(tints, "texture");
        if (glyph != null && texture != null) {
            if (glyph.equals(texture)) {
                lines.add(TetraGeneratedKjsExporter.spaces(indent) + ".tint(" + TetraGeneratedKjsExporter.toJsString(glyph) + ")");
            } else {
                lines.add(TetraGeneratedKjsExporter.spaces(indent) + ".tint(" + TetraGeneratedKjsExporter.toJsString(glyph) + ", " + TetraGeneratedKjsExporter.toJsString(texture) + ")");
            }
        } else if (glyph != null) {
            lines.add(TetraGeneratedKjsExporter.spaces(indent) + ".glyphTint(" + TetraGeneratedKjsExporter.toJsString(glyph) + ")");
        } else if (texture != null) {
            lines.add(TetraGeneratedKjsExporter.spaces(indent) + ".textureTint(" + TetraGeneratedKjsExporter.toJsString(texture) + ")");
        }
        tints.remove("glyph");
        tints.remove("texture");
        if (!tints.entrySet().isEmpty()) {
            data.add("tints", (JsonElement)tints);
        }
    }

    private static void emitModuleVariantCalls(List<String> lines, JsonObject data, ExportContext context, int indent) {
        JsonArray variants = TetraGeneratedKjsExporter.removeArray(data, "variants");
        if (variants == null) {
            return;
        }
        for (JsonElement element : variants) {
            if (!element.isJsonObject()) {
                lines.add(TetraGeneratedKjsExporter.spaces(indent) + ".variantData(" + TetraGeneratedKjsExporter.inlineJson(element) + ")");
                continue;
            }
            JsonObject variant = element.getAsJsonObject().deepCopy();
            String variantKey = TetraGeneratedKjsExporter.readString(variant, "key");
            lines.add(TetraGeneratedKjsExporter.spaces(indent) + ".addVariant(variant => {");
            lines.add(TetraGeneratedKjsExporter.spaces(indent + 4) + "variant");
            if (variantKey != null) {
                lines.add(TetraGeneratedKjsExporter.spaces(indent + 8) + ".key(" + TetraGeneratedKjsExporter.toJsString(variantKey) + ")");
                TetraGeneratedKjsExporter.emitTranslationCalls(lines, "name", context.translationsFor(TetraDataUtil.variantNameKey(variantKey)), indent + 8);
                TetraGeneratedKjsExporter.emitTranslationCalls(lines, "itemName", context.translationsFor(TetraDataUtil.variantItemNameKey(variantKey)), indent + 8);
                TetraGeneratedKjsExporter.emitTranslationCalls(lines, "description", context.translationsFor(TetraDataUtil.variantDescriptionKey(variantKey)), indent + 8);
                TetraGeneratedKjsExporter.emitTranslationCalls(lines, "prefix", context.translationsFor(TetraDataUtil.variantPrefixKey(variantKey)), indent + 8);
                variant.remove("key");
            }
            TetraGeneratedKjsExporter.emitJsonCall(lines, variant, "materials", "materials", indent + 8);
            TetraGeneratedKjsExporter.emitStringCall(lines, variant, "category", "category", indent + 8);
            TetraGeneratedKjsExporter.emitNumberCall(lines, variant, "durability", "durability", indent + 8);
            TetraGeneratedKjsExporter.emitNumberCall(lines, variant, "durabilityMultiplier", "durabilityMultiplier", indent + 8);
            TetraGeneratedKjsExporter.emitNumberCall(lines, variant, "integrity", "integrity", indent + 8);
            TetraGeneratedKjsExporter.emitNumberCall(lines, variant, "magicCapacity", "magicCapacity", indent + 8);
            TetraGeneratedKjsExporter.emitJsonCall(lines, variant, "attributes", "attributes", indent + 8);
            TetraGeneratedKjsExporter.emitJsonCall(lines, variant, "tools", "tools", indent + 8);
            TetraGeneratedKjsExporter.emitJsonCall(lines, variant, "effects", "effects", indent + 8);
            TetraGeneratedKjsExporter.emitJsonCall(lines, variant, "aspects", "aspects", indent + 8);
            TetraGeneratedKjsExporter.emitJsonCall(lines, variant, "tags", "tags", indent + 8);
            TetraGeneratedKjsExporter.emitStringCall(lines, variant, "rarity", "rarity", indent + 8);
            TetraGeneratedKjsExporter.emitJsonCall(lines, variant, "glyph", "glyph", indent + 8);
            TetraGeneratedKjsExporter.emitStringCall(lines, variant, "prefixPriority", "prefixPriority", indent + 8);
            TetraGeneratedKjsExporter.emitJsonCall(lines, variant, "models", "models", indent + 8);
            TetraGeneratedKjsExporter.emitJsonCall(lines, variant, "extract", "extractData", indent + 8);
            TetraGeneratedKjsExporter.emitMergeCall(lines, variant, indent + 8, null);
            lines.add(TetraGeneratedKjsExporter.spaces(indent) + "})");
        }
    }

    private static String toRawDataKjs(String directory, String key, JsonElement json) {
        StringBuilder builder = new StringBuilder();
        builder.append("hp_TetraDataEvents.queue(event => {\n");
        builder.append("    event.data(").append(TetraGeneratedKjsExporter.toJsString(directory)).append(", ").append(TetraGeneratedKjsExporter.toJsString(key)).append(",\n");
        builder.append(TetraGeneratedKjsExporter.indentJson(PRETTY_GSON.toJson(json), "        "));
        builder.append("\n    )\n");
        builder.append("})\n");
        return builder.toString();
    }

    private static String toImprovementKjs(ExportContext context, String key, JsonElement json) {
        int levelStartIndex;
        JsonArray array = json != null && json.isJsonArray() ? json.getAsJsonArray() : new JsonArray();
        JsonObject base = array.size() > 0 && array.get(0).isJsonObject() ? array.get(0).getAsJsonObject().deepCopy() : new JsonObject();
        String improvementKey = TetraGeneratedKjsExporter.readString(base, "key");
        if (improvementKey == null || improvementKey.isBlank()) {
            improvementKey = key;
        }
        boolean useAtLevelOnly = array.size() > 1;
        ArrayList<String> lines = new ArrayList<String>();
        lines.add("hp_TetraDataEvents.queue(event => {");
        lines.add("    event.improvement()");
        lines.add("        .path(" + TetraGeneratedKjsExporter.toJsString(key) + ")");
        lines.add("        .improvementKey(" + TetraGeneratedKjsExporter.toJsString(improvementKey) + ")");
        TetraGeneratedKjsExporter.emitTranslationCalls(lines, "name", context.translationsFor(TetraDataUtil.improvementNameKey(improvementKey)), 8);
        TetraGeneratedKjsExporter.emitTranslationCalls(lines, "description", context.translationsFor(TetraDataUtil.improvementDescriptionKey(improvementKey)), 8);
        TetraGeneratedKjsExporter.emitBooleanCall(lines, base, "replace", "replace", 8, false);
        TetraGeneratedKjsExporter.emitStringCall(lines, base, "group", "group", 8);
        TetraGeneratedKjsExporter.emitNumberCall(lines, base, "durability", "durability", 8);
        TetraGeneratedKjsExporter.emitNumberCall(lines, base, "durabilityMultiplier", "durabilityMultiplier", 8);
        TetraGeneratedKjsExporter.emitNumberCall(lines, base, "integrity", "integrity", 8);
        TetraGeneratedKjsExporter.emitNumberCall(lines, base, "magicCapacity", "magicCapacity", 8);
        TetraGeneratedKjsExporter.emitJsonCall(lines, base, "attributes", "attributes", 8);
        TetraGeneratedKjsExporter.emitJsonCall(lines, base, "tools", "tools", 8);
        TetraGeneratedKjsExporter.emitJsonCall(lines, base, "effects", "effects", 8);
        TetraGeneratedKjsExporter.emitJsonCall(lines, base, "aspects", "aspects", 8);
        TetraGeneratedKjsExporter.emitJsonCall(lines, base, "tags", "tags", 8);
        TetraGeneratedKjsExporter.emitStringCall(lines, base, "rarity", "rarity", 8);
        TetraGeneratedKjsExporter.emitStringCall(lines, base, "prefixPriority", "prefixPriority", 8);
        TetraGeneratedKjsExporter.emitJsonCall(lines, base, "materials", "materials", 8);
        TetraGeneratedKjsExporter.emitJsonCall(lines, base, "models", "models", 8);
        TetraGeneratedKjsExporter.emitJsonCall(lines, base, "extract", "extractData", 8);
        base.remove("key");
        if (!useAtLevelOnly) {
            TetraGeneratedKjsExporter.emitNumberCall(lines, base, "level", "level", 8);
        } else {
            base.remove("level");
        }
        int n = levelStartIndex = useAtLevelOnly ? 0 : 1;
        if (array.size() > levelStartIndex) {
            for (int index = levelStartIndex; index < array.size(); ++index) {
                JsonObject level;
                JsonObject jsonObject = level = array.get(index).isJsonObject() ? array.get(index).getAsJsonObject().deepCopy() : null;
                if (level == null) continue;
                int levelValue = level.has("level") ? level.get("level").getAsInt() : (useAtLevelOnly ? index + 1 : index);
                level.remove("level");
                String levelKey = TetraGeneratedKjsExporter.removeString(level, "key");
                lines.add("        .atLevel(" + levelValue + ", level => {");
                lines.add("            level");
                if (levelKey != null && !levelKey.isBlank()) {
                    lines.add("                .key(" + TetraGeneratedKjsExporter.toJsString(levelKey) + ")");
                }
                TetraGeneratedKjsExporter.emitStringCall(lines, level, "group", "group", 16);
                TetraGeneratedKjsExporter.emitNumberCall(lines, level, "durability", "durability", 16);
                TetraGeneratedKjsExporter.emitNumberCall(lines, level, "durabilityMultiplier", "durabilityMultiplier", 16);
                TetraGeneratedKjsExporter.emitNumberCall(lines, level, "integrity", "integrity", 16);
                TetraGeneratedKjsExporter.emitNumberCall(lines, level, "magicCapacity", "magicCapacity", 16);
                TetraGeneratedKjsExporter.emitJsonCall(lines, level, "attributes", "attributes", 16);
                TetraGeneratedKjsExporter.emitJsonCall(lines, level, "tools", "tools", 16);
                TetraGeneratedKjsExporter.emitJsonCall(lines, level, "effects", "effects", 16);
                TetraGeneratedKjsExporter.emitJsonCall(lines, level, "aspects", "aspects", 16);
                TetraGeneratedKjsExporter.emitJsonCall(lines, level, "tags", "tags", 16);
                TetraGeneratedKjsExporter.emitStringCall(lines, level, "rarity", "rarity", 16);
                TetraGeneratedKjsExporter.emitStringCall(lines, level, "prefixPriority", "prefixPriority", 16);
                TetraGeneratedKjsExporter.emitJsonCall(lines, level, "materials", "materials", 16);
                TetraGeneratedKjsExporter.emitJsonCall(lines, level, "models", "models", 16);
                TetraGeneratedKjsExporter.emitJsonCall(lines, level, "extract", "extractData", 16);
                TetraGeneratedKjsExporter.emitMergeCall(lines, level, 16, null);
                lines.add("        })");
            }
        }
        TetraGeneratedKjsExporter.emitMergeCall(lines, base, 8, null);
        lines.add("})");
        return String.join((CharSequence)"\n", lines) + "\n";
    }

    private static String toSchematicKjs(ExportContext context, String key, JsonElement json) {
        JsonObject data = json != null && json.isJsonObject() ? json.getAsJsonObject().deepCopy() : new JsonObject();
        ArrayList<String> lines = new ArrayList<String>();
        lines.add("hp_TetraDataEvents.queue(event => {");
        lines.add("    event.schematic(" + TetraGeneratedKjsExporter.toJsString(key) + ")");
        TetraGeneratedKjsExporter.emitBooleanCall(lines, data, "replace", "replace", 8, false);
        TetraGeneratedKjsExporter.emitTranslationCalls(lines, "name", context.translationsFor(TetraDataUtil.schematicNameKey(key)), 8);
        TetraGeneratedKjsExporter.emitTranslationCalls(lines, "description", context.translationsFor(TetraDataUtil.schematicDescriptionKey(key)), 8);
        TetraGeneratedKjsExporter.emitSchematicSlotTranslations(lines, key, data, context, 8);
        TetraGeneratedKjsExporter.emitStringCall(lines, data, "localizationKey", "localizationKey", 8);
        TetraGeneratedKjsExporter.emitJsonCall(lines, data, "slots", "slots", 8);
        TetraGeneratedKjsExporter.emitJsonCall(lines, data, "keySuffixes", "keySuffixes", 8);
        TetraGeneratedKjsExporter.emitIntegerCall(lines, data, "materialSlotCount", "materialSlotCount", 8);
        TetraGeneratedKjsExporter.emitBooleanCall(lines, data, "repair", "repair", 8, false);
        TetraGeneratedKjsExporter.emitBooleanCall(lines, data, "hone", "hone", 8, false);
        TetraGeneratedKjsExporter.emitRequirementCall(lines, data, "requirement", 8);
        TetraGeneratedKjsExporter.emitIntegerCall(lines, data, "materialRevealSlot", "materialRevealSlot", 8);
        TetraGeneratedKjsExporter.emitStringCall(lines, data, "displayType", "displayType", 8);
        TetraGeneratedKjsExporter.emitStringCall(lines, data, "rarity", "rarity", 8);
        TetraGeneratedKjsExporter.emitJsonCall(lines, data, "glyph", "glyph", 8);
        TetraGeneratedKjsExporter.emitJsonCall(lines, data, "translation", "translation", 8);
        TetraGeneratedKjsExporter.emitStringCall(lines, data, "preview", "preview", 8);
        TetraGeneratedKjsExporter.emitJsonCall(lines, data, "applicableMaterials", "applicableMaterials", 8);
        TetraGeneratedKjsExporter.emitOutcomeCalls(lines, data, 8);
        TetraGeneratedKjsExporter.emitMergeCall(lines, data, 8, null);
        lines.add("})");
        return String.join((CharSequence)"\n", lines) + "\n";
    }

    private static void emitOutcomeCalls(List<String> lines, JsonObject data, int indent) {
        JsonArray outcomes = TetraGeneratedKjsExporter.removeArray(data, "outcomes");
        if (outcomes == null) {
            return;
        }
        for (JsonElement element : outcomes) {
            if (!element.isJsonObject()) {
                lines.add(TetraGeneratedKjsExporter.spaces(indent) + ".outcomeData(" + TetraGeneratedKjsExporter.inlineJson(element) + ")");
                continue;
            }
            JsonObject outcome = element.getAsJsonObject().deepCopy();
            lines.add(TetraGeneratedKjsExporter.spaces(indent) + ".addOutcome(outcome => {");
            TetraGeneratedKjsExporter.emitOutcomeBody(lines, outcome, indent + 4);
            lines.add(TetraGeneratedKjsExporter.spaces(indent) + "})");
        }
    }

    private static void emitOutcomeBody(List<String> lines, JsonObject outcome, int indent) {
        JsonElement material;
        JsonArray materials = TetraGeneratedKjsExporter.removeArray(outcome, "materials");
        if (materials != null) {
            lines.add(TetraGeneratedKjsExporter.spaces(indent) + "outcome.materials(" + TetraGeneratedKjsExporter.inlineJson((JsonElement)materials) + ")");
        }
        if ((material = outcome.remove("material")) != null && !material.isJsonNull()) {
            TetraGeneratedKjsExporter.emitOutcomeMaterial(lines, material, indent);
        }
        TetraGeneratedKjsExporter.emitIntegerCall(lines, outcome, "materialSlot", "materialSlot", indent, "outcome");
        TetraGeneratedKjsExporter.emitNumberCall(lines, outcome, "experienceCost", "experienceCost", indent, "outcome");
        TetraGeneratedKjsExporter.emitNumberCall(lines, outcome, "countFactor", "countFactor", indent, "outcome");
        TetraGeneratedKjsExporter.emitNumberCall(lines, outcome, "countOffset", "countOffset", indent, "outcome");
        TetraGeneratedKjsExporter.emitNumberCall(lines, outcome, "toolFactor", "toolFactor", indent, "outcome");
        TetraGeneratedKjsExporter.emitNumberCall(lines, outcome, "toolOffset", "toolOffset", indent, "outcome");
        TetraGeneratedKjsExporter.emitJsonCall(lines, outcome, "requiredTools", "requiredTools", indent, "outcome");
        TetraGeneratedKjsExporter.emitLegacyRequiredToolCalls(lines, outcome, indent);
        String moduleKey = TetraGeneratedKjsExporter.removeString(outcome, "moduleKey");
        String moduleVariant = TetraGeneratedKjsExporter.removeString(outcome, "moduleVariant");
        if (moduleKey != null && moduleVariant != null) {
            lines.add(TetraGeneratedKjsExporter.spaces(indent) + "outcome.module(" + TetraGeneratedKjsExporter.toJsString(moduleKey) + ", " + TetraGeneratedKjsExporter.toJsString(moduleVariant) + ")");
        } else {
            if (moduleKey != null) {
                lines.add(TetraGeneratedKjsExporter.spaces(indent) + "outcome.moduleKey(" + TetraGeneratedKjsExporter.toJsString(moduleKey) + ")");
            }
            if (moduleVariant != null) {
                lines.add(TetraGeneratedKjsExporter.spaces(indent) + "outcome.moduleVariant(" + TetraGeneratedKjsExporter.toJsString(moduleVariant) + ")");
            }
        }
        JsonObject improvements = TetraGeneratedKjsExporter.removeObject(outcome, "improvements");
        if (improvements != null) {
            lines.add(TetraGeneratedKjsExporter.spaces(indent) + "outcome.improvements(" + TetraGeneratedKjsExporter.inlineJson((JsonElement)improvements) + ")");
        }
        TetraGeneratedKjsExporter.emitLegacyImprovementCalls(lines, outcome, indent);
        TetraGeneratedKjsExporter.emitBooleanCall(lines, outcome, "hidden", "hidden", indent, "outcome", false);
        TetraGeneratedKjsExporter.emitMergeCall(lines, outcome, indent, "outcome");
    }

    private static void emitOutcomeMaterial(List<String> lines, JsonElement material, int indent) {
        TetraGeneratedKjsExporter.emitIngredientCall(lines, material, indent, "outcome");
    }

    private static void emitIngredientCall(List<String> lines, JsonElement material, int indent, String receiver) {
        if (!material.isJsonObject()) {
            lines.add(TetraGeneratedKjsExporter.spaces(indent) + TetraGeneratedKjsExporter.receiver(receiver) + "ingredient(" + TetraGeneratedKjsExporter.inlineJson(material) + ")");
            return;
        }
        JsonObject materialObject = material.getAsJsonObject().deepCopy();
        JsonArray items = materialObject.has("items") && materialObject.get("items").isJsonArray() ? materialObject.getAsJsonArray("items") : null;
        JsonElement tag = materialObject.get("tag");
        JsonElement count = materialObject.get("count");
        JsonElement nbt = materialObject.get("nbt");
        if (items != null && items.size() == 1) {
            JsonElement onlyItem = items.get(0);
            if (onlyItem.isJsonPrimitive() && onlyItem.getAsJsonPrimitive().isString()) {
                String item = onlyItem.getAsString();
                if (item.startsWith("#")) {
                    TetraGeneratedKjsExporter.emitIngredientStringWithOptions(lines, receiver, indent, item, count, nbt);
                    return;
                }
            } else if (onlyItem.isJsonObject()) {
                JsonElement nestedNbt;
                JsonObject nested = onlyItem.getAsJsonObject();
                JsonElement nestedTag = nested.get("tag");
                JsonElement nestedItem = nested.get("item");
                JsonElement nestedCount = nested.has("count") ? nested.get("count") : count;
                JsonElement jsonElement = nestedNbt = nested.has("nbt") ? nested.get("nbt") : nbt;
                if (nestedTag != null && nestedTag.isJsonPrimitive() && nestedTag.getAsJsonPrimitive().isString()) {
                    TetraGeneratedKjsExporter.emitIngredientStringWithOptions(lines, receiver, indent, "#" + nestedTag.getAsString(), nestedCount, nestedNbt);
                    return;
                }
                if (nestedItem != null && nestedItem.isJsonPrimitive() && nestedItem.getAsJsonPrimitive().isString()) {
                    TetraGeneratedKjsExporter.emitIngredientStringWithOptions(lines, receiver, indent, nestedItem.getAsString(), nestedCount, nestedNbt);
                    return;
                }
            }
        }
        if (items != null && items.size() == 1 && items.get(0).isJsonPrimitive() && items.get(0).getAsJsonPrimitive().isString() && (nbt == null || nbt.isJsonNull())) {
            String item = items.get(0).getAsString();
            if (count != null && count.isJsonPrimitive() && count.getAsJsonPrimitive().isNumber()) {
                lines.add(TetraGeneratedKjsExporter.spaces(indent) + TetraGeneratedKjsExporter.receiver(receiver) + "item(" + TetraGeneratedKjsExporter.toJsString(item) + ", " + count.getAsInt() + ")");
            } else {
                lines.add(TetraGeneratedKjsExporter.spaces(indent) + TetraGeneratedKjsExporter.receiver(receiver) + "item(" + TetraGeneratedKjsExporter.toJsString(item) + ")");
            }
            return;
        }
        if (tag != null && tag.isJsonPrimitive() && (count == null || count.isJsonNull()) && (nbt == null || nbt.isJsonNull())) {
            lines.add(TetraGeneratedKjsExporter.spaces(indent) + TetraGeneratedKjsExporter.receiver(receiver) + "itemTag(" + TetraGeneratedKjsExporter.toJsString(tag.getAsString()) + ")");
            return;
        }
        if (tag != null && tag.isJsonPrimitive() && tag.getAsJsonPrimitive().isString()) {
            TetraGeneratedKjsExporter.emitIngredientStringWithOptions(lines, receiver, indent, "#" + tag.getAsString(), count, nbt);
            return;
        }
        lines.add(TetraGeneratedKjsExporter.spaces(indent) + TetraGeneratedKjsExporter.receiver(receiver) + "ingredient(" + TetraGeneratedKjsExporter.inlineJson((JsonElement)materialObject) + ")");
    }

    private static void emitIngredientStringWithOptions(List<String> lines, String receiver, int indent, String value, JsonElement count, JsonElement nbt) {
        if (count == null || count.isJsonNull()) {
            if (nbt == null || nbt.isJsonNull()) {
                lines.add(TetraGeneratedKjsExporter.spaces(indent) + TetraGeneratedKjsExporter.receiver(receiver) + "ingredient(" + TetraGeneratedKjsExporter.toJsString(value) + ")");
                return;
            }
            JsonObject options = new JsonObject();
            options.add("nbt", nbt.deepCopy());
            lines.add(TetraGeneratedKjsExporter.spaces(indent) + TetraGeneratedKjsExporter.receiver(receiver) + "ingredient(" + TetraGeneratedKjsExporter.toJsString(value) + ", " + TetraGeneratedKjsExporter.inlineJson((JsonElement)options) + ")");
            return;
        }
        if (nbt == null || nbt.isJsonNull()) {
            if (count.isJsonPrimitive() && count.getAsJsonPrimitive().isNumber()) {
                lines.add(TetraGeneratedKjsExporter.spaces(indent) + TetraGeneratedKjsExporter.receiver(receiver) + "ingredient(" + TetraGeneratedKjsExporter.toJsString(value) + ", " + count.getAsInt() + ")");
                return;
            }
            JsonObject options = new JsonObject();
            options.add("count", count.deepCopy());
            lines.add(TetraGeneratedKjsExporter.spaces(indent) + TetraGeneratedKjsExporter.receiver(receiver) + "ingredient(" + TetraGeneratedKjsExporter.toJsString(value) + ", " + TetraGeneratedKjsExporter.inlineJson((JsonElement)options) + ")");
            return;
        }
        JsonObject options = new JsonObject();
        options.add("count", count.deepCopy());
        options.add("nbt", nbt.deepCopy());
        lines.add(TetraGeneratedKjsExporter.spaces(indent) + TetraGeneratedKjsExporter.receiver(receiver) + "ingredient(" + TetraGeneratedKjsExporter.toJsString(value) + ", " + TetraGeneratedKjsExporter.inlineJson((JsonElement)options) + ")");
    }

    private static void emitLegacyRequiredToolCalls(List<String> lines, JsonObject outcome, int indent) {
        JsonElement key = outcome.remove("requiredTool");
        JsonElement value = outcome.remove("requiredToolLevel");
        if (key == null || key.isJsonNull() || value == null || value.isJsonNull()) {
            if (key != null && !key.isJsonNull()) {
                outcome.add("requiredTool", key);
            }
            if (value != null && !value.isJsonNull()) {
                outcome.add("requiredToolLevel", value);
            }
            return;
        }
        if (key.isJsonPrimitive() && key.getAsJsonPrimitive().isString()) {
            lines.add(TetraGeneratedKjsExporter.spaces(indent) + "outcome.requiredTool(" + TetraGeneratedKjsExporter.toJsString(key.getAsString()) + ", " + TetraGeneratedKjsExporter.inlineJson(value) + ")");
            return;
        }
        outcome.add("requiredTool", key);
        outcome.add("requiredToolLevel", value);
    }

    private static void emitLegacyImprovementCalls(List<String> lines, JsonObject outcome, int indent) {
        JsonElement key = outcome.remove("improvement");
        JsonElement level = outcome.remove("improvementLevel");
        if (key == null || key.isJsonNull() || level == null || level.isJsonNull()) {
            if (key != null && !key.isJsonNull()) {
                outcome.add("improvement", key);
            }
            if (level != null && !level.isJsonNull()) {
                outcome.add("improvementLevel", level);
            }
            return;
        }
        if (key.isJsonPrimitive() && key.getAsJsonPrimitive().isString() && level.isJsonPrimitive() && level.getAsJsonPrimitive().isNumber()) {
            lines.add(TetraGeneratedKjsExporter.spaces(indent) + "outcome.improvement(" + TetraGeneratedKjsExporter.toJsString(key.getAsString()) + ", " + level.getAsInt() + ")");
            return;
        }
        outcome.add("improvement", key);
        outcome.add("improvementLevel", level);
    }

    private static void emitRequirementCall(List<String> lines, JsonObject data, String key, int indent) {
        JsonObject requirement = TetraGeneratedKjsExporter.removeObject(data, key);
        if (requirement == null || requirement.entrySet().isEmpty()) {
            return;
        }
        lines.add(TetraGeneratedKjsExporter.spaces(indent) + ".requirement(requirement => {");
        TetraGeneratedKjsExporter.emitRequirementBody(lines, requirement, indent + 4, "requirement");
        lines.add(TetraGeneratedKjsExporter.spaces(indent) + "})");
    }

    private static void emitRequirementBody(List<String> lines, JsonObject requirement, int indent, String variableName) {
        String type = TetraGeneratedKjsExporter.readString(requirement, "type");
        if (type == null) {
            lines.add(TetraGeneratedKjsExporter.spaces(indent) + variableName + ".data(" + TetraGeneratedKjsExporter.inlineJson((JsonElement)requirement) + ")");
            return;
        }
        switch (type) {
            case "tetra:and": {
                lines.add(TetraGeneratedKjsExporter.spaces(indent) + variableName + ".and()");
                TetraGeneratedKjsExporter.emitChildRequirements(lines, requirement, indent, variableName);
                break;
            }
            case "tetra:or": {
                lines.add(TetraGeneratedKjsExporter.spaces(indent) + variableName + ".or()");
                TetraGeneratedKjsExporter.emitChildRequirements(lines, requirement, indent, variableName);
                break;
            }
            case "tetra:not": {
                lines.add(TetraGeneratedKjsExporter.spaces(indent) + variableName + ".not()");
                JsonObject nested = TetraGeneratedKjsExporter.removeObject(requirement, "requirement");
                if (nested == null) break;
                lines.add(TetraGeneratedKjsExporter.spaces(indent) + variableName + ".requirement(nested => {");
                TetraGeneratedKjsExporter.emitRequirementBody(lines, nested, indent + 4, "nested");
                lines.add(TetraGeneratedKjsExporter.spaces(indent) + "})");
                break;
            }
            case "tetra:never": {
                lines.add(TetraGeneratedKjsExporter.spaces(indent) + variableName + ".never()");
                break;
            }
            case "tetra:slot": {
                lines.add(TetraGeneratedKjsExporter.spaces(indent) + variableName + ".slot(" + TetraGeneratedKjsExporter.toJsString(TetraGeneratedKjsExporter.readString(requirement, "slot")) + ")");
                break;
            }
            case "tetra:module": {
                String module = TetraGeneratedKjsExporter.readString(requirement, "module");
                String variant = TetraGeneratedKjsExporter.readString(requirement, "variant");
                if (module != null && variant != null) {
                    lines.add(TetraGeneratedKjsExporter.spaces(indent) + variableName + ".module(" + TetraGeneratedKjsExporter.toJsString(module) + ", " + TetraGeneratedKjsExporter.toJsString(variant) + ")");
                } else if (module != null) {
                    lines.add(TetraGeneratedKjsExporter.spaces(indent) + variableName + ".module(" + TetraGeneratedKjsExporter.toJsString(module) + ")");
                }
                if (!requirement.has("material")) break;
                lines.add(TetraGeneratedKjsExporter.spaces(indent) + variableName + ".material(" + TetraGeneratedKjsExporter.inlineJson(requirement.get("material")) + ")");
                break;
            }
            case "tetra:accepts_improvement": {
                String improvement = TetraGeneratedKjsExporter.readString(requirement, "improvement");
                JsonElement level = requirement.get("level");
                if (level != null && level.isJsonPrimitive() && level.getAsJsonPrimitive().isNumber()) {
                    lines.add(TetraGeneratedKjsExporter.spaces(indent) + variableName + ".acceptsImprovement(" + TetraGeneratedKjsExporter.toJsString(improvement) + ", " + level.getAsInt() + ")");
                    break;
                }
                lines.add(TetraGeneratedKjsExporter.spaces(indent) + variableName + ".acceptsImprovement(" + TetraGeneratedKjsExporter.toJsString(improvement) + ")");
                if (level == null || level.isJsonNull()) break;
                lines.add(TetraGeneratedKjsExporter.spaces(indent) + variableName + ".level(" + TetraGeneratedKjsExporter.inlineJson(level) + ")");
                break;
            }
            case "tetra:improvement": {
                String improvement = TetraGeneratedKjsExporter.readString(requirement, "improvement");
                JsonElement level = requirement.get("level");
                if (level != null && !level.isJsonNull()) {
                    lines.add(TetraGeneratedKjsExporter.spaces(indent) + variableName + ".hasImprovement(" + TetraGeneratedKjsExporter.toJsString(improvement) + ", " + TetraGeneratedKjsExporter.inlineJson(level) + ")");
                    break;
                }
                lines.add(TetraGeneratedKjsExporter.spaces(indent) + variableName + ".hasImprovement(" + TetraGeneratedKjsExporter.toJsString(improvement) + ")");
                break;
            }
            case "tetra:aspect": {
                String aspect = TetraGeneratedKjsExporter.readString(requirement, "aspect");
                JsonElement level = requirement.get("level");
                if (level != null && !level.isJsonNull()) {
                    lines.add(TetraGeneratedKjsExporter.spaces(indent) + variableName + ".aspect(" + TetraGeneratedKjsExporter.toJsString(aspect) + ", " + TetraGeneratedKjsExporter.inlineJson(level) + ")");
                    break;
                }
                lines.add(TetraGeneratedKjsExporter.spaces(indent) + variableName + ".aspect(" + TetraGeneratedKjsExporter.toJsString(aspect) + ")");
                break;
            }
            case "tetra:feature_flag": {
                String feature = TetraGeneratedKjsExporter.readString(requirement, "feature");
                lines.add(TetraGeneratedKjsExporter.spaces(indent) + variableName + ".featureFlag(" + TetraGeneratedKjsExporter.toJsString(feature) + ")");
                String flag = TetraGeneratedKjsExporter.readString(requirement, "flag");
                if (flag == null || flag.equals(feature)) break;
                lines.add(TetraGeneratedKjsExporter.spaces(indent) + variableName + ".flag(" + TetraGeneratedKjsExporter.toJsString(flag) + ")");
                break;
            }
            case "tetra:perk": {
                String perk = TetraGeneratedKjsExporter.readString(requirement, "perk");
                JsonElement level = requirement.get("level");
                if (level != null && !level.isJsonNull()) {
                    lines.add(TetraGeneratedKjsExporter.spaces(indent) + variableName + ".perk(" + TetraGeneratedKjsExporter.toJsString(perk) + ", " + TetraGeneratedKjsExporter.inlineJson(level) + ")");
                    break;
                }
                lines.add(TetraGeneratedKjsExporter.spaces(indent) + variableName + ".perk(" + TetraGeneratedKjsExporter.toJsString(perk) + ")");
                break;
            }
            case "tetra:locked": {
                lines.add(TetraGeneratedKjsExporter.spaces(indent) + variableName + ".locked(" + TetraGeneratedKjsExporter.toJsString(TetraGeneratedKjsExporter.readString(requirement, "key")) + ")");
                break;
            }
            default: {
                lines.add(TetraGeneratedKjsExporter.spaces(indent) + variableName + ".data(" + TetraGeneratedKjsExporter.inlineJson((JsonElement)requirement) + ")");
            }
        }
    }

    private static void emitChildRequirements(List<String> lines, JsonObject requirement, int indent, String variableName) {
        JsonArray children = TetraGeneratedKjsExporter.removeArray(requirement, "requirements");
        if (children == null) {
            return;
        }
        for (JsonElement childElement : children) {
            if (!childElement.isJsonObject()) {
                lines.add(TetraGeneratedKjsExporter.spaces(indent) + variableName + ".addRequirement(child => {");
                lines.add(TetraGeneratedKjsExporter.spaces(indent + 4) + "child.data(" + TetraGeneratedKjsExporter.inlineJson(childElement) + ")");
                lines.add(TetraGeneratedKjsExporter.spaces(indent) + "})");
                continue;
            }
            if (childElement.getAsJsonObject().entrySet().isEmpty()) continue;
            lines.add(TetraGeneratedKjsExporter.spaces(indent) + variableName + ".addRequirement(child => {");
            TetraGeneratedKjsExporter.emitRequirementBody(lines, childElement.getAsJsonObject().deepCopy(), indent + 4, "child");
            lines.add(TetraGeneratedKjsExporter.spaces(indent) + "})");
        }
    }

    private static void emitSchematicSlotTranslations(List<String> lines, String key, JsonObject data, ExportContext context, int indent) {
        int slotCount = data.has("materialSlotCount") && data.get("materialSlotCount").isJsonPrimitive() ? data.get("materialSlotCount").getAsInt() : 0;
        for (int slotNumber = 1; slotNumber <= Math.max(slotCount, 8); ++slotNumber) {
            Map<String, String> values = context.translationsFor(TetraDataUtil.schematicSlotKey(key, slotNumber));
            if (values.isEmpty()) continue;
            TetraGeneratedKjsExporter.emitIndexedTranslationCalls(lines, "slot", slotNumber, values, indent);
        }
    }

    private static void emitTranslationCalls(List<String> lines, String method, Map<String, String> values, int indent) {
        TetraGeneratedKjsExporter.emitTranslationCalls(lines, method, values, indent, null);
    }

    private static void emitTranslationCalls(List<String> lines, String method, Map<String, String> values, int indent, String receiver) {
        if (values.isEmpty()) {
            return;
        }
        String defaultValue = TetraGeneratedKjsExporter.cleanExportTranslationValue(values.get("zh_cn"));
        if (defaultValue != null && !defaultValue.isBlank()) {
            lines.add(TetraGeneratedKjsExporter.spaces(indent) + TetraGeneratedKjsExporter.receiver(receiver) + method + "(" + TetraGeneratedKjsExporter.toJsString(defaultValue) + ")");
        }
        for (Map.Entry<String, String> entry : values.entrySet()) {
            String value;
            if ("zh_cn".equals(entry.getKey()) || (value = TetraGeneratedKjsExporter.cleanExportTranslationValue(entry.getValue())) == null || value.isBlank()) continue;
            lines.add(TetraGeneratedKjsExporter.spaces(indent) + TetraGeneratedKjsExporter.receiver(receiver) + method + "(" + TetraGeneratedKjsExporter.toJsString(entry.getKey()) + ", " + TetraGeneratedKjsExporter.toJsString(value) + ")");
        }
    }

    private static void emitIndexedTranslationCalls(List<String> lines, String method, int index, Map<String, String> values, int indent) {
        String defaultValue = TetraGeneratedKjsExporter.cleanExportTranslationValue(values.get("zh_cn"));
        if (defaultValue != null && !defaultValue.isBlank()) {
            lines.add(TetraGeneratedKjsExporter.spaces(indent) + "." + method + "(" + index + ", " + TetraGeneratedKjsExporter.toJsString(defaultValue) + ")");
        }
        for (Map.Entry<String, String> entry : values.entrySet()) {
            String value;
            if ("zh_cn".equals(entry.getKey()) || (value = TetraGeneratedKjsExporter.cleanExportTranslationValue(entry.getValue())) == null || value.isBlank()) continue;
            lines.add(TetraGeneratedKjsExporter.spaces(indent) + "." + method + "(" + TetraGeneratedKjsExporter.toJsString(entry.getKey()) + ", " + index + ", " + TetraGeneratedKjsExporter.toJsString(value) + ")");
        }
    }

    private static String cleanExportTranslationValue(String value) {
        if (value == null) {
            return null;
        }
        String prefix = "Format error:";
        return value.startsWith(prefix) ? value.substring(prefix.length()).stripLeading() : value;
    }

    private static void emitStringCall(List<String> lines, JsonObject data, String key, String method, int indent) {
        TetraGeneratedKjsExporter.emitStringCall(lines, data, key, method, indent, null);
    }

    private static void emitStringCall(List<String> lines, JsonObject data, String key, String method, int indent, String receiver) {
        String value = TetraGeneratedKjsExporter.removeString(data, key);
        if (value != null) {
            lines.add(TetraGeneratedKjsExporter.spaces(indent) + TetraGeneratedKjsExporter.receiver(receiver) + method + "(" + TetraGeneratedKjsExporter.toJsString(value) + ")");
        }
    }

    private static void emitIntegerCall(List<String> lines, JsonObject data, String key, String method, int indent) {
        TetraGeneratedKjsExporter.emitIntegerCall(lines, data, key, method, indent, null);
    }

    private static void emitIntegerCall(List<String> lines, JsonObject data, String key, String method, int indent, String receiver) {
        JsonElement value = data.remove(key);
        if (value != null && value.isJsonPrimitive() && value.getAsJsonPrimitive().isNumber()) {
            lines.add(TetraGeneratedKjsExporter.spaces(indent) + TetraGeneratedKjsExporter.receiver(receiver) + method + "(" + value.getAsInt() + ")");
        } else if (value != null) {
            data.add(key, value);
        }
    }

    private static void emitNumberCall(List<String> lines, JsonObject data, String key, String method, int indent) {
        TetraGeneratedKjsExporter.emitNumberCall(lines, data, key, method, indent, null);
    }

    private static void emitNumberCall(List<String> lines, JsonObject data, String key, String method, int indent, String receiver) {
        JsonElement value = data.remove(key);
        if (value != null && value.isJsonPrimitive() && value.getAsJsonPrimitive().isNumber()) {
            lines.add(TetraGeneratedKjsExporter.spaces(indent) + TetraGeneratedKjsExporter.receiver(receiver) + method + "(" + TetraGeneratedKjsExporter.numberLiteral(value.getAsNumber()) + ")");
        } else if (value != null) {
            data.add(key, value);
        }
    }

    private static void emitBooleanCall(List<String> lines, JsonObject data, String key, String method, int indent, boolean omitTrueShortcut) {
        TetraGeneratedKjsExporter.emitBooleanCall(lines, data, key, method, indent, null, omitTrueShortcut);
    }

    private static void emitBooleanCall(List<String> lines, JsonObject data, String key, String method, int indent, String receiver, boolean omitTrueShortcut) {
        JsonElement value = data.remove(key);
        if (value != null && value.isJsonPrimitive() && value.getAsJsonPrimitive().isBoolean()) {
            boolean bool = value.getAsBoolean();
            if (bool && omitTrueShortcut) {
                lines.add(TetraGeneratedKjsExporter.spaces(indent) + TetraGeneratedKjsExporter.receiver(receiver) + method + "()");
            } else {
                lines.add(TetraGeneratedKjsExporter.spaces(indent) + TetraGeneratedKjsExporter.receiver(receiver) + method + "(" + bool + ")");
            }
        } else if (value != null) {
            data.add(key, value);
        }
    }

    private static void emitJsonCall(List<String> lines, JsonObject data, String key, String method, int indent) {
        TetraGeneratedKjsExporter.emitJsonCall(lines, data, key, method, indent, null);
    }

    private static void emitJsonCall(List<String> lines, JsonObject data, String key, String method, int indent, String receiver) {
        JsonElement value = data.remove(key);
        if (value != null && !value.isJsonNull()) {
            lines.add(TetraGeneratedKjsExporter.spaces(indent) + TetraGeneratedKjsExporter.receiver(receiver) + method + "(" + TetraGeneratedKjsExporter.inlineJson(value) + ")");
        }
    }

    private static void emitMergeCall(List<String> lines, JsonObject data, int indent, String receiver) {
        if (data == null || data.entrySet().isEmpty()) {
            return;
        }
        lines.add(TetraGeneratedKjsExporter.spaces(indent) + TetraGeneratedKjsExporter.receiver(receiver) + "merge(" + TetraGeneratedKjsExporter.inlineJson((JsonElement)data) + ")");
    }

    private static String receiver(String receiver) {
        return receiver == null ? "." : receiver + ".";
    }

    private static void appendExportBody(List<String> lines, String script) {
        if (script == null || script.isBlank()) {
            return;
        }
        String normalized = script.replace("\r\n", "\n").trim();
        String[] split = normalized.split("\n");
        if (split.length < 3) {
            return;
        }
        for (int i = 1; i < split.length - 1; ++i) {
            String line = split[i];
            if (line.isBlank()) continue;
            lines.add(line);
        }
    }

    private static Map<String, JsonElement> loadDirectoryEntries(ResourceManager resourceManager, String directory) {
        String normalizedDirectory = TetraDataUtil.normalizeDirectory(directory);
        Map<ResourceLocation, Resource> resources = resourceManager.listResources(normalizedDirectory, location -> "tetra".equals(location.getNamespace()) && location.getPath().endsWith(".json"));
        LinkedHashMap<String, JsonElement> result = new LinkedHashMap<String, JsonElement>();
        ArrayList<ResourceLocation> ids = new ArrayList<ResourceLocation>(resources.keySet());
        ids.sort(Comparator.comparing(ResourceLocation::toString));
        for (ResourceLocation resourceId : ids) {
            JsonElement json;
            if (!resourceId.getPath().startsWith(normalizedDirectory + "/") || (json = TetraDataUtil.readJson(resourceManager, resourceId)) == null || json.isJsonNull()) continue;
            result.put(TetraDataUtil.extractKeyFromPath(normalizedDirectory, resourceId.getPath()), json);
        }
        return result;
    }

    private static Map<String, JsonElement> loadImprovementEntries(ResourceManager resourceManager) {
        Map<String, JsonElement> entriesByPath = TetraGeneratedKjsExporter.loadDirectoryEntries(resourceManager, "improvements");
        LinkedHashMap<String, JsonElement> result = new LinkedHashMap<String, JsonElement>();
        for (Map.Entry<String, JsonElement> entry : entriesByPath.entrySet()) {
            JsonArray array;
            String pathKey = entry.getKey();
            JsonElement json = entry.getValue();
            if (json == null || json.isJsonNull()) continue;
            if (json.isJsonArray() && (array = json.getAsJsonArray()).size() > 0 && array.get(0).isJsonObject()) {
                String improvementKey = TetraGeneratedKjsExporter.readString(array.get(0).getAsJsonObject(), "key");
                result.put(improvementKey == null || improvementKey.isBlank() ? pathKey : improvementKey, json);
                continue;
            }
            if (json.isJsonObject()) {
                String improvementKey = TetraGeneratedKjsExporter.readString(json.getAsJsonObject(), "key");
                result.put(improvementKey == null || improvementKey.isBlank() ? pathKey : improvementKey, json);
                continue;
            }
            result.put(pathKey, json);
        }
        return result;
    }

    private static GroupedOutcome classifySchematicOutcome(JsonObject schematic) {
        JsonArray outcomes;
        JsonArray jsonArray = outcomes = schematic.has("outcomes") && schematic.get("outcomes").isJsonArray() ? schematic.getAsJsonArray("outcomes") : null;
        if (outcomes == null || outcomes.isEmpty()) {
            return new GroupedOutcome(List.of(), List.of());
        }
        ArrayList<String> moduleKeys = new ArrayList<String>();
        ArrayList<String> improvementKeys = new ArrayList<String>();
        for (JsonElement outcomeElement : outcomes) {
            JsonObject improvements;
            if (!outcomeElement.isJsonObject()) continue;
            JsonObject outcome = outcomeElement.getAsJsonObject();
            String moduleKey = TetraGeneratedKjsExporter.readString(outcome, "moduleKey");
            if (moduleKey != null && !moduleKey.isBlank() && !moduleKeys.contains(moduleKey)) {
                moduleKeys.add(moduleKey);
            }
            if ((improvements = outcome.has("improvements") && outcome.get("improvements").isJsonObject() ? outcome.getAsJsonObject("improvements") : null) == null) continue;
            for (Map.Entry entry : improvements.entrySet()) {
                if (improvementKeys.contains(entry.getKey())) continue;
                improvementKeys.add((String)entry.getKey());
            }
        }
        return new GroupedOutcome(moduleKeys, improvementKeys);
    }

    private static JsonObject removeObject(JsonObject data, String key) {
        JsonElement value = data.remove(key);
        return value != null && value.isJsonObject() ? value.getAsJsonObject() : null;
    }

    private static JsonArray removeArray(JsonObject data, String key) {
        JsonElement value = data.remove(key);
        return value != null && value.isJsonArray() ? value.getAsJsonArray() : null;
    }

    private static String removeString(JsonObject data, String key) {
        JsonElement value = data.remove(key);
        if (value != null && value.isJsonPrimitive() && value.getAsJsonPrimitive().isString()) {
            return value.getAsString();
        }
        if (value != null) {
            data.add(key, value);
        }
        return null;
    }

    private static String readString(JsonObject data, String key) {
        JsonElement value = data.get(key);
        if (value != null && value.isJsonPrimitive() && value.getAsJsonPrimitive().isString()) {
            return value.getAsString();
        }
        return null;
    }

    private static String materialBuilderStart(String key) {
        return "event.material(" + TetraGeneratedKjsExporter.toJsString(TetraDataUtil.normalizeKey(key)) + ")";
    }

    private static String inlineJson(JsonElement json) {
        return PRETTY_GSON.toJson(json).replace("\r\n", "\n").replace("\n", "\n" + TetraGeneratedKjsExporter.spaces(12)).trim();
    }

    private static String indentJson(String json, String indent) {
        String normalized = json.replace("\r\n", "\n");
        String[] lines = normalized.split("\n", -1);
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < lines.length; ++i) {
            if (i > 0) {
                builder.append('\n');
            }
            builder.append(indent).append(lines[i]);
        }
        return builder.toString();
    }

    private static String spaces(int count) {
        return " ".repeat(Math.max(0, count));
    }

    private static String numberLiteral(Number number) {
        String literal = String.valueOf(number);
        if (literal.endsWith(".0")) {
            return literal.substring(0, literal.length() - 2);
        }
        return literal;
    }

    private static String toJsString(String value) {
        if (value == null) {
            return "\"\"";
        }
        return "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "\\r").replace("\n", "\\n") + "\"";
    }

    private static Map<String, Map<String, String>> loadTranslations(ResourceManager resourceManager) {
        LinkedHashMap<String, Map<String, String>> result = new LinkedHashMap<String, Map<String, String>>();
        Map<ResourceLocation, Resource> resources = resourceManager.listResources("lang", location -> "tetra".equals(location.getNamespace()) && location.getPath().endsWith(".json"));
        ArrayList<ResourceLocation> ids = new ArrayList<ResourceLocation>(resources.keySet());
        ids.sort(Comparator.comparing(ResourceLocation::toString));
        for (ResourceLocation resourceId : ids) {
            String language = resourceId.getPath().substring("lang/".length(), resourceId.getPath().length() - ".json".length());
            Optional optional = resourceManager.getResource(resourceId);
            if (optional.isEmpty()) continue;
            try {
                BufferedReader reader = ((Resource)optional.get()).openAsReader();
                try {
                    JsonObject json = (JsonObject)PRETTY_GSON.fromJson((Reader)reader, JsonObject.class);
                    if (json == null) continue;
                    Map values = result.computeIfAbsent(language, ignored -> new LinkedHashMap());
                    for (Map.Entry entry : json.entrySet()) {
                        JsonElement value = (JsonElement)entry.getValue();
                        if (value == null || !value.isJsonPrimitive() || !value.getAsJsonPrimitive().isString()) continue;
                        values.put((String)entry.getKey(), value.getAsString());
                    }
                }
                finally {
                    if (reader == null) continue;
                    ((Reader)reader).close();
                }
            }
            catch (Exception exception) {
                TetraDataKubejs.LOGGER.warn("\u8bfb\u53d6\u8bed\u8a00\u6587\u4ef6\u5931\u8d25 {}", (Object)resourceId, (Object)exception);
            }
        }
        return result;
    }

    private static void clearDirectory(Path directory) throws IOException {
        if (!Files.exists(directory, new LinkOption[0])) {
            return;
        }
        try (Stream<Path> paths = Files.walk(directory, new FileVisitOption[0]);){
            paths.sorted(Comparator.reverseOrder()).forEach(path -> {
                try {
                    Files.deleteIfExists(path);
                }
                catch (IOException exception) {
                    throw new RuntimeException(exception);
                }
            });
        }
        catch (RuntimeException exception) {
            Throwable throwable = exception.getCause();
            if (throwable instanceof IOException) {
                IOException ioException = (IOException)throwable;
                throw ioException;
            }
            throw exception;
        }
    }

    private record ExportContext(ResourceManager resourceManager, Map<String, Map<String, String>> translations) {
        private Map<String, String> translationsFor(String fullKey) {
            LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
            for (Map.Entry<String, Map<String, String>> entry : this.translations.entrySet()) {
                String value = entry.getValue().get(fullKey);
                if (value == null || value.isBlank()) continue;
                result.put(entry.getKey(), value);
            }
            if (FMLEnvironment.dist == Dist.CLIENT) {
                Map<String, String> clientTranslations = ClientTranslationHook.export(fullKey);
                if (clientTranslations != null) {
                    clientTranslations.forEach(result::putIfAbsent);
                }
            }
            return result;
        }
    }

    private static final class ClientTranslationHook {
        private static Map<String, String> export(String key) {
            return com.hp.tetra_data_kubejs.client.TetraClientTranslationHelper.exportTranslation(key);
        }
    }

    public static final class ExportSummary {
        private final Path root;
        private final Map<String, Integer> counts = new LinkedHashMap<String, Integer>();
        private int total;

        private ExportSummary(Path root) {
            this.root = root;
        }

        private void record(String directory) {
            this.counts.merge(directory, 1, Integer::sum);
            ++this.total;
        }

        public Path root() {
            return this.root;
        }

        public int total() {
            return this.total;
        }

        public Map<String, Integer> counts() {
            return this.counts;
        }
    }

    private record GroupedOutcome(List<String> moduleKeys, List<String> improvementKeys) {
        private boolean hasModule() {
            return this.moduleKeys != null && !this.moduleKeys.isEmpty();
        }

        private boolean hasExportableTarget() {
            return this.hasModule() || this.improvementKeys != null && !this.improvementKeys.isEmpty();
        }
    }
}

