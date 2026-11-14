# tetracelium 1.3.1 → Minecraft 1.21.1 / NeoForge 21.1.238 移植说明

## 1. 源版本与产物

| 项目 | 值 |
| --- | --- |
| 原 jar | `2026-11-13/reference/work/addons/tetracelium.jar`（Forge 1.20.1，1.3.1） |
| 反编译源 | `2026-11-13/reference/work/addons/tetracelium_dec`（32 个 .java + 1 个内部类，共 33 个 class） |
| 新工程 | `2026-11-13/addons/tetracelium` |
| 产物 | `build/libs/tetracelium-1.21-1.21.1-1.3.1.jar`（295,285 字节） |
| 目标 | Minecraft 1.21.1 / NeoForge 21.1.238 / Java 21 |
| modId | `tetracelium`（未变） |
| 公开包名/类名 | 未变（`se.mickelus.tetracelium.*`） |

## 2. 数量核对

| 类别 | 原 jar | 新 jar | 差异与原因 |
| --- | --- | --- | --- |
| class 文件 | 33 | 33 | 32 个顶层类 + 1 个匿名内部类；原 `FarmersDelightCuttingRecipeProvider$FinishedRecipeConditionWrapper` 被 1.21 的 `RecipeOutput` 包装器取代（仍计入匿名类） |
| mixin 配置 | 1 | 1 | `tetracelium.mixins.json`，去掉 refmap，`compatibilityLevel: JAVA_21` |
| Mixin 类 | 1 | 1 | `CuttingBoardBlockEntityMixin` |
| AT 文件 | 1 | 1 | `META-INF/accesstransformer.cfg`，SRG 名改为官方名 |
| assets 文件 | 120 | 120 | 原样复制 |
| data 文件 | 183 | 183 | 内容按 1.21.1 迁移，另有目录重命名见下 |
| 元数据 | `mods.toml` | `neoforge.mods.toml` | Forge→NeoForge 必需 |
| 构建缓存 | `.cache/*` | 无 | Forge 构建缓存，不随产物发布 |

数据目录重命名（内容文件数不变）：

- `data/tetracelium/recipes/` → `data/tetracelium/recipe/`
- `data/minecraft/tags/items/` → `data/minecraft/tags/item/`
- `data/forge/loot_modifiers/` → `data/neoforge/loot_modifiers/`

## 3. Mixin 与 AT 清单

### Mixin

- 文件：`src/main/java/se/mickelus/tetracelium/mixin/CuttingBoardBlockEntityMixin.java`
- 目标：`vectorwing.farmersdelight.common.block.entity.CuttingBoardBlockEntity#processStoredItemUsingTool(ItemStack, Player)Z`
- 注入：`@At("RETURN")`，返回值 true 且工具为 `IModularItem` 时调用 `applyUsageEffects(player, stack, 2.0)`
- `@Mixin(remap = false)`：1.21.1 运行时为官方名，FD 目标无需 remap；配置中无 refmap
- 配置：`tetracelium.mixins.json`，`required: true`（与原配置一致；FD 未安装时该类不会被加载，mixin 不会尝试应用）

### Access Transformer

```
public net.minecraft.world.level.biome.Biome getTemperature (Lnet/minecraft/core/BlockPos;)F
```

- 原 SRG：`public net.minecraft.world.level.biome.Biome m_47505_ (Lnet/minecraft/core/BlockPos;)F`
- 1.21.1 中该方法仍为 `private`，AT 已改为官方名并在 `neoforge.mods.toml` 通过 `[[accessTransformers]] file = "META-INF/accesstransformer.cfg"` 声明
- 说明：移植后的代码不再直接调用该方法（与原反编译源码一致，原 AT 实际未被代码引用）；AT 保留用于保持清单一致

## 4. 兼容性统计（按 33 个源 class）

| 状态 | 数量 |
| --- | --- |
| ✅ 直接等价迁移 | 24 |
| ⚠️ 有语义/依赖适配，需运行期验证 | 9 |
| ❌ 无法迁移/删除功能 | 0 |

明细：

