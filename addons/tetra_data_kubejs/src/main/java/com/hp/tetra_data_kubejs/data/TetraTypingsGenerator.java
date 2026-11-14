/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.latvian.mods.kubejs.KubeJSPaths
 */
package com.hp.tetra_data_kubejs.data;

import com.hp.tetra_data_kubejs.TetraDataKubejs;
import com.hp.tetra_data_kubejs.data.ScriptedTetraEntry;
import com.hp.tetra_data_kubejs.data.TetraColorUtil;
import com.hp.tetra_data_kubejs.data.TetraDataUtil;
import dev.latvian.mods.kubejs.KubeJSPaths;
import java.io.IOException;
import java.lang.invoke.CallSite;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.StringJoiner;
import java.util.TreeSet;

public final class TetraTypingsGenerator {
    private static final Path PROBE_OUTPUT = KubeJSPaths.DIRECTORY.resolve("probe").resolve("generated").resolve("tetra_data_kubejs.generated.d.ts");
    private static final List<Path> LEGACY_OUTPUTS = List.of(KubeJSPaths.STARTUP_SCRIPTS.resolve("tetra_data_kubejs.generated.d.ts"), KubeJSPaths.SERVER_SCRIPTS.resolve("tetra_data_kubejs.generated.d.ts"));

    private TetraTypingsGenerator() {
    }

    public static void write(Collection<ScriptedTetraEntry> entries, Map<String, ? extends Collection<String>> knownKeysByDirectory, Collection<String> knownEffectKeys, Collection<String> knownAttributeKeys, Collection<String> knownModuleVariantKeys, Map<String, ? extends Collection<String>> knownModuleVariantsByModule, Collection<String> knownImprovementGroups, Collection<String> knownSlotKeys, Collection<String> knownMaterialImprovementKeys) {
        try {
            String contents = TetraTypingsGenerator.buildTypings(entries, knownKeysByDirectory, knownEffectKeys, knownAttributeKeys, knownModuleVariantKeys, knownModuleVariantsByModule, knownImprovementGroups, knownSlotKeys, knownMaterialImprovementKeys);
            Files.createDirectories(PROBE_OUTPUT.getParent(), new FileAttribute[0]);
            if (!Files.exists(PROBE_OUTPUT, new LinkOption[0]) || !Files.readString(PROBE_OUTPUT, StandardCharsets.UTF_8).equals(contents)) {
                Files.writeString(PROBE_OUTPUT, (CharSequence)contents, StandardCharsets.UTF_8, new OpenOption[0]);
            }
            for (Path legacyOutput : LEGACY_OUTPUTS) {
                Files.deleteIfExists(legacyOutput);
            }
        }
        catch (IOException exception) {
            TetraDataKubejs.LOGGER.warn("Failed to write tetra_data_kubejs typings", (Throwable)exception);
        }
    }

