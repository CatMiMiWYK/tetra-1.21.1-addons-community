/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.Item$Properties
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  se.mickelus.tetra.data.DataManager
 *  se.mickelus.tetra.items.modular.IModularItem
 *  se.mickelus.tetra.items.modular.ModularItem
 */
package com.hp.tetra_data_kubejs.mixin;

import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import se.mickelus.tetra.data.DataManager;
import se.mickelus.tetra.items.modular.IModularItem;
import se.mickelus.tetra.items.modular.ModularItem;

@Mixin(value={ModularItem.class}, remap=false)
public abstract class ModularItemMixin {
    @Inject(method={"<init>"}, at={@At(value="RETURN")}, remap=false)
    private void tetraDataKubejs$listenExtraReloads(Item.Properties properties, CallbackInfo ci) {
        IModularItem modularItem = (IModularItem)this;
        DataManager.instance.materialData.onReload(() -> ((IModularItem)modularItem).clearCaches());
        DataManager.instance.improvementData.onReload(() -> ((IModularItem)modularItem).clearCaches());
        DataManager.instance.synergyData.onReload(() -> ((IModularItem)modularItem).clearCaches());
    }
}

