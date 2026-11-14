/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonNull
 *  com.google.gson.JsonObject
 */
package com.hp.tetra_data_kubejs.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

public final class JsonMergeUtil {
    private JsonMergeUtil() {
    }

    public static JsonElement merge(JsonElement base, JsonElement addition) {
        if (addition == null || addition.isJsonNull()) {
            return base == null ? JsonNull.INSTANCE : base.deepCopy();
        }
        if (base == null || base.isJsonNull()) {
            return addition.deepCopy();
        }
        if (base.isJsonObject() && addition.isJsonObject()) {
            JsonObject result = base.deepCopy().getAsJsonObject();
            JsonObject additionObject = addition.getAsJsonObject();
            for (String key : additionObject.keySet()) {
                result.add(key, JsonMergeUtil.merge(result.get(key), additionObject.get(key)));
            }
            return result;
        }
        if (base.isJsonArray() && addition.isJsonArray()) {
            JsonArray result = base.deepCopy().getAsJsonArray();
            JsonArray additionArray = addition.getAsJsonArray();
            for (JsonElement element : additionArray) {
                JsonElement copy = element.deepCopy();
                if (JsonMergeUtil.contains(result, copy)) continue;
                result.add(copy);
            }
            return result;
        }
        return addition.deepCopy();
    }

    private static boolean contains(JsonArray array, JsonElement candidate) {
        for (JsonElement element : array) {
            if (!element.equals(candidate)) continue;
            return true;
        }
        return false;
    }
}

