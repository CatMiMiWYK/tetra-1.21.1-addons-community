/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  dev.latvian.mods.kubejs.core.MinecraftServerKJS
 *  dev.latvian.mods.kubejs.event.KubeEvent
 *  dev.latvian.mods.kubejs.script.ScriptType
 *  dev.latvian.mods.kubejs.script.ScriptTypeHolder
 *  dev.latvian.mods.kubejs.server.ServerScriptManager
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.commands.Commands
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.commands.ReloadCommand
 *  net.minecraft.server.packs.resources.CloseableResourceManager
 *  net.minecraft.server.packs.resources.ResourceManager
 *  net.minecraft.util.profiling.InactiveProfiler
 *  net.minecraft.util.profiling.ProfilerFiller
 *  net.minecraft.world.level.storage.WorldData
 *  net.neoforged.neoforge.event.RegisterCommandsEvent
 *  net.neoforged.bus.api.SubscribeEvent
 *  net.neoforged.fml.common.EventBusSubscriber
 *  net.neoforged.fml.common.EventBusSubscriber$Bus
 *  se.mickelus.mutil.data.DataStore
 *  se.mickelus.tetra.data.DataManager
 */
package com.hp.tetra_data_kubejs.command;

import com.hp.tetra_data_kubejs.TetraDataKubejs;
import com.hp.tetra_data_kubejs.data.TetraGeneratedJsonExporter;
import com.hp.tetra_data_kubejs.data.TetraGeneratedKjsExporter;
import com.hp.tetra_data_kubejs.data.TetraRuntimeDataPackManager;
import com.hp.tetra_data_kubejs.data.TetraScriptRegistry;
import com.hp.tetra_data_kubejs.debug.SchematicVisibilityDebug;
import com.hp.tetra_data_kubejs.kube.TetraDataEvents;
import com.hp.tetra_data_kubejs.kube.event.TetraDataRegistryEventJS;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.latvian.mods.kubejs.core.ReloadableServerResourcesKJS;
import dev.latvian.mods.kubejs.event.KubeEvent;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.script.ScriptTypeHolder;
import dev.latvian.mods.kubejs.server.ServerScriptManager;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.commands.ReloadCommand;
import net.minecraft.server.packs.resources.CloseableResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.InactiveProfiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.storage.WorldData;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import se.mickelus.mutil.data.DataStore;
import se.mickelus.tetra.data.DataManager;

