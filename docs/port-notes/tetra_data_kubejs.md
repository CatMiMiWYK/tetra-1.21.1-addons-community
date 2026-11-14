# tetra_data_kubejs 1.20.1 (Forge) → 1.21.1 (NeoForge 21.1.238) 移植说明

## 1. 源与产物

| 项目 | 内容 |
| --- | --- |
| 源 jar | `2026-11-13/reference/work/addons/tetra_data_kubejs.jar` (Forge 1.20.1, 1.3.2) |
| 源反编译 | `2026-11-13/reference/work/addons/tetra_data_kubejs_dec` (33 个 .java) |
| 新工程 | `2026-11-13/addons/tetra_data_kubejs` |
| 产物 | `2026-11-13/addons/tetra_data_kubejs/build/libs/tetra_data_kubejs-1.21-1.21.1-1.3.2.jar` (186,348 bytes) |
| 目标 | Minecraft 1.21.1 / NeoForge 21.1.238 / Java 21 / KubeJS 2101.7.2 |

## 2. 数量核对

### 2.1 class 数量

| 项 | 原 jar | 新 jar | 差异 |
| --- | --- | --- | --- |
| .class 条目 | **55**（任务描述写 61，实测 jar 内为 55） | **53** | -2 |

逐项差异：

| 变化 | 类 | 原因 |
| --- | --- | --- |
| 删除 | `kube/TetraDataProbeSnippets.class` | ProbeJS 不存在，按 #13 例外移除（仅开发期类型片段，无游戏内功能） |
| 删除 | `mixin/ProbeEventCompilerMixin.class` | ProbeJS 编译事件目标不存在，按 #13 例外移除 |
| 删除 | `mixin/ServerScriptManagerMixin.class` | KubeJS 2101 删除 `ServerScriptManager#wrapResourceManager`，改由插件 `afterScriptsLoaded` + `VirtualDataPack` 实现（等价替换） |
| 删除 | `mixin/MultiPackResourceManagerAccessor.class` | 新模型不再需要向 `MultiPackResourceManager` push 私有 pack；直接写入 KubeJS 的 `GeneratedDataStage.LAST` 虚拟包（等价替换） |
| 删除 | `data/TetraScriptRegistry$1.class` | javac 21 对 enum switch 不再生成旧式 `$SwitchMap` 合成类，源码逻辑不变 |
| 新增 | `mixin/ReloadableServerResourcesMixin.class` | 捕获本次 reload 的 `ResourceManager`，供 mergeExisting/copyFrom 读取现有 tetra 数据 |
| 新增 | `data/TetraGeneratedKjsExporter$ClientTranslationHook.class` | 替代已移除的 Forge `DistExecutor`，隔离客户端类引用 |
| 新增 | `network/TetraTranslationSyncPacket$ClientHandler.class` | 同上，隔离客户端翻译表写入 |

> 说明：任务描述中的 “61 个 class” 与本机实际 jar 不符；以 `ZipFile` 枚举结果 55 为准。

### 2.2 mixin 数量

| 项 | 原 jar | 新 jar |
| --- | --- | --- |
| mixins.json 条目 | 12 | 10 |
| 具体 | 10 通用 + 1 client + 1 ProbeJS | 9 通用 + 1 client |

移除 3 个（ServerScriptManager、MultiPackResourceManagerAccessor、ProbeEventCompilerMixin），新增 1 个（ReloadableServerResourcesMixin）。

### 2.3 资源

| 资源 | 原 jar | 新 jar | 说明 |
| --- | --- | --- | --- |
| `kubejs.plugins.txt` | ✅ | ✅ | 插件类名保持不变：`com.hp.tetra_data_kubejs.kube.TetraDataKubejsPlugin` |
| `pack.mcmeta` | ✅ | ✅ | `pack_format` 改为 34（1.21.1 资源包） |
| `tetra_data_kubejs.mixins.json` | ✅ | ✅ | 去掉 refmap，`compatibilityLevel: "JAVA_21"` |
| `META-INF/mods.toml` | ✅ | ❌ | NeoForge 使用 `META-INF/neoforge.mods.toml` |
| `META-INF/neoforge.mods.toml` | ❌ | ✅ | 含 `[[mixins]] config` 与依赖声明 |
| `tetra_data_kubejs.refmap.json` | ✅ | ❌ | NeoForge 1.21 运行期 mojmap，按任务要求移除 refmap |

