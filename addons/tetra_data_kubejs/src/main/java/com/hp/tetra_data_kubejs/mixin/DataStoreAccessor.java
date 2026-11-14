/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Accessor
 *  se.mickelus.mutil.data.DataStore
 */
package com.hp.tetra_data_kubejs.mixin;

import com.google.gson.Gson;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import se.mickelus.mutil.data.DataStore;

@Mixin(value={DataStore.class}, remap=false)
public interface DataStoreAccessor {
    @Accessor(value="gson")
    public Gson tetraDataKubejs$getGson();

    @Accessor(value="directory")
    public String tetraDataKubejs$getDirectory();

    @Accessor(value="dataClass")
    public Class<?> tetraDataKubejs$getDataClass();
}