| # | 类 | 状态 | 说明 |
| --- | --- | --- | --- |
| 1 | TetraceliumMod | ✅ | 构造器注入 `(IEventBus, ModContainer)`；`NeoForge.EVENT_BUS`；`FMLCommonSetupEvent`/`FMLClientSetupEvent`/`GatherDataEvent`；包注册移到构造器并 `addListener(packetHandler::register)` |
| 2 | TetraceliumRegistries | ✅ | `DeferredRegister.create(BuiltInRegistries.ITEM, "tetracelium")` |
| 3 | TetraceliumTextures | ✅ | `ResourceLocation.fromNamespaceAndPath` |
| 4 | ApotheosisCompat | ✅ | `ModList` + `ItemUpgradeRegistry.instance.registerReplacementHook`（签名已核对） |
| 5 | AffixReplacementHook | ⚠️ | 1.21 无裸 NBT，改用 `DataComponents.CUSTOM_DATA`；Apotheosis 1.21 实际组件语义无法用所给依赖验证（未提供 apotheosis jar） |
| 6 | BotaniaCompat | ✅ | `NeoForge.EVENT_BUS` |
| 7 | ManaRepair | ✅ | `PlayerTickEvent.Post`；`ManaItemHandler.instance().requestManaForTool(stack, player, int, true)` 已按 Botania 457 javap 核对 |
| 8 | ManaRepairStatGetter | ✅ | `StatGetterEffectEfficiency` API 未变 |
| 9 | DummyKnifeBladeItem | ✅ | `canPerformAction(ItemStack, ItemAbility)`；不再硬引用 FD 类 |
| 10 | FarmersDelightCompat | ✅ | `NeoForge.EVENT_BUS` |
| 11 | FarmersDelightEvents | ⚠️ | `setCancellationResult` 等 API 已核对；原音效 `SoundEvents.f_12635_` 已失效无法从 SRG 精确反查，改为 `SoundEvents.WOOD_BREAK`（音高 0.8，与 FD 砧板处理音效一致，仅音效差异） |
| 12 | FarmersDelightToolActions | ✅ | `ToolAction("blade_cut")` → `ItemAbility.get("knife_dig")` |
| 13 | FinishedRecipeConditionWrapper | ✅ | 1.21 无 `FinishedRecipe`，改为匿名 `RecipeOutput` 包装，保留“改命名空间 + 加 mod_loaded 条件”行为 |
| 14 | FarmersDelightCuttingRecipeProvider | ⚠️ | `buildRecipes(RecipeOutput)`；构造器新增 `HolderLookup.Provider`；`ICondition` + `withConditions`；`ItemAbilityIngredient.toVanilla` 包装；仅 runData 使用，不影响成品 jar 内已迁移的 JSON |
| 15 | SoulFiredCompat | ✅ | `NeoForge.EVENT_BUS` |
| 16 | SoulFiredEffect | ⚠️ | soul_fire_d 6.1.0 已把 API 拆分到 `prometheus`（不在所给依赖中），改用 `FireRegistry.SOUL_FIRE_TYPE` + 反射 `it.crystalnest.prometheus.api.type.FireTypeChanger.setFireType`（沿用已完成的 art_of_forging 移植先例）；附魔检测改为检查 `minecraft:soul_flame` / `minecraft:soul_fire_aspect` |
| 17 | TwilightForestCompat | ✅ | 事件注册改造；GUI 统计条初始化保持 |
| 18 | ChargingEffect | ✅ | `LivingHurtEvent` → `LivingDamageEvent.Pre`，`setNewDamage` |
| 19 | EnderProjectilesEffect | ✅ | `ProjectileImpactEvent` 未变；`player.noActionTime=40` → `setNoActionTime(40)` |
| 20 | EndershotEffect | ✅ | TF 4.8 仍使用 `PersistentData` 键 `twilightforest:ender`（已 javap 核对 EnderBowItem） |
| 21 | FortifyingEffect | ⚠️ | TF 能力 `CapabilityList.SHIELDS` 移除，改用数据附件 `TFDataAttachments.FORTIFICATION_SHIELDS` + `setShields(player, n, true)` |
| 22 | HydralBondEffect | ✅ | `DamageTypeTags.IS_FIRE`、`LivingDamageEvent.Pre` |
| 23 | IceProjectilesEffect | ✅ | `IceBowItem.customArrow(arrow, firingStack, ammoStack)` 三参版本 |
| 24 | IgnitingEffect | ✅ | `igniteForSeconds`、`ApplyHitTargetEffectsEvent` 未变 |
| 25 | PunishingEffect | ✅ | `LivingDamageEvent.Pre` + `ClientboundAnimatePacket` 广播 |
| 26 | ReanimatingEffect | ⚠️ | `MobType.UNDEAD` → `isInvertedHealAndHarm()`；`TFSounds.LOYAL_ZOMBIE_SUMMON` 在 TF 4.8 已删除，改用 `TFSounds.MINION_SUMMON`；`setTame(true, true)` |
| 27 | SapParticlePacket | ✅ | 新 `CustomPacketPayload.type()`；`writeVec3/readVec3` |
| 28 | SappingEffect | ⚠️ | 包 API/`FoodData.eat` 已核对；粒子类型原为 SRG `f_123811_`，按语义映射为 `ParticleTypes.CRIT`（红色魔法轨迹）；`sendToAllPlayersNear(packet, ServerLevel, BlockPos, double)` |
| 29 | SeekerProjectilesEffect | ✅ | `SeekerBowItem.customArrow` 三参版本 |
| 30 | TwilightBoltEffect | ⚠️ | `TFSounds.SCEPTER_PEARL` 已删除，改用 `TWILIGHT_SCEPTER_USE`；`InputEvent.InteractionKeyMappingTriggered`；`fireProjectile` 在 7.0.0 只保留一个 `piercingLevel` int 参数（power/punch/flame 由 tetra 内部处理） |
| 31 | TwilightBoltPacket | ✅ | 新 `CustomPacketPayload.type()` |
| 32 | VerticalMultishotEffect | ⚠️ | 附魔改用 `RegistryAccess.registryOrThrow` + `getTagEnchantmentLevel(Holder,...)`；`fireProjectile` 参数收敛（见 #30） |
| 33 | CuttingBoardBlockEntityMixin | ✅ | FD 方法签名未变，`remap = false` |

