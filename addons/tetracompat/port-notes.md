# tetracompat 1.20.1 → 1.21.1 / NeoForge 21.1.238 移植说明

## 1. 源版本与目标

| 项目 | 值 |
| --- | --- |
| 源 mod | tetracompat 1.0.0（Minecraft 1.20.1 / Forge 47.x，Java 17） |
| 源 jar | `2026-11-13/reference/work/addons/tetracompat.jar` |
| 目标 | Minecraft 1.21.1 / NeoForge 21.1.238 / Java 21 |
| modId | `tetracompat`（未变） |
| 包名 | `com.sswltzz.tetracompat`（未变） |
| 产物 | `build/libs/tetracompat-1.21-1.21.1-1.0.0.jar` |

## 2. 数量核对

### 2.1 class 文件

| 源 jar | 新 jar | 差异 |
| --- | --- | --- |
| 13 个 class | 10 个 class | -3（`mixin/ftbultimine/*` 三个类删除，见 §4.5） |

新 jar class 清单：

```
com/sswltzz/tetracompat/TCConfig.class
com/sswltzz/tetracompat/TetraCompat.class
com/sswltzz/tetracompat/TetraCompat$ClientModEvents.class
com/sswltzz/tetracompat/integration/ExDeorumUtil.class
com/sswltzz/tetracompat/integration/FarmersDelightCompat.class
com/sswltzz/tetracompat/integration/RecipeInjection.class
com/sswltzz/tetracompat/mixin/RecipeManagerAccessor.class
com/sswltzz/tetracompat/mixin/RecipeManagerMixin.class
com/sswltzz/tetracompat/mixin/alexscaves/GalenaGauntletItemMixin.class
com/sswltzz/tetracompat/mixin/tetra/ToolActionHelperMixin.class
```

### 2.2 mixin

| 源 | 新 | 说明 |
| --- | --- | --- |
| 7 条 | 4 条 | FTB Ultimine 3 条删除（上游重构后功能内建，见 §4.5） |

保留/改写：

1. `RecipeManagerAccessor`（Minecraft `RecipeManager`，@Accessor）
2. `RecipeManagerMixin`（Minecraft `RecipeManager#apply`，@Inject RETURN）
3. `alexscaves.GalenaGauntletItemMixin`（Alex's Caves `GalenaGauntletItem`）
4. `tetra.ToolActionHelperMixin`（Tetra `ItemAbilityHelper`）

删除：

- `ftbultimine.FTBUltimineMixin`
- `ftbultimine.RightClickHandlersInvoker`
- `ftbultimine.RightClickHandlersMixin`

### 2.3 资源

| 源资源 | 新资源 | 处理 |
| --- | --- | --- |
| `META-INF/mods.toml` | `META-INF/neoforge.mods.toml` | 改写为 NeoForge 格式，`[[mixins]]` 注册 mixin 配置，`mandatory` → `type` |
| `tetracompat.mixins.json` | `tetracompat.mixins.json` | 删除 `refmap`，`JAVA_17` → `JAVA_21`，mixin 列表缩减为 4 条 |
| `tetracompat.refmap.json` | — | 删除。1.21.1 NeoForge 运行时使用官方（Mojang）名，无需 refmap |
| `META-INF/jarjar/mixinextras-forge-0.4.1.jar` | — | 删除。NeoForge 21.1 内置 MixinExtras 0.5.3（`@Local` 继续可用） |
| `META-INF/jarjar/metadata.json` | — | 删除（无嵌套 jar） |
| `assets/tetra/stat_bars/magnetizable.json` | 同路径 | 原样保留（对照 tetra 7.0.0 的 stat_bars 格式一致） |
| `assets/tetra/stat_bars/crystal_magnetizable.json` | 同路径 | 原样保留 |
| `assets/tetracompat/lang/en_us.json` | 同路径 | 原样保留 |
| `assets/tetracompat/lang/zh_cn.json` | 同路径 | 原样保留 |
| `data/tetra/materials/gem/*.json`（3 个） | 同路径 | 原样保留（`replace`/`effects` 格式与 7.0.0 兼容） |
| `data/tetra/materials/metal/*.json`（2 个） | 同路径 | 原样保留 |
| `data/tetra/replacements/knife.json` | 同路径 | 条件 `forge:mod_loaded` → `neoforge:mod_loaded` |
| `data/forge/loot_modifiers/global_loot_modifiers.json` | `data/neoforge/loot_modifiers/global_loot_modifiers.json` | NeoForge 命名空间迁移 |
| `data/tetracompat/loot_modifiers/hammer.json` | 同路径 | 1.21.1 物品谓词改为子谓词格式：`predicate.predicates["tetra:modular_item"]` |
| `pack.mcmeta` | 同路径 | 原样保留（`pack_format: 15`，与同批 geotetraarmor 产物一致） |
| `META-INF/MANIFEST.MF` | Gradle 重新生成 | — |

