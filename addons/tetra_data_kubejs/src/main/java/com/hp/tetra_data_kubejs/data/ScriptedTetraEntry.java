/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.packs.resources.ResourceManager
 */
package com.hp.tetra_data_kubejs.data;

import com.google.gson.JsonElement;
import com.hp.tetra_data_kubejs.data.JsonMergeUtil;
import com.hp.tetra_data_kubejs.data.TetraDataUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

public class ScriptedTetraEntry {
    private final String directory;
    private String key;
    private JsonElement data;
    private boolean mergeExisting;
    private String copyFromKey;

    public ScriptedTetraEntry(String directory, String key) {
        this.directory = directory;
        this.key = key;
    }

    public ScriptedTetraEntry(ScriptedTetraEntry other) {
        this.directory = other.directory;
        this.key = other.key;
        this.data = other.data == null ? null : other.data.deepCopy();
        this.mergeExisting = other.mergeExisting;
        this.copyFromKey = other.copyFromKey;
    }

    public String getDirectory() {
        return this.directory;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key == null ? null : TetraDataUtil.normalizeKey(key);
    }

    public void setFolderKey(String key) {
        this.key = key == null ? null : TetraDataUtil.normalizeFolderKey(key);
    }

    public JsonElement getData() {
        return this.data;
    }

    public void setData(JsonElement data) {
        this.data = data == null ? null : data.deepCopy();
    }

    public void merge(JsonElement json) {
        if (json == null || json.isJsonNull()) {
            return;
        }
        this.data = this.data == null ? json.deepCopy() : JsonMergeUtil.merge(this.data, json);
    }

    public boolean shouldReplace() {
        return ScriptedTetraEntry.isReplaceData(this.data);
    }

    public boolean shouldMergeExisting() {
        return this.mergeExisting;
    }

    public void setMergeExisting(boolean mergeExisting) {
        this.mergeExisting = mergeExisting;
    }

    public String getCopyFromKey() {
        return this.copyFromKey;
    }

    public void setCopyFromKey(String copyFromKey) {
        this.copyFromKey = copyFromKey == null ? null : TetraDataUtil.normalizeKey(copyFromKey);
    }

    public boolean hasData() {
        return this.data != null && !this.data.isJsonNull();
    }

    public ResourceLocation getResourceId() {
        return TetraDataUtil.dataId(this.directory, this.key);
    }

    public JsonElement createInjectedData(ResourceManager resourceManager) {
        JsonElement existing;
        JsonElement copied;
        if (!this.hasData() && !this.mergeExisting && this.copyFromKey == null) {
            return null;
        }
        JsonElement result = null;
        if (this.copyFromKey != null && (copied = TetraDataUtil.readJson(resourceManager, TetraDataUtil.dataId(this.directory, this.copyFromKey))) != null && !copied.isJsonNull()) {
            result = copied.deepCopy();
        }
        if (this.mergeExisting && (existing = TetraDataUtil.readJson(resourceManager, this.getResourceId())) != null && !existing.isJsonNull()) {
            JsonElement jsonElement = result = result == null ? existing.deepCopy() : JsonMergeUtil.merge(result, existing);
        }
        if (this.hasData()) {
            result = result == null ? this.data.deepCopy() : JsonMergeUtil.merge(result, this.data);
        }
        return result;
    }

    private static boolean isReplaceData(JsonElement json) {
        return json != null && json.isJsonObject() && json.getAsJsonObject().has("replace") && json.getAsJsonObject().get("replace").isJsonPrimitive() && json.getAsJsonObject().get("replace").getAsBoolean();
    }
}