## 5. 依赖声明

与原 `mods.toml` 的语义保持一致（见 `src/main/resources/META-INF/neoforge.mods.toml`）：

| 依赖 | 类型 | 说明 |
| --- | --- | --- |
| neoforge | required | `[21.1.0,)` |
| minecraft | required | `[1.21.1,1.22)` |
| tetra | required | `[7.0.0,)`，ordering AFTER（原为 Forge 1.20.1 的 tetra 版本范围，已按目标版本调整） |
| farmersdelight | optional | 与原 toml 一致 |
| soul_fire_d | optional | 与原 toml 一致 |
| twilightforest / botania | 未声明 | 与原 toml 一致（软兼容，运行期 `ModList` 检测）；仅作为编译期 `libs/` 依赖 |

编译期依赖 jar（`libs/`）：tetra 7.0.0、mutil 7.0.0、TF 4.8.3345、Botania 457、FarmersDelight 1.3.4、soul_fire_d 6.1.0。未引入 prometheus/cobweb 等额外 jar，也未伪造 stub。

## 6. 主要数据迁移

- `forge:mod_loaded` → `neoforge:mod_loaded`（全部 tetra 数据与 tetracelium 配方）
- 通用 `forge:*` 物品标签 → `c:*`（如 `c:ingots/*`、`c:gems/*`、`c:tools/knife`）
- FD 切割配方：`farmersdelight:tool_action` + `blade_cut` → `farmersdelight:item_ability` + `knife_dig`（tool 变为数组）；结果格式改为 1.21 的 `{"item":{"id":...,"count":...}}`；`forge:dough` → `c:foods/dough`
- `tetra:modular_toolbelt` 配方：`result.nbt` → `result.components.minecraft:custom_data`（与 tetra 7.0.0 自带配方格式一致）
- 全局掉落修改器移到 `data/neoforge/loot_modifiers/global_loot_modifiers.json`；`tetra:item_effect` 谓词在 7.0.0 仍受支持
- 鞘翅/武器模块：`blade_cut` → `knife_dig`；`xpCost` → `experienceCost`（7.0.0 `OutcomeDefinition` 字段）
- 修正 `botania:manasteel_pick` → `botania:manasteel_pickaxe`（Botania 457 实际 id）
- `pack.mcmeta`：`pack_format: 34`，`supported_formats: [34, 48]`

已知数据风险（未改结构，仅记录）：

- TF 4.8 已移除 Baobab/Maple 木材，`c:planks/baobab`、`c:planks/maple` 标签在 1.21 无提供者，对应两个材料实际不可获得（数据保留，未删除 TF 兼容块）
- 原数据中 `bow/seeker_stave` 等模块的替换变体键写作 `tri_stave/*`，与模块自身变体键 `seeker_stave/*` 不一致（1.20.1 原样存在），按“保持原数据”原则未改动
- `livingwood.json` 等对其他可选 mod（botania 之外的 naturesaura/thermal 等）的材料仍按原文件保留，未安装时由 `neoforge:mod_loaded` 条件跳过

## 7. 构建

```powershell
cd d:\mod移植\2026-11-13\addons\tetracelium
.\gradlew build --offline --console=plain
```

- JDK 21（`C:\Program Files\Java\jdk-21.0.12`）
- 复用 `geotetraarmor/build/moddev/artifacts`（已复制到本工程），`createMinecraftArtifacts` 已禁用
- 强制 `net.neoforged:accesstransformers:10.0.1`
- 构建结果：`BUILD SUCCESSFUL`，jar 见第 1 节
