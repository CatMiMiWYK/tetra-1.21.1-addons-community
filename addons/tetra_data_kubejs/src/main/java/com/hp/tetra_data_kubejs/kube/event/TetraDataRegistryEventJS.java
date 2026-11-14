/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  dev.latvian.mods.kubejs.event.KubeEvent
 *  dev.latvian.mods.kubejs.util.JsonIO
 *  net.minecraft.resources.ResourceLocation
 */
package com.hp.tetra_data_kubejs.kube.event;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.hp.tetra_data_kubejs.data.JsonMergeUtil;
import com.hp.tetra_data_kubejs.data.ScriptedTetraEntry;
import com.hp.tetra_data_kubejs.data.TetraColorUtil;
import com.hp.tetra_data_kubejs.data.TetraDataUtil;
import com.hp.tetra_data_kubejs.data.TetraScriptRegistry;
import dev.latvian.mods.kubejs.event.KubeEvent;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.resources.ResourceLocation;

public class TetraDataRegistryEventJS
implements KubeEvent {
    private final TetraScriptRegistry.Scope scope;

    public TetraDataRegistryEventJS() {
        this(TetraScriptRegistry.Scope.STARTUP);
    }

    public TetraDataRegistryEventJS(TetraScriptRegistry.Scope scope) {
        this.scope = scope;
    }

    public MaterialBuilder material(String key) {
        String normalizedKey = TetraDataUtil.normalizeKey(key);
        if (!normalizedKey.contains("/")) {
            return new MaterialBuilder(this.scope, TetraScriptRegistry.entry(this.scope, "materials", TetraDataRegistryEventJS.autoMaterialPath(normalizedKey))).category(normalizedKey).autoKeyCategory(normalizedKey);
        }
        return new MaterialBuilder(this.scope, TetraScriptRegistry.entry(this.scope, "materials", key));
    }

    public MaterialBuilder material(String category, String key) {
        return this.material(TetraDataRegistryEventJS.materialPath(category, key)).category(category);
    }

    public MaterialBuilder material(String key, Object data) {
        return this.material(key).merge(data);
    }

    public MaterialBuilder material(String category, String key, Object data) {
        return this.material(category, key).merge(data);
    }

    public ModuleBuilder module(String key) {
        return new ModuleBuilder(this.scope, TetraScriptRegistry.entry(this.scope, "modules", key));
    }

    public ModuleBuilder module(String key, Object data) {
        return (ModuleBuilder)this.module(key).merge(data);
    }

    private ImprovementBuilder improvementAtPath(String key) {
        return new ImprovementBuilder(this.scope, TetraScriptRegistry.entry(this.scope, "improvements", key));
    }

    public ImprovementBuilder improvement() {
        return this.improvementAtPath("shared/_generated/improvement_" + UUID.randomUUID().toString().replace("-", ""));
    }

    public SchematicBuilder schematic(String key) {
        return new SchematicBuilder(this.scope, TetraScriptRegistry.entry(this.scope, "schematics", key));
    }

    public SchematicBuilder schematic(String key, Object data) {
        return (SchematicBuilder)this.schematic(key).merge(data);
    }

    public GenericBuilder data(String directory, String key) {
        return new GenericBuilder(this.scope, TetraScriptRegistry.entry(this.scope, directory, key));
    }

    public GenericBuilder data(String directory, String key, Object data) {
        return (GenericBuilder)this.data(directory, key).merge(data);
    }

    public GenericBuilder entry(String directory, String key) {
        return this.data(directory, key);
    }

    public GenericBuilder entry(String directory, String key, Object data) {
        return this.data(directory, key, data);
    }

    public TetraDataRegistryEventJS lang(String fullKey, String value) {
        return this.lang("zh_cn", fullKey, value);
    }

    public TetraDataRegistryEventJS lang(String language, String fullKey, String value) {
        TetraScriptRegistry.addTranslation(this.scope, language, fullKey, value);
        return this;
    }

    public TetraDataRegistryEventJS debugOutput() {
        return this.debugOutput(true);
    }

    public TetraDataRegistryEventJS debugOutput(boolean enabled) {
        TetraScriptRegistry.setDebugOutput(this.scope, enabled);
        return this;
    }

    public TetraDataRegistryEventJS verboseOutput() {
        return this.verboseOutput(true);
    }

    public TetraDataRegistryEventJS verboseOutput(boolean enabled) {
        return this.debugOutput(enabled);
    }

    private static JsonObject modelRef(String location, String renderLayer) {
        JsonObject model = new JsonObject();
        model.addProperty("location", location);
        if (renderLayer != null && !renderLayer.isBlank()) {
            model.addProperty("renderLayer", renderLayer);
        }
        return model;
    }

    private static void setTieredValue(JsonObject target, String key, Object level, Double efficiency) {
        if (level == null) {
            target.remove(key);
            return;
        }
        JsonElement levelJson = TetraDataRegistryEventJS.toJson(level);
        if (levelJson == null || levelJson.isJsonNull()) {
            target.remove(key);
            return;
        }
        if (efficiency == null) {
            target.add(key, levelJson.deepCopy());
            return;
        }
        JsonArray value = new JsonArray();
        value.add(levelJson.deepCopy());
        value.add((Number)efficiency);
        target.add(key, (JsonElement)value);
    }

    private static void setJson(JsonObject target, String key, Object value) {
        JsonElement json = TetraDataRegistryEventJS.toJson(value);
        if (json == null || json.isJsonNull()) {
            target.remove(key);
        } else {
            target.add(key, json.deepCopy());
        }
    }

    private static void setStringArrayLike(JsonObject target, String key, Object values) {
        JsonElement json = TetraDataRegistryEventJS.toJson(values);
        if (json == null || json.isJsonNull()) {
            target.remove(key);
            return;
        }
        JsonArray array = new JsonArray();
        if (json.isJsonPrimitive()) {
            array.add(json.getAsString());
        } else if (json.isJsonArray()) {
            json.getAsJsonArray().forEach(arg_0 -> ((JsonArray)array).add(arg_0));
        } else {
            throw new IllegalArgumentException(key + "() expects a string or array of strings");
        }
        target.add(key, (JsonElement)array);
    }

    private static void setStringValues(JsonObject target, String key, Object ... values) {
        JsonElement json = TetraDataRegistryEventJS.normalizeStringValues(values);
        if (json == null || json.isJsonNull()) {
            target.remove(key);
            return;
        }
        target.add(key, (JsonElement)json.getAsJsonArray());
    }

    private static JsonElement normalizeStringValues(Object ... values) {
        if (values == null || values.length == 0) {
            return null;
        }
        if (values.length == 1) {
            JsonElement json = TetraDataRegistryEventJS.toJson(values[0]);
            if (json == null || json.isJsonNull()) {
                return null;
            }
            if (json.isJsonArray()) {
                return json;
            }
            if (json.isJsonPrimitive()) {
                JsonArray array = new JsonArray();
                array.add(json.getAsString());
                return array;
            }
            throw new IllegalArgumentException("Expected string or array of strings");
        }
        JsonArray array = new JsonArray();
        for (Object value : values) {
            JsonElement json;
            if (value == null || (json = TetraDataRegistryEventJS.toJson(value)) == null || json.isJsonNull()) continue;
            if (!json.isJsonPrimitive()) {
                throw new IllegalArgumentException("Expected string or array of strings");
            }
            array.add(json.getAsString());
        }
        return array;
    }

    private static JsonObject nestedObject(JsonObject target, String key) {
        JsonElement element = target.get(key);
        if (element == null || element.isJsonNull()) {
            JsonObject nested = new JsonObject();
            target.add(key, (JsonElement)nested);
            return nested;
        }
        if (!element.isJsonObject()) {
            throw new IllegalStateException(key + " must be a json object");
        }
        return element.getAsJsonObject();
    }

    private static JsonArray arrayData(JsonObject target, String key) {
        JsonElement element = target.get(key);
        if (element == null || element.isJsonNull()) {
            JsonArray array = new JsonArray();
            target.add(key, (JsonElement)array);
            return array;
        }
        if (!element.isJsonArray()) {
            throw new IllegalStateException(key + " must be a json array");
        }
        return element.getAsJsonArray();
    }

    private static void setEntries(JsonObject target, String methodName, String key, Object ... values) {
        JsonObject json = TetraDataRegistryEventJS.normalizeEntries(methodName, values);
        if (json == null) {
            target.remove(key);
        } else {
            target.add(key, (JsonElement)json);
        }
    }

    private static JsonObject normalizeEntries(String methodName, Object ... values) {
        JsonArray array;
        JsonElement json;
        if (values == null || values.length == 0) {
            return null;
        }
        if (values.length == 1) {
            json = TetraDataRegistryEventJS.toJson(values[0]);
        } else {
            array = new JsonArray();
            for (Object value : values) {
                array.add(TetraDataRegistryEventJS.toJson(value));
            }
            json = array;
        }
        if (json == null || json.isJsonNull()) {
            return null;
        }
        if (json.isJsonObject()) {
            return json.getAsJsonObject().deepCopy();
        }
        if (!json.isJsonArray()) {
            throw new IllegalArgumentException(methodName + " expects an object or entry arrays");
        }
        array = json.getAsJsonArray();
        JsonObject result = new JsonObject();
        if (TetraDataRegistryEventJS.isEntryPair(array)) {
            result.add(array.get(0).getAsString(), array.get(1).deepCopy());
            return result;
        }
        for (JsonElement element : array) {
            if (!element.isJsonArray()) {
                throw new IllegalArgumentException(methodName + " expects entries like ['key', value]");
            }
            JsonArray entry = element.getAsJsonArray();
            if (!TetraDataRegistryEventJS.isEntryPair(entry)) {
                throw new IllegalArgumentException(methodName + " expects entries like ['key', value]");
            }
            result.add(entry.get(0).getAsString(), entry.get(1).deepCopy());
        }
        return result;
    }

    private static boolean isEntryPair(JsonArray array) {
        return array.size() == 2 && array.get(0).isJsonPrimitive() && array.get(0).getAsJsonPrimitive().isString();
    }

    private static void replaceContents(JsonObject target, JsonObject replacement) {
        JsonArray keys = new JsonArray();
        target.keySet().forEach(arg_0 -> ((JsonArray)keys).add(arg_0));
        for (JsonElement key : keys) {
            target.remove(key.getAsString());
        }
        replacement.entrySet().forEach(entry -> target.add((String)entry.getKey(), ((JsonElement)entry.getValue()).deepCopy()));
    }

    private static String materialPath(String category, String key) {
        String normalizedCategory = TetraDataUtil.normalizeKey(category);
        String normalizedKey = TetraDataUtil.normalizeKey(key);
        return normalizedCategory + "/" + normalizedKey;
    }

    private static String autoMaterialPath(String category) {
        return TetraDataRegistryEventJS.materialPath(category, "__auto_" + UUID.randomUUID().toString().replace("-", ""));
    }

    public static class MaterialBuilder
    extends AbstractBuilder<MaterialBuilder> {
        private String pendingAutoKeyCategory;

        protected MaterialBuilder(TetraScriptRegistry.Scope scope, ScriptedTetraEntry entry) {
            super(scope, entry);
        }

        @Override
        public MaterialBuilder merge(Object data) {
            super.merge(data);
            if (this.entry.getData() != null && this.entry.getData().isJsonObject()) {
                this.objectData();
            }
            return this;
        }

        public MaterialBuilder name(String value) {
            return this.name("zh_cn", value);
        }

        public MaterialBuilder name(String language, String value) {
            this.lang(language, TetraDataUtil.materialNameKey(this.entry.getKey()), value);
            String prefixKey = TetraDataUtil.materialPrefixKey(this.entry.getKey());
            if (!TetraScriptRegistry.hasTranslation(this.scope, language, prefixKey)) {
                this.lang(language, prefixKey, value);
            }
            return this.self();
        }

        public MaterialBuilder prefix(String value) {
            return this.prefix("zh_cn", value);
        }

        public MaterialBuilder prefix(String language, String value) {
            return (MaterialBuilder)this.lang(language, TetraDataUtil.materialPrefixKey(this.entry.getKey()), value);
        }

        public MaterialBuilder category(String value) {
            return (MaterialBuilder)this.set("category", value);
        }

        public MaterialBuilder hidden() {
            return this.hidden(true, true);
        }

        public MaterialBuilder hidden(boolean value) {
            return this.hidden(value, value);
        }

        public MaterialBuilder hidden(boolean hidden, boolean hiddenOutcomes) {
            this.set("hidden", hidden);
            this.set("hiddenOutcomes", hiddenOutcomes);
            return this.self();
        }

        public MaterialBuilder durability(double value) {
            return (MaterialBuilder)this.set("durability", value);
        }

        public MaterialBuilder integrity(double gain, double cost) {
            return this.integrityValues(gain, cost);
        }

        public MaterialBuilder integrity(Object values) {
            JsonArray array = this.requireArray(values, 2, "integrity()");
            return this.integrityValues(array.get(0).getAsDouble(), array.get(1).getAsDouble());
        }

        public MaterialBuilder stats(double primary, double secondary, double tertiary) {
            return this.statValues(primary, secondary, tertiary);
        }

        public MaterialBuilder stats(Object values) {
            JsonArray array = this.requireArray(values, 3, "stats()");
            return this.statValues(array.get(0).getAsDouble(), array.get(1).getAsDouble(), array.get(2).getAsDouble());
        }

        public MaterialBuilder magicCapacity(int value) {
            return (MaterialBuilder)this.set("magicCapacity", value);
        }

        public MaterialBuilder tool(Object level, double efficiency) {
            return this.toolValues(level, efficiency);
        }

        public MaterialBuilder tool(Object values) {
            JsonArray array = this.requireArray(values, 2, "tool()");
            JsonElement level = array.get(0);
            String normalizedLevel = level.isJsonPrimitive() && level.getAsJsonPrimitive().isNumber() ? String.valueOf(level.getAsDouble()) : level.getAsString();
            return this.toolValues(normalizedLevel, array.get(1).getAsDouble());
        }

        public MaterialBuilder effects(Object ... values) {
            return (MaterialBuilder)this.setEntries("effects", "effects()", values);
        }

        public MaterialBuilder attributes(Object ... values) {
            return (MaterialBuilder)this.setEntries("attributes", "attributes()", values);
        }

        public MaterialBuilder aspects(Object value) {
            return (MaterialBuilder)this.setJson("aspects", value);
        }

        public MaterialBuilder improvements(Object value) {
            return (MaterialBuilder)this.setJson("improvements", value);
        }

        public MaterialBuilder item(String value) {
            return this.ingredient(value);
        }

        public MaterialBuilder item(String value, int count) {
            return this.ingredient(value, count);
        }

        public MaterialBuilder items(Object values) {
            return this.ingredient(values);
        }

        public MaterialBuilder itemTag(String value) {
            return this.ingredient("#" + value);
        }

        public MaterialBuilder itemNbt(String value) {
            JsonObject material = this.nestedObject("material");
            material.addProperty("nbt", value);
            return this.self();
        }

        public MaterialBuilder count(int value) {
            JsonObject material = this.nestedObject("material");
            material.addProperty("count", (Number)value);
            return this.self();
        }

        public MaterialBuilder ingredient(String value) {
            return this.ingredient((Object)value, null);
        }

        public MaterialBuilder ingredient(String value, int count) {
            JsonObject options = new JsonObject();
            options.addProperty("count", (Number)count);
            return this.ingredient(value, (Object)options);
        }

        public MaterialBuilder ingredient(String value, Object options) {
            return this.ingredient((Object)value, options);
        }

        public MaterialBuilder ingredient(Object value) {
            return this.ingredient(value, null);
        }

        public MaterialBuilder ingredient(Object value, Object options) {
            JsonElement definition = TetraDataRegistryEventJS.toJson(value);
            JsonElement extraOptions = TetraDataRegistryEventJS.toJson(options);
            if (definition == null || definition.isJsonNull()) {
                this.objectData().remove("material");
                return this.self();
            }
            JsonObject material = this.nestedObject("material");
            this.tryResolveAutoKey(definition);
            this.applyIngredientDefinition(material, definition);
            this.applyIngredientOptions(material, extraOptions);
            return this.self();
        }

        public MaterialBuilder requires(Object value) {
            return (MaterialBuilder)this.setJson("requiredTools", value);
        }

        public MaterialBuilder tags(Object values) {
            return (MaterialBuilder)this.setStringArrayLike("tags", values);
        }

        public MaterialBuilder addTag(String value) {
            return (MaterialBuilder)this.addString("tags", value);
        }

        public MaterialBuilder glyphTint(String value) {
            String resolved = TetraColorUtil.resolve(value);
            this.setNested("tints", "glyph", resolved);
            JsonObject tints = this.nestedObject("tints");
            if (!tints.has("texture")) {
                tints.addProperty("texture", resolved);
            }
            return this.self();
        }

        public MaterialBuilder textureTint(String value) {
            String resolved = TetraColorUtil.resolve(value);
            this.setNested("tints", "texture", resolved);
            JsonObject tints = this.nestedObject("tints");
            if (!tints.has("glyph")) {
                tints.addProperty("glyph", resolved);
            }
            return this.self();
        }

        public MaterialBuilder tint(String value) {
            return this.tint(value, value);
        }

        public MaterialBuilder tint(String glyph, String texture) {
            JsonObject tints = this.nestedObject("tints");
            tints.addProperty("glyph", TetraColorUtil.resolve(glyph));
            tints.addProperty("texture", TetraColorUtil.resolve(texture));
            return this.self();
        }

        public MaterialBuilder textures(Object values) {
            return (MaterialBuilder)this.setStringArrayLike("textures", values);
        }

        public MaterialBuilder addTexture(String value) {
            return (MaterialBuilder)this.addString("textures", value);
        }

        public MaterialBuilder textureOverrides(Object values) {
            return (MaterialBuilder)this.setStringArrayLike("textureOverrides", values);
        }

        public MaterialBuilder addTextureOverride(String value) {
            return (MaterialBuilder)this.addString("textureOverrides", value);
        }

        public MaterialBuilder tintOverrides() {
            return this.tintOverrides(true);
        }

        public MaterialBuilder tintOverrides(boolean value) {
            return (MaterialBuilder)this.set("tintOverrides", value);
        }

        public MaterialBuilder experienceCost(double value) {
            return (MaterialBuilder)this.set("experienceCost", value);
        }

        public MaterialBuilder features(Object values) {
            return (MaterialBuilder)this.setStringArrayLike("features", values);
        }

        public MaterialBuilder addFeature(String value) {
            return (MaterialBuilder)this.addString("features", value);
        }

        @Override
        protected MaterialBuilder self() {
            return this;
        }

        private MaterialBuilder autoKeyCategory(String value) {
            this.pendingAutoKeyCategory = value;
            return this.self();
        }

        @Override
        protected JsonObject objectData() {
            String inferredCategory;
            JsonObject json = super.objectData();
            if (!json.has("key")) {
                json.addProperty("key", this.materialDataKey());
            }
            if (!json.has("category") && (inferredCategory = this.materialCategoryKey()) != null) {
                json.addProperty("category", inferredCategory);
            }
            if (!json.has("textures")) {
                JsonArray textures = new JsonArray();
                textures.add("default");
                json.add("textures", (JsonElement)textures);
            }
            if (!json.has("primary")) {
                json.addProperty("primary", (Number)0);
            }
            if (!json.has("secondary")) {
                json.addProperty("secondary", (Number)0);
            }
            if (!json.has("tertiary")) {
                json.addProperty("tertiary", (Number)0);
            }
            return json;
        }

        @Override
        protected JsonElement normalizeJson(JsonElement json) {
            if (!json.isJsonObject()) {
                throw new IllegalArgumentException("material() data must be a json object");
            }
            return json.deepCopy();
        }

        private String materialDataKey() {
            return TetraDataUtil.materialKeySuffix(this.entry.getKey());
        }

        private String materialCategoryKey() {
            String normalizedKey = TetraDataUtil.normalizeKey(this.entry.getKey());
            int separatorIndex = normalizedKey.lastIndexOf(47);
            return separatorIndex >= 0 ? normalizedKey.substring(0, separatorIndex).toLowerCase(Locale.ROOT) : null;
        }

        private MaterialBuilder integrityValues(double gain, double cost) {
            this.set("integrityGain", gain);
            this.set("integrityCost", cost);
            return this.self();
        }

        private MaterialBuilder statValues(double primary, double secondary, double tertiary) {
            this.set("primary", primary);
            this.set("secondary", secondary);
            this.set("tertiary", tertiary);
            return this.self();
        }

        private MaterialBuilder toolValues(Object level, double efficiency) {
            if (level instanceof Number) {
                Number number = (Number)level;
                this.set("toolLevel", number);
            } else {
                this.set("toolLevel", String.valueOf(level));
            }
            this.set("toolEfficiency", efficiency);
            return this.self();
        }

        private void applyIngredientDefinition(JsonObject material, JsonElement definition) {
            if (definition.isJsonPrimitive()) {
                this.applyIngredientString(material, definition.getAsString());
                material.remove("count");
                material.remove("nbt");
                return;
            }
            if (definition.isJsonArray()) {
                material.add("items", (JsonElement)this.copyStringArray(definition.getAsJsonArray(), "ingredient()"));
                material.remove("tag");
                material.remove("count");
                material.remove("nbt");
                return;
            }
            if (!definition.isJsonObject()) {
                throw new IllegalArgumentException("ingredient() expects a string, array, or object");
            }
            JsonObject json = definition.getAsJsonObject();
            if (json.has("item")) {
                this.applyIngredientString(material, json.get("item").getAsString());
            } else if (json.has("items")) {
                JsonElement items = json.get("items");
                if (!items.isJsonArray()) {
                    throw new IllegalArgumentException("ingredient().items must be an array of strings");
                }
                material.add("items", (JsonElement)this.copyStringArray(items.getAsJsonArray(), "ingredient().items"));
                material.remove("tag");
            } else if (json.has("tag")) {
                this.applyIngredientTag(material, json.get("tag").getAsString());
            } else {
                throw new IllegalArgumentException("ingredient() object must contain item, items, or tag");
            }
            this.applyIngredientField(material, "count", json.get("count"));
            this.applyIngredientField(material, "nbt", json.get("nbt"));
        }

        private void tryResolveAutoKey(JsonElement definition) {
            if (this.pendingAutoKeyCategory == null) {
                return;
            }
            if (definition.isJsonPrimitive()) {
                JsonElement primitive;
                Object object = primitive = definition.getAsJsonPrimitive().isString() ? definition : null;
                if (primitive != null) {
                    this.resolveAutoKeyFromString(primitive.getAsString());
                }
                return;
            }
            if (!definition.isJsonObject()) {
                if (definition.isJsonArray()) {
                    this.resolveAutoKeyFromItems(definition.getAsJsonArray());
                }
                return;
            }
            JsonObject json = definition.getAsJsonObject();
            JsonElement item = json.get("item");
            if (item != null && item.isJsonPrimitive() && item.getAsJsonPrimitive().isString()) {
                this.resolveAutoKeyFromItem(item.getAsString());
                return;
            }
            JsonElement tag = json.get("tag");
            if (tag != null && tag.isJsonPrimitive() && tag.getAsJsonPrimitive().isString()) {
                this.resolveAutoKeyFromTag(tag.getAsString());
                return;
            }
            JsonElement items = json.get("items");
            if (items != null && items.isJsonArray()) {
                this.resolveAutoKeyFromItems(items.getAsJsonArray());
            }
        }

        private void resolveAutoKeyFromString(String value) {
            if (value.startsWith("#")) {
                this.resolveAutoKeyFromTag(value.substring(1));
                return;
            }
            this.resolveAutoKeyFromItem(value);
        }

        private void resolveAutoKeyFromItem(String itemId) {
            ResourceLocation location = ResourceLocation.tryParse((String)itemId);
            if (location == null) {
                return;
            }
            this.applyResolvedAutoKey(this.sanitizeAutoKey(location.getPath()));
        }

        private void resolveAutoKeyFromTag(String tag) {
            ResourceLocation location = ResourceLocation.tryParse((String)tag);
            if (location == null) {
                return;
            }
            this.applyResolvedAutoKey(this.sanitizeAutoKey(location.getNamespace() + "_" + location.getPath()));
        }

        private void resolveAutoKeyFromItems(JsonArray items) {
            ArrayList<String> parts = new ArrayList<String>();
            for (JsonElement element : items) {
                String sanitized;
                ResourceLocation location;
                if (parts.size() >= 3) break;
                if (!element.isJsonPrimitive() || !element.getAsJsonPrimitive().isString() || (location = ResourceLocation.tryParse((String)element.getAsString())) == null || (sanitized = this.sanitizeAutoKey(location.getNamespace() + "_" + location.getPath())).isBlank()) continue;
                parts.add(sanitized);
            }
            if (!parts.isEmpty()) {
                this.applyResolvedAutoKey(String.join((CharSequence)"_", parts));
            }
        }

        private void applyResolvedAutoKey(String resolvedKey) {
            if (this.pendingAutoKeyCategory == null || resolvedKey == null || resolvedKey.isBlank()) {
                return;
            }
            String oldKey = this.entry.getKey();
            String newKey = TetraDataRegistryEventJS.materialPath(this.pendingAutoKeyCategory, resolvedKey);
            if (newKey.equals(oldKey)) {
                this.pendingAutoKeyCategory = null;
                return;
            }
            TetraScriptRegistry.remapEntryKey(this.scope, this.entry, oldKey, newKey);
            TetraScriptRegistry.remapTranslation(this.scope, TetraDataUtil.materialNameKey(oldKey), TetraDataUtil.materialNameKey(newKey));
            TetraScriptRegistry.remapTranslation(this.scope, TetraDataUtil.materialPrefixKey(oldKey), TetraDataUtil.materialPrefixKey(newKey));
            if (this.entry.getData() != null && this.entry.getData().isJsonObject()) {
                JsonObject json = this.entry.getData().getAsJsonObject();
                json.addProperty("key", this.materialDataKey());
                if (!json.has("category")) {
                    json.addProperty("category", this.pendingAutoKeyCategory);
                }
            }
            this.pendingAutoKeyCategory = null;
        }

        private String sanitizeAutoKey(String value) {
            String normalized = value.trim().toLowerCase(Locale.ROOT).replace('\\', '/');
            normalized = normalized.replace(':', '_').replace('/', '_');
            normalized = normalized.replaceAll("[^a-z0-9._/-]", "_");
            normalized = normalized.replaceAll("_+", "_");
            normalized = normalized.replaceAll("^[._-]+", "");
            normalized = normalized.replaceAll("[._-]+$", "");
            return normalized;
        }

        private void applyIngredientOptions(JsonObject material, JsonElement options) {
            if (options == null || options.isJsonNull()) {
                return;
            }
            if (options.isJsonPrimitive()) {
                if (!options.getAsJsonPrimitive().isNumber()) {
                    throw new IllegalArgumentException("ingredient() options must be a count number or object");
                }
                material.addProperty("count", (Number)options.getAsInt());
                return;
            }
            if (!options.isJsonObject()) {
                throw new IllegalArgumentException("ingredient() options must be a count number or object");
            }
            JsonObject json = options.getAsJsonObject();
            this.applyIngredientField(material, "count", json.get("count"));
            this.applyIngredientField(material, "nbt", json.get("nbt"));
        }

        private void applyIngredientString(JsonObject material, String value) {
            if (value.startsWith("#")) {
                this.applyIngredientTag(material, value.substring(1));
                return;
            }
            JsonArray items = new JsonArray();
            items.add(value);
            material.add("items", (JsonElement)items);
            material.remove("tag");
        }

        private void applyIngredientTag(JsonObject material, String tag) {
            material.addProperty("tag", tag);
            material.remove("items");
        }

        private void applyIngredientField(JsonObject material, String key, JsonElement value) {
            if (value == null || value.isJsonNull()) {
                material.remove(key);
                return;
            }
            if ("count".equals(key)) {
                material.addProperty(key, (Number)value.getAsInt());
            } else {
                material.addProperty(key, value.getAsString());
            }
        }

        private JsonArray copyStringArray(JsonArray source, String methodName) {
            JsonArray items = new JsonArray();
            for (JsonElement element : source) {
                if (!element.isJsonPrimitive()) {
                    throw new IllegalArgumentException(methodName + " expects strings only");
                }
                items.add(element.getAsString());
            }
            return items;
        }
    }

    public static class ModuleBuilder
    extends AbstractBuilder<ModuleBuilder> {
        protected ModuleBuilder(TetraScriptRegistry.Scope scope, ScriptedTetraEntry entry) {
            super(scope, entry);
        }

        public ModuleBuilder name(String value) {
            return this.name("zh_cn", value);
        }

        public ModuleBuilder name(String language, String value) {
            this.lang(language, TetraDataUtil.moduleNameKey(this.entry.getKey()), value);
            String materialNameKey = TetraDataUtil.moduleMaterialNameKey(this.entry.getKey());
            if (!TetraScriptRegistry.hasTranslation(this.scope, language, materialNameKey)) {
                this.lang(language, materialNameKey, value);
            }
            return this.self();
        }

        public ModuleBuilder materialName(String value) {
            return this.materialName("zh_cn", value);
        }

        public ModuleBuilder materialName(String language, String value) {
            return (ModuleBuilder)this.lang(language, TetraDataUtil.moduleMaterialNameKey(this.entry.getKey()), value);
        }

        public ModuleBuilder itemName(String value) {
            return this.itemName("zh_cn", value);
        }

        public ModuleBuilder itemName(String language, String value) {
            return (ModuleBuilder)this.lang(language, TetraDataUtil.moduleItemNameKey(this.entry.getKey()), value);
        }

        public ModuleBuilder description(String value) {
            return this.description("zh_cn", value);
        }

        public ModuleBuilder description(String language, String value) {
            return (ModuleBuilder)this.lang(language, TetraDataUtil.moduleDescriptionKey(this.entry.getKey()), value);
        }

        public ModuleBuilder prefix(String value) {
            return this.prefix("zh_cn", value);
        }

        public ModuleBuilder prefix(String language, String value) {
            return (ModuleBuilder)this.lang(language, TetraDataUtil.modulePrefixKey(this.entry.getKey()), value);
        }

        public ModuleBuilder slots(Object ... values) {
            return (ModuleBuilder)this.setStringValues("slots", values);
        }

        public ModuleBuilder addSlot(String value) {
            return (ModuleBuilder)this.addString("slots", value);
        }

        public ModuleBuilder slotSuffixes(Object ... values) {
            return (ModuleBuilder)this.setStringValues("slotSuffixes", values);
        }

        public ModuleBuilder addSlotSuffix(String value) {
            return (ModuleBuilder)this.addString("slotSuffixes", value);
        }

        public ModuleBuilder type(String value) {
            return (ModuleBuilder)this.set("type", value);
        }

        public ModuleBuilder improvements(Object ... values) {
            return (ModuleBuilder)this.setStringValues("improvements", values);
        }

        public ModuleBuilder addImprovement(String value) {
            return (ModuleBuilder)this.addString("improvements", value);
        }

        public ModuleBuilder renderLayer(String value) {
            return (ModuleBuilder)this.set("renderLayer", value);
        }

        public ModuleBuilder namePriority(String value) {
            return (ModuleBuilder)this.set("namePriority", value);
        }

        public ModuleBuilder prefixPriority(String value) {
            return (ModuleBuilder)this.set("prefixPriority", value);
        }

        public ModuleBuilder tweakKey(String value) {
            return (ModuleBuilder)this.set("tweakKey", value);
        }

        public ModuleBuilder variants(Object values) {
            JsonElement json = TetraDataRegistryEventJS.toJson(values);
            if (json == null || json.isJsonNull()) {
                return this.self();
            }
            if (!json.isJsonArray()) {
                throw new IllegalArgumentException("variants() expects an array");
            }
            this.setJson("variants", this.extractVariantTranslations(json.deepCopy()));
            return this.self();
        }

        public ModuleVariantBuilder addVariant() {
            JsonObject variant = new JsonObject();
            this.arrayData("variants").add((JsonElement)variant);
            return new ModuleVariantBuilder(this, variant);
        }

        public ModuleBuilder addVariant(Consumer<ModuleVariantBuilder> callback) {
            if (callback == null) {
                return this.self();
            }
            ModuleVariantBuilder builder = this.addVariant();
            callback.accept(builder);
            return this.self();
        }

        public ModuleBuilder variantData(Object value) {
            JsonElement json = TetraDataRegistryEventJS.toJson(value);
            if (json == null || json.isJsonNull()) {
                return this.self();
            }
            if (!json.isJsonObject()) {
                throw new IllegalArgumentException("variantData() expects an object");
            }
            this.arrayData("variants").add(this.extractVariantTranslations(json.deepCopy()));
            return this.self();
        }

        private JsonElement extractVariantTranslations(JsonElement json) {
            if (json == null || json.isJsonNull()) {
                return json;
            }
            if (json.isJsonArray()) {
                JsonArray array = json.getAsJsonArray();
                for (JsonElement element : array) {
                    if (element == null || !element.isJsonObject()) continue;
                    this.extractVariantTranslationsFromObject(element.getAsJsonObject());
                }
                return array;
            }
            if (json.isJsonObject()) {
                this.extractVariantTranslationsFromObject(json.getAsJsonObject());
            }
            return json;
        }

        private void extractVariantTranslationsFromObject(JsonObject variant) {
            String key = this.readVariantKey(variant);
            if (key == null) {
                return;
            }
            this.extractVariantTranslationField(variant, key, "name", null);
            this.extractVariantTranslationField(variant, key, "itemName", ".item_name");
            this.extractVariantTranslationField(variant, key, "description", ".description");
            this.extractVariantTranslationField(variant, key, "prefix", ".prefix");
        }

        private String readVariantKey(JsonObject variant) {
            JsonElement keyElement = variant.get("key");
            if (keyElement == null || !keyElement.isJsonPrimitive() || !keyElement.getAsJsonPrimitive().isString()) {
                return null;
            }
            String key = keyElement.getAsString().trim();
            return key.isEmpty() ? null : TetraDataUtil.normalizeVariantKey(key);
        }

        private void extractVariantTranslationField(JsonObject variant, String key, String fieldName, String suffix) {
            String translationKey;
            JsonElement translation = variant.remove(fieldName);
            if (translation == null || translation.isJsonNull()) {
                return;
            }
            if (suffix == null || suffix.isEmpty()) {
                translationKey = TetraDataUtil.variantNameKey(key);
            } else {
                switch (suffix) {
                    case ".item_name": {
                        translationKey = TetraDataUtil.variantItemNameKey(key);
                        break;
                    }
                    case ".description": {
                        translationKey = TetraDataUtil.variantDescriptionKey(key);
                        break;
                    }
                    case ".prefix": {
                        translationKey = TetraDataUtil.variantPrefixKey(key);
                        break;
                    }
                    default: {
                        translationKey = TetraDataUtil.variantNameKey(key) + suffix;
                    }
                }
            }
            if (translation.isJsonPrimitive() && translation.getAsJsonPrimitive().isString()) {
                TetraScriptRegistry.addTranslation(this.scope, "zh_cn", translationKey, translation.getAsString());
                return;
            }
            if (!translation.isJsonObject()) {
                return;
            }
            JsonObject translationObject = translation.getAsJsonObject();
            for (Map.Entry entry : translationObject.entrySet()) {
                JsonElement value = (JsonElement)entry.getValue();
                if (value == null || !value.isJsonPrimitive() || !value.getAsJsonPrimitive().isString()) continue;
                TetraScriptRegistry.addTranslation(this.scope, (String)entry.getKey(), translationKey, value.getAsString());
            }
        }

        @Override
        protected ModuleBuilder self() {
            return this;
        }

        @Override
        protected JsonElement normalizeJson(JsonElement json) {
            if (!json.isJsonObject()) {
                throw new IllegalArgumentException("module() data must be a json object");
            }
            return json.deepCopy();
        }
    }

    public static abstract class AbstractBuilder<T extends AbstractBuilder<T>> {
        protected final TetraScriptRegistry.Scope scope;
        protected final ScriptedTetraEntry entry;

        protected AbstractBuilder(TetraScriptRegistry.Scope scope, ScriptedTetraEntry entry) {
            this.scope = scope;
            this.entry = entry;
        }

        public T data(Object data) {
            return this.merge(data);
        }

        public T merge(Object data) {
            JsonElement json = TetraDataRegistryEventJS.toJson(data);
            if (json == null || json.isJsonNull()) {
                return this.self();
            }
            this.entry.merge(this.normalizeJson(json));
            return this.self();
        }

        public T mergeExisting() {
            return this.mergeExisting(true);
        }

        public T mergeExisting(boolean enabled) {
            this.entry.setMergeExisting(enabled);
            return this.self();
        }

        public T copyFrom(String sourceKey) {
            this.entry.setCopyFromKey(sourceKey);
            return this.self();
        }

        public T replace() {
            return this.replace(true);
        }

        public T replace(boolean enabled) {
            JsonObject jsonObject;
            if (this.entry.getData() == null) {
                jsonObject = new JsonObject();
                this.entry.setData((JsonElement)jsonObject);
                jsonObject = this.entry.getData().getAsJsonObject();
            } else if (this.entry.getData().isJsonObject()) {
                jsonObject = this.entry.getData().getAsJsonObject();
            } else {
                throw new IllegalStateException("replace() can only be used on object-based tetra entries");
            }
            jsonObject.addProperty("replace", Boolean.valueOf(enabled));
            return this.self();
        }

        public T lang(String fullKey, String value) {
            return this.lang("zh_cn", fullKey, value);
        }

        public T lang(String language, String fullKey, String value) {
            TetraScriptRegistry.addTranslation(this.scope, language, fullKey, value);
            return this.self();
        }

        protected JsonArray requireArray(Object values, int expectedLength, String methodName) {
            JsonElement json = TetraDataRegistryEventJS.toJson(values);
            if (json == null || !json.isJsonArray()) {
                throw new IllegalArgumentException(methodName + " expects an array");
            }
            JsonArray array = json.getAsJsonArray();
            if (array.size() != expectedLength) {
                throw new IllegalArgumentException(methodName + " expects an array of length " + expectedLength);
            }
            return array;
        }

        protected JsonObject objectData() {
            if (this.entry.getData() == null || this.entry.getData().isJsonNull()) {
                JsonObject jsonObject = new JsonObject();
                this.entry.setData((JsonElement)jsonObject);
                return this.entry.getData().getAsJsonObject();
            }
            if (!this.entry.getData().isJsonObject()) {
                throw new IllegalStateException("This tetra entry expects object-based data");
            }
            return this.entry.getData().getAsJsonObject();
        }

        protected JsonObject nestedObject(String key) {
            JsonObject jsonObject = this.objectData();
            JsonElement element = jsonObject.get(key);
            if (element == null || element.isJsonNull()) {
                JsonObject nested = new JsonObject();
                jsonObject.add(key, (JsonElement)nested);
                return nested;
            }
            if (!element.isJsonObject()) {
                throw new IllegalStateException(key + " must be a json object");
            }
            return element.getAsJsonObject();
        }

        protected JsonArray arrayData(String key) {
            JsonObject jsonObject = this.objectData();
            JsonElement element = jsonObject.get(key);
            if (element == null || element.isJsonNull()) {
                JsonArray array = new JsonArray();
                jsonObject.add(key, (JsonElement)array);
                return array;
            }
            if (!element.isJsonArray()) {
                throw new IllegalStateException(key + " must be a json array");
            }
            return element.getAsJsonArray();
        }

        protected T set(String key, String value) {
            this.objectData().addProperty(key, value);
            return this.self();
        }

        protected T set(String key, Number value) {
            this.objectData().addProperty(key, value);
            return this.self();
        }

        protected T set(String key, boolean value) {
            this.objectData().addProperty(key, Boolean.valueOf(value));
            return this.self();
        }

        protected T setJson(String key, Object value) {
            JsonElement json = TetraDataRegistryEventJS.toJson(value);
            if (json == null || json.isJsonNull()) {
                this.objectData().remove(key);
            } else {
                this.objectData().add(key, json.deepCopy());
            }
            return this.self();
        }

        protected T setEntries(String key, String methodName, Object ... values) {
            JsonObject json = this.normalizeEntries(methodName, values);
            if (json == null) {
                this.objectData().remove(key);
            } else {
                this.objectData().add(key, (JsonElement)json);
            }
            return this.self();
        }

        protected T setNested(String parentKey, String key, String value) {
            this.nestedObject(parentKey).addProperty(key, value);
            return this.self();
        }

        protected T setNested(String parentKey, String key, Number value) {
            this.nestedObject(parentKey).addProperty(key, value);
            return this.self();
        }

        protected T setNested(String parentKey, String key, boolean value) {
            this.nestedObject(parentKey).addProperty(key, Boolean.valueOf(value));
            return this.self();
        }

        protected T setNestedJson(String parentKey, String key, Object value) {
            JsonElement json = TetraDataRegistryEventJS.toJson(value);
            JsonObject nested = this.nestedObject(parentKey);
            if (json == null || json.isJsonNull()) {
                nested.remove(key);
            } else {
                nested.add(key, json.deepCopy());
            }
            return this.self();
        }

        protected T setStringArrayLike(String key, Object values) {
            JsonElement json = TetraDataRegistryEventJS.toJson(values);
            if (json == null || json.isJsonNull()) {
                this.objectData().remove(key);
                return this.self();
            }
            JsonArray array = new JsonArray();
            if (json.isJsonPrimitive()) {
                array.add(json.getAsString());
            } else if (json.isJsonArray()) {
                json.getAsJsonArray().forEach(arg_0 -> ((JsonArray)array).add(arg_0));
            } else {
                throw new IllegalArgumentException(key + "() expects a string or array of strings");
            }
            this.objectData().add(key, (JsonElement)array);
            return this.self();
        }

        protected T setStringValues(String key, Object ... values) {
            JsonElement json = TetraDataRegistryEventJS.normalizeStringValues(values);
            if (json == null || json.isJsonNull()) {
                this.objectData().remove(key);
                return this.self();
            }
            this.objectData().add(key, (JsonElement)json.getAsJsonArray());
            return this.self();
        }

        protected T addString(String key, String value) {
            this.arrayData(key).add(value);
            return this.self();
        }

        private JsonObject normalizeEntries(String methodName, Object ... values) {
            JsonArray array;
            JsonElement json;
            if (values == null || values.length == 0) {
                return null;
            }
            if (values.length == 1) {
                json = TetraDataRegistryEventJS.toJson(values[0]);
            } else {
                array = new JsonArray();
                for (Object value : values) {
                    array.add(TetraDataRegistryEventJS.toJson(value));
                }
                json = array;
            }
            if (json == null || json.isJsonNull()) {
                return null;
            }
            if (json.isJsonObject()) {
                return json.getAsJsonObject().deepCopy();
            }
            if (!json.isJsonArray()) {
                throw new IllegalArgumentException(methodName + " expects an object or entry arrays");
            }
            array = json.getAsJsonArray();
            JsonObject result = new JsonObject();
            if (this.isEntryPair(array)) {
                result.add(array.get(0).getAsString(), array.get(1).deepCopy());
                return result;
            }
            for (JsonElement element : array) {
                if (!element.isJsonArray()) {
                    throw new IllegalArgumentException(methodName + " expects entries like ['key', value]");
                }
                JsonArray entry = element.getAsJsonArray();
                if (!this.isEntryPair(entry)) {
                    throw new IllegalArgumentException(methodName + " expects entries like ['key', value]");
                }
                result.add(entry.get(0).getAsString(), entry.get(1).deepCopy());
            }
            return result;
        }

        private boolean isEntryPair(JsonArray array) {
            return array.size() == 2 && array.get(0).isJsonPrimitive() && array.get(0).getAsJsonPrimitive().isString();
        }

        protected abstract T self();

        protected abstract JsonElement normalizeJson(JsonElement var1);
    }

    public static class ImprovementBuilder
    extends AbstractBuilder<ImprovementBuilder> {
        private JsonObject currentObject;

        protected ImprovementBuilder(TetraScriptRegistry.Scope scope, ScriptedTetraEntry entry) {
            super(scope, entry);
        }

        @Override
        public ImprovementBuilder merge(Object data) {
            String previousKey = this.improvementTranslationKey(false);
            JsonElement json = TetraDataRegistryEventJS.toJson(data);
            if (json != null && !json.isJsonNull() && json.isJsonObject()) {
                JsonObject base = this.objectData();
                JsonObject merged = JsonMergeUtil.merge((JsonElement)base, json).getAsJsonObject();
                TetraDataRegistryEventJS.replaceContents(base, merged);
            } else {
                super.merge(data);
            }
            this.remapImprovementTranslations(previousKey, this.improvementTranslationKey(false));
            return this.self();
        }

        public ImprovementBuilder name(String value) {
            return this.name("zh_cn", value);
        }

        public ImprovementBuilder name(String language, String value) {
            return (ImprovementBuilder)this.lang(language, TetraDataUtil.improvementNameKey(this.improvementTranslationKey()), value);
        }

        public ImprovementBuilder description(String value) {
            return this.description("zh_cn", value);
        }

        public ImprovementBuilder description(String language, String value) {
            return (ImprovementBuilder)this.lang(language, TetraDataUtil.improvementDescriptionKey(this.improvementTranslationKey()), value);
        }

        public ImprovementBuilder path(String value) {
            String previousKey = this.improvementTranslationKey(false);
            String oldKey = this.entry.getKey();
            boolean hadMeaningfulData = this.hasMeaningfulImprovementData(this.entry.getData());
            String newKey = TetraDataUtil.normalizeFolderKey(value);
            TetraScriptRegistry.remapEntryKey(this.scope, this.entry, oldKey, newKey);
            if (!hadMeaningfulData && this.entry.getData() != null && !this.entry.getData().isJsonNull()) {
                this.currentObject = this.appendImprovementObject();
            }
            this.remapImprovementTranslations(previousKey, this.improvementTranslationKey(false));
            return this.self();
        }

        public ImprovementBuilder improvementKey(String value) {
            String previousKey = this.improvementTranslationKey(false);
            this.objectData().addProperty("key", TetraDataUtil.normalizeImprovementKey(value));
            this.remapImprovementTranslations(previousKey, this.improvementTranslationKey(false));
            return this.self();
        }

        public ImprovementBuilder level(int value) {
            return (ImprovementBuilder)this.set("level", value);
        }

        public ImprovementBuilder group(String value) {
            return (ImprovementBuilder)this.set("group", value);
        }

        public ImprovementBuilder durability(double value) {
            return (ImprovementBuilder)this.set("durability", value);
        }

        public ImprovementBuilder durabilityMultiplier(double value) {
            return (ImprovementBuilder)this.set("durabilityMultiplier", value);
        }

        public ImprovementBuilder integrity(double value) {
            return (ImprovementBuilder)this.set("integrity", value);
        }

        public ImprovementBuilder magicCapacity(double value) {
            return (ImprovementBuilder)this.set("magicCapacity", value);
        }

        public ImprovementBuilder attributes(Object ... values) {
            return (ImprovementBuilder)this.setEntries("attributes", "attributes()", values);
        }

        public ImprovementBuilder tools(Object value) {
            return (ImprovementBuilder)this.setJson("tools", value);
        }

        public ImprovementBuilder tool(String key, Object level) {
            return this.tool(key, level, null);
        }

        public ImprovementBuilder tool(String key, Object level, Double efficiency) {
            TetraDataRegistryEventJS.setTieredValue(this.nestedObject("tools"), key, level, efficiency);
            return this.self();
        }

        public ImprovementBuilder effects(Object ... values) {
            return (ImprovementBuilder)this.setEntries("effects", "effects()", values);
        }

        public ImprovementBuilder aspects(Object value) {
            return (ImprovementBuilder)this.setJson("aspects", value);
        }

        public ImprovementBuilder tags(Object ... values) {
            return (ImprovementBuilder)this.setStringValues("tags", values);
        }

        public ImprovementBuilder addTag(String value) {
            return (ImprovementBuilder)this.addString("tags", value);
        }

        public ImprovementBuilder rarity(String value) {
            return (ImprovementBuilder)this.set("rarity", value);
        }

        public ImprovementBuilder prefixPriority(String value) {
            return (ImprovementBuilder)this.set("prefixPriority", value);
        }

        public ImprovementBuilder materials(Object ... values) {
            return (ImprovementBuilder)this.setStringValues("materials", values);
        }

        public ImprovementBuilder addMaterial(String value) {
            return (ImprovementBuilder)this.addString("materials", value);
        }

        public ImprovementBuilder models(Object values) {
            return (ImprovementBuilder)this.setJson("models", values);
        }

        public ImprovementBuilder addModel(Object value) {
            this.arrayData("models").add(TetraDataRegistryEventJS.toJson(value));
            return this.self();
        }

        public ImprovementBuilder addModel(String location) {
            return this.addModel(TetraDataRegistryEventJS.modelRef(location, null));
        }

        public ImprovementBuilder addModel(String location, String renderLayer) {
            return this.addModel(TetraDataRegistryEventJS.modelRef(location, renderLayer));
        }

        public ModuleExtractBuilder<ImprovementBuilder> extract() {
            JsonObject extract = this.nestedObject("extract");
            return new ModuleExtractBuilder<ImprovementBuilder>(this, extract);
        }

        public ImprovementBuilder extract(Consumer<ModuleExtractBuilder<ImprovementBuilder>> callback) {
            if (callback == null) {
                return this.self();
            }
            ModuleExtractBuilder<ImprovementBuilder> builder = this.extract();
            callback.accept(builder);
            return this.self();
        }

        public ImprovementBuilder extractData(Object value) {
            return (ImprovementBuilder)this.setJson("extract", value);
        }

        public ImprovementBuilder atLevel(int value, Consumer<ImprovementLevelBuilder> callback) {
            if (callback == null) {
                return this.self();
            }
            ImprovementLevelBuilder builder = this.atLevel(value);
            callback.accept(builder);
            return this.self();
        }

        public ImprovementLevelBuilder atLevel(int value) {
            JsonObject levelData = this.findOrCreateLevelData(value);
            return new ImprovementLevelBuilder(this, levelData);
        }

        private JsonObject findOrCreateLevelData(int value) {
            JsonArray array = this.levelArrayData();
            for (int i = 1; i < array.size(); ++i) {
                JsonObject object = TetraDataUtil.jsonObject(array.get(i));
                if (object == null || !object.has("level") || object.get("level").getAsInt() != value) continue;
                return object;
            }
            JsonObject object = new JsonObject();
            object.addProperty("level", (Number)value);
            array.add((JsonElement)object);
            return object;
        }

        private JsonArray levelArrayData() {
            this.objectData();
            return this.entry.getData().getAsJsonArray();
        }

        @Override
        protected ImprovementBuilder self() {
            return this;
        }

        @Override
        protected JsonObject objectData() {
            if (this.currentObject != null) {
                return this.currentObject;
            }
            if (this.entry.getData() == null || this.entry.getData().isJsonNull()) {
                JsonArray array = new JsonArray();
                JsonObject object = new JsonObject();
                array.add((JsonElement)object);
                this.entry.setData((JsonElement)array);
                this.currentObject = this.entry.getData().getAsJsonArray().get(0).getAsJsonObject();
                return this.currentObject;
            }
            if (this.entry.getData().isJsonObject()) {
                JsonObject object = this.entry.getData().getAsJsonObject();
                JsonArray array = new JsonArray();
                array.add((JsonElement)object.deepCopy());
                this.entry.setData((JsonElement)array);
                return this.entry.getData().getAsJsonArray().get(0).getAsJsonObject();
            }
            if (!this.entry.getData().isJsonArray()) {
                throw new IllegalStateException("Improvement entries expect object or array-based data");
            }
            JsonArray array = this.entry.getData().getAsJsonArray();
            if (array.size() == 0 || !array.get(0).isJsonObject()) {
                JsonObject object = new JsonObject();
                if (array.size() == 0) {
                    array.add((JsonElement)object);
                } else {
                    array.set(0, (JsonElement)object);
                }
                return object;
            }
            return array.get(0).getAsJsonObject();
        }

        private JsonObject appendImprovementObject() {
            if (this.entry.getData().isJsonObject()) {
                JsonArray array = new JsonArray();
                array.add((JsonElement)this.entry.getData().getAsJsonObject().deepCopy());
                this.entry.setData((JsonElement)array);
            }
            if (!this.entry.getData().isJsonArray()) {
                throw new IllegalStateException("Improvement entries expect object or array-based data");
            }
            JsonObject object = new JsonObject();
            this.entry.getData().getAsJsonArray().add((JsonElement)object);
            return object;
        }

        private boolean hasMeaningfulImprovementData(JsonElement data) {
            if (data == null || data.isJsonNull()) {
                return false;
            }
            if (data.isJsonObject()) {
                return !data.getAsJsonObject().entrySet().isEmpty();
            }
            if (data.isJsonArray()) {
                for (JsonElement element : data.getAsJsonArray()) {
                    if (!this.hasMeaningfulImprovementData(element)) continue;
                    return true;
                }
                return false;
            }
            return true;
        }

        @Override
        protected JsonElement normalizeJson(JsonElement json) {
            if (json.isJsonArray()) {
                return json.deepCopy();
            }
            if (json.isJsonObject()) {
                JsonArray array = new JsonArray();
                array.add(json.deepCopy());
                return array;
            }
            throw new IllegalArgumentException("improvement() data must be a json object or array");
        }

        private String improvementTranslationKey() {
            return this.improvementTranslationKey(true);
        }

        private String improvementTranslationKey(boolean ensureObject) {
            String value;
            JsonElement keyElement;
            JsonObject base = this.improvementBaseObject(ensureObject);
            if (base != null && (keyElement = base.get("key")) != null && keyElement.isJsonPrimitive() && keyElement.getAsJsonPrimitive().isString() && !(value = keyElement.getAsString().trim()).isEmpty()) {
                return TetraDataUtil.normalizeKey(value);
            }
            return this.entry.getKey();
        }

        private JsonObject improvementBaseObject(boolean ensureObject) {
            JsonElement data = this.entry.getData();
            if (data == null || data.isJsonNull()) {
                return ensureObject ? this.objectData() : null;
            }
            if (data.isJsonObject()) {
                return data.getAsJsonObject();
            }
            if (!data.isJsonArray()) {
                return ensureObject ? this.objectData() : null;
            }
            JsonArray array = data.getAsJsonArray();
            if (array.size() == 0 || !array.get(0).isJsonObject()) {
                return ensureObject ? this.objectData() : null;
            }
            return array.get(0).getAsJsonObject();
        }

        private void remapImprovementTranslations(String previousKey, String nextKey) {
            if (previousKey == null || nextKey == null || previousKey.equals(nextKey)) {
                return;
            }
            TetraScriptRegistry.remapTranslation(this.scope, TetraDataUtil.improvementNameKey(previousKey), TetraDataUtil.improvementNameKey(nextKey));
            TetraScriptRegistry.remapTranslation(this.scope, TetraDataUtil.improvementDescriptionKey(previousKey), TetraDataUtil.improvementDescriptionKey(nextKey));
        }
    }

    public static class SchematicBuilder
    extends AbstractBuilder<SchematicBuilder> {
        protected SchematicBuilder(TetraScriptRegistry.Scope scope, ScriptedTetraEntry entry) {
            super(scope, entry);
        }

        public SchematicBuilder name(String value) {
            return this.name("zh_cn", value);
        }

        public SchematicBuilder name(String language, String value) {
            return (SchematicBuilder)this.lang(language, TetraDataUtil.schematicNameKey(this.entry.getKey()), value);
        }

        public SchematicBuilder description(String value) {
            return this.description("zh_cn", value);
        }

        public SchematicBuilder description(String language, String value) {
            return (SchematicBuilder)this.lang(language, TetraDataUtil.schematicDescriptionKey(this.entry.getKey()), value);
        }

        public SchematicBuilder slot(int slotNumber, String value) {
            return this.slot("zh_cn", slotNumber, value);
        }

        public SchematicBuilder slot(String language, int slotNumber, String value) {
            return (SchematicBuilder)this.lang(language, TetraDataUtil.schematicSlotKey(this.entry.getKey(), slotNumber), value);
        }

        public SchematicBuilder localizationKey(String value) {
            return (SchematicBuilder)this.set("localizationKey", value);
        }

        public SchematicBuilder slots(Object ... values) {
            return (SchematicBuilder)this.setStringValues("slots", values);
        }

        public SchematicBuilder addSlot(String value) {
            return (SchematicBuilder)this.addString("slots", value);
        }

        public SchematicBuilder keySuffixes(Object ... values) {
            return (SchematicBuilder)this.setStringValues("keySuffixes", values);
        }

        public SchematicBuilder addKeySuffix(String value) {
            return (SchematicBuilder)this.addString("keySuffixes", value);
        }

        public SchematicBuilder materialSlotCount(int value) {
            return (SchematicBuilder)this.set("materialSlotCount", value);
        }

        public SchematicBuilder repair() {
            return this.repair(true);
        }

        public SchematicBuilder repair(boolean value) {
            return (SchematicBuilder)this.set("repair", value);
        }

        public SchematicBuilder hone() {
            return this.hone(true);
        }

        public SchematicBuilder hone(boolean value) {
            return (SchematicBuilder)this.set("hone", value);
        }

        public SchematicRequirementBuilder<SchematicBuilder> requirement() {
            JsonObject requirement = new JsonObject();
            this.objectData().add("requirement", (JsonElement)requirement);
            return new SchematicRequirementBuilder<SchematicBuilder>(this, requirement);
        }

        public SchematicBuilder requirement(Consumer<SchematicRequirementBuilder<SchematicBuilder>> callback) {
            if (callback == null) {
                return this.self();
            }
            JsonObject requirement = new JsonObject();
            SchematicRequirementBuilder<SchematicBuilder> builder = new SchematicRequirementBuilder<SchematicBuilder>(this, requirement);
            callback.accept(builder);
            if (!requirement.entrySet().isEmpty()) {
                this.objectData().add("requirement", (JsonElement)requirement);
            }
            return this.self();
        }

        public SchematicBuilder materialRevealSlot(int value) {
            return (SchematicBuilder)this.set("materialRevealSlot", value);
        }

        public SchematicBuilder displayType(String value) {
            return (SchematicBuilder)this.set("displayType", value);
        }

        public SchematicBuilder rarity(String value) {
            return (SchematicBuilder)this.set("rarity", value);
        }

        public SchematicBuilder glyph(Object value) {
            return (SchematicBuilder)this.setJson("glyph", value);
        }

        public SchematicBuilder glyph(int textureX, int textureY) {
            JsonObject glyph = new JsonObject();
            glyph.addProperty("textureX", (Number)textureX);
            glyph.addProperty("textureY", (Number)textureY);
            return this.glyph(glyph);
        }

        public SchematicBuilder glyph(String textureLocation, int textureX, int textureY) {
            JsonObject glyph = new JsonObject();
            glyph.addProperty("textureX", (Number)textureX);
            glyph.addProperty("textureY", (Number)textureY);
            glyph.addProperty("textureLocation", textureLocation);
            return this.glyph(glyph);
        }

        public SchematicBuilder translation(Object value) {
            return (SchematicBuilder)this.setJson("translation", value);
        }

        public SchematicBuilder preview(String value) {
            return (SchematicBuilder)this.set("preview", value);
        }

        public SchematicBuilder applicableMaterials(Object ... values) {
            return (SchematicBuilder)this.setStringValues("applicableMaterials", values);
        }

        public SchematicBuilder addApplicableMaterial(String value) {
            return (SchematicBuilder)this.addString("applicableMaterials", value);
        }

        public SchematicBuilder outcomes(Object value) {
            JsonElement json = TetraDataRegistryEventJS.toJson(value);
            if (json == null || json.isJsonNull()) {
                this.objectData().remove("outcomes");
                return this.self();
            }
            if (!json.isJsonArray()) {
                throw new IllegalArgumentException("outcomes() expects an array");
            }
            this.objectData().add("outcomes", json.deepCopy());
            return this.self();
        }

        public SchematicOutcomeBuilder addOutcome() {
            JsonObject outcome = new JsonObject();
            this.arrayData("outcomes").add((JsonElement)outcome);
            return new SchematicOutcomeBuilder(this, outcome);
        }

        public SchematicBuilder addOutcome(Consumer<SchematicOutcomeBuilder> callback) {
            if (callback == null) {
                return this.self();
            }
            SchematicOutcomeBuilder builder = this.addOutcome();
            callback.accept(builder);
            return this.self();
        }

        public SchematicBuilder outcomeData(Object value) {
            JsonElement json = TetraDataRegistryEventJS.toJson(value);
            if (json == null || json.isJsonNull()) {
                return this.self();
            }
            if (!json.isJsonObject()) {
                throw new IllegalArgumentException("outcomeData() expects an object");
            }
            this.arrayData("outcomes").add(json.deepCopy());
            return this.self();
        }

        @Override
        protected SchematicBuilder self() {
            return this;
        }

        @Override
        protected JsonElement normalizeJson(JsonElement json) {
            if (!json.isJsonObject()) {
                throw new IllegalArgumentException("schematic() data must be a json object");
            }
            return json.deepCopy();
        }
    }

    public static class GenericBuilder
    extends AbstractBuilder<GenericBuilder> {
        protected GenericBuilder(TetraScriptRegistry.Scope scope, ScriptedTetraEntry entry) {
            super(scope, entry);
        }

        @Override
        protected GenericBuilder self() {
            return this;
        }

        @Override
        protected JsonElement normalizeJson(JsonElement json) {
            return json.deepCopy();
        }
    }

    public static class SchematicRequirementBuilder<P> {
        private final P parent;
        private final JsonObject data;

        protected SchematicRequirementBuilder(P parent, JsonObject data) {
            this.parent = parent;
            this.data = data;
        }

        public SchematicRequirementBuilder<P> data(Object value) {
            return this.merge(value);
        }

        public SchematicRequirementBuilder<P> merge(Object value) {
            JsonElement json = TetraDataRegistryEventJS.toJson(value);
            if (json == null || json.isJsonNull()) {
                return this;
            }
            if (!json.isJsonObject()) {
                throw new IllegalArgumentException("requirement().merge() expects an object");
            }
            JsonObject merged = JsonMergeUtil.merge((JsonElement)this.data, json).getAsJsonObject();
            this.replaceContents(this.data, merged);
            return this;
        }

        public SchematicRequirementBuilder<P> type(String value) {
            this.data.addProperty("type", value);
            return this;
        }

        public SchematicRequirementBuilder<P> and() {
            return this.type("tetra:and");
        }

        public SchematicRequirementBuilder<P> or() {
            return this.type("tetra:or");
        }

        public SchematicRequirementBuilder<P> not() {
            return this.type("tetra:not");
        }

        public SchematicRequirementBuilder<P> never() {
            return this.type("tetra:never");
        }

        public SchematicRequirementBuilder<P> slot(String value) {
            this.type("tetra:slot");
            this.data.addProperty("slot", value);
            return this;
        }

        public SchematicRequirementBuilder<P> module(String value) {
            this.type("tetra:module");
            this.data.addProperty("module", value);
            return this;
        }

        public SchematicRequirementBuilder<P> module(String value, String variant) {
            this.module(value);
            return this.variant(variant);
        }

        public SchematicRequirementBuilder<P> variant(String value) {
            this.data.addProperty("variant", value);
            return this;
        }

        public SchematicRequirementBuilder<P> material(String value) {
            this.data.addProperty("material", value);
            return this;
        }

        public SchematicRequirementBuilder<P> acceptsImprovement(String value) {
            this.type("tetra:accepts_improvement");
            this.data.addProperty("improvement", value);
            return this;
        }

        public SchematicRequirementBuilder<P> acceptsImprovement(String value, int level) {
            return this.acceptsImprovement(value).level(level);
        }

        public SchematicRequirementBuilder<P> hasImprovement(String value) {
            this.type("tetra:improvement");
            this.data.addProperty("improvement", value);
            return this;
        }

        public SchematicRequirementBuilder<P> hasImprovement(String value, Object level) {
            return this.hasImprovement(value).level(level);
        }

        public SchematicRequirementBuilder<P> aspect(String value) {
            this.type("tetra:aspect");
            this.data.addProperty("aspect", value);
            return this;
        }

        public SchematicRequirementBuilder<P> aspect(String value, Object level) {
            return this.aspect(value).level(level);
        }

        public SchematicRequirementBuilder<P> featureFlag(String value) {
            this.type("tetra:feature_flag");
            this.data.addProperty("feature", value);
            this.data.addProperty("flag", value);
            return this;
        }

        public SchematicRequirementBuilder<P> perk(String value) {
            this.type("tetra:perk");
            this.data.addProperty("perk", value);
            return this;
        }

        public SchematicRequirementBuilder<P> perk(String value, Object level) {
            return this.perk(value).level(level);
        }

        public SchematicRequirementBuilder<P> locked(String value) {
            this.type("tetra:locked");
            this.data.addProperty("key", value);
            return this;
        }

        public SchematicRequirementBuilder<P> flag(String value) {
            this.data.addProperty("flag", value);
            return this;
        }

        public SchematicRequirementBuilder<P> feature(String value) {
            this.data.addProperty("feature", value);
            return this;
        }

        public SchematicRequirementBuilder<P> key(String value) {
            this.data.addProperty("key", value);
            return this;
        }

        public SchematicRequirementBuilder<P> improvement(String value) {
            this.data.addProperty("improvement", value);
            return this;
        }

        public SchematicRequirementBuilder<P> level(Object value) {
            this.setJson("level", value);
            return this;
        }

        public SchematicRequirementBuilder<P> level(int value) {
            this.data.addProperty("level", (Number)value);
            return this;
        }

        public SchematicRequirementBuilder<P> level(int min, int max) {
            JsonObject predicate = new JsonObject();
            predicate.addProperty("min", (Number)min);
            predicate.addProperty("max", (Number)max);
            this.data.add("level", (JsonElement)predicate);
            return this;
        }

        public SchematicRequirementBuilder<P> requirements(Object value) {
            this.setJson("requirements", value);
            return this;
        }

        public SchematicRequirementBuilder<SchematicRequirementBuilder<P>> addRequirement() {
            JsonObject requirement = new JsonObject();
            this.arrayData("requirements").add((JsonElement)requirement);
            return new SchematicRequirementBuilder<SchematicRequirementBuilder<P>>(this, requirement);
        }

        public SchematicRequirementBuilder<P> addRequirement(Consumer<SchematicRequirementBuilder<SchematicRequirementBuilder<P>>> callback) {
            if (callback == null) {
                return this;
            }
            JsonObject requirement = new JsonObject();
            SchematicRequirementBuilder<SchematicRequirementBuilder<P>> builder = new SchematicRequirementBuilder<SchematicRequirementBuilder<P>>(this, requirement);
            callback.accept(builder);
            if (!requirement.entrySet().isEmpty()) {
                this.arrayData("requirements").add((JsonElement)requirement);
            }
            return this;
        }

        public SchematicRequirementBuilder<SchematicRequirementBuilder<P>> requirement() {
            JsonObject requirement = new JsonObject();
            this.data.add("requirement", (JsonElement)requirement);
            return new SchematicRequirementBuilder<SchematicRequirementBuilder<P>>(this, requirement);
        }

        public SchematicRequirementBuilder<P> requirement(Consumer<SchematicRequirementBuilder<SchematicRequirementBuilder<P>>> callback) {
            if (callback == null) {
                return this;
            }
            JsonObject requirement = new JsonObject();
            SchematicRequirementBuilder<SchematicRequirementBuilder<P>> builder = new SchematicRequirementBuilder<SchematicRequirementBuilder<P>>(this, requirement);
            callback.accept(builder);
            if (!requirement.entrySet().isEmpty()) {
                this.data.add("requirement", (JsonElement)requirement);
            }
            return this;
        }

        public P done() {
            return this.parent;
        }

        private JsonArray arrayData(String key) {
            JsonElement element = this.data.get(key);
            if (element == null || element.isJsonNull()) {
                JsonArray array = new JsonArray();
                this.data.add(key, (JsonElement)array);
                return array;
            }
            if (!element.isJsonArray()) {
                throw new IllegalStateException(key + " must be a json array");
            }
            return element.getAsJsonArray();
        }

        private void setJson(String key, Object value) {
            JsonElement json = TetraDataRegistryEventJS.toJson(value);
            if (json == null || json.isJsonNull()) {
                this.data.remove(key);
            } else {
                this.data.add(key, json.deepCopy());
            }
        }

        private void replaceContents(JsonObject target, JsonObject replacement) {
            JsonArray keys = new JsonArray();
            target.keySet().forEach(arg_0 -> ((JsonArray)keys).add(arg_0));
            for (JsonElement key : keys) {
                target.remove(key.getAsString());
            }
            replacement.entrySet().forEach(entry -> target.add((String)entry.getKey(), ((JsonElement)entry.getValue()).deepCopy()));
        }
    }

    public static class SchematicOutcomeBuilder {
        private final SchematicBuilder parent;
        private final JsonObject data;

        protected SchematicOutcomeBuilder(SchematicBuilder parent, JsonObject data) {
            this.parent = parent;
            this.data = data;
        }

        public SchematicOutcomeBuilder data(Object value) {
            return this.merge(value);
        }

        public SchematicOutcomeBuilder merge(Object value) {
            JsonElement json = TetraDataRegistryEventJS.toJson(value);
            if (json == null || json.isJsonNull()) {
                return this;
            }
            if (!json.isJsonObject()) {
                throw new IllegalArgumentException("addOutcome().merge() expects an object");
            }
            JsonObject merged = JsonMergeUtil.merge((JsonElement)this.data, json).getAsJsonObject();
            this.replaceContents(this.data, merged);
            return this;
        }

        public SchematicOutcomeBuilder materials(Object ... values) {
            JsonElement json = TetraDataRegistryEventJS.normalizeStringValues(values);
            if (json == null || json.isJsonNull()) {
                this.data.remove("materials");
                return this;
            }
            JsonArray array = new JsonArray();
            if (json.isJsonPrimitive()) {
                array.add(json.getAsString());
            } else if (json.isJsonArray()) {
                for (JsonElement element : json.getAsJsonArray()) {
                    if (!element.isJsonPrimitive()) {
                        throw new IllegalArgumentException("materials() expects strings only");
                    }
                    array.add(element.getAsString());
                }
            } else {
                throw new IllegalArgumentException("materials() expects a string or array of strings");
            }
            this.data.add("materials", (JsonElement)array);
            return this;
        }

        public SchematicOutcomeBuilder addMaterialReference(String value) {
            this.arrayData("materials").add(value);
            return this;
        }

        public SchematicOutcomeBuilder item(String value) {
            return this.ingredient(value);
        }

        public SchematicOutcomeBuilder item(String value, int count) {
            return this.ingredient(value, count);
        }

        public SchematicOutcomeBuilder items(Object values) {
            return this.ingredient(values);
        }

        public SchematicOutcomeBuilder itemTag(String value) {
            return this.ingredient("#" + value);
        }

        public SchematicOutcomeBuilder ingredientCount(int value) {
            this.nestedObject("material").addProperty("count", (Number)value);
            return this;
        }

        public SchematicOutcomeBuilder ingredientNbt(Object value) {
            JsonObject material = this.nestedObject("material");
            JsonElement json = TetraDataRegistryEventJS.toJson(value);
            if (json == null || json.isJsonNull()) {
                material.remove("nbt");
            } else {
                material.add("nbt", json.deepCopy());
            }
            return this;
        }

        public SchematicOutcomeBuilder ingredient(String value) {
            return this.ingredient((Object)value, null);
        }

        public SchematicOutcomeBuilder ingredient(String value, int count) {
            JsonObject options = new JsonObject();
            options.addProperty("count", (Number)count);
            return this.ingredient(value, (Object)options);
        }

        public SchematicOutcomeBuilder ingredient(String value, Object options) {
            return this.ingredient((Object)value, options);
        }

        public SchematicOutcomeBuilder ingredient(Object value) {
            return this.ingredient(value, null);
        }

        public SchematicOutcomeBuilder ingredient(Object value, Object options) {
            JsonElement definition = TetraDataRegistryEventJS.toJson(value);
            JsonElement extraOptions = TetraDataRegistryEventJS.toJson(options);
            if (definition == null || definition.isJsonNull()) {
                this.data.remove("material");
                return this;
            }
            JsonObject material = this.nestedObject("material");
            this.applyIngredientDefinition(material, definition);
            this.applyIngredientOptions(material, extraOptions);
            return this;
        }

        public SchematicOutcomeBuilder materialSlot(int value) {
            this.data.addProperty("materialSlot", (Number)value);
            return this;
        }

        public SchematicOutcomeBuilder experienceCost(double value) {
            this.data.addProperty("experienceCost", (Number)value);
            return this;
        }

        public SchematicOutcomeBuilder countFactor(double value) {
            this.data.addProperty("countFactor", (Number)value);
            return this;
        }

        public SchematicOutcomeBuilder countOffset(double value) {
            this.data.addProperty("countOffset", (Number)value);
            return this;
        }

        public SchematicOutcomeBuilder toolFactor(double value) {
            this.data.addProperty("toolFactor", (Number)value);
            return this;
        }

        public SchematicOutcomeBuilder toolOffset(double value) {
            this.data.addProperty("toolOffset", (Number)value);
            return this;
        }

        public SchematicOutcomeBuilder requiredTools(Object value) {
            this.setJson("requiredTools", value);
            return this;
        }

        public SchematicOutcomeBuilder requiredTool(String key, Object value) {
            JsonObject tools = this.nestedObject("requiredTools");
            if (value instanceof Number) {
                Number number = (Number)value;
                tools.addProperty(key, number);
            } else {
                tools.addProperty(key, String.valueOf(value));
            }
            return this;
        }

        public SchematicOutcomeBuilder module(String value, String variant) {
            this.moduleKey(value);
            return this.moduleVariant(variant);
        }

        public SchematicOutcomeBuilder moduleKey(String value) {
            this.data.addProperty("moduleKey", value);
            return this;
        }

        public SchematicOutcomeBuilder moduleVariant(String value) {
            this.data.addProperty("moduleVariant", value);
            return this;
        }

        public SchematicOutcomeBuilder improvements(Object value) {
            this.setJson("improvements", value);
            return this;
        }

        public SchematicOutcomeBuilder improvement(String key, int level) {
            this.nestedObject("improvements").addProperty(key, (Number)level);
            return this;
        }

        public SchematicOutcomeBuilder hidden() {
            return this.hidden(true);
        }

        public SchematicOutcomeBuilder hidden(boolean value) {
            this.data.addProperty("hidden", Boolean.valueOf(value));
            return this;
        }

        public SchematicBuilder done() {
            return this.parent;
        }

        private JsonObject nestedObject(String key) {
            JsonElement element = this.data.get(key);
            if (element == null || element.isJsonNull()) {
                JsonObject nested = new JsonObject();
                this.data.add(key, (JsonElement)nested);
                return nested;
            }
            if (!element.isJsonObject()) {
                throw new IllegalStateException(key + " must be a json object");
            }
            return element.getAsJsonObject();
        }

        private JsonArray arrayData(String key) {
            JsonElement element = this.data.get(key);
            if (element == null || element.isJsonNull()) {
                JsonArray array = new JsonArray();
                this.data.add(key, (JsonElement)array);
                return array;
            }
            if (!element.isJsonArray()) {
                throw new IllegalStateException(key + " must be a json array");
            }
            return element.getAsJsonArray();
        }

        private void setJson(String key, Object value) {
            JsonElement json = TetraDataRegistryEventJS.toJson(value);
            if (json == null || json.isJsonNull()) {
                this.data.remove(key);
            } else {
                this.data.add(key, json.deepCopy());
            }
        }

        private void applyIngredientDefinition(JsonObject material, JsonElement definition) {
            if (definition.isJsonPrimitive()) {
                this.applyIngredientString(material, definition.getAsString());
                material.remove("count");
                material.remove("nbt");
                return;
            }
            if (definition.isJsonArray()) {
                material.add("items", (JsonElement)this.copyStringArray(definition.getAsJsonArray(), "addOutcome().ingredient()"));
                material.remove("tag");
                material.remove("count");
                material.remove("nbt");
                return;
            }
            if (!definition.isJsonObject()) {
                throw new IllegalArgumentException("addOutcome().ingredient() expects a string, array, or object");
            }
            JsonObject json = definition.getAsJsonObject();
            if (json.has("item")) {
                this.applyIngredientString(material, json.get("item").getAsString());
            } else if (json.has("items")) {
                JsonElement items = json.get("items");
                if (!items.isJsonArray()) {
                    throw new IllegalArgumentException("addOutcome().ingredient().items must be an array of strings");
                }
                material.add("items", (JsonElement)this.copyStringArray(items.getAsJsonArray(), "addOutcome().ingredient().items"));
                material.remove("tag");
            } else if (json.has("tag")) {
                this.applyIngredientTag(material, json.get("tag").getAsString());
            } else {
                throw new IllegalArgumentException("addOutcome().ingredient() object must contain item, items, or tag");
            }
            this.applyIngredientField(material, "count", json.get("count"));
            this.applyIngredientField(material, "nbt", json.get("nbt"));
        }

        private void applyIngredientOptions(JsonObject material, JsonElement options) {
            if (options == null || options.isJsonNull()) {
                return;
            }
            if (options.isJsonPrimitive()) {
                if (!options.getAsJsonPrimitive().isNumber()) {
                    throw new IllegalArgumentException("addOutcome().ingredient() options must be a count number or object");
                }
                material.addProperty("count", (Number)options.getAsInt());
                return;
            }
            if (!options.isJsonObject()) {
                throw new IllegalArgumentException("addOutcome().ingredient() options must be a count number or object");
            }
            JsonObject json = options.getAsJsonObject();
            this.applyIngredientField(material, "count", json.get("count"));
            this.applyIngredientField(material, "nbt", json.get("nbt"));
        }

        private void applyIngredientString(JsonObject material, String value) {
            if (value.startsWith("#")) {
                this.applyIngredientTag(material, value.substring(1));
                return;
            }
            JsonArray items = new JsonArray();
            items.add(value);
            material.add("items", (JsonElement)items);
            material.remove("tag");
        }

        private void applyIngredientTag(JsonObject material, String tag) {
            material.addProperty("tag", tag);
            material.remove("items");
        }

        private void applyIngredientField(JsonObject material, String key, JsonElement value) {
            if (value == null || value.isJsonNull()) {
                material.remove(key);
                return;
            }
            if ("count".equals(key)) {
                material.addProperty(key, (Number)value.getAsInt());
            } else {
                material.add(key, value.deepCopy());
            }
        }

        private JsonArray copyStringArray(JsonArray source, String methodName) {
            JsonArray items = new JsonArray();
            for (JsonElement element : source) {
                if (!element.isJsonPrimitive()) {
                    throw new IllegalArgumentException(methodName + " expects strings only");
                }
                items.add(element.getAsString());
            }
            return items;
        }

        private void replaceContents(JsonObject target, JsonObject replacement) {
            JsonArray keys = new JsonArray();
            target.keySet().forEach(arg_0 -> ((JsonArray)keys).add(arg_0));
            for (JsonElement key : keys) {
                target.remove(key.getAsString());
            }
            replacement.entrySet().forEach(entry -> target.add((String)entry.getKey(), ((JsonElement)entry.getValue()).deepCopy()));
        }
    }

    public static class ModuleExtractBuilder<P> {
        private final P parent;
        private final JsonObject data;

        protected ModuleExtractBuilder(P parent, JsonObject data) {
            this.parent = parent;
            this.data = data;
        }

        public ModuleExtractBuilder<P> data(Object value) {
            return this.merge(value);
        }

        public ModuleExtractBuilder<P> merge(Object value) {
            JsonElement json = TetraDataRegistryEventJS.toJson(value);
            if (json == null || json.isJsonNull()) {
                return this;
            }
            if (!json.isJsonObject()) {
                throw new IllegalArgumentException("extract().merge() expects an object");
            }
            JsonObject merged = JsonMergeUtil.merge((JsonElement)this.data, json).getAsJsonObject();
            TetraDataRegistryEventJS.replaceContents(this.data, merged);
            return this;
        }

        public ModuleExtractBuilder<P> primaryAttributes(Object ... values) {
            TetraDataRegistryEventJS.setEntries(this.data, "primaryAttributes()", "primaryAttributes", values);
            return this;
        }

        public ModuleExtractBuilder<P> secondaryAttributes(Object ... values) {
            TetraDataRegistryEventJS.setEntries(this.data, "secondaryAttributes()", "secondaryAttributes", values);
            return this;
        }

        public ModuleExtractBuilder<P> tertiaryAttributes(Object ... values) {
            TetraDataRegistryEventJS.setEntries(this.data, "tertiaryAttributes()", "tertiaryAttributes", values);
            return this;
        }

        public ModuleExtractBuilder<P> primaryEffects(Object ... values) {
            TetraDataRegistryEventJS.setEntries(this.data, "primaryEffects()", "primaryEffects", values);
            return this;
        }

        public ModuleExtractBuilder<P> secondaryEffects(Object ... values) {
            TetraDataRegistryEventJS.setEntries(this.data, "secondaryEffects()", "secondaryEffects", values);
            return this;
        }

        public ModuleExtractBuilder<P> tertiaryEffects(Object ... values) {
            TetraDataRegistryEventJS.setEntries(this.data, "tertiaryEffects()", "tertiaryEffects", values);
            return this;
        }

        public ModuleExtractBuilder<P> durability(double value) {
            this.data.addProperty("durability", (Number)value);
            return this;
        }

        public ModuleExtractBuilder<P> durabilityMultiplier(double value) {
            this.data.addProperty("durabilityMultiplier", (Number)value);
            return this;
        }

        public ModuleExtractBuilder<P> integrity(double value) {
            this.data.addProperty("integrity", (Number)value);
            return this;
        }

        public ModuleExtractBuilder<P> magicCapacity(double value) {
            this.data.addProperty("magicCapacity", (Number)value);
            return this;
        }

        public ModuleExtractBuilder<P> tools(Object value) {
            TetraDataRegistryEventJS.setJson(this.data, "tools", value);
            return this;
        }

        public ModuleExtractBuilder<P> tool(String key, Object level) {
            return this.tool(key, level, null);
        }

        public ModuleExtractBuilder<P> tool(String key, Object level, Double efficiency) {
            TetraDataRegistryEventJS.setTieredValue(TetraDataRegistryEventJS.nestedObject(this.data, "tools"), key, level, efficiency);
            return this;
        }

        public ModuleExtractBuilder<P> glyph(Object value) {
            TetraDataRegistryEventJS.setJson(this.data, "glyph", value);
            return this;
        }

        public ModuleExtractBuilder<P> glyph(int textureX, int textureY) {
            JsonObject glyph = new JsonObject();
            glyph.addProperty("textureX", (Number)textureX);
            glyph.addProperty("textureY", (Number)textureY);
            return this.glyph(glyph);
        }

        public ModuleExtractBuilder<P> glyph(String textureLocation, int textureX, int textureY) {
            JsonObject glyph = new JsonObject();
            glyph.addProperty("textureLocation", textureLocation);
            glyph.addProperty("textureX", (Number)textureX);
            glyph.addProperty("textureY", (Number)textureY);
            return this.glyph(glyph);
        }

        public ModuleExtractBuilder<P> glyphTint(String value) {
            JsonObject glyph = TetraDataRegistryEventJS.nestedObject(this.data, "glyph");
            glyph.addProperty("tint", TetraColorUtil.resolve(value));
            return this;
        }

        public ModuleExtractBuilder<P> models(Object values) {
            TetraDataRegistryEventJS.setJson(this.data, "models", values);
            return this;
        }

        public ModuleExtractBuilder<P> addModel(Object value) {
            TetraDataRegistryEventJS.arrayData(this.data, "models").add(TetraDataRegistryEventJS.toJson(value));
            return this;
        }

        public ModuleExtractBuilder<P> addModel(String location) {
            return this.addModel(TetraDataRegistryEventJS.modelRef(location, null));
        }

        public ModuleExtractBuilder<P> addModel(String location, String renderLayer) {
            return this.addModel(TetraDataRegistryEventJS.modelRef(location, renderLayer));
        }

        public ModuleExtractBuilder<P> availableTextures(Object ... values) {
            TetraDataRegistryEventJS.setStringValues(this.data, "availableTextures", values);
            return this;
        }

        public ModuleExtractBuilder<P> addAvailableTexture(String value) {
            TetraDataRegistryEventJS.arrayData(this.data, "availableTextures").add(value);
            return this;
        }

        public P done() {
            return this.parent;
        }
    }

    public static class ModuleVariantBuilder {
        private final ModuleBuilder parent;
        private final JsonObject data;
        private final Map<String, Map<String, String>> pendingTranslations = new LinkedHashMap<String, Map<String, String>>();

        protected ModuleVariantBuilder(ModuleBuilder parent, JsonObject data) {
            this.parent = parent;
            this.data = data;
        }

        public ModuleVariantBuilder data(Object value) {
            return this.merge(value);
        }

        public ModuleVariantBuilder merge(Object value) {
            String previousKey = this.currentVariantKey();
            JsonElement json = TetraDataRegistryEventJS.toJson(value);
            if (json == null || json.isJsonNull()) {
                return this;
            }
            if (!json.isJsonObject()) {
                throw new IllegalArgumentException("variant().merge() expects an object");
            }
            JsonObject merged = JsonMergeUtil.merge((JsonElement)this.data, json).getAsJsonObject();
            TetraDataRegistryEventJS.replaceContents(this.data, merged);
            this.normalizeVariantKeyInData();
            this.remapVariantTranslations(previousKey, this.currentVariantKey());
            this.flushPendingTranslations();
            return this;
        }

        public ModuleVariantBuilder key(String value) {
            String previousKey = this.currentVariantKey();
            this.data.addProperty("key", TetraDataUtil.normalizeVariantKey(value));
            this.remapVariantTranslations(previousKey, this.currentVariantKey());
            this.flushPendingTranslations();
            return this;
        }

        public ModuleVariantBuilder name(String value) {
            return this.name("zh_cn", value);
        }

        public ModuleVariantBuilder name(String language, String value) {
            return this.variantLang(language, null, value);
        }

        public ModuleVariantBuilder itemName(String value) {
            return this.itemName("zh_cn", value);
        }

        public ModuleVariantBuilder itemName(String language, String value) {
            return this.variantLang(language, ".item_name", value);
        }

        public ModuleVariantBuilder description(String value) {
            return this.description("zh_cn", value);
        }

        public ModuleVariantBuilder description(String language, String value) {
            return this.variantLang(language, ".description", value);
        }

        public ModuleVariantBuilder prefix(String value) {
            return this.prefix("zh_cn", value);
        }

        public ModuleVariantBuilder prefix(String language, String value) {
            return this.variantLang(language, ".prefix", value);
        }

        public ModuleVariantBuilder materials(Object values) {
            TetraDataRegistryEventJS.setStringArrayLike(this.data, "materials", values);
            return this;
        }

        public ModuleVariantBuilder addMaterial(String value) {
            TetraDataRegistryEventJS.arrayData(this.data, "materials").add(value);
            return this;
        }

        public ModuleVariantBuilder category(String value) {
            this.data.addProperty("category", value);
            return this;
        }

        public ModuleVariantBuilder durability(double value) {
            this.data.addProperty("durability", (Number)value);
            return this;
        }

        public ModuleVariantBuilder durabilityMultiplier(double value) {
            this.data.addProperty("durabilityMultiplier", (Number)value);
            return this;
        }

        public ModuleVariantBuilder integrity(double value) {
            this.data.addProperty("integrity", (Number)value);
            return this;
        }

        public ModuleVariantBuilder magicCapacity(double value) {
            this.data.addProperty("magicCapacity", (Number)value);
            return this;
        }

        public ModuleVariantBuilder attributes(Object ... values) {
            TetraDataRegistryEventJS.setEntries(this.data, "attributes()", "attributes", values);
            return this;
        }

        public ModuleVariantBuilder tools(Object value) {
            TetraDataRegistryEventJS.setJson(this.data, "tools", value);
            return this;
        }

        public ModuleVariantBuilder tool(String key, Object level) {
            return this.tool(key, level, null);
        }

        public ModuleVariantBuilder tool(String key, Object level, Double efficiency) {
            TetraDataRegistryEventJS.setTieredValue(TetraDataRegistryEventJS.nestedObject(this.data, "tools"), key, level, efficiency);
            return this;
        }

        public ModuleVariantBuilder effects(Object ... values) {
            TetraDataRegistryEventJS.setEntries(this.data, "effects()", "effects", values);
            return this;
        }

        public ModuleVariantBuilder aspects(Object value) {
            TetraDataRegistryEventJS.setJson(this.data, "aspects", value);
            return this;
        }

        public ModuleVariantBuilder tags(Object values) {
            TetraDataRegistryEventJS.setStringArrayLike(this.data, "tags", values);
            return this;
        }

        public ModuleVariantBuilder addTag(String value) {
            TetraDataRegistryEventJS.arrayData(this.data, "tags").add(value);
            return this;
        }

        public ModuleVariantBuilder rarity(String value) {
            this.data.addProperty("rarity", value);
            return this;
        }

        public ModuleVariantBuilder glyph(Object value) {
            TetraDataRegistryEventJS.setJson(this.data, "glyph", value);
            return this;
        }

        public ModuleVariantBuilder glyph(int textureX, int textureY) {
            JsonObject glyph = new JsonObject();
            glyph.addProperty("textureX", (Number)textureX);
            glyph.addProperty("textureY", (Number)textureY);
            return this.glyph(glyph);
        }

        public ModuleVariantBuilder glyph(String textureLocation, int textureX, int textureY) {
            JsonObject glyph = new JsonObject();
            glyph.addProperty("textureLocation", textureLocation);
            glyph.addProperty("textureX", (Number)textureX);
            glyph.addProperty("textureY", (Number)textureY);
            return this.glyph(glyph);
        }

        public ModuleVariantBuilder glyphTint(String value) {
            JsonObject glyph = TetraDataRegistryEventJS.nestedObject(this.data, "glyph");
            glyph.addProperty("tint", TetraColorUtil.resolve(value));
            return this;
        }

        public ModuleVariantBuilder prefixPriority(String value) {
            this.data.addProperty("prefixPriority", value);
            return this;
        }

        public ModuleVariantBuilder models(Object values) {
            TetraDataRegistryEventJS.setJson(this.data, "models", values);
            return this;
        }

        public ModuleVariantBuilder addModel(Object value) {
            TetraDataRegistryEventJS.arrayData(this.data, "models").add(TetraDataRegistryEventJS.toJson(value));
            return this;
        }

        public ModuleVariantBuilder addModel(String location) {
            return this.addModel(TetraDataRegistryEventJS.modelRef(location, null));
        }

        public ModuleVariantBuilder addModel(String location, String renderLayer) {
            return this.addModel(TetraDataRegistryEventJS.modelRef(location, renderLayer));
        }

        public ModuleExtractBuilder<ModuleVariantBuilder> extract() {
            JsonObject extract = TetraDataRegistryEventJS.nestedObject(this.data, "extract");
            return new ModuleExtractBuilder<ModuleVariantBuilder>(this, extract);
        }

        public ModuleVariantBuilder extract(Consumer<ModuleExtractBuilder<ModuleVariantBuilder>> callback) {
            if (callback == null) {
                return this;
            }
            ModuleExtractBuilder<ModuleVariantBuilder> builder = this.extract();
            callback.accept(builder);
            return this;
        }

        public ModuleVariantBuilder extractData(Object value) {
            TetraDataRegistryEventJS.setJson(this.data, "extract", value);
            return this;
        }

        public ModuleBuilder done() {
            return this.parent;
        }

        private void normalizeVariantKeyInData() {
            String key;
            JsonElement keyElement = this.data.get("key");
            if (keyElement != null && keyElement.isJsonPrimitive() && keyElement.getAsJsonPrimitive().isString() && !(key = keyElement.getAsString().trim()).isEmpty()) {
                this.data.addProperty("key", TetraDataUtil.normalizeVariantKey(key));
            }
        }

        private String currentVariantKey() {
            JsonElement keyElement = this.data.get("key");
            if (keyElement == null || !keyElement.isJsonPrimitive() || !keyElement.getAsJsonPrimitive().isString()) {
                return null;
            }
            String key = keyElement.getAsString().trim();
            return key.isEmpty() ? null : TetraDataUtil.normalizeVariantKey(key);
        }

        private ModuleVariantBuilder variantLang(String language, String suffix, String value) {
            String key = this.currentVariantKey();
            if (key == null) {
                this.pendingTranslations.computeIfAbsent(suffix == null ? "" : suffix, ignored -> new LinkedHashMap()).put(language, value);
                return this;
            }
            TetraScriptRegistry.addTranslation(this.parent.scope, language, this.variantTranslationKey(key, suffix), value);
            return this;
        }

        private void flushPendingTranslations() {
            String key = this.currentVariantKey();
            if (key == null || this.pendingTranslations.isEmpty()) {
                return;
            }
            for (Map.Entry<String, Map<String, String>> suffixEntry : this.pendingTranslations.entrySet()) {
                for (Map.Entry<String, String> languageEntry : suffixEntry.getValue().entrySet()) {
                    TetraScriptRegistry.addTranslation(this.parent.scope, languageEntry.getKey(), this.variantTranslationKey(key, suffixEntry.getKey()), languageEntry.getValue());
                }
            }
            this.pendingTranslations.clear();
        }

        private void remapVariantTranslations(String previousKey, String nextKey) {
            if (previousKey == null || nextKey == null || previousKey.equals(nextKey)) {
                return;
            }
            this.remapVariantTranslation(previousKey, nextKey, null);
            this.remapVariantTranslation(previousKey, nextKey, ".item_name");
            this.remapVariantTranslation(previousKey, nextKey, ".description");
            this.remapVariantTranslation(previousKey, nextKey, ".prefix");
        }

        private void remapVariantTranslation(String previousKey, String nextKey, String suffix) {
            TetraScriptRegistry.remapTranslation(this.parent.scope, this.variantTranslationKey(previousKey, suffix), this.variantTranslationKey(nextKey, suffix));
        }

        private String variantTranslationKey(String key, String suffix) {
            if (suffix == null || suffix.isEmpty()) {
                return TetraDataUtil.variantNameKey(key);
            }
            return switch (suffix) {
                case ".item_name" -> TetraDataUtil.variantItemNameKey(key);
                case ".description" -> TetraDataUtil.variantDescriptionKey(key);
                case ".prefix" -> TetraDataUtil.variantPrefixKey(key);
                default -> TetraDataUtil.variantNameKey(key) + suffix;
            };
        }
    }

    public static class ImprovementLevelBuilder {
        private final ImprovementBuilder parent;
        private final JsonObject data;

        protected ImprovementLevelBuilder(ImprovementBuilder parent, JsonObject data) {
            this.parent = parent;
            this.data = data;
        }

        public ImprovementLevelBuilder data(Object value) {
            return this.merge(value);
        }

        public ImprovementLevelBuilder merge(Object value) {
            JsonElement json = TetraDataRegistryEventJS.toJson(value);
            if (json == null || json.isJsonNull()) {
                return this;
            }
            if (!json.isJsonObject()) {
                throw new IllegalArgumentException("atLevel().merge() expects an object");
            }
            JsonObject merged = JsonMergeUtil.merge((JsonElement)this.data, json).getAsJsonObject();
            TetraDataRegistryEventJS.replaceContents(this.data, merged);
            return this;
        }

        public ImprovementLevelBuilder level(int value) {
            this.data.addProperty("level", (Number)value);
            return this;
        }

        public ImprovementLevelBuilder key(String value) {
            this.data.addProperty("key", TetraDataUtil.normalizeKey(value));
            return this;
        }

        public ImprovementLevelBuilder group(String value) {
            this.data.addProperty("group", value);
            return this;
        }

        public ImprovementLevelBuilder durability(double value) {
            this.data.addProperty("durability", (Number)value);
            return this;
        }

        public ImprovementLevelBuilder durabilityMultiplier(double value) {
            this.data.addProperty("durabilityMultiplier", (Number)value);
            return this;
        }

        public ImprovementLevelBuilder integrity(double value) {
            this.data.addProperty("integrity", (Number)value);
            return this;
        }

        public ImprovementLevelBuilder magicCapacity(double value) {
            this.data.addProperty("magicCapacity", (Number)value);
            return this;
        }

        public ImprovementLevelBuilder attributes(Object ... values) {
            TetraDataRegistryEventJS.setEntries(this.data, "attributes()", "attributes", values);
            return this;
        }

        public ImprovementLevelBuilder tools(Object value) {
            TetraDataRegistryEventJS.setJson(this.data, "tools", value);
            return this;
        }

        public ImprovementLevelBuilder tool(String key, Object level) {
            return this.tool(key, level, null);
        }

        public ImprovementLevelBuilder tool(String key, Object level, Double efficiency) {
            TetraDataRegistryEventJS.setTieredValue(TetraDataRegistryEventJS.nestedObject(this.data, "tools"), key, level, efficiency);
            return this;
        }

        public ImprovementLevelBuilder effects(Object ... values) {
            TetraDataRegistryEventJS.setEntries(this.data, "effects()", "effects", values);
            return this;
        }

        public ImprovementLevelBuilder aspects(Object value) {
            TetraDataRegistryEventJS.setJson(this.data, "aspects", value);
            return this;
        }

        public ImprovementLevelBuilder tags(Object ... values) {
            TetraDataRegistryEventJS.setStringValues(this.data, "tags", values);
            return this;
        }

        public ImprovementLevelBuilder addTag(String value) {
            TetraDataRegistryEventJS.arrayData(this.data, "tags").add(value);
            return this;
        }

        public ImprovementLevelBuilder rarity(String value) {
            this.data.addProperty("rarity", value);
            return this;
        }

        public ImprovementLevelBuilder prefixPriority(String value) {
            this.data.addProperty("prefixPriority", value);
            return this;
        }

        public ImprovementLevelBuilder materials(Object ... values) {
            TetraDataRegistryEventJS.setStringValues(this.data, "materials", values);
            return this;
        }

        public ImprovementLevelBuilder addMaterial(String value) {
            TetraDataRegistryEventJS.arrayData(this.data, "materials").add(value);
            return this;
        }

        public ImprovementLevelBuilder models(Object values) {
            TetraDataRegistryEventJS.setJson(this.data, "models", values);
            return this;
        }

        public ImprovementLevelBuilder addModel(Object value) {
            TetraDataRegistryEventJS.arrayData(this.data, "models").add(TetraDataRegistryEventJS.toJson(value));
            return this;
        }

        public ImprovementLevelBuilder addModel(String location) {
            return this.addModel(TetraDataRegistryEventJS.modelRef(location, null));
        }

        public ImprovementLevelBuilder addModel(String location, String renderLayer) {
            return this.addModel(TetraDataRegistryEventJS.modelRef(location, renderLayer));
        }

        public ModuleExtractBuilder<ImprovementLevelBuilder> extract() {
            JsonObject extract = TetraDataRegistryEventJS.nestedObject(this.data, "extract");
            return new ModuleExtractBuilder<ImprovementLevelBuilder>(this, extract);
        }

        public ImprovementLevelBuilder extract(Consumer<ModuleExtractBuilder<ImprovementLevelBuilder>> callback) {
            if (callback == null) {
                return this;
            }
            ModuleExtractBuilder<ImprovementLevelBuilder> builder = this.extract();
            callback.accept(builder);
            return this;
        }

        public ImprovementLevelBuilder extractData(Object value) {
            TetraDataRegistryEventJS.setJson(this.data, "extract", value);
            return this;
        }

        public ImprovementBuilder done() {
            return this.parent;
        }
    }

    static JsonElement toJson(Object value) {
        if (value == null) {
            return JsonNull.INSTANCE;
        }
        if (value instanceof JsonElement) {
            return ((JsonElement)value).deepCopy();
        }
        if (value instanceof Map) {
            JsonObject object = new JsonObject();
            ((Map<?, ?>)value).forEach((key, entryValue) -> object.add(String.valueOf(key), TetraDataRegistryEventJS.toJson(entryValue)));
            return object;
        }
        if (value instanceof Iterable) {
            JsonArray array = new JsonArray();
            for (Object element : (Iterable<?>)value) {
                array.add(TetraDataRegistryEventJS.toJson(element));
            }
            return array;
        }
        if (value instanceof Object[]) {
            JsonArray array = new JsonArray();
            for (Object element : (Object[])value) {
                array.add(TetraDataRegistryEventJS.toJson(element));
            }
            return array;
        }
        if (value instanceof Boolean) {
            return new JsonPrimitive((Boolean)value);
        }
        if (value instanceof Number) {
            return new JsonPrimitive((Number)value);
        }
        if (value instanceof Character) {
            return new JsonPrimitive((Character)value);
        }
        return new JsonPrimitive(String.valueOf(value));
    }
}

