# 移植记录：TetraAddition 6.9.0-1.1 → 1.21.1 NeoForge

源 jar：`d:\mod移植\tetra_addition-6.9.0-1.1.jar`（14,313 字节）
工程：`d:\mod移植\2026-11-13\addons\tetra_addition\`
产物：`build\libs\tetra_addition-1.21-1.21.1-6.9.0-1.1.jar`（11,125 字节）
构建：`gradlew --offline clean build` BUILD SUCCESSFUL
基准：torch 无公开源码，全部基于 CFR 0.152 反编译（`reference\work\addons\tetra_addition_dec`），已逐类对照 tetra 1.21.1-7.0.0 的官方源码/字节码核验。

## 1. 挂载项数量核对

| 项 | 原 jar | 新 jar |
|---|---|---|
| class | 9 | 9 |
| mixin 类 | 3 | 3 |
| mixins json | 1 | 1 |
| KubeJS 插件声明（kubejs.plugins.txt） | 1 | 1 |
| pack.mcmeta | 1 | 1 |
| 数据文件（data/assets） | 0 | 0 |
| 注册项（物品/方块/配方/网络包/数据组件） | 0 | 0 |

文件级：`META-INF/mods.toml` → `META-INF/neoforge.mods.toml`；`tetra_addition.refmap.json` 按 NeoForge 1.21 规范移除（运行时 Mojang 官方名，无需 refmap）。

## 2. 逐项结论

### ✅ 直接迁移（6）
- `TetraAddition`（入口）：`FMLJavaModLoadingContext` → 构造注入 `IEventBus` + `ModContainer`。
- `MyTetraUtil`：逻辑不变；`getOrCreateTag()` → `ItemStackTagHelper.getOrCreateTag(stack)`；`ItemStack.f_41583_` → `ItemStack.EMPTY`；`getItem()` 正常。
- `WorkbenchTileCraftEvent`：基类 `net.minecraftforge.eventbus.api.Event` → `net.neoforged.bus.api.Event`；`player.m_9236_()` → `player.level()`。
- `util/IConfigSchematic`、`util/ISchematicRegistry`：无变化。
- `SchematicRegistryMixin`：`schematicMap`/`instance` 在 1.21.1 仍为同名字段，`@Shadow` 成立。

### ⚠️ 需改造（3）
- `WorkbenchTileMixin`：
  - `handler` 字段类型 `LazyOptional<ItemStackHandler>` → `net.neoforged.neoforge.items.ItemStackHandler`，直接 `handler.setStackInSlot(0, ...)`，去掉 `ifPresent`。
  - 注入点 `@At(INVOKE clearSchematic)` 在 1.21.1 仍唯一存在（craft 偏移 617）。
  - **LocalCapture 参数由 7 个增至 8 个**：1.21.1 新增局部变量 `ItemStack tempStack`（槽位 9，作用域 569–622 覆盖注入点），已按字节码 LocalVariableTable 补齐；`availableTools` 泛型 `Map<ToolAction,Integer>` → `Map<ItemAbility,Integer>`，擦除后描述符同为 `Ljava/util/Map;`，故声明为 `Map<?, ?>`。
  - `MinecraftForge.EVENT_BUS` → `NeoForge.EVENT_BUS`；`(WorkbenchTile) this` → `(WorkbenchTile) (Object) this`（javac 需要）。
- `ConfigSchematicMixin`：`definition` 在 1.21.1 为 `private final`，`@Shadow @Final` 成立；原 `@Shadow protected abstract getOutcomeFromMaterial` 在 1.21.1 已改为 **private**（Mixin 无法以 protected 声明 shadow，也无 `@Invoker` 需求），且其唯一使用者是类内未被任何代码调用的 `myGetOutcomeFromMaterial`——一并删除。**这是死代码清理，不涉及业务行为**。
- `kubejs/TetraAdditionPlugin`：KubeJS 1.21.1（2101.7.2）API 变更——`KubeJSPlugin` 由抽象类改为接口（`dev.latvian.mods.kubejs.plugin.KubeJSPlugin`），`registerBindings(BindingsEvent)` → `registerBindings(dev.latvian.mods.kubejs.script.BindingRegistry)`；`BindingRegistry#add(String,Object)` 签名不变，绑定名 `MyTetraUtil`、`$WorkbenchTileCraftEvent` 保持一致。`kubejs.plugins.txt` 机制在 2101.7.2 仍被 `KubeJSPlugins` 读取（字节码可见该资源名），故保留。

### ❌ 无
无功能删除、无 API 无等价替换。

## 3. 依赖

- 新增编译依赖：KubeJS NeoForge `2101.7.2-build.381`（来自 Modrinth CDN，已落盘 `libs/`）。
- 元数据新增：`tetra [7.0.0,)` required、`kubejs [2101.7.0,)` optional/AFTER（原 mods.toml 未声明任何前置，属原作者的缺省声明，这里补齐）。
- 注意：缺 kubejs 时 `kubejs.plugins.txt` 不会被读取，插件类不会被加载，不会崩服。

## 4. 风险与待实测

1. `WorkbenchTileMixin` 的 LocalCapture 依赖 1.21.1-7.0.0 的字节码布局（8 个局部变量）。若日后换 tetra 构建导致 `craft` 增删局部变量，需按新 LVT 调整参数。
2. `craft` 注入点位于 `handler.setStackInSlot(0, tempStack)` 之后、`clearSchematic()` 之前；已核对 `onContentsChanged` 对 slot 0 的比较是「新栈 vs 新栈」，不会把 `currentSchematic/currentSlot` 置空，事件字段仍有效。
3. 功能链路需实机验证：装 KubeJS 后 `MyTetraUtil` 全局可见、工作台合成事件可被脚本改产物。