    private static String buildTypings(Collection<ScriptedTetraEntry> entries, Map<String, ? extends Collection<String>> knownKeysByDirectory, Collection<String> knownEffectKeys, Collection<String> knownAttributeKeys, Collection<String> knownModuleVariantKeys, Map<String, ? extends Collection<String>> knownModuleVariantsByModule, Collection<String> knownImprovementGroups, Collection<String> knownSlotKeys, Collection<String> knownMaterialImprovementKeys) {
        LinkedHashMap keysByDirectory = new LinkedHashMap();
        for (String directory : TetraDataUtil.COMPLETION_DIRECTORIES) {
            keysByDirectory.put(directory, new TreeSet());
        }
        TreeSet<String> effectKeys = new TreeSet<String>(TetraDataUtil.MATERIAL_EFFECT_KEYS);
        TreeSet<String> attributeKeys = new TreeSet<String>(TetraDataUtil.baseKnownAttributeKeys());
        TreeSet<String> moduleVariantKeys = new TreeSet<String>();
        LinkedHashMap<String, SortedSet<String>> moduleVariantsByModule = new LinkedHashMap<String, SortedSet<String>>();
        TreeSet<String> improvementGroups = new TreeSet<String>();
        TreeSet<String> slotKeys = new TreeSet<String>();
        TreeSet<String> materialImprovementKeys = new TreeSet<String>();
        for (ScriptedTetraEntry entry : entries) {
            SortedSet keys = (SortedSet)keysByDirectory.get(entry.getDirectory());
            if (keys == null) continue;
            keys.add(entry.getKey());
        }
        if (knownKeysByDirectory != null) {
            for (String directory : TetraDataUtil.COMPLETION_DIRECTORIES) {
                Collection<String> knownKeys = knownKeysByDirectory.get(directory);
                if (knownKeys == null) continue;
                ((SortedSet)keysByDirectory.get(directory)).addAll(knownKeys);
            }
        }
        if (knownEffectKeys != null) {
            effectKeys.addAll(knownEffectKeys);
        }
        if (knownAttributeKeys != null) {
            attributeKeys.addAll(knownAttributeKeys);
        }
        if (knownModuleVariantKeys != null) {
            moduleVariantKeys.addAll(knownModuleVariantKeys);
        }
        if (knownModuleVariantsByModule != null) {
            knownModuleVariantsByModule.forEach((moduleKey, variants) -> {
                SortedSet<String> normalized = moduleVariantsByModule.computeIfAbsent(moduleKey, ignored -> new TreeSet<String>());
                if (variants != null) {
                    normalized.addAll(variants);
                    moduleVariantKeys.addAll((Collection<String>)variants);
                }
            });
        }
        if (knownImprovementGroups != null) {
            improvementGroups.addAll(knownImprovementGroups);
        }
        if (knownSlotKeys != null) {
            slotKeys.addAll(knownSlotKeys);
        }
        if (knownMaterialImprovementKeys != null) {
            materialImprovementKeys.addAll(knownMaterialImprovementKeys);
        }
        SortedSet<String> materialReferences = TetraTypingsGenerator.buildKeyReferences((Collection)keysByDirectory.get("materials"));
        SortedSet<String> materialCategories = TetraTypingsGenerator.buildFirstSegments((Collection)keysByDirectory.get("materials"));
        SortedSet<String> improvementReferences = TetraTypingsGenerator.buildKeyReferences((Collection)keysByDirectory.get("improvements"));
        SortedSet<String> improvementPaths = TetraTypingsGenerator.buildFolderPaths((Collection)keysByDirectory.get("improvements"));
        StringBuilder builder = new StringBuilder();
        builder.append("// Generated by tetra_data_kubejs.\n");
        builder.append("// Existing Tetra keys become more complete after the first server/resource reload.\n\n");
        builder.append("type hp_TetraDataDirectory = ").append(TetraTypingsGenerator.toUnion(TetraDataUtil.ALL_DIRECTORIES)).append(";\n");
        builder.append("type hp_TetraMaterialKey = ").append(TetraTypingsGenerator.toUnion((Collection)keysByDirectory.get("materials"))).append(";\n");
        builder.append("type hp_TetraMaterialCategory = ").append(TetraTypingsGenerator.toUnion(materialCategories)).append(";\n");
        builder.append("type hp_TetraModuleKey = ").append(TetraTypingsGenerator.toUnion((Collection)keysByDirectory.get("modules"))).append(";\n");
        builder.append("type hp_TetraModuleVariantKey = ").append(TetraTypingsGenerator.toUnion(moduleVariantKeys)).append(";\n");
        builder.append("type hp_TetraModuleVariantMap = ").append(TetraTypingsGenerator.toModuleVariantMap(moduleVariantsByModule, (Collection)keysByDirectory.get("modules"))).append(";\n");
        builder.append("type hp_TetraModuleVariantFor<K extends hp_TetraModuleKey> = K extends keyof hp_TetraModuleVariantMap ? hp_TetraModuleVariantMap[K] : hp_TetraModuleVariantKey;\n");
        builder.append("type hp_TetraImprovementKey = ").append(TetraTypingsGenerator.toUnion((Collection)keysByDirectory.get("improvements"))).append(";\n");
        builder.append("type hp_TetraImprovementPath = ").append(TetraTypingsGenerator.toUnion(improvementPaths)).append(";\n");
        builder.append("type hp_TetraImprovementGroup = ").append(TetraTypingsGenerator.toUnion(improvementGroups)).append(";\n");
        builder.append("type hp_TetraMaterialImprovementKey = ").append(TetraTypingsGenerator.toUnion(materialImprovementKeys)).append(";\n");
        builder.append("type hp_TetraSchematicKey = ").append(TetraTypingsGenerator.toUnion((Collection)keysByDirectory.get("schematics"))).append(";\n");
        builder.append("type hp_TetraColorPreset = ").append(TetraTypingsGenerator.toUnion(TetraColorUtil.presetNames())).append(";\n\n");
        builder.append("type hp_TetraIngredientOptions = { count?: number; nbt?: string };\n");
        builder.append("type hp_TetraSchematicIngredientOptions = { count?: number; nbt?: string | Record<string, any> };\n");
        builder.append("type hp_TetraItemId = Special.Item | (string & {});\n");
        builder.append("type hp_TetraItemTag = Special.ItemTag | (string & {});\n");
        builder.append("type hp_TetraTaggedItem = `#${Special.ItemTag}` | `#${string}`;\n");
        builder.append("type hp_TetraIngredientDefinition = hp_TetraItemId | hp_TetraTaggedItem | hp_TetraItemId[] | { item?: hp_TetraItemId; items?: hp_TetraItemId[]; tag?: hp_TetraItemTag; count?: number; nbt?: string };\n\n");
        builder.append("type hp_TetraMaterialEffectKey = ").append(TetraTypingsGenerator.toUnion(effectKeys)).append(";\n");
        builder.append("type hp_TetraAttributeKey = ").append(TetraTypingsGenerator.toUnion(attributeKeys)).append(";\n");
        builder.append("type hp_TetraSchematicDisplayType = \"other\" | \"improvement\" | \"minor\" | \"major\" | (string & {});\n");
        builder.append("type hp_TetraSchematicRarity = \"temporary\" | \"hone\" | \"basic\" | (string & {});\n");
        builder.append("type hp_TetraSchematicPreview = \"always\" | \"revealable\" | \"applicable\" | \"never\" | (string & {});\n");
        builder.append("type hp_TetraModuleType = \"tetra:basic_module\" | \"tetra:multi_module\" | \"tetra:basic_major_module\" | \"tetra:multi_major_module\" | \"tetra:toolbelt_module\" | (string & {});\n");
        builder.append("type hp_TetraLayerPriority = \"LOWEST\" | \"LOWER\" | \"LOW\" | \"BASE\" | \"HIGH\" | \"HIGHER\" | \"HIGHEST\" | (string & {});\n");
        builder.append("type hp_TetraItemRarity = \"COMMON\" | \"UNCOMMON\" | \"RARE\" | \"EPIC\" | (string & {});\n");
        builder.append("type hp_TetraRequirementType = \"tetra:and\" | \"tetra:or\" | \"tetra:not\" | \"tetra:never\" | \"tetra:feature_flag\" | \"tetra:locked\" | \"tetra:improvement\" | \"tetra:accepts_improvement\" | \"tetra:module\" | \"tetra:aspect\" | \"tetra:perk\" | \"tetra:slot\" | (string & {});\n");
        builder.append("type hp_TetraIntegerPredicate = number | { min?: number; max?: number };\n");
        builder.append("type hp_TetraSlotKey = ").append(TetraTypingsGenerator.toUnion(slotKeys)).append(";\n");
        builder.append("type hp_TetraMaterialReference = ").append(TetraTypingsGenerator.toUnion(materialReferences)).append(";\n");
        builder.append("type hp_TetraImprovementReference = ").append(TetraTypingsGenerator.toUnion(improvementReferences)).append(";\n");
        builder.append("type hp_TetraGlyph = { textureX?: number; textureY?: number; textureLocation?: string };\n");
        builder.append("type hp_TetraTransformVec3 = [number, number, number];\n");
        builder.append("type hp_TetraTransformRotation = [number, number, number] | [number, number, number, number];\n");
        builder.append("type hp_TetraModelPerspective = \"none\" | \"thirdperson_lefthand\" | \"thirdperson_righthand\" | \"firstperson_lefthand\" | \"firstperson_righthand\" | \"head\" | \"gui\" | \"ground\" | \"fixed\";\n");
        builder.append("type hp_TetraFlatModel = {\n");
        builder.append("  type?: string;\n");
        builder.append("  location: string;\n");
        builder.append("  renderType?: string;\n");
        builder.append("  transform?: {\n");
        builder.append("    translation?: hp_TetraTransformVec3;\n");
        builder.append("    scale?: hp_TetraTransformVec3;\n");
        builder.append("    rotation?: hp_TetraTransformRotation;\n");
        builder.append("  };\n");
        builder.append("  emission?: number;\n");
        builder.append("  tint?: hp_TetraColorPreset;\n");
        builder.append("  overlayTint?: hp_TetraColorPreset;\n");
        builder.append("  renderLayer?: hp_TetraLayerPriority;\n");
        builder.append("  perspectives?: hp_TetraModelPerspective[];\n");
        builder.append("  invertPerspectives?: boolean;\n");
        builder.append("} & Record<string, any>;\n");
        builder.append("type hp_TetraModelRef = hp_TetraFlatModel;\n");
        builder.append("type hp_TetraSchematicIngredient = { items?: hp_TetraItemId[]; tag?: hp_TetraItemTag; nbt?: Record<string, any>; count?: number };\n");
        builder.append("type hp_TetraSchematicRequirementAnd = {\n");
        builder.append("  type: \"tetra:and\";\n");
        builder.append("  requirements: hp_TetraSchematicRequirement[];\n");
        builder.append("};\n");
        builder.append("type hp_TetraSchematicRequirementOr = {\n");
        builder.append("  type: \"tetra:or\";\n");
        builder.append("  requirements: hp_TetraSchematicRequirement[];\n");
        builder.append("};\n");
        builder.append("type hp_TetraSchematicRequirementNot = {\n");
        builder.append("  type: \"tetra:not\";\n");
        builder.append("  requirement: hp_TetraSchematicRequirement;\n");
        builder.append("};\n");
        builder.append("type hp_TetraSchematicRequirementNever = {\n");
        builder.append("  type: \"tetra:never\";\n");
        builder.append("};\n");
        builder.append("type hp_TetraSchematicRequirementSlot = {\n");
        builder.append("  type: \"tetra:slot\";\n");
        builder.append("  slot: hp_TetraSlotKey;\n");
        builder.append("};\n");
        builder.append("type hp_TetraSchematicRequirementModule = {\n");
        builder.append("  type: \"tetra:module\";\n");
        builder.append("  module?: hp_TetraModuleKey;\n");
        builder.append("  variant?: hp_TetraModuleVariantKey;\n");
        builder.append("  material?: hp_TetraMaterialKey | string;\n");
        builder.append("};\n");
        builder.append("type hp_TetraSchematicRequirementAcceptsImprovement = {\n");
        builder.append("  type: \"tetra:accepts_improvement\";\n");
        builder.append("  improvement: hp_TetraMaterialImprovementKey;\n");
        builder.append("  level?: number;\n");
        builder.append("};\n");
        builder.append("type hp_TetraSchematicRequirementHasImprovement = {\n");
        builder.append("  type: \"tetra:improvement\";\n");
        builder.append("  improvement: hp_TetraMaterialImprovementKey;\n");
        builder.append("  level?: hp_TetraIntegerPredicate;\n");
        builder.append("};\n");
        builder.append("type hp_TetraSchematicRequirementAspect = {\n");
        builder.append("  type: \"tetra:aspect\";\n");
        builder.append("  aspect: string;\n");
        builder.append("  level?: hp_TetraIntegerPredicate;\n");
        builder.append("};\n");
        builder.append("type hp_TetraSchematicRequirementFeatureFlag = {\n");
        builder.append("  type: \"tetra:feature_flag\";\n");
        builder.append("  feature: string;\n");
        builder.append("  flag?: string;\n");
        builder.append("};\n");
        builder.append("type hp_TetraSchematicRequirementPerk = {\n");
        builder.append("  type: \"tetra:perk\";\n");
        builder.append("  perk: string;\n");
        builder.append("  level?: hp_TetraIntegerPredicate;\n");
        builder.append("};\n");
        builder.append("type hp_TetraSchematicRequirementLocked = {\n");
        builder.append("  type: \"tetra:locked\";\n");
        builder.append("  key: string;\n");
        builder.append("};\n");
        builder.append("type hp_TetraSchematicRequirement =\n");
        builder.append("  | hp_TetraSchematicRequirementAnd\n");
        builder.append("  | hp_TetraSchematicRequirementOr\n");
        builder.append("  | hp_TetraSchematicRequirementNot\n");
        builder.append("  | hp_TetraSchematicRequirementNever\n");
        builder.append("  | hp_TetraSchematicRequirementSlot\n");
        builder.append("  | hp_TetraSchematicRequirementModule\n");
        builder.append("  | hp_TetraSchematicRequirementAcceptsImprovement\n");
        builder.append("  | hp_TetraSchematicRequirementHasImprovement\n");
        builder.append("  | hp_TetraSchematicRequirementAspect\n");
        builder.append("  | hp_TetraSchematicRequirementFeatureFlag\n");
        builder.append("  | hp_TetraSchematicRequirementPerk\n");
        builder.append("  | hp_TetraSchematicRequirementLocked;\n");
        builder.append("type hp_TetraRequiredTools = Record<string, number | string>;\n");
        builder.append("type hp_TetraSchematicStandardOutcome = {\n");
        builder.append("  material?: hp_TetraSchematicIngredient;\n");
        builder.append("  materialSlot?: number;\n");
        builder.append("  experienceCost?: number;\n");
        builder.append("  requiredTools?: hp_TetraRequiredTools;\n");
        builder.append("  moduleKey?: hp_TetraModuleKey;\n");
        builder.append("  moduleVariant?: hp_TetraModuleVariantKey;\n");
        builder.append("  improvements?: hp_TetraMaterialImprovements;\n");
        builder.append("  hidden?: boolean;\n");
        builder.append("} & Record<string, any>;\n");
        builder.append("type hp_TetraSchematicMaterialOutcome = {\n");
        builder.append("  materials: hp_TetraMaterialReference[];\n");
        builder.append("  materialSlot?: number;\n");
        builder.append("  experienceCost?: number;\n");
        builder.append("  countFactor?: number;\n");
        builder.append("  countOffset?: number;\n");
        builder.append("  toolFactor?: number;\n");
        builder.append("  toolOffset?: number;\n");
        builder.append("  requiredTools?: hp_TetraRequiredTools;\n");
        builder.append("  moduleKey?: hp_TetraModuleKey;\n");
        builder.append("  moduleVariant?: hp_TetraModuleVariantKey;\n");
        builder.append("  improvements?: hp_TetraMaterialImprovements;\n");
        builder.append("  hidden?: boolean;\n");
        builder.append("} & Record<string, any>;\n");
        builder.append("type hp_TetraSchematicOutcome = hp_TetraSchematicStandardOutcome | hp_TetraSchematicMaterialOutcome;\n");
        builder.append("type hp_TetraEffectValue = number | [number, number];\n");
        builder.append("type hp_TetraEffectEntry = [hp_TetraMaterialEffectKey, hp_TetraEffectValue] | [string, hp_TetraEffectValue];\n");
        builder.append("type hp_TetraAttributeEntry = [hp_TetraAttributeKey, number] | [string, number];\n");
        builder.append("type hp_TetraMaterialEffects = Partial<Record<hp_TetraMaterialEffectKey, hp_TetraEffectValue>> & Record<string, hp_TetraEffectValue>;\n");
        builder.append("type hp_TetraMaterialAttributes = Partial<Record<hp_TetraAttributeKey, number>> & Record<string, number>;\n\n");
        builder.append("type hp_TetraMaterialImprovements = Partial<Record<hp_TetraMaterialImprovementKey, number>> & Record<string, number>;\n\n");
        builder.append("type hp_TetraTieredData = Record<string, number | string | [number | string, number]>;\n");
        builder.append("type hp_TetraModuleExtract = {\n");
        builder.append("  primaryAttributes?: hp_TetraMaterialAttributes;\n");
        builder.append("  secondaryAttributes?: hp_TetraMaterialAttributes;\n");
        builder.append("  tertiaryAttributes?: hp_TetraMaterialAttributes;\n");
        builder.append("  primaryEffects?: hp_TetraMaterialEffects;\n");
        builder.append("  secondaryEffects?: hp_TetraMaterialEffects;\n");
        builder.append("  tertiaryEffects?: hp_TetraMaterialEffects;\n");
        builder.append("  durability?: number;\n");
        builder.append("  durabilityMultiplier?: number;\n");
        builder.append("  integrity?: number;\n");
        builder.append("  magicCapacity?: number;\n");
        builder.append("  tools?: hp_TetraTieredData;\n");
        builder.append("  glyph?: hp_TetraGlyph;\n");
        builder.append("  models?: hp_TetraModelRef[];\n");
        builder.append("  availableTextures?: string[];\n");
        builder.append("} & Record<string, any>;\n");
        builder.append("type hp_TetraModuleVariant = {\n");
        builder.append("  key?: hp_TetraModuleVariantKey;\n");
        builder.append("  name?: string | Record<string, string>;\n");
        builder.append("  itemName?: string | Record<string, string>;\n");
        builder.append("  description?: string | Record<string, string>;\n");
        builder.append("  prefix?: string | Record<string, string>;\n");
        builder.append("  materials?: hp_TetraMaterialReference[];\n");
        builder.append("  category?: string;\n");
        builder.append("  durability?: number;\n");
        builder.append("  durabilityMultiplier?: number;\n");
        builder.append("  integrity?: number;\n");
        builder.append("  magicCapacity?: number;\n");
        builder.append("  attributes?: hp_TetraMaterialAttributes;\n");
        builder.append("  tools?: hp_TetraTieredData;\n");
        builder.append("  effects?: hp_TetraMaterialEffects;\n");
        builder.append("  aspects?: Record<string, number>;\n");
        builder.append("  tags?: string[];\n");
        builder.append("  rarity?: hp_TetraItemRarity;\n");
        builder.append("  glyph?: hp_TetraGlyph;\n");
        builder.append("  prefixPriority?: hp_TetraLayerPriority;\n");
        builder.append("  models?: hp_TetraModelRef[];\n");
        builder.append("  extract?: hp_TetraModuleExtract;\n");
        builder.append("} & Record<string, any>;\n\n");
        builder.append("declare const hp_TetraColors: {\n");
        for (String color : TetraColorUtil.presetNames()) {
            builder.append("  ").append(color).append(": \"").append(color).append("\";\n");
        }
        builder.append("};\n\n");
        builder.append("declare interface hp_TetraDataBuilder {\n");
        builder.append("  data(data: Record<string, any> | any[]): this;\n");
        builder.append("  merge(data: Record<string, any> | any[]): this;\n");
        builder.append("  mergeExisting(): this;\n");
        builder.append("  mergeExisting(enabled: boolean): this;\n");
        builder.append("  copyFrom(sourceKey: string): this;\n");
        builder.append("  replace(): this;\n");
        builder.append("  replace(enabled: boolean): this;\n");
        builder.append("  lang(fullKey: string, value: string): this;\n");
        builder.append("  lang(language: string, fullKey: string, value: string): this;\n");
        builder.append("}\n\n");
        builder.append("declare interface hp_TetraMaterialBuilder extends hp_TetraDataBuilder {\n");
        builder.append("  name(value: string): this;\n");
        builder.append("  name(language: string, value: string): this;\n");
        builder.append("  prefix(value: string): this;\n");
        builder.append("  prefix(language: string, value: string): this;\n");
        builder.append("  category(value: string): this;\n");
        builder.append("  hidden(): this;\n");
        builder.append("  hidden(value: boolean): this;\n");
        builder.append("  hidden(hidden: boolean, hiddenOutcomes: boolean): this;\n");
        builder.append("  stats(primary: number, secondary: number, tertiary: number): this;\n");
        builder.append("  stats(values: [number, number, number]): this;\n");
        builder.append("  durability(value: number): this;\n");
        builder.append("  integrity(gain: number, cost: number): this;\n");
        builder.append("  integrity(values: [number, number]): this;\n");
        builder.append("  magicCapacity(value: number): this;\n");
        builder.append("  tool(level: number | string, efficiency: number): this;\n");
        builder.append("  tool(values: [number | string, number]): this;\n");
        builder.append("  effects(value: hp_TetraMaterialEffects): this;\n");
        builder.append("  effects(entries: hp_TetraEffectEntry[]): this;\n");
        builder.append("  effects(...entries: hp_TetraEffectEntry[]): this;\n");
        builder.append("  attributes(value: hp_TetraMaterialAttributes): this;\n");
        builder.append("  attributes(entries: hp_TetraAttributeEntry[]): this;\n");
        builder.append("  attributes(...entries: hp_TetraAttributeEntry[]): this;\n");
        builder.append("  aspects(value: Record<string, number>): this;\n");
        builder.append("  improvements(value: hp_TetraMaterialImprovements): this;\n");
        builder.append("  item(value: hp_TetraItemId): this;\n");
        builder.append("  item(value: hp_TetraItemId, count: number): this;\n");
        builder.append("  items(values: hp_TetraItemId[]): this;\n");
        builder.append("  itemTag(value: hp_TetraItemTag): this;\n");
        builder.append("  itemNbt(value: string): this;\n");
        builder.append("  count(value: number): this;\n");
        builder.append("  ingredient(value: hp_TetraIngredientDefinition): this;\n");
        builder.append("  ingredient(value: string, count: number): this;\n");
        builder.append("  ingredient(value: string, options: hp_TetraIngredientOptions): this;\n");
        builder.append("  ingredient(value: hp_TetraIngredientDefinition, options: hp_TetraIngredientOptions): this;\n");
        builder.append("  requires(value: Record<string, number | string>): this;\n");
        builder.append("  tags(values: string | string[]): this;\n");
        builder.append("  addTag(value: string): this;\n");
        builder.append("  glyphTint(value: hp_TetraColorPreset): this;\n");
        builder.append("  textureTint(value: hp_TetraColorPreset): this;\n");
        builder.append("  tint(value: hp_TetraColorPreset): this;\n");
        builder.append("  tint(glyph: hp_TetraColorPreset, texture: hp_TetraColorPreset): this;\n");
        builder.append("  textures(values: string | string[]): this;\n");
        builder.append("  addTexture(value: string): this;\n");
        builder.append("  textureOverrides(values: string | string[]): this;\n");
        builder.append("  addTextureOverride(value: string): this;\n");
        builder.append("  tintOverrides(): this;\n");
        builder.append("  tintOverrides(value: boolean): this;\n");
        builder.append("  experienceCost(value: number): this;\n");
        builder.append("  features(values: string | string[]): this;\n");
        builder.append("  addFeature(value: string): this;\n");
        builder.append("}\n\n");
        builder.append("declare interface hp_TetraModuleBuilder extends hp_TetraDataBuilder {\n");
        builder.append("  name(value: string): this;\n");
        builder.append("  name(language: string, value: string): this;\n");
        builder.append("  materialName(value: string): this;\n");
        builder.append("  materialName(language: string, value: string): this;\n");
        builder.append("  itemName(value: string): this;\n");
        builder.append("  itemName(language: string, value: string): this;\n");
        builder.append("  description(value: string): this;\n");
        builder.append("  description(language: string, value: string): this;\n");
        builder.append("  prefix(value: string): this;\n");
        builder.append("  prefix(language: string, value: string): this;\n");
        builder.append("  slots(values: hp_TetraSlotKey | hp_TetraSlotKey[]): this;\n");
        builder.append("  slots(...values: hp_TetraSlotKey[]): this;\n");
        builder.append("  addSlot(value: hp_TetraSlotKey): this;\n");
        builder.append("  slotSuffixes(values: string | string[]): this;\n");
        builder.append("  addSlotSuffix(value: string): this;\n");
        builder.append("  type(value: hp_TetraModuleType): this;\n");
        builder.append("  improvements(values: hp_TetraImprovementReference | hp_TetraImprovementReference[]): this;\n");
        builder.append("  addImprovement(value: hp_TetraImprovementReference): this;\n");
        builder.append("  renderLayer(value: hp_TetraLayerPriority): this;\n");
        builder.append("  namePriority(value: hp_TetraLayerPriority): this;\n");
        builder.append("  prefixPriority(value: hp_TetraLayerPriority): this;\n");
        builder.append("  tweakKey(value: string): this;\n");
        builder.append("  variants(values: hp_TetraModuleVariant[]): this;\n");
        builder.append("  addVariant(): hp_TetraModuleVariantBuilder;\n");
        builder.append("  addVariant(callback: (variant: hp_TetraModuleVariantBuilder) => void): this;\n");
        builder.append("  variantData(value: hp_TetraModuleVariant): this;\n");
        builder.append("}\n\n");
        builder.append("declare interface hp_TetraImprovementBuilder extends hp_TetraDataBuilder {\n");
        builder.append("  path(value: hp_TetraImprovementPath): this;\n");
        builder.append("  name(value: string): this;\n");
        builder.append("  name(language: string, value: string): this;\n");
        builder.append("  description(value: string): this;\n");
        builder.append("  description(language: string, value: string): this;\n");
        builder.append("  improvementKey(value: string): this;\n");
        builder.append("  level(value: number): this;\n");
        builder.append("  group(value: hp_TetraImprovementGroup): this;\n");
        builder.append("  durability(value: number): this;\n");
        builder.append("  durabilityMultiplier(value: number): this;\n");
        builder.append("  integrity(value: number): this;\n");
        builder.append("  magicCapacity(value: number): this;\n");
        builder.append("  attributes(value: hp_TetraMaterialAttributes): this;\n");
        builder.append("  attributes(entries: hp_TetraAttributeEntry[]): this;\n");
        builder.append("  attributes(...entries: hp_TetraAttributeEntry[]): this;\n");
        builder.append("  tools(value: hp_TetraTieredData): this;\n");
        builder.append("  tool(key: string, level: number | string): this;\n");
        builder.append("  tool(key: string, level: number | string, efficiency: number): this;\n");
        builder.append("  effects(value: hp_TetraMaterialEffects): this;\n");
        builder.append("  effects(entries: hp_TetraEffectEntry[]): this;\n");
        builder.append("  effects(...entries: hp_TetraEffectEntry[]): this;\n");
        builder.append("  aspects(value: Record<string, number>): this;\n");
        builder.append("  tags(values: string | string[]): this;\n");
        builder.append("  addTag(value: string): this;\n");
        builder.append("  rarity(value: hp_TetraItemRarity): this;\n");
        builder.append("  prefixPriority(value: hp_TetraLayerPriority): this;\n");
        builder.append("  materials(values: hp_TetraMaterialReference | hp_TetraMaterialReference[]): this;\n");
        builder.append("  addMaterial(value: hp_TetraMaterialReference): this;\n");
        builder.append("  models(values: hp_TetraModelRef | hp_TetraModelRef[]): this;\n");
        builder.append("  addModel(value: hp_TetraModelRef): this;\n");
        builder.append("  addModel(location: string): this;\n");
        builder.append("  addModel(location: string, renderLayer: hp_TetraLayerPriority): this;\n");
        builder.append("  extract(): hp_TetraModuleExtractBuilder<hp_TetraImprovementBuilder>;\n");
        builder.append("  extract(callback: (extract: hp_TetraModuleExtractBuilder<hp_TetraImprovementBuilder>) => void): this;\n");
        builder.append("  extractData(value: hp_TetraModuleExtract): this;\n");
        builder.append("  atLevel(value: number): hp_TetraImprovementLevelBuilder;\n");
        builder.append("  atLevel(value: number, callback: (level: hp_TetraImprovementLevelBuilder) => void): this;\n");
        builder.append("}\n\n");
        builder.append("declare interface hp_TetraImprovementLevelBuilder {\n");
        builder.append("  data(data: Record<string, any>): this;\n");
        builder.append("  merge(data: Record<string, any>): this;\n");
        builder.append("  level(value: number): this;\n");
        builder.append("  group(value: hp_TetraImprovementGroup): this;\n");
        builder.append("  durability(value: number): this;\n");
        builder.append("  durabilityMultiplier(value: number): this;\n");
        builder.append("  integrity(value: number): this;\n");
        builder.append("  magicCapacity(value: number): this;\n");
        builder.append("  attributes(value: hp_TetraMaterialAttributes): this;\n");
        builder.append("  attributes(entries: hp_TetraAttributeEntry[]): this;\n");
        builder.append("  attributes(...entries: hp_TetraAttributeEntry[]): this;\n");
        builder.append("  tools(value: hp_TetraTieredData): this;\n");
        builder.append("  tool(key: string, level: number | string): this;\n");
        builder.append("  tool(key: string, level: number | string, efficiency: number): this;\n");
        builder.append("  effects(value: hp_TetraMaterialEffects): this;\n");
        builder.append("  effects(entries: hp_TetraEffectEntry[]): this;\n");
        builder.append("  effects(...entries: hp_TetraEffectEntry[]): this;\n");
        builder.append("  aspects(value: Record<string, number>): this;\n");
        builder.append("  tags(values: string | string[]): this;\n");
        builder.append("  addTag(value: string): this;\n");
        builder.append("  rarity(value: hp_TetraItemRarity): this;\n");
        builder.append("  prefixPriority(value: hp_TetraLayerPriority): this;\n");
        builder.append("  materials(values: hp_TetraMaterialReference | hp_TetraMaterialReference[]): this;\n");
        builder.append("  addMaterial(value: hp_TetraMaterialReference): this;\n");
        builder.append("  models(values: hp_TetraModelRef | hp_TetraModelRef[]): this;\n");
        builder.append("  addModel(value: hp_TetraModelRef): this;\n");
        builder.append("  addModel(location: string): this;\n");
        builder.append("  addModel(location: string, renderLayer: hp_TetraLayerPriority): this;\n");
        builder.append("  extract(): hp_TetraModuleExtractBuilder<hp_TetraImprovementLevelBuilder>;\n");
        builder.append("  extract(callback: (extract: hp_TetraModuleExtractBuilder<hp_TetraImprovementLevelBuilder>) => void): this;\n");
        builder.append("  extractData(value: hp_TetraModuleExtract): this;\n");
        builder.append("  done(): hp_TetraImprovementBuilder;\n");
        builder.append("}\n\n");
        builder.append("declare interface hp_TetraModuleVariantBuilder {\n");
        builder.append("  data(data: hp_TetraModuleVariant): this;\n");
        builder.append("  merge(data: hp_TetraModuleVariant): this;\n");
        builder.append("  key(value: hp_TetraModuleVariantKey): this;\n");
        builder.append("  name(value: string): this;\n");
        builder.append("  name(language: string, value: string): this;\n");
        builder.append("  itemName(value: string): this;\n");
        builder.append("  itemName(language: string, value: string): this;\n");
        builder.append("  description(value: string): this;\n");
        builder.append("  description(language: string, value: string): this;\n");
        builder.append("  prefix(value: string): this;\n");
        builder.append("  prefix(language: string, value: string): this;\n");
        builder.append("  materials(values: hp_TetraMaterialReference | hp_TetraMaterialReference[]): this;\n");
        builder.append("  addMaterial(value: hp_TetraMaterialReference): this;\n");
        builder.append("  category(value: string): this;\n");
        builder.append("  durability(value: number): this;\n");
        builder.append("  durabilityMultiplier(value: number): this;\n");
        builder.append("  integrity(value: number): this;\n");
        builder.append("  magicCapacity(value: number): this;\n");
        builder.append("  attributes(value: hp_TetraMaterialAttributes): this;\n");
        builder.append("  attributes(entries: hp_TetraAttributeEntry[]): this;\n");
        builder.append("  attributes(...entries: hp_TetraAttributeEntry[]): this;\n");
        builder.append("  tools(value: hp_TetraTieredData): this;\n");
        builder.append("  tool(key: string, level: number | string): this;\n");
        builder.append("  tool(key: string, level: number | string, efficiency: number): this;\n");
        builder.append("  effects(value: hp_TetraMaterialEffects): this;\n");
        builder.append("  effects(entries: hp_TetraEffectEntry[]): this;\n");
        builder.append("  effects(...entries: hp_TetraEffectEntry[]): this;\n");
        builder.append("  aspects(value: Record<string, number>): this;\n");
        builder.append("  tags(values: string | string[]): this;\n");
        builder.append("  addTag(value: string): this;\n");
        builder.append("  rarity(value: hp_TetraItemRarity): this;\n");
        builder.append("  glyph(value: hp_TetraGlyph): this;\n");
        builder.append("  glyph(textureX: number, textureY: number): this;\n");
        builder.append("  glyph(textureLocation: string, textureX: number, textureY: number): this;\n");
        builder.append("  glyphTint(value: hp_TetraColorPreset): this;\n");
        builder.append("  prefixPriority(value: hp_TetraLayerPriority): this;\n");
        builder.append("  models(values: hp_TetraModelRef | hp_TetraModelRef[]): this;\n");
        builder.append("  addModel(value: hp_TetraModelRef): this;\n");
        builder.append("  addModel(location: string): this;\n");
        builder.append("  addModel(location: string, renderLayer: hp_TetraLayerPriority): this;\n");
        builder.append("  extract(): hp_TetraModuleExtractBuilder<hp_TetraModuleVariantBuilder>;\n");
        builder.append("  extract(callback: (extract: hp_TetraModuleExtractBuilder<hp_TetraModuleVariantBuilder>) => void): this;\n");
        builder.append("  extractData(value: hp_TetraModuleExtract): this;\n");
        builder.append("  done(): hp_TetraModuleBuilder;\n");
        builder.append("}\n\n");
        builder.append("declare interface hp_TetraModuleExtractBuilder<TParent = any> {\n");
        builder.append("  data(data: hp_TetraModuleExtract): this;\n");
        builder.append("  merge(data: hp_TetraModuleExtract): this;\n");
        builder.append("  primaryAttributes(value: hp_TetraMaterialAttributes): this;\n");
        builder.append("  primaryAttributes(entries: hp_TetraAttributeEntry[]): this;\n");
        builder.append("  primaryAttributes(...entries: hp_TetraAttributeEntry[]): this;\n");
        builder.append("  secondaryAttributes(value: hp_TetraMaterialAttributes): this;\n");
        builder.append("  secondaryAttributes(entries: hp_TetraAttributeEntry[]): this;\n");
        builder.append("  secondaryAttributes(...entries: hp_TetraAttributeEntry[]): this;\n");
        builder.append("  tertiaryAttributes(value: hp_TetraMaterialAttributes): this;\n");
        builder.append("  tertiaryAttributes(entries: hp_TetraAttributeEntry[]): this;\n");
        builder.append("  tertiaryAttributes(...entries: hp_TetraAttributeEntry[]): this;\n");
        builder.append("  primaryEffects(value: hp_TetraMaterialEffects): this;\n");
        builder.append("  primaryEffects(entries: hp_TetraEffectEntry[]): this;\n");
        builder.append("  primaryEffects(...entries: hp_TetraEffectEntry[]): this;\n");
        builder.append("  secondaryEffects(value: hp_TetraMaterialEffects): this;\n");
        builder.append("  secondaryEffects(entries: hp_TetraEffectEntry[]): this;\n");
        builder.append("  secondaryEffects(...entries: hp_TetraEffectEntry[]): this;\n");
        builder.append("  tertiaryEffects(value: hp_TetraMaterialEffects): this;\n");
        builder.append("  tertiaryEffects(entries: hp_TetraEffectEntry[]): this;\n");
        builder.append("  tertiaryEffects(...entries: hp_TetraEffectEntry[]): this;\n");
        builder.append("  durability(value: number): this;\n");
        builder.append("  durabilityMultiplier(value: number): this;\n");
        builder.append("  integrity(value: number): this;\n");
        builder.append("  magicCapacity(value: number): this;\n");
        builder.append("  tools(value: hp_TetraTieredData): this;\n");
        builder.append("  tool(key: string, level: number | string): this;\n");
        builder.append("  tool(key: string, level: number | string, efficiency: number): this;\n");
        builder.append("  glyph(value: hp_TetraGlyph): this;\n");
        builder.append("  glyph(textureX: number, textureY: number): this;\n");
        builder.append("  glyph(textureLocation: string, textureX: number, textureY: number): this;\n");
        builder.append("  glyphTint(value: hp_TetraColorPreset): this;\n");
        builder.append("  models(values: hp_TetraModelRef | hp_TetraModelRef[]): this;\n");
        builder.append("  addModel(value: hp_TetraModelRef): this;\n");
        builder.append("  addModel(location: string): this;\n");
        builder.append("  addModel(location: string, renderLayer: hp_TetraLayerPriority): this;\n");
        builder.append("  availableTextures(values: string | string[]): this;\n");
        builder.append("  addAvailableTexture(value: string): this;\n");
        builder.append("  done(): TParent;\n");
        builder.append("}\n\n");
        builder.append("declare interface hp_TetraSchematicBuilder extends hp_TetraDataBuilder {\n");
        builder.append("  name(value: string): this;\n");
        builder.append("  name(language: string, value: string): this;\n");
        builder.append("  description(value: string): this;\n");
        builder.append("  description(language: string, value: string): this;\n");
        builder.append("  slot(slotNumber: number, value: string): this;\n");
        builder.append("  slot(language: string, slotNumber: number, value: string): this;\n");
        builder.append("  localizationKey(value: string): this;\n");
        builder.append("  slots(values: hp_TetraSlotKey | hp_TetraSlotKey[]): this;\n");
        builder.append("  slots(...values: hp_TetraSlotKey[]): this;\n");
        builder.append("  addSlot(value: hp_TetraSlotKey): this;\n");
        builder.append("  keySuffixes(values: string | string[]): this;\n");
        builder.append("  addKeySuffix(value: string): this;\n");
        builder.append("  materialSlotCount(value: number): this;\n");
        builder.append("  repair(): this;\n");
        builder.append("  repair(value: boolean): this;\n");
        builder.append("  hone(): this;\n");
        builder.append("  hone(value: boolean): this;\n");
        builder.append("  requirement(): hp_TetraSchematicRequirementBuilder<hp_TetraSchematicBuilder>;\n");
        builder.append("  requirement(callback: (requirement: hp_TetraSchematicRequirementBuilder<hp_TetraSchematicBuilder>) => void): this;\n");
        builder.append("  materialRevealSlot(value: number): this;\n");
        builder.append("  displayType(value: hp_TetraSchematicDisplayType): this;\n");
        builder.append("  rarity(value: hp_TetraSchematicRarity): this;\n");
        builder.append("  glyph(value: hp_TetraGlyph): this;\n");
        builder.append("  glyph(textureX: number, textureY: number): this;\n");
        builder.append("  glyph(textureLocation: string, textureX: number, textureY: number): this;\n");
        builder.append("  translation(value: Record<string, any>): this;\n");
        builder.append("  preview(value: hp_TetraSchematicPreview): this;\n");
        builder.append("  applicableMaterials(values: string | string[]): this;\n");
        builder.append("  addApplicableMaterial(value: string): this;\n");
        builder.append("  outcomes(values: hp_TetraSchematicOutcome[]): this;\n");
        builder.append("  addOutcome(): hp_TetraSchematicOutcomeBuilder;\n");
        builder.append("  addOutcome(callback: (outcome: hp_TetraSchematicOutcomeBuilder) => void): this;\n");
        builder.append("  outcomeData(value: hp_TetraSchematicOutcome): this;\n");
        builder.append("}\n\n");
        builder.append("declare interface hp_TetraSchematicOutcomeBuilder {\n");
        builder.append("  data(data: hp_TetraSchematicOutcome): this;\n");
        builder.append("  merge(data: hp_TetraSchematicOutcome): this;\n");
        builder.append("  materials(values: hp_TetraMaterialReference | hp_TetraMaterialReference[]): this;\n");
        builder.append("  addMaterialReference(value: hp_TetraMaterialReference): this;\n");
        builder.append("  item(value: hp_TetraItemId): this;\n");
        builder.append("  item(value: hp_TetraItemId, count: number): this;\n");
        builder.append("  items(values: hp_TetraItemId[]): this;\n");
        builder.append("  itemTag(value: hp_TetraItemTag): this;\n");
        builder.append("  ingredientCount(value: number): this;\n");
        builder.append("  ingredientNbt(value: string | Record<string, any>): this;\n");
        builder.append("  ingredient(value: hp_TetraIngredientDefinition): this;\n");
        builder.append("  ingredient(value: string, count: number): this;\n");
        builder.append("  ingredient(value: string, options: hp_TetraSchematicIngredientOptions): this;\n");
        builder.append("  ingredient(value: hp_TetraIngredientDefinition, options: hp_TetraSchematicIngredientOptions): this;\n");
        builder.append("  materialSlot(value: number): this;\n");
        builder.append("  experienceCost(value: number): this;\n");
        builder.append("  countFactor(value: number): this;\n");
        builder.append("  countOffset(value: number): this;\n");
        builder.append("  toolFactor(value: number): this;\n");
        builder.append("  toolOffset(value: number): this;\n");
        builder.append("  requiredTools(value: hp_TetraRequiredTools): this;\n");
        builder.append("  requiredTool(key: string, value: number | string): this;\n");
        builder.append("  module<K extends hp_TetraModuleKey>(value: K, variant: hp_TetraModuleVariantFor<K>): this;\n");
        builder.append("  improvements(value: hp_TetraMaterialImprovements): this;\n");
        builder.append("  improvement(key: hp_TetraMaterialImprovementKey, level: number): this;\n");
        builder.append("  hidden(): this;\n");
        builder.append("  hidden(value: boolean): this;\n");
        builder.append("  done(): hp_TetraSchematicBuilder;\n");
        builder.append("}\n\n");
        builder.append("declare interface hp_TetraSchematicRequirementBuilder<TParent = any> {\n");
        builder.append("  data(data: hp_TetraSchematicRequirement): this;\n");
        builder.append("  merge(data: hp_TetraSchematicRequirement): this;\n");
        builder.append("  type(value: hp_TetraRequirementType): this;\n");
        builder.append("  and(): this;\n");
        builder.append("  or(): this;\n");
        builder.append("  not(): this;\n");
        builder.append("  never(): this;\n");
        builder.append("  slot(value: hp_TetraSlotKey): this;\n");
        builder.append("  module<K extends hp_TetraModuleKey>(value: K, variant: hp_TetraModuleVariantFor<K>): this;\n");
        builder.append("  material(value: hp_TetraMaterialKey | string): this;\n");
        builder.append("  acceptsImprovement(value: hp_TetraMaterialImprovementKey): this;\n");
        builder.append("  acceptsImprovement(value: hp_TetraMaterialImprovementKey, level: number): this;\n");
        builder.append("  hasImprovement(value: hp_TetraMaterialImprovementKey): this;\n");
        builder.append("  hasImprovement(value: hp_TetraMaterialImprovementKey, level: hp_TetraIntegerPredicate): this;\n");
        builder.append("  aspect(value: string): this;\n");
        builder.append("  aspect(value: string, level: hp_TetraIntegerPredicate): this;\n");
        builder.append("  featureFlag(value: string): this;\n");
        builder.append("  perk(value: string): this;\n");
        builder.append("  perk(value: string, level: hp_TetraIntegerPredicate): this;\n");
        builder.append("  locked(value: string): this;\n");
        builder.append("  flag(value: string): this;\n");
        builder.append("  feature(value: string): this;\n");
        builder.append("  key(value: string): this;\n");
        builder.append("  improvement(value: hp_TetraMaterialImprovementKey): this;\n");
        builder.append("  level(value: hp_TetraIntegerPredicate): this;\n");
        builder.append("  level(min: number, max: number): this;\n");
        builder.append("  requirements(values: hp_TetraSchematicRequirement[]): this;\n");
        builder.append("  addRequirement(): hp_TetraSchematicRequirementBuilder<hp_TetraSchematicRequirementBuilder<TParent>>;\n");
        builder.append("  addRequirement(callback: (requirement: hp_TetraSchematicRequirementBuilder<hp_TetraSchematicRequirementBuilder<TParent>>) => void): this;\n");
        builder.append("  requirement(): hp_TetraSchematicRequirementBuilder<hp_TetraSchematicRequirementBuilder<TParent>>;\n");
        builder.append("  requirement(callback: (requirement: hp_TetraSchematicRequirementBuilder<hp_TetraSchematicRequirementBuilder<TParent>>) => void): this;\n");
        builder.append("  done(): TParent;\n");
        builder.append("}\n\n");
        builder.append("declare interface hp_TetraDataRegistryEventJS {\n");
        builder.append("  material(category: hp_TetraMaterialCategory): hp_TetraMaterialBuilder;\n");
        builder.append("  material(category: hp_TetraMaterialCategory, data: Record<string, any>): hp_TetraMaterialBuilder;\n");
        builder.append("  material(key: hp_TetraMaterialKey): hp_TetraMaterialBuilder;\n");
        builder.append("  material(key: hp_TetraMaterialKey, data: Record<string, any>): hp_TetraMaterialBuilder;\n");
        builder.append("  material(category: hp_TetraMaterialCategory, key: string): hp_TetraMaterialBuilder;\n");
        builder.append("  material(category: hp_TetraMaterialCategory, key: string, data: Record<string, any>): hp_TetraMaterialBuilder;\n");
        builder.append("  module(key: hp_TetraModuleKey): hp_TetraModuleBuilder;\n");
        builder.append("  module(key: hp_TetraModuleKey, data: Record<string, any>): hp_TetraModuleBuilder;\n");
        builder.append("  improvement(): hp_TetraImprovementBuilder;\n");
        builder.append("  schematic(key: hp_TetraSchematicKey): hp_TetraSchematicBuilder;\n");
        builder.append("  schematic(key: hp_TetraSchematicKey, data: Record<string, any>): hp_TetraSchematicBuilder;\n");
        builder.append("  data(directory: hp_TetraDataDirectory, key: string): hp_TetraDataBuilder;\n");
        builder.append("  data(directory: hp_TetraDataDirectory, key: string, data: Record<string, any> | any[]): hp_TetraDataBuilder;\n");
        builder.append("  entry(directory: hp_TetraDataDirectory, key: string): hp_TetraDataBuilder;\n");
        builder.append("  entry(directory: hp_TetraDataDirectory, key: string, data: Record<string, any> | any[]): hp_TetraDataBuilder;\n");
        builder.append("  lang(fullKey: string, value: string): this;\n");
        builder.append("  lang(language: string, fullKey: string, value: string): this;\n");
        builder.append("}\n\n");
        builder.append("declare namespace Internal {\n");
        builder.append("  interface TetraDataRegistryEventJS extends hp_TetraDataRegistryEventJS {}\n");
        builder.append("  interface TetraDataRegistryEventJS$AbstractBuilder<T extends Internal.TetraDataRegistryEventJS$AbstractBuilder<T>> extends hp_TetraDataBuilder {}\n");
        builder.append("  interface TetraDataRegistryEventJS$GenericBuilder extends hp_TetraDataBuilder {}\n");
        builder.append("  interface TetraDataRegistryEventJS$MaterialBuilder extends hp_TetraMaterialBuilder {}\n");
        builder.append("  interface TetraDataRegistryEventJS$ModuleBuilder extends hp_TetraModuleBuilder {}\n");
        builder.append("  interface TetraDataRegistryEventJS$ImprovementBuilder extends hp_TetraImprovementBuilder {}\n");
        builder.append("  interface TetraDataRegistryEventJS$ImprovementLevelBuilder extends hp_TetraImprovementLevelBuilder {}\n");
        builder.append("  interface TetraDataRegistryEventJS$ModuleVariantBuilder extends hp_TetraModuleVariantBuilder {}\n");
        builder.append("  interface TetraDataRegistryEventJS$ModuleExtractBuilder<P> extends hp_TetraModuleExtractBuilder<P> {}\n");
        builder.append("  interface TetraDataRegistryEventJS$SchematicBuilder extends hp_TetraSchematicBuilder {}\n");
        builder.append("  interface TetraDataRegistryEventJS$SchematicOutcomeBuilder extends hp_TetraSchematicOutcomeBuilder {}\n");
        builder.append("  interface TetraDataRegistryEventJS$SchematicRequirementBuilder<P> extends hp_TetraSchematicRequirementBuilder<P> {}\n");
        builder.append("}\n\n");
        builder.append("declare const hp_TetraDataEvents: {\n");
        builder.append("  register(callback: (event: hp_TetraDataRegistryEventJS) => void): void;\n");
        builder.append("  queue(callback: (event: hp_TetraDataRegistryEventJS) => void): void;\n");
        builder.append("};\n");
        return builder.toString();
    }

