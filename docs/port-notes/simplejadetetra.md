# simplejadetetra 移植记录（1.21.1 NeoForge）

- 原 jar：`d:\mod移植\Simple Jade Tetra-6.9.0-1.0.0.jar`（4 KB，2 class，0 mixin）
- 源码：无公开仓库（作者 `_yi_ran_`，ARR），**全部逻辑由 javap 反编译核对**；仅 2 个类，无逻辑存疑处。
- 新 jar：`build\libs\simplejadetetra-1.21-1.21.1-6.9.0-1.0.0.jar`

## 挂载项对照

| 项 | 原 jar | 新 jar |
| --- | --- | --- |
| class | 2 | 2 |
| mixin | 0 | 0 |
| 注册项 / 网络包 / 数据组件 / assets / lang | 0 | 0 |
| 数据文件 | 0 | 0 |
| pack.mcmeta | 1（format 15） | 1（format 48） |

## 兼容性分类

| # | 原实现 | 1.21.1 | 判定 |
| --- | --- | --- | --- |
| 1 | `FMLJavaModLoadingContext.get().getModEventBus()` 注册 `FMLCommonSetupEvent` | `@Mod` 构造注入 `IEventBus`，`modEventBus.addListener(this::onCommonSetup)` | ⚠️ 接口变更 |
| 2 | `snownee.jade.addon.harvest.HarvestToolProvider.registerHandler(ToolHandler)` | 同名同包，签名不变（Jade 15.10.6） | ✅ |
| 3 | `ToolHandler#test(BlockState, Level, BlockPos)` / `#getTools()` | 不变 | ✅ |
| 4 | `ToolHandler#getName()`（旧 Jade 接口） | 1.21 接口改为 `IJadeProvider#getUid()` | ⚠️ 接口变更（`getName` 删除，新增 `getUid`） |
| 5 | `se.mickelus.tetra.util.ToolActionHelper.hammerMineable` | 类已不存在；标签移到 `se.mickelus.tetra.util.ItemAbilityHelper.hammerMineable`（`mineable/hammer`） | ⚠️ 类迁移 |
| 6 | `ModularDoubleHeadedItem.getCreativeTabItemStacks()` | 存在（`items/modular/impl/ModularDoubleHeadedItem.java:93`） | ✅ |
| 7 | `ItemStack#isCorrectToolForDrops(BlockState)` | 1.21.1 仍存在 | ✅ |

无业务降级。

## 依赖补全（原 jar 未声明）

原 `mods.toml` 只声明 `forge`/`minecraft`，但代码引用 tetra 与 jade 类。新元数据补为：`neoforge [21.1.0,)`、`minecraft [1.21.1,1.22)`、`tetra [7.0.0,)`、`jade [15.10.0,)`（AFTER）。属正确性修复，不改变运行时行为。

## 构建

`gradlew --offline clean build` BUILD SUCCESSFUL；依赖：本地 tetra 1.21.1-7.0.0 + Jade 15.10.6（Modrinth）。
