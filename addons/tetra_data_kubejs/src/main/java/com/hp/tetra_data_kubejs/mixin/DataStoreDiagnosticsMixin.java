/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.JsonElement
 *  net.minecraft.resources.ResourceLocation
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  se.mickelus.mutil.data.DataStore
 */
package com.hp.tetra_data_kubejs.mixin;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.hp.tetra_data_kubejs.data.TetraDataLoadDiagnostics;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import se.mickelus.mutil.data.DataStore;

@Mixin(value={DataStore.class}, remap=false)
public abstract class DataStoreDiagnosticsMixin {
    @Shadow
    protected Gson gson;
    @Shadow
    protected String directory;
    @Shadow
    protected Class<?> dataClass;

    @Inject(method={"parseData"}, at={@At(value="HEAD")}, remap=false)
    private void tetraDataKubejs$logDataIssues(Map<ResourceLocation, JsonElement> data, CallbackInfo ci) {
        TetraDataLoadDiagnostics.inspectDataStore(this.directory, data, this.gson, this.dataClass);
    }
}

