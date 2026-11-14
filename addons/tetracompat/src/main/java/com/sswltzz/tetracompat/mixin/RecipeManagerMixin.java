package com.sswltzz.tetracompat.mixin;

import com.google.common.collect.Multimap;
import com.google.gson.JsonElement;
import com.sswltzz.tetracompat.integration.RecipeInjection;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = RecipeManager.class)
public abstract class RecipeManagerMixin {
    @Accessor
    public abstract void setByName(Map<ResourceLocation, RecipeHolder<?>> value);

    @Accessor
    public abstract Map<ResourceLocation, RecipeHolder<?>> getByName();

    @Accessor
    public abstract void setByType(Multimap<RecipeType<?>, RecipeHolder<?>> value);

    @Inject(at = @At("RETURN"), method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V")
    public void injectRecipes(Map<ResourceLocation, JsonElement> json, ResourceManager resourceManager, ProfilerFiller profiler, CallbackInfo ci) {
        RecipeInjection.injectRecipes((RecipeManagerAccessor) (Object) this);
    }
}