    private static String toUnion(Collection<String> values) {
        if (values == null || values.isEmpty()) {
            return "(string & {})";
        }
        StringJoiner joiner = new StringJoiner(" | ");
        for (String value : values) {
            joiner.add("\"" + TetraTypingsGenerator.escape(value) + "\"");
        }
        joiner.add("(string & {})");
        return joiner.toString();
    }

    private static SortedSet<String> buildFirstSegments(Collection<String> values) {
        TreeSet<String> segments = new TreeSet<String>();
        if (values == null) {
            return segments;
        }
        for (String value : values) {
            int separatorIndex = value.indexOf(47);
            if (separatorIndex <= 0) continue;
            segments.add(value.substring(0, separatorIndex));
        }
        return segments;
    }

    private static String toModuleVariantMap(Map<String, ? extends Collection<String>> variantsByModule, Collection<String> moduleKeys) {
        StringBuilder builder = new StringBuilder();
        builder.append("{ ");
        boolean wroteAny = false;
        if (moduleKeys != null) {
            for (String moduleKey : moduleKeys) {
                if (moduleKey == null || moduleKey.isBlank()) continue;
                if (wroteAny) {
                    builder.append("; ");
                }
                Collection<String> variants = variantsByModule == null ? null : variantsByModule.get(moduleKey);
                builder.append("\"").append(TetraTypingsGenerator.escape(moduleKey)).append("\": ").append(TetraTypingsGenerator.toUnion(variants));
                wroteAny = true;
            }
        }
        if (wroteAny) {
            builder.append(" ");
        }
        builder.append("}");
        return builder.toString();
    }