@EventBusSubscriber(modid="tetra_data_kubejs", bus=EventBusSubscriber.Bus.GAME)
public final class TetraDataCommands {
    private TetraDataCommands() {
    }

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        TetraDataCommands.register((CommandDispatcher<CommandSourceStack>)event.getDispatcher());
    }

    private static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal((String)"hp_tetra_data_kubejs").requires(source -> source.hasPermission(2))).then(Commands.literal((String)"apply").executes(context -> TetraDataCommands.apply((CommandSourceStack)context.getSource())))).then(Commands.literal((String)"reload").then(Commands.literal((String)"tetra").executes(context -> TetraDataCommands.reloadTetra((CommandSourceStack)context.getSource()))))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal((String)"schematic_debug").then(Commands.literal((String)"on").executes(context -> TetraDataCommands.setSchematicDebug((CommandSourceStack)context.getSource(), true)))).then(Commands.literal((String)"off").executes(context -> TetraDataCommands.setSchematicDebug((CommandSourceStack)context.getSource(), false)))).then(Commands.literal((String)"toggle").executes(context -> TetraDataCommands.toggleSchematicDebug((CommandSourceStack)context.getSource())))).then(Commands.literal((String)"status").executes(context -> TetraDataCommands.schematicDebugStatus((CommandSourceStack)context.getSource()))))).then(Commands.literal((String)"export_kjs").executes(context -> TetraDataCommands.exportKjs((CommandSourceStack)context.getSource())))).then(Commands.literal((String)"export_kjs_grouped").executes(context -> TetraDataCommands.exportGroupedKjs((CommandSourceStack)context.getSource())))).then(Commands.literal((String)"export_generated").executes(context -> TetraDataCommands.exportGenerated((CommandSourceStack)context.getSource())))).then(Commands.literal((String)"reload_completions").executes(context -> TetraDataCommands.reloadCompletions((CommandSourceStack)context.getSource()))));
    }

    private static int apply(CommandSourceStack source) {
        MinecraftServer server = source.getServer();
        ServerScriptManager manager = TetraDataCommands.serverScriptManager(server);
        if (manager == null) {
            source.sendFailure((Component)Component.literal((String)"KubeJS server script manager is not ready yet."));
            return 0;
        }
        try {
            TetraDataCommands.reloadServerScriptsAndQueue(manager);
        }
        catch (Throwable throwable) {
            TetraDataKubejs.LOGGER.error("Failed to apply queued Tetra server_scripts data", throwable);
            source.sendFailure((Component)Component.literal((String)"Failed to reload KubeJS server_scripts. Check kubejs/logs and latest.log."));
            return 0;
        }
        source.sendSuccess(() -> Component.literal((String)"Reloaded KubeJS server_scripts and staged hp_TetraDataEvents.queue(...). Running datapack reload..."), true);
        ReloadCommand.reloadPacks(TetraDataCommands.discoverNewPacks(server), (CommandSourceStack)source);
        return 1;
    }

    private static ServerScriptManager serverScriptManager(MinecraftServer server) {
        try {
            return ((ReloadableServerResourcesKJS)server.getServerResources().managers()).kjs$getServerScriptManager();
        }
        catch (RuntimeException exception) {
            return null;
        }
    }

    private static Collection<String> discoverNewPacks(MinecraftServer server) {
        server.getPackRepository().reload();
        LinkedHashSet<String> selected = new LinkedHashSet<String>(server.getPackRepository().getSelectedIds());
        WorldData worldData = server.getWorldData();
        List<String> disabled = worldData.getDataConfiguration().dataPacks().getDisabled();
        for (String id : server.getPackRepository().getAvailableIds()) {
            if (disabled.contains(id) || selected.contains(id)) continue;
            selected.add(id);
        }
        return selected;
    }

    private static int setSchematicDebug(CommandSourceStack source, boolean enabled) {
        SchematicVisibilityDebug.setEnabled(enabled);
        source.sendSuccess(() -> Component.literal((String)("\u52a0\u5de5\u53f0 schematic \u8c03\u8bd5\u65e5\u5fd7\u5df2" + (enabled ? "\u5f00\u542f" : "\u5173\u95ed") + "\u3002")), true);
        return 1;
    }

    private static int toggleSchematicDebug(CommandSourceStack source) {
        boolean enabled = SchematicVisibilityDebug.toggle();
        source.sendSuccess(() -> Component.literal((String)("\u52a0\u5de5\u53f0 schematic \u8c03\u8bd5\u65e5\u5fd7\u5df2" + (enabled ? "\u5f00\u542f" : "\u5173\u95ed") + "\u3002")), true);
        return 1;
    }

    private static int schematicDebugStatus(CommandSourceStack source) {
        source.sendSuccess(() -> Component.literal((String)("\u52a0\u5de5\u53f0 schematic \u8c03\u8bd5\u65e5\u5fd7\u5f53\u524d\u4e3a" + (SchematicVisibilityDebug.isEnabled() ? "\u5f00\u542f" : "\u5173\u95ed") + "\u72b6\u6001\u3002")), false);
        return 1;
    }

    private static int exportKjs(CommandSourceStack source) {
        try {
            CloseableResourceManager resourceManager = source.getServer().getServerResources().resourceManager();
            TetraGeneratedKjsExporter.ExportSummary summary = TetraGeneratedKjsExporter.export((ResourceManager)resourceManager);
            source.sendSuccess(() -> Component.literal((String)("\u5df2\u5bfc\u51fa " + summary.total() + " \u4e2a tetra \u6570\u636e\u6587\u4ef6\u4e3a KJS \u811a\u672c\uff0c\u8f93\u51fa\u76ee\u5f55\uff1a" + String.valueOf(summary.root()))), true);
            return 1;
        }
        catch (Exception exception) {
            TetraDataKubejs.LOGGER.error("Failed to export tetra datapack jsons as KJS scripts", (Throwable)exception);
            source.sendFailure((Component)Component.literal((String)"\u5bfc\u51fa tetra \u6570\u636e\u5230 KJS \u811a\u672c\u5931\u8d25\uff0c\u8bf7\u68c0\u67e5 latest.log\u3002"));
            return 0;
        }
    }

    private static int exportGroupedKjs(CommandSourceStack source) {
        try {
            CloseableResourceManager resourceManager = source.getServer().getServerResources().resourceManager();
            TetraGeneratedKjsExporter.ExportSummary summary = TetraGeneratedKjsExporter.exportGroupedBySchematicOutcome((ResourceManager)resourceManager);
            source.sendSuccess(() -> Component.literal((String)("\u5df2\u6309 schematic outcome \u5206\u7ec4\u5bfc\u51fa KJS \u811a\u672c\uff0c\u8f93\u51fa\u76ee\u5f55\uff1a" + String.valueOf(summary.root()))), true);
            return 1;
        }
        catch (Exception exception) {
            TetraDataKubejs.LOGGER.error("Failed to export grouped tetra KJS scripts", (Throwable)exception);
            source.sendFailure((Component)Component.literal((String)"\u6309 schematic outcome \u5206\u7ec4\u5bfc\u51fa KJS \u811a\u672c\u5931\u8d25\uff0c\u8bf7\u68c0\u67e5 latest.log\u3002"));
            return 0;
        }
    }

    private static int reloadTetra(CommandSourceStack source) {
        DataManager dataManager = DataManager.instance;
        if (dataManager == null) {
            source.sendFailure((Component)Component.literal((String)"Tetra \u6570\u636e\u7ba1\u7406\u5668\u5c1a\u672a\u521d\u59cb\u5316\u3002"));
            return 0;
        }
        try {
            ServerScriptManager manager = TetraDataCommands.serverScriptManager(source.getServer());
            if (manager == null) {
                source.sendFailure((Component)Component.literal((String)"KubeJS server script manager is not ready yet."));
                return 0;
            }
            TetraDataCommands.reloadServerScriptsAndQueue(manager);
            ResourceManager resourceManager = source.getServer().getResourceManager();
            for (DataStore<?> dataStore : TetraDataCommands.tetraDataStores(dataManager)) {
                TetraDataCommands.reloadDataStore(dataStore, resourceManager);
            }
            source.sendSuccess(() -> Component.literal((String)"\u5df2\u91cd\u8f7d KubeJS server_scripts \u5e76\u5355\u72ec\u91cd\u8f7d Tetra \u6570\u636e\u3002"), true);
            return 1;
        }
        catch (Exception exception) {
            TetraDataKubejs.LOGGER.error("Failed to reload tetra data stores only", (Throwable)exception);
            source.sendFailure((Component)Component.literal((String)"\u5355\u72ec\u91cd\u8f7d Tetra \u6570\u636e\u5931\u8d25\uff0c\u8bf7\u68c0\u67e5 latest.log\u3002"));
            return 0;
        }
    }

    private static int exportGenerated(CommandSourceStack source) {
        try {
            CloseableResourceManager resourceManager = source.getServer().getServerResources().resourceManager();
            TetraScriptRegistry.InjectStats stats = TetraScriptRegistry.exportGeneratedJson((ResourceManager)resourceManager);
            source.sendSuccess(() -> Component.literal((String)("\u5df2\u5bfc\u51fa " + stats.injectedEntries() + " \u4e2a tetra JSON \u5230 " + String.valueOf(TetraGeneratedJsonExporter.root()) + "\uff0c\u8df3\u8fc7 " + stats.skippedEntries() + " \u4e2a\uff0c\u6821\u9a8c\u5931\u8d25 " + stats.validationFailedEntries() + " \u4e2a\u3002")), true);
            TetraDataKubejs.LOGGER.info("[tetra_data_kubejs] export generated timings: entries={}, exported={}, skipped={}, validationFailed={}, refreshKnownKeys={} ms, prepareExport={} ms, normalizeValidate={} ms, writeGenerated={} ms, total={} ms", new Object[]{stats.totalEntries(), stats.injectedEntries(), stats.skippedEntries(), stats.validationFailedEntries(), TetraDataCommands.nanosToMillis(stats.refreshKnownKeysNanos()), TetraDataCommands.nanosToMillis(stats.prepareExportNanos()), TetraDataCommands.nanosToMillis(stats.normalizeAndValidateNanos()), TetraDataCommands.nanosToMillis(stats.writeGeneratedNanos()), TetraDataCommands.nanosToMillis(stats.totalNanos())});
            return 1;
        }
        catch (Exception exception) {
            TetraDataKubejs.LOGGER.error("Failed to export generated tetra jsons", (Throwable)exception);
            source.sendFailure((Component)Component.literal((String)"\u5bfc\u51fa tetra generated JSON \u5931\u8d25\uff0c\u8bf7\u68c0\u67e5 latest.log\u3002"));
            return 0;
        }
    }

    private static void reloadServerScriptsAndQueue(ServerScriptManager manager) {
        manager.reload();
        TetraRuntimeDataPackManager.RefreshStats refreshStats = TetraRuntimeDataPackManager.refresh(manager);
        if (refreshStats == null) {
            throw new IllegalStateException("\u5f53\u524d\u8d44\u6e90\u7ba1\u7406\u5668\u4e0d\u652f\u6301\u5237\u65b0\u811a\u672c\u5316 Tetra \u6570\u636e\u5305");
        }
        TetraScriptRegistry.InjectStats injectStats = refreshStats.injectStats();
        TetraDataKubejs.LOGGER.info("[tetra_data_kubejs] command reload tetra timings: entries={}, injected={}, skipped={}, validationFailed={}, refreshKnownKeys={} ms, normalizeValidate={} ms, addData={} ms, attach={} ms, clearRuntime={} ms, injectTotal={} ms, registerNamespaces={} ms, total={} ms", new Object[]{injectStats.totalEntries(), injectStats.injectedEntries(), injectStats.skippedEntries(), injectStats.validationFailedEntries(), TetraDataCommands.nanosToMillis(injectStats.refreshKnownKeysNanos()), TetraDataCommands.nanosToMillis(injectStats.normalizeAndValidateNanos()), TetraDataCommands.nanosToMillis(injectStats.addDataNanos()), TetraDataCommands.nanosToMillis(refreshStats.attachNanos()), TetraDataCommands.nanosToMillis(refreshStats.clearRuntimeDataNanos()), TetraDataCommands.nanosToMillis(refreshStats.injectNanos()), TetraDataCommands.nanosToMillis(refreshStats.registerNamespacesNanos()), TetraDataCommands.nanosToMillis(refreshStats.totalNanos())});
    }

    private static int reloadCompletions(CommandSourceStack source) {
        try {
            CloseableResourceManager resourceManager = source.getServer().getServerResources().resourceManager();
            TetraScriptRegistry.updateTypings((ResourceManager)resourceManager);
            source.sendSuccess(() -> Component.literal((String)"\u5df2\u91cd\u65b0\u751f\u6210 tetra_data_kubejs \u8865\u5168\u3002"), true);
            return 1;
        }
        catch (Exception exception) {
            TetraDataKubejs.LOGGER.error("Failed to reload tetra_data_kubejs completions", (Throwable)exception);
            source.sendFailure((Component)Component.literal((String)"\u91cd\u65b0\u751f\u6210 tetra_data_kubejs \u8865\u5168\u5931\u8d25\uff0c\u8bf7\u68c0\u67e5 latest.log\u3002"));
            return 0;
        }
    }

    private static List<DataStore<?>> tetraDataStores(DataManager dataManager) {
        try {
            Field field = DataManager.class.getDeclaredField("dataStores");
            field.setAccessible(true);
            DataStore[] stores = (DataStore[])field.get(dataManager);
            return Arrays.asList(stores);
        }
        catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("\u65e0\u6cd5\u8bfb\u53d6 Tetra \u6570\u636e\u4ed3\u5e93\u5217\u8868", exception);
        }
    }

    private static void reloadDataStore(DataStore<?> dataStore, ResourceManager resourceManager) throws Exception {
        Method prepareMethod = TetraDataCommands.findMethod(dataStore.getClass(), "prepare", ResourceManager.class, ProfilerFiller.class);
        Method applyMethod = TetraDataCommands.findMethod(dataStore.getClass(), "apply", Map.class, ResourceManager.class, ProfilerFiller.class);
        Map prepared = (Map)prepareMethod.invoke(dataStore, resourceManager, InactiveProfiler.INSTANCE);
        applyMethod.invoke(dataStore, prepared, resourceManager, InactiveProfiler.INSTANCE);
    }

    private static Method findMethod(Class<?> type, String name, Class<?> ... parameterTypes) throws NoSuchMethodException {
        for (Class<?> current = type; current != null; current = current.getSuperclass()) {
            try {
                Method method = current.getDeclaredMethod(name, parameterTypes);
                method.setAccessible(true);
                return method;
            }
            catch (NoSuchMethodException ignored) {
                continue;
            }
        }
        throw new NoSuchMethodException(name);
    }

    private static String nanosToMillis(long nanos) {
        return String.format("%.2f", (double)nanos / 1000000.0);
    }
}