## 3. Mixin 逐条清单（均已用 `javap -p/-c` 对照 1.21.1/tetra 7.0.0/mutil 7.0.0 实际字节码）

| # | Mixin | 目标类 | 目标方法/字段 | 注入点 | 迁移说明 |
| --- | --- | --- | --- | --- | --- |
| 1 | `AbstractUpdateDataPacketMixin` | `se.mickelus.mutil.data.AbstractUpdateDataPacket`（`remap=false`） | `toBytes` / `fromBytes` | `@Redirect`：`toBytes` 与 `lambda$toBytes$1` 的 `writeUtf(String)`；`fromBytes` 中两个 `readUtf()`（ordinal 0/1） | 1.20.1 SRG `m_130072_/m_130136_` 改为 mojmap `writeUtf/readUtf`；mutil 7.0.0 的 `fromBytes` 有 directory 与 map value 两次 `readUtf`，用 ordinal 分别重定向，避免多候选注入失败 |
| 2 | `DataStoreAccessor` | `se.mickelus.mutil.data.DataStore` | 字段 `gson` / `directory` / `dataClass` | `@Accessor` | 字段在 mutil 7.0.0 中仍为 protected，未改名 |
| 3 | `DataStoreDiagnosticsMixin` | `se.mickelus.mutil.data.DataStore` | `parseData(Map)` | `@At("RETURN")` | `@Shadow dataClass` 描述符一致；仅输出诊断日志 |
| 4 | `MaterialImprovementDataMixin` | `se.mickelus.tetra.module.data.MaterialImprovementData` | `combine(MaterialData)` | `@At("RETURN")` | `tags/rarity` 为继承的 public 字段；修正 CFR 的非法类转换 `(MaterialImprovementData)(Object)this` |
| 5 | `MergingDataStoreDiagnosticsMixin` | `se.mickelus.mutil.data.MergingDataStore` | `parseData(Map)` | `@At("HEAD")`，cancellable | `@Shadow arrayClass` 存在；非 Minecraft 目标，`remap=false` |
| 6 | `ModularItemMixin` | `se.mickelus.tetra.items.modular.ModularItem` | 构造器 `(Item.Properties)` | `@At("TAIL")` | 构造器签名未变；`DataManager.instance.materialData/improvementData/synergyData.onReload` 均存在 |
| 7 | `ReloadableServerResourcesMixin`（新增） | `net.minecraft.server.ReloadableServerResources` | `loadResources(ResourceManager, LayeredRegistryAccess, FeatureFlagSet, Commands.CommandSelection, int, Executor, Executor)` | `@At("HEAD")`，static，priority 500 | 捕获本次 reload 的 ResourceManager，供脚本数据 mergeExisting/copyFrom 读取；先于 KubeJS 默认 priority 的执行 |
| 8 | `SchematicRegistryMixin` | `se.mickelus.tetra.module.SchematicRegistry` | `processDefinition(SchematicDefinition)` | `@At("RETURN")` | `applicableMaterials` 字段仍存在；仅补充脚本科目可见性缓存 |
| 9 | `VariantDataMixin` | `se.mickelus.tetra.module.data.VariantData` | `merge(VariantData, VariantData)` | `@At("RETURN")`，cancellable | 静态方法签名未变；tags/rarity 字段存在 |
| 10 | `client.I18nMixin` | `net.minecraft.client.resources.language.I18n` | `exists(String)`、`get(String, Object[])` | `@At("HEAD")`，cancellable | 1.21.1 签名未变；显式写完整描述符避免重载歧义（`get` 为 varargs） |

