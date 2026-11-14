package com.hp.tetra_data_kubejs.data;

import dev.latvian.mods.kubejs.script.data.GeneratedDataStage;
import dev.latvian.mods.kubejs.script.data.VirtualDataPack;
import dev.latvian.mods.kubejs.server.ServerScriptManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

public final class TetraRuntimeDataPackManager {
    private static volatile ResourceManager capturedResourceManager;

    private TetraRuntimeDataPackManager() {
    }

    public static void captureResourceManager(ResourceManager resourceManager) {
        capturedResourceManager = resourceManager;
    }

    public static synchronized RefreshStats refresh(ServerScriptManager manager) {
        if (manager == null) {
            return null;
        }
        VirtualDataPack pack = manager.virtualPacks.get(GeneratedDataStage.LAST);
        if (pack == null) {
            return null;
        }
        ResourceManager resourceManager = TetraRuntimeDataPackManager.resolveResourceManager();
        long injectStart = System.nanoTime();
        TetraScriptRegistry.InjectStats injectStats = TetraScriptRegistry.inject(pack, resourceManager, false);
        long injectNanos = System.nanoTime() - injectStart;
        return new RefreshStats(injectStats, 0L, 0L, injectNanos, 0L);
    }

    private static ResourceManager resolveResourceManager() {
        ResourceManager resourceManager = capturedResourceManager;
        if (resourceManager != null) {
            return resourceManager;
        }
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        return server == null ? null : server.getResourceManager();
    }

    public record RefreshStats(TetraScriptRegistry.InjectStats injectStats, long attachNanos, long clearRuntimeDataNanos, long injectNanos, long registerNamespacesNanos) {
        public long totalNanos() {
            return this.attachNanos + this.clearRuntimeDataNanos + this.injectNanos + this.registerNamespacesNanos;
        }
    }
}