## 3. mixin 逐条清单

所有目标均已用 `javap -p/-c` 对照实际 jar 核对。

### 3.1 `RecipeManagerAccessor`
- 目标：`net.minecraft.world.item.crafting.RecipeManager`（Minecraft，默认 remap）
- 字段：`byName: Map<ResourceLocation, RecipeHolder<?>>`、`byType: Multimap<RecipeType<?>, RecipeHolder<?>>`
- 迁移：1.20.1 的 `recipes: Map<RecipeType, Map<ResourceLocation, Recipe>>` 在 1.21.1 已变为 `byType` Multimap，且值类型由 `Recipe<?>` 变为 `RecipeHolder<?>`；`setRecipes` → `setByType`。

### 3.2 `RecipeManagerMixin`
- 目标：`RecipeManager#apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V`
- 注入点：`@At("RETURN")`
- 迁移：`RecipeInjection` 内部改为重建 `ImmutableMap<ResourceLocation, RecipeHolder<?>>` 与 `ImmutableMultimap<RecipeType<?>, RecipeHolder<?>>`，再写回 `byName`/`byType`。1.21.1 配方以 `RecipeHolder` 为单位，`Recipe#getType()` → `RecipeHolder#value().getType()`。

### 3.3 `alexscaves.GalenaGauntletItemMixin`
- 目标：`com.github.alexmodguy.alexscaves.server.item.GalenaGauntletItem`（`remap = false`）
- 注入 1：`use(Level, Player, InteractionHand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>>)`，`HEAD`，cancellable。
  - 迁移：`player.m_21120_` → `getItemInHand`；`enchantmentLevel` 改为 `ItemStack#getEnchantmentLevel(Holder<Enchantment>)`；`m_6672_` → `startUsingItem`；`m_19096_/m_19100_` → `InteractionResultHolder.consume/fail`；`m_41622_(1,player,Consumer)` → `hurtAndBreak(1, player, EquipmentSlot)`。
- 注入 2：`onUseTick(Level, LivingEntity, ItemStack, int, CallbackInfo)`，`@At("INVOKE")` 目标 `Lnet/minecraft/world/item/ItemStack;getEnchantmentLevel(Lnet/minecraft/core/Holder;)I`，`Shift.AFTER`，`@Local InteractionHand otherHand`。
  - 迁移：`ACEnchantmentRegistry.CRYSTALLIZATION` 在 2.0.10 是 `ResourceKey<Enchantment>`，需 `registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(...)` 得到 `Holder<Enchantment>`；`level.m_45976_` → `getEntitiesOfClass`；`m_20191_.m_82377_` → `getBoundingBox().inflate`；`m_7306_` → `Entity#is`；`m_41777_/m_41764_` → `copy()/setCount(0)`；`m_146884_` → `setPos`；`m_20148_` → `getUUID`；`m_7967_` → `addFreshEntity`；`m_5810_` → `stopUsingItem`。`EntityType#create(Level)` 结果增加 null 检查。

### 3.4 `tetra.ToolActionHelperMixin`
- 目标：`se.mickelus.tetra.util.ItemAbilityHelper`（`remap = false`）
- 方法：`isEffectiveOn(Lnet/neoforged/neoforge/common/ItemAbility;Lnet/minecraft/world/level/block/state/BlockState;)Z`，`@At("RETURN")`，cancellable。
- 迁移：目标类改名（`ToolActionHelper` → `ItemAbilityHelper`），参数 `ToolAction` → `ItemAbility`；`TetraToolActions.hammer` → `TetraItemAbilities.hammer`；mixin 类名按“公开类名保持不变”原则保留原名。
- ExDeorum 3.12 中 `RecipeUtil.getHammerRecipe(Item)` 已移除，改为 `RecipeUtil.getServerRecipeCaches(true).getHammerRecipe(Item)`（`true` 跳过仅服务端的断言，客户端返回 null 时安全退化）。

### 3.5 已删除的 FTB Ultimine mixin（3 个）
1. `FTBUltimineMixin`：目标 `FTBUltimine#blockRightClick` + `FTBUltiminePlayerData#updateBlocks` 后注入。
2. `RightClickHandlersInvoker`：`@Invoker` 调用 `RightClickHandlers.axeStripping/shovelFlattening/farmlandConversion`。
3. `RightClickHandlersMixin`：注入 `RightClickHandlers.axeStripping` HEAD，重写 Tetra 斧类工具逻辑。

