/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.resources.language.I18n
 */
package com.hp.tetra_data_kubejs.client;

import com.hp.tetra_data_kubejs.data.TetraScriptRegistry;
import java.util.IllegalFormatException;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;

public final class TetraClientTranslationHelper {
    private static final Map<String, Map<String, String>> CLIENT_TRANSLATIONS = new LinkedHashMap<String, Map<String, String>>();

    private TetraClientTranslationHelper() {
    }

    public static boolean hasCustomTranslation(String key) {
        return TetraClientTranslationHelper.resolveRaw(key) != null;
    }

    public static String translate(String key, Object[] args) {
        String value = TetraClientTranslationHelper.resolveRaw(key);
        if (value == null) {
            return null;
        }
        try {
            return String.format(Locale.ROOT, value, args == null ? new Object[]{} : args);
        }
        catch (IllegalFormatException exception) {
            return "Format error: " + value;
        }
    }

    public static Map<String, String> exportTranslation(String key) {
        String value = I18n.get((String)key, (Object[])new Object[0]);
        if (value == null || value.isBlank() || key.equals(value)) {
            return Map.of();
        }
        return Map.of(TetraClientTranslationHelper.currentLanguage(), value);
    }

    public static synchronized void replaceClientTranslations(Map<String, Map<String, String>> translations) {
        CLIENT_TRANSLATIONS.clear();
        if (translations == null || translations.isEmpty()) {
            return;
        }
        translations.forEach((language, values) -> {
            if (language == null || language.isBlank() || values == null || values.isEmpty()) {
                return;
            }
            CLIENT_TRANSLATIONS.put(language.toLowerCase(Locale.ROOT), new LinkedHashMap(values));
        });
    }

    public static synchronized void clearClientTranslations() {
        CLIENT_TRANSLATIONS.clear();
    }

    private static String resolveRaw(String key) {
        String language = TetraClientTranslationHelper.currentLanguage();
        String value = TetraClientTranslationHelper.findClientTranslation(language, key);
        if (value != null) {
            return value;
        }
        value = TetraScriptRegistry.findTranslation(language, key);
        if (value != null) {
            return value;
        }
        if (!"zh_cn".equals(language)) {
            value = TetraClientTranslationHelper.findClientTranslation("zh_cn", key);
            if (value != null) {
                return value;
            }
            return TetraScriptRegistry.findTranslation("zh_cn", key);
        }
        return null;
    }

    private static synchronized String findClientTranslation(String language, String key) {
        Map<String, String> values = CLIENT_TRANSLATIONS.get(language.toLowerCase(Locale.ROOT));
        return values == null ? null : values.get(key);
    }

    private static String currentLanguage() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft == null || minecraft.getLanguageManager() == null || minecraft.getLanguageManager().getSelected() == null) {
            return "zh_cn";
        }
        return minecraft.getLanguageManager().getSelected();
    }
}