已移除的 mixin：

| Mixin | 原目标 | 原因/替代 |
| --- | --- | --- |
| `ServerScriptManagerMixin` | `ServerScriptManager#wrapResourceManager(ResourceManager)` | KubeJS 2101 已无该方法；改用官方插件钩子 `afterScriptsLoaded(ScriptManager)`，把数据写入 `manager.virtualPacks.get(GeneratedDataStage.LAST)` |
| `MultiPackResourceManagerAccessor` | `MultiPackResourceManager#namespacedManagers` | 新 KubeJS 虚拟包已在 pack 列表中，无需反射 push；直接 `VirtualDataPack.json(id, json)` |
| `ProbeEventCompilerMixin` | ProbeJS `ProbeEventCompiler` | 本机无 ProbeJS 依赖，按 #13 例外移除（仅开发期类型导出） |

## 4. 兼容性统计（按 33 个源 .java）

| 状态 | 数量 | 文件 |
| --- | --- | --- |
| ✅ 等价迁移 | 18 | `TetraDataKubejs`、`kube/TetraDataEvents`、`data/JsonMergeUtil`、`data/ScriptedTetraEntry`、`data/TetraColorUtil`、`data/TetraDataLoadDiagnostics`、`data/TetraGeneratedJsonExporter`、`data/TetraTypingsGenerator`、`client/TetraClientTranslationHelper`、`mixin/AbstractUpdateDataPacketMixin`、`mixin/DataStoreAccessor`、`mixin/DataStoreDiagnosticsMixin`、`mixin/MaterialImprovementDataMixin`、`mixin/MergingDataStoreDiagnosticsMixin`、`mixin/ModularItemMixin`、`mixin/SchematicRegistryMixin`、`mixin/VariantDataMixin`、`mixin/client/I18nMixin` |
| ⚠️ 实现差异 | 11 | `kube/TetraDataKubejsPlugin`、`kube/event/TetraDataRegistryEventJS`、`data/TetraDataUtil`、`data/TetraGeneratedKjsExporter`、`data/TetraRuntimeDataPackManager`、`data/TetraScriptRegistry`、`debug/SchematicVisibilityDebug`、`command/TetraDataCommands`、`network/TetraNetwork`、`network/TetraTranslationSyncPacket`、`network/TetraTranslationSyncEvents` |
| ❌ 降级删除 | 4 | `kube/TetraDataProbeSnippets`（ProbeJS）、`mixin/ProbeEventCompilerMixin`（ProbeJS）、`mixin/ServerScriptManagerMixin`（架构替换，功能未降级）、`mixin/MultiPackResourceManagerAccessor`（架构替换，功能未降级） |

主要 ⚠️ 变更点：