    private static SortedSet<String> buildKeyReferences(Collection<String> keys) {
        TreeSet<String> references = new TreeSet<String>();
        if (keys == null) {
            return references;
        }
        LinkedHashSet<String> folders = new LinkedHashSet<String>();
        for (String key : keys) {
            if (key == null || key.isBlank()) continue;
            String normalized = TetraDataUtil.normalizeKey(key);
            references.add("tetra:" + normalized);
            int separator = normalized.indexOf(47);
            while (separator >= 0) {
                folders.add("tetra:" + normalized.substring(0, separator + 1));
                separator = normalized.indexOf(47, separator + 1);
            }
        }
        references.addAll(folders);
        return references;
    }

    private static SortedSet<String> buildFolderPaths(Collection<String> keys) {
        TreeSet<String> folders = new TreeSet<String>();
        if (keys == null) {
            return folders;
        }
        for (String key : keys) {
            if (key == null || key.isBlank()) continue;
            String normalized = TetraDataUtil.normalizeVariantKey(key);
            int separator = normalized.indexOf(47);
            while (separator >= 0) {
                folders.add(normalized.substring(0, separator + 1));
                separator = normalized.indexOf(47, separator + 1);
            }
            if (!normalized.endsWith("/")) continue;
            folders.add(normalized);
        }
        return folders;
    }

    private static String escape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}

