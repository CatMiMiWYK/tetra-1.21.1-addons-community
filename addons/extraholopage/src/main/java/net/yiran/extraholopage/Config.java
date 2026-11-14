package net.yiran.extraholopage;

import java.util.ArrayList;
import java.util.List;
import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    public static final ModConfigSpec SPEC;
    private static final ModConfigSpec.Builder BUILDER;
    public static final ModConfigSpec.ConfigValue<Boolean> HOLO_SHOW_ALL_RARITY;
    public static final ModConfigSpec.ConfigValue<Boolean> ENABLE_STATBAR_SHOW_CONTROL;
    public static final ModConfigSpec.ConfigValue<Boolean> IGNORE_HOLO_ITEM;
    public static final ModConfigSpec.ConfigValue<Integer> HOLO_MATERIAL_LINE;
    public static final ModConfigSpec.ConfigValue<Integer> HOLO_MODULE_LINE;
    public static final ModConfigSpec.ConfigValue<Integer> SHOW_FILTER_AND_SORTER_ELEMENTS;
    public static final ModConfigSpec.ConfigValue<Integer> MODIFY_IMPROVEMENT_SPACING;
    public static final ModConfigSpec.ConfigValue<Boolean> ENABLE_MATERIAL_TOOLTIP;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> HIDE_MATERIAL_KEYS;
    public static final ModConfigSpec.ConfigValue<Boolean> ENABLE_SPECIAL_MATERIAL_TOOLTIP;
    public static final ModConfigSpec.ConfigValue<Boolean> ENABLE_SCROLL_TOOLTIP;
    public static final ModConfigSpec.ConfigValue<Boolean> SHOW_NEED_ADVANCED_TOOLTIP;
    public static final ModConfigSpec.ConfigValue<Boolean> SHOW_REQUIRED_TOOL;
    public static final ModConfigSpec.ConfigValue<Boolean> SHOW_DURABILITY;
    public static final ModConfigSpec.ConfigValue<Boolean> SHOW_SOURCE;
    public static final ModConfigSpec.ConfigValue<Boolean> SHOW_CATEGORY;
    public static final ModConfigSpec.ConfigValue<Boolean> SHOW_BASIC;
    public static final ModConfigSpec.ConfigValue<Boolean> SHOW_XP_COST;
    public static final ModConfigSpec.ConfigValue<Boolean> SHOW_EFFECTS;
    public static final ModConfigSpec.ConfigValue<Boolean> SHOW_ATTRIBUTES;
    public static final ModConfigSpec.ConfigValue<Boolean> SHOW_ASPECTS;
    public static final ModConfigSpec.ConfigValue<Boolean> SHOW_IMPROVEMENTS;
    public static final ModConfigSpec.ConfigValue<Boolean> SHOW_TOOL_LEVEL;
    public static final ModConfigSpec.ConfigValue<Boolean> SHOW_INTEGRITY_INFO;
    public static final ModConfigSpec.ConfigValue<Integer> PROGRESS_BAR_SIZE;
    public static final ModConfigSpec.ConfigValue<Integer> PROGRESS_SHOW_TICK;
    public static final ModConfigSpec.ConfigValue<String> PROGRESS_STYLE;
    public static final ModConfigSpec.ConfigValue<Integer> COLOR_1;
    public static final ModConfigSpec.ConfigValue<Integer> COLOR_2;
    public static final ModConfigSpec.ConfigValue<Integer> COLOR_3;
    public static final ModConfigSpec.ConfigValue<Integer> COLOR_GOOD;
    public static final ModConfigSpec.ConfigValue<Integer> COLOR_BAD;

    static {
        BUILDER = new ModConfigSpec.Builder();
        HOLO_SHOW_ALL_RARITY = BUILDER.comment("\u663e\u793a\u6240\u6709\u7a00\u6709\u5ea6\u7684\u539f\u7406\u56fe\u5c5e\u6027\u3002").comment("Display schematic properties for all rarities.").define("holoShowAllRarity", true);
        IGNORE_HOLO_ITEM = BUILDER.comment("\u5ffd\u7565\u5168\u606f\u7403\u7269\u54c1\u9700\u6c42\u3002").comment("ignoring holo item requirements.").define("ignoreHoloItem", true);
        HOLO_MATERIAL_LINE = BUILDER.comment("\u4fee\u6539\u5168\u606f\u7403\u663e\u793a\u6750\u6599\u884c\u6570").comment("midify material line in holo").define("holoMaterialLine",  5);
        HOLO_MODULE_LINE = BUILDER.comment("\u4fee\u6539\u5168\u606f\u7403\u663e\u793a\u6a21\u5757\u884c\u6570").comment("midify module line in holo").define("holoModuleLine",  5);
        ENABLE_STATBAR_SHOW_CONTROL = BUILDER.comment("\u662f\u5426\u542f\u7528statbar\u663e\u793a\u63a7\u5236").define("enableStatBarShowControl", false);
        MODIFY_IMPROVEMENT_SPACING = BUILDER.comment("\u4fee\u6539\u6539\u8fdb\u95f4\u8ddd\u3002").comment("Modify Improvement spacing.").define("modify_improvement_spacing",  50);
        SHOW_FILTER_AND_SORTER_ELEMENTS = BUILDER.comment("\u5728\u6750\u6599\u9875\u9762\u663e\u793asorter\u548cfilter\u7684\u6570\u91cf").define("showFilterAndSorterElements",  10);
        BUILDER.push("Tooltip");
        ENABLE_SCROLL_TOOLTIP = BUILDER.comment("\u662f\u5426\u542f\u7528\u6dfb\u52a0\u5377\u8f74Tooltip-\u4fee\u6539\u6b64\u914d\u7f6e\u9700\u91cd\u542f\u751f\u6548").define("enableScrollTooltip", true);
        ENABLE_SPECIAL_MATERIAL_TOOLTIP = BUILDER.comment("\u662f\u5426\u542f\u7528\u6dfb\u52a0\u7279\u6b8a\u6750\u6599Tooltip-\u4fee\u6539\u6b64\u914d\u7f6e\u9700\u91cd\u542f\u751f\u6548").define("enableSpecialMaterialTooltip", true);
        ENABLE_MATERIAL_TOOLTIP = BUILDER.comment("\u662f\u5426\u542f\u7528\u6dfb\u52a0\u6750\u6599Tooltip-\u4fee\u6539\u6b64\u914d\u7f6e\u9700\u91cd\u542f\u751f\u6548").define("enableMaterialTooltip", true);
        HIDE_MATERIAL_KEYS = BUILDER.comment("\u6309material\u7684key\u9690\u85cftooltip").defineList("hideMaterialKeys", new ArrayList<>(), o -> o instanceof String);
        SHOW_NEED_ADVANCED_TOOLTIP = BUILDER.comment("\u662f\u5426\u9700\u8981\u5728\u6253\u5f00\u9ad8\u7ea7\u7269\u54c1\u63d0\u793a\u7684\u65f6\u5019\u624d\u663e\u793atooltip-\u4fee\u6539\u6b64\u914d\u7f6e\u9700\u91cd\u542f\u751f\u6548").define("showNeedAdvancedTooltip", true);
        SHOW_REQUIRED_TOOL = BUILDER.comment("\u662f\u5426\u5728tooltip\u91cc\u9762\u63d0\u793a\u9700\u6c42\u5de5\u5177\u7b49\u7ea7").define("showRequiredTool", true);
        SHOW_DURABILITY = BUILDER.comment("\u662f\u5426\u5728tooltip\u91cc\u9762\u63d0\u793a\u6750\u6599\u57fa\u7840\u8010\u4e45\u5ea6").define("showDurability", true);
        SHOW_SOURCE = BUILDER.comment("\u662f\u5426\u5728tooltip\u91cc\u9762\u63d0\u793a\u6750\u6599\u6765\u6e90\u6587\u4ef6").define("showSource", false);
        SHOW_CATEGORY = BUILDER.comment("\u662f\u5426\u5728tooltip\u91cc\u9762\u63d0\u793a\u6750\u6599\u7c7b\u578b").define("showCategory", true);
        SHOW_BASIC = BUILDER.comment("\u662f\u5426\u5728tooltip\u91cc\u63d0\u793a\u6750\u6599\u4e09\u7ef4").define("showBasic", true);
        SHOW_XP_COST = BUILDER.comment("\u662f\u5426\u5728tooltip\u91cc\u63d0\u793a\u7ecf\u9a8c\u6d88\u8017").define("showXpCost", true);
        SHOW_EFFECTS = BUILDER.comment("\u662f\u5426\u5728tooltip\u91cc\u9762\u63d0\u793a\u6750\u6599\u81ea\u5e26effects").define("showEffects", true);
        SHOW_ATTRIBUTES = BUILDER.comment("\u662f\u5426\u5728tooltip\u91cc\u9762\u63d0\u793a\u6750\u6599\u81ea\u5e26attributes").define("showAttributes", true);
        SHOW_ASPECTS = BUILDER.comment("\u662f\u5426\u5728tooltip\u91cc\u9762\u63d0\u793a\u6750\u6599\u81ea\u5e26aspects").define("showAspects", true);
        SHOW_IMPROVEMENTS = BUILDER.comment("\u662f\u5426\u5728tooltip\u91cc\u9762\u63d0\u793a\u6750\u6599\u81ea\u5e26improvements").define("showImprovements", true);
        SHOW_TOOL_LEVEL = BUILDER.comment("\u662f\u5426\u5728tooltip\u91cc\u9762\u63d0\u793a\u6750\u6599\u5de5\u5177\u7b49\u7ea7\u53ca\u5176\u6548\u7387").define("showToolLevel", true);
        SHOW_INTEGRITY_INFO = BUILDER.comment("\u662f\u5426\u5728tooltip\u91cc\u9762\u63d0\u793a\u6750\u6599\u5b8c\u6574\u5ea6\u5956\u52b1\u4e0e\u82b1\u8d39").define("showIntegrityInfo", true);
        BUILDER.push("ProgressBar");
        PROGRESS_BAR_SIZE = BUILDER.comment("\u8bbe\u7f6e\u8fdb\u5ea6\u6761\u7684\u957f\u5ea6").define("progressBarSize",  30);
        PROGRESS_SHOW_TICK = BUILDER.comment("\u8bbe\u7f6e\u8fdb\u5ea6\u6761\u7684\u663e\u793a\u65f6\u95f4").define("progressShowTick",  50);
        PROGRESS_STYLE = BUILDER.comment("\u8bbe\u7f6e\u8fdb\u5ea6\u6761\u7684\u6837\u5f0f,\u5355\u4e2a\u5b57\u7b26").define("progressStyle",  "|");
        BUILDER.pop();
        BUILDER.push("Color");
        BUILDER.comment("\u8bbe\u7f6e\u989c\u8272\uff0c\u9700\u91cd\u542f\u751f\u6548\uff0c\u4e3a0\u5219\u4f7f\u7528\u9884\u8bbe\u989c\u8272");
        COLOR_1 = BUILDER.define("color1",  0);
        COLOR_2 = BUILDER.define("color2",  0);
        COLOR_3 = BUILDER.define("color3",  0);
        COLOR_GOOD = BUILDER.define("colorGood",  0);
        COLOR_BAD = BUILDER.define("colorBad",  0);
        BUILDER.pop(2);
        SPEC = BUILDER.build();
    }
}