- **KubeJS 插件 API**：`KubeJSPlugin` 变为接口（`dev.latvian.mods.kubejs.plugin.KubeJSPlugin`），事件组改用 `registerEvents(EventGroupRegistry)`；脚本事件基类 `EventJS` → `KubeEvent`（接口）。
- **启动事件时机**：2101 的 `initStartup()` 在 startup 脚本加载**之前**调用，直接在此 post 事件会导致脚本尚未注册监听器。已改为在 `afterScriptsLoaded(ScriptManager)` 且 `scriptType == STARTUP` 时 post。
- **服务端脚本重载**：原 `onServerReload` 钩子消失，改用 `afterScriptsLoaded`（仅 `SERVER`）：先 staging/queue/commit，再 `TetraRuntimeDataPackManager.refresh`。
- **运行期数据包**：原 `VirtualKubeJSDataPack` + 反射 `/data` 注入，改为直接写入 KubeJS `ServerScriptManager.virtualPacks.get(GeneratedDataStage.LAST)`；`ServerScriptManager.reload()` 每次开始会 `reset()` 全部虚拟包，因此无需手动清空，且 LAST 阶段保持最高优先级。
- **JSON 转换**：KubeJS 2101 的 `JsonUtils.of` 需要 `Context`，事件参数为 Rhino `NativeObject`（实现 `Map`）/`NativeArray`（实现 `Iterable`），因此在事件类内实现 `toJson(Object)` 递归转换（Map/Iterable/数组/JsonElement/基础类型），行为等价于旧 `JsonIO.of`。
- **网络**：Forge `SimpleChannel` 已移除；`TetraTranslationSyncPacket` 改为继承 `mutil AbstractPacket`，补充 `TYPE = new CustomPacketPayload.Type<>(tetra_data_kubejs:translation_sync)` 与 `type()`，并通过 mutil `PacketHandler.register(RegisterPayloadHandlersEvent)` 注册。
- **注册表/资源 API**：`ForgeRegistries.ATTRIBUTES` → `BuiltInRegistries.ATTRIBUTE.keySet()`；`new ResourceLocation(ns, path)` → `ResourceLocation.fromNamespaceAndPath`；1.21.1 `Resource#openAsReader()` 抛 `IOException`，`TetraDataUtil.readJson` 改为 try-with-resources。
- **SRG 名称**：所有 `m_135827_/m_135815_/m_130072_/m_130136_/m_213713_` 等替换为 mojmap（`getNamespace/getPath/writeUtf/readUtf/getResource` 等）。
- **命令**：`ServerScriptManager.instance` → `server.getServerResources().managers().kjs$getServerScriptManager()`（`ReloadableServerResourcesKJS` 接口）；`manager.reload(resourceManager)` → `manager.reload()`；`ReloadCommand.m_138235_` → `ReloadCommand.reloadPacks`；`InactiveProfiler.f_18554_` → `InactiveProfiler.INSTANCE`。
- **调试/事件**：`@Mod.EventBusSubscriber(bus=FORGE)` → `@EventBusSubscriber(bus=GAME)`；`DistExecutor` 移除，改用 `FMLEnvironment.dist` + 客户端内部类隔离；`PlayerContainerEvent`/`ClientPlayerNetworkEvent` 改到 `net.neoforged.neoforge.*`。

## 5. 依赖声明

编译期（`libs/`，不打包进 jar）：

- `tetra-1.21.1-7.0.0.jar`
- `mutil-1.21.1-7.0.0.jar`
- `kubejs-neoforge-2101.7.2-build.381.jar`
- `rhino-2101.2.7-build.85.jar`（KubeJS 的脚本引擎依赖，编译需要）

`META-INF/neoforge.mods.toml`：

- `neoforge` `[21.1.0,)` required
- `minecraft` `[1.21.1,1.22)` required
- `kubejs` `[2101.7.2,)` required（AFTER）
- `tetra` `[7.0.0,)` required（AFTER）

## 6. 构建环境记录

- 复制 `geotetraarmor/build/moddev/artifacts/*` 到本工程 `build/moddev/artifacts/`。
- `build.gradle` 强制 `net.neoforged:accesstransformers:10.0.1`，并禁用 `createMinecraftArtifacts`（因该任务名在当前 ModDevGradle 配置下不存在，使用 `tasks.matching { ... }.configureEach` 惰性禁用，效果等同）。
- `processResources` 的 `expand` 包含 `version` 与 `loader_version_range`，toml 中 `${version}`/`${loader_version_range}` 已正确展开。
- 构建命令：`.\gradlew build --offline --console=plain`（未 clean），BUILD SUCCESSFUL。

## 7. 保留的公开 API

- `com.hp.tetra_data_kubejs.kube.TetraDataEvents`：事件组/Handler 名称与用法不变（`hp_TetraDataEvents.register` / `.queue`）。
- `com.hp.tetra_data_kubejs.kube.event.TetraDataRegistryEventJS` 及其全部 Builder 内部类的公开方法名与签名保持不变（仅基类由 `EventJS` 改为 `KubeEvent`）。
- `kubejs.plugins.txt` 中的插件类名不变。
