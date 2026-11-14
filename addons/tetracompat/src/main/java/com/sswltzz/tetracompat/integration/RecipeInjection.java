package com.sswltzz.tetracompat.integration;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.sswltzz.tetracompat.TCConfig;
import com.sswltzz.tetracompat.mixin.RecipeManagerAccessor;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.fml.ModList;

public class RecipeInjection {
    public static void injectRecipes(RecipeManagerAccessor manager) {
        ImmutableMap.Builder<ResourceLocation, RecipeHolder<?>> byNameBuilder = ImmutableMap.builder();
        byNameBuilder.putAll(manager.getByName());
        if (ModList.get().isLoaded("farmersdelight") && ModList.get().isLoaded("tetracelium") && TCConfig.tetraceliumKnifeCompat) {
            FarmersDelightCompat.injectRecipes(manager.getByName(), byNameBuilder::put);
        }
        ImmutableMap<ResourceLocation, RecipeHolder<?>> newByName = byNameBuilder.build();
        ImmutableMultimap.Builder<RecipeType<?>, RecipeHolder<?>> byTypeBuilder = ImmutableMultimap.builder();
        newByName.forEach((id, holder) -> byTypeBuilder.put(holder.value().getType(), holder));
        manager.setByName(newByName);
        manager.setByType(byTypeBuilder.build());
    }
}
