/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  net.minecraft.resources.ResourceLocation
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  se.mickelus.mutil.data.MergingDataStore
 */
package com.hp.tetra_data_kubejs.mixin;

import com.google.gson.JsonElement;
import com.hp.tetra_data_kubejs.data.TetraDataLoadDiagnostics;
import com.hp.tetra_data_kubejs.mixin.DataStoreAccessor;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import se.mickelus.mutil.data.MergingDataStore;

@Mixin(value={MergingDataStore.class}, remap=false)
public abstract class MergingDataStoreDiagnosticsMixin {
    @Shadow
    protected Class<?> arrayClass;

    @Inject(method={"parseData"}, at={@At(value="HEAD")}, remap=false)
    private void tetraDataKubejs$logDataIssues(Map<ResourceLocation, JsonElement> data, CallbackInfo ci) {
        DataStoreAccessor accessor = (DataStoreAccessor)((Object)this);
        TetraDataLoadDiagnostics.inspectDataStore(accessor.tetraDataKubejs$getDirectory(), data, accessor.tetraDataKubejs$getGson(), this.arrayClass);
    }
}

