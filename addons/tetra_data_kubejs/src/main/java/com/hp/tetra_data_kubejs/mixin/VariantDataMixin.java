/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Rarity
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 *  se.mickelus.tetra.module.data.VariantData
 */
package com.hp.tetra_data_kubejs.mixin;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import se.mickelus.tetra.module.data.VariantData;

@Mixin(value={VariantData.class}, remap=false)
public abstract class VariantDataMixin {
    @Inject(method={"merge"}, at={@At(value="RETURN")}, cancellable=true, remap=false)
    private static void tetraDataKubejs$mergeInheritedItemProperties(VariantData first, VariantData second, CallbackInfoReturnable<VariantData> cir) {
        VariantData result = (VariantData)cir.getReturnValue();
        if (result == null || second.replace) {
            return;
        }
        result.tags = VariantDataMixin.tetraDataKubejs$mergeTags(first.tags, second.tags);
        result.rarity = VariantDataMixin.tetraDataKubejs$mergeRarity(first.rarity, second.rarity);
    }

    private static Set<TagKey<Item>> tetraDataKubejs$mergeTags(Set<TagKey<Item>> first, Set<TagKey<Item>> second) {
        if (first == null) {
            return second;
        }
        if (second == null) {
            return first;
        }
        return Stream.concat(first.stream(), second.stream()).collect(Collectors.toSet());
    }

    private static Rarity tetraDataKubejs$mergeRarity(Rarity first, Rarity second) {
        if (first == null) {
            return second;
        }
        if (second == null) {
            return first;
        }
        return first.ordinal() > second.ordinal() ? first : second;
    }
}