删除原因（已 javap 逐项核对 FTB Ultimine 2101.1.16 与 tetra 7.0.0）：
- 2101 中 `dev.ftb.mods.ftbultimine.RightClickHandlers`、`BrokenItemHandler` 已不存在，改为 `dev.ftb.mods.ftbultimine.rightclick.{AxeStripping,FarmlandConversion,ShovelFlattening}` 实现 `RightClickHandler`，原 `@Invoker`/HEAD 注入目标消失。
- 新流程由 `FTBUltimine#blockRightClick` → `RightClickDispatcher` 统一调度，管线内部检查 `PlatformUtil.canAxeStrip/canTillSoil/canFlattenPath` 并执行 `ItemStack#useOn`。
- tetra 7.0.0 `ItemModularHandheld` 已实现 `canPerformAction(ItemStack, ItemAbility)` 与 `useOn(UseOnContext)`；其工具数据包含 `axe_strip`/`axe_scrape`/`axe_wax_off`、`shovel_flatten`、`till`（= `ItemAbilities.HOE_TILL`），与 NeoForge `IBlockExtension#getToolModifiedState` 的原生分支一一对应。
- 因此 FTB 2101 + tetra 7.0.0 已原生覆盖原 3 个 mixin 的功能；继续注入反而会重复执行/重复扣耐久。此删除不构成功能降级，仅去除冗余代码。

## 4. 兼容性统计（按源 .java 计）

| 状态 | 数量 | 明细 |
| --- | --- | --- |
| ✅ 已移植 | 9 | `TCConfig`、`TetraCompat`（含内部类 `ClientModEvents`）、`ExDeorumUtil`、`FarmersDelightCompat`、`RecipeInjection`、`RecipeManagerAccessor`、`RecipeManagerMixin`、`GalenaGauntletItemMixin`、`ToolActionHelperMixin` |
| ⚠️ 迁移有外部前提 | 3 | `FTBUltimineMixin`、`RightClickHandlersInvoker`、`RightClickHandlersMixin`：删除，由 FTB Ultimine 2101 + tetra 7.0.0 原生等价实现替代（非降级） |
| ❌ 功能缺失 | 0 | — |

补充说明：
- ⚠️ `FarmersDelightCompat` + `RecipeInjection` 的刀类配方注入保留原逻辑，但运行时同时要求 `farmersdelight` 与 `tetracelium` 已加载；tetracelium 1.21.1 尚不可用（第 17 个移植任务搁置），因此该分支当前不会激活。`blade_cut` 能力用 `ItemAbility.get("blade_cut")` 按原 `ToolAction.get("blade_cut")` 的同名 id 重建，待 tetracelium 1.21.1 出现后可继续匹配。
- ⚠️ `data/tetracompat/loot_modifiers/hammer.json` 已改为 1.21.1 子谓词格式；ExDeorum 3.12 的 `exdeorum:hammer` 战利品修饰器本身格式未变。

## 5. 依赖声明

### 5.1 编译期（`libs/`，全部 `compileOnly`，不打包）
- `tetra-1.21.1-7.0.0.jar`
- `mutil-1.21.1-7.0.0.jar`
- `ftb-ultimine-neoforge-2101.1.16.jar`
- `architectury-13.0.11-neoforge.jar`
- `alexscaves-2.0.10.jar`
- `FarmersDelight-1.21.1-1.3.4.jar`
- `exdeorum-3.12.jar`

### 5.2 运行期（`META-INF/neoforge.mods.toml`）
| modId | 类型 | versionRange |
| --- | --- | --- |
| `neoforge` | required | `[21.1.0,)` |
| `minecraft` | required | `[1.21.1,1.22)` |
| `tetra` | required | `[7.0.0,)` |
| `ftbultimine` | optional | `[2101,)` |
| `alexscaves` | optional | `[2.0.0,)` |
| `farmersdelight` | optional | `[1,)` |
| `exdeorum` | optional | `[3,)` |
| `tetracelium` | optional | `[1,)` |

说明：原 toml 未声明 exdeorum，本次按代码中的软依赖语义补为 optional。

## 6. 构建环境

- 离线构建：`.\gradlew build --offline --console=plain`（JDK 21）。
- 已复制 `geotetraarmor/build/moddev/artifacts/*` 到 `build/moddev/artifacts/`，并禁用 `createMinecraftArtifacts`。
- 强制 `net.neoforged:accesstransformers:10.0.1` 以绕开本机缺失的 13.0.1 外部工具。
- `processResources` 展开 `${version}` 与 `${loader_version_range}`。
- 编译仅有 `EventBusSubscriber.Bus` 的 deprecation 警告（NeoForge 21.1 标记待删除，但仍可用），无错误。
