package com.sswltzz.tetracompat.mixin;

import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = RecipeManager.class)
public interface RecipeManagerAccessor {
    @Accessor
    public void setByName(Map<ResourceLocation, RecipeHolder<?>> value);

    @Accessor
    public Map<ResourceLocation, RecipeHolder<?>> getByName();

    @Accessor
    public void setByType(com.google.common.collect.Multimap<RecipeType<?>, RecipeHolder<?>> value);
}
