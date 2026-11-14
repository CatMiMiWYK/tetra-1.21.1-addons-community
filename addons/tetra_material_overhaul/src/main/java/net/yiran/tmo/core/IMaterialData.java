/*
 * Decompiled with CFR 0.152.
 */
package net.yiran.tmo.core;

import java.util.Map;
import net.yiran.tmo.ContextData;

public interface IMaterialData {
    public void setContextData(Map<String, ContextData> var1);

    public Map<String, ContextData> getContextData();
}

