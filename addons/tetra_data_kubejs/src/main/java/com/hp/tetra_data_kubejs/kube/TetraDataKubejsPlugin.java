package com.hp.tetra_data_kubejs.kube;

import com.hp.tetra_data_kubejs.TetraDataKubejs;
import com.hp.tetra_data_kubejs.data.TetraRuntimeDataPackManager;
import com.hp.tetra_data_kubejs.data.TetraScriptRegistry;
import com.hp.tetra_data_kubejs.kube.event.TetraDataRegistryEventJS;
import dev.latvian.mods.kubejs.event.EventGroupRegistry;
import dev.latvian.mods.kubejs.generator.KubeAssetGenerator;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.ScriptManager;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.server.ServerScriptManager;

public class TetraDataKubejsPlugin implements KubeJSPlugin {
    @Override
    public void registerEvents(EventGroupRegistry registry) {
        registry.register(TetraDataEvents.GROUP);
    }

    @Override
    public void afterScriptsLoaded(ScriptManager manager) {
        if (manager.scriptType == ScriptType.STARTUP) {
            TetraScriptRegistry.clearStartup();
            TetraDataEvents.REGISTER.post(ScriptType.STARTUP, new TetraDataRegistryEventJS(TetraScriptRegistry.Scope.STARTUP));
            return;
        }
        if (manager.scriptType != ScriptType.SERVER || !(manager instanceof ServerScriptManager)) {
            return;
        }
        long totalStart = System.nanoTime();
        long beginStagingStart = System.nanoTime();
        TetraScriptRegistry.beginServerStaging();
        long beginStagingNanos = System.nanoTime() - beginStagingStart;
        long queueStart = System.nanoTime();
        TetraDataEvents.QUEUE.post(ScriptType.SERVER, new TetraDataRegistryEventJS(TetraScriptRegistry.Scope.SERVER_STAGING));
        long queueNanos = System.nanoTime() - queueStart;
        long commitStart = System.nanoTime();
        TetraScriptRegistry.commitServerStaging();
        long commitNanos = System.nanoTime() - commitStart;
        long refreshStart = System.nanoTime();
        TetraRuntimeDataPackManager.RefreshStats refreshStats = TetraRuntimeDataPackManager.refresh((ServerScriptManager) manager);
        long refreshNanos = System.nanoTime() - refreshStart;
        long totalNanos = System.nanoTime() - totalStart;
        if (refreshStats == null) {
            TetraDataKubejs.LOGGER.info("[tetra_data_kubejs] server reload timings: beginStaging={} ms, queue={} ms, commit={} ms, runtimeRefreshSkipped, total={} ms", new Object[]{TetraDataKubejsPlugin.nanosToMillis(beginStagingNanos), TetraDataKubejsPlugin.nanosToMillis(queueNanos), TetraDataKubejsPlugin.nanosToMillis(commitNanos), TetraDataKubejsPlugin.nanosToMillis(totalNanos)});
            return;
        }
        TetraScriptRegistry.InjectStats injectStats = refreshStats.injectStats();
        TetraDataKubejs.LOGGER.info("[tetra_data_kubejs] server reload timings: beginStaging={} ms, queue={} ms, commit={} ms, refresh={} ms, total={} ms", new Object[]{TetraDataKubejsPlugin.nanosToMillis(beginStagingNanos), TetraDataKubejsPlugin.nanosToMillis(queueNanos), TetraDataKubejsPlugin.nanosToMillis(commitNanos), TetraDataKubejsPlugin.nanosToMillis(refreshNanos), TetraDataKubejsPlugin.nanosToMillis(totalNanos)});
        TetraDataKubejs.LOGGER.info("[tetra_data_kubejs] tetra inject timings: entries={}, injected={}, skipped={}, validationFailed={}, refreshKnownKeys={} ms, prepareExport={} ms, normalizeValidate={} ms, addData={} ms, writeGenerated={} ms, attach={} ms, clearRuntime={} ms, injectTotal={} ms, registerNamespaces={} ms, generatedExportWritten={}", new Object[]{injectStats.totalEntries(), injectStats.injectedEntries(), injectStats.skippedEntries(), injectStats.validationFailedEntries(), TetraDataKubejsPlugin.nanosToMillis(injectStats.refreshKnownKeysNanos()), TetraDataKubejsPlugin.nanosToMillis(injectStats.prepareExportNanos()), TetraDataKubejsPlugin.nanosToMillis(injectStats.normalizeAndValidateNanos()), TetraDataKubejsPlugin.nanosToMillis(injectStats.addDataNanos()), TetraDataKubejsPlugin.nanosToMillis(injectStats.writeGeneratedNanos()), TetraDataKubejsPlugin.nanosToMillis(refreshStats.attachNanos()), TetraDataKubejsPlugin.nanosToMillis(refreshStats.clearRuntimeDataNanos()), TetraDataKubejsPlugin.nanosToMillis(refreshStats.injectNanos()), TetraDataKubejsPlugin.nanosToMillis(refreshStats.registerNamespacesNanos()), injectStats.generatedExportWritten()});
    }

    @Override
    public void generateAssets(KubeAssetGenerator generator) {
        TetraScriptRegistry.generateAssetJsons(generator);
    }

    private static String nanosToMillis(long nanos) {
        return String.format("%.2f", (double)nanos / 1000000.0);
    }
}
