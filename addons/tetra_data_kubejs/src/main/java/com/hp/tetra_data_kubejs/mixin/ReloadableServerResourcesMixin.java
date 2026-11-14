package com.hp.tetra_data_kubejs.mixin;

import com.hp.tetra_data_kubejs.data.TetraRuntimeDataPackManager;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.commands.Commands;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.flag.FeatureFlagSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={ReloadableServerResources.class}, priority=500)
public abstract class ReloadableServerResourcesMixin {
    @Inject(method={"loadResources"}, at={@At(value="HEAD")})
    private static void tetraDataKubejs$captureResourceManager(ResourceManager resourceManager, LayeredRegistryAccess<RegistryLayer> registries, FeatureFlagSet enabledFeatures, Commands.CommandSelection commandSelection, int functionCompilationLevel, Executor backgroundExecutor, Executor gameExecutor, CallbackInfoReturnable<CompletableFuture<ReloadableServerResources>> cir) {
        TetraRuntimeDataPackManager.captureResourceManager(resourceManager);
    }
}
