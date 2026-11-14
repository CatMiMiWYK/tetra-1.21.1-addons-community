/*
 * Decompiled with CFR 0.152.
 */
package com.hp.tetra_data_kubejs.data;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public final class TetraColorUtil {
    private static final Map<String, String> PRESET_COLORS = TetraColorUtil.createPresetColors();

    private TetraColorUtil() {
    }

    public static String resolve(String value) {
        String normalized = TetraColorUtil.normalize(value);
        return PRESET_COLORS.getOrDefault(normalized, normalized);
    }

    public static Set<String> presetNames() {
        return PRESET_COLORS.keySet();
    }

    private static String normalize(String value) {
        String normalized = value.trim().toLowerCase(Locale.ROOT).replace('-', '_').replace(' ', '_');
        if (normalized.startsWith("#")) {
            normalized = normalized.substring(1);
        }
        return normalized;
    }

    private static Map<String, String> createPresetColors() {
        LinkedHashMap<String, String> colors = new LinkedHashMap<String, String>();
        colors.put("white", "f9fffe");
        colors.put("light_gray", "9d9d97");
        colors.put("gray", "474f52");
        colors.put("black", "1d1d21");
        colors.put("brown", "835432");
        colors.put("red", "b02e26");
        colors.put("orange", "f9801d");
        colors.put("yellow", "fed83d");
        colors.put("lime", "80c71f");
        colors.put("green", "5e7c16");
        colors.put("cyan", "169c9c");
        colors.put("light_blue", "3ab3da");
        colors.put("blue", "3c44aa");
        colors.put("purple", "8932b8");
        colors.put("magenta", "c74ebd");
        colors.put("pink", "f38baa");
        colors.put("iron", "c4c4c4");
        colors.put("steel", "6f7b84");
        colors.put("silver", "cfd6e3");
        colors.put("gold", "f9c63d");
        colors.put("copper", "c46c44");
        colors.put("bronze", "b27a3f");
        colors.put("netherite", "443a3b");
        colors.put("obsidian", "342d4b");
        colors.put("diamond", "5ce6e6");
        colors.put("emerald", "17dd62");
        colors.put("lapis", "2546ad");
        colors.put("redstone", "b51212");
        colors.put("amethyst", "9a5cc6");
        colors.put("quartz", "e9ecec");
        colors.put("prismarine", "64c7b8");
        colors.put("warden", "2f6f6c");
        colors.put("echo", "3db7c4");
        return colors;
    }
}

