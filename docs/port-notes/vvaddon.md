# VVAddon 1.21.1 NeoForge 移植笔记

## 0. 移植基准

- **源码基准**：GitHub `VV-VittorioVeneto/VVAddon` 分支 `1.20.1`（压缩包，2024-06 最后提交；`mod_version=3.0.1`）。
- **差异核对基准**：测试实例 `vvaddon-1.20.1-alpha3.0.1.jar`（modid `vvaddon`，版本 3.0.1）。
- 目标环境：Minecraft 1.21.1 / NeoForge 21.1.266 / Tetra 7.0.0 / mutil 7.0.0。
- 红线：不改 tetra/mutil 本体；1.21 使用 DataComponents（本 mod 无物品 NBT 持久化）；无 mixin/AT。

## 1. 基数清单（仓库 1.20.1 分支）

### 1.1 代码

| 类别 | 数量 | 明细 |
|---|---|---|
| Java 文件 | 36 | 主类 1、Config 1、Effect 1、Entity 1、Feature 15、Handler 2、Init 7、Renderer 1、TestE 3、Utils 4 |
| 运行时注册项 | 见下 | 物品 5、实体 1、效果 1（未注册）、音效 1、创造标签 1、配置 39 项 |
| mixin | 0 | 无 mixin 配置 |
| access transformer | 0 | 无 AT |
| 网络包 | 0 | 无自定义 payload；箭实体使用 EntityData 同步 |

**Feature 类（15）**：Blood、Charge、Combo、Confuse、EffectProcedure、Evil、Exp、Height、HitAway、Homology、Honor、Phantom、Rush、Sniper、Vampire。

**TestE / Utils（不注册，随源码保留）**：TestE/Cheater、DamagedProcedure、Dimensions；Utils/GenerateJSON、PEMap、VANameMe、VANameRe。

### 1.2 注册项

| 类型 | 数量 | 内容 |
|---|---|---|
| 物品 | 5 | `refine_ia`、`refine_ib`、`refine_iia`、`refine_iib`、`mine_ingot`（原包均无配方） |
| 实体 | 1 | `phantom_arrow`（PhantomArrow，MobCategory.MISC，0.5×0.5） |
| MobEffect | 1 | `phantom_persuit`（`VVAddonEffectRegistry` 在 Main 中**从未注册**，死代码，按原样保留） |
| SoundEvent | 1 | `katana_rush` |
| 创造标签 | 1 | `vvaddon:vvaddon_tab`，放入上述 5 个物品 |
| 配置项 | 39 | `VVAddonConfig`（充能/远射/高地/血统/连击/邪恶/荣誉/经验/幻影/突进/吸血等；中文配置注释） |

### 1.3 数据文件（`data/tetra/**`，共 47 个）

| 类别 | 数量 | 明细 |
|---|---|---|
| improvements | 22 | bloodfull、bloodlow、charge、combo、comfused、evil、exp_i/ii/iii、hasteeffect、height、hitaway、homology、honor、nightvisioneffect、phantom_persuit、phantom_rain、rush、sniper、speedeffect、strengtheffect、vampire |
| materials | 3 | metal/refine_ia、metal/refine_iia、metal/refine_iib |
| modules（merge） | 22 | bow：flatbow_stave、long_stave、recurve_stave、straight_stave；double：adze、basic_axe、basic_pickaxe、claw、hoe、sickle；single：basic_shovel、spearhead、trident；sword：basic_blade、bastard_blade、executioner_blade、heavy_blade、machete、rapier_blade、saber_blade、short_blade、throwing_knife |

### 1.4 资源（`assets/vvaddon/**`）

| 类别 | 数量 | 明细 |
|---|---|---|
| 物品模型 | 5 | 与 5 个物品一一对应 |
| 贴图 | 6 | 5 个物品 + `entity/phantom_arrow.png` |
| 音效 | 1 | `sounds/katana_rush.ogg` + `sounds.json` |
| 语言 | 46 键 | 仅 `zh_cn.json`（仓库/原 jar 都没有 en_us） |
| 其他 | 2 | `pack.mcmeta`、`META-INF/mods.toml` |

**资源文件合计 63 个**（47 数据 + 16 非数据，含目录外文件）。

## 2. 仓库 1.20.1 与 alpha3.0.1 jar 的差异

| 项 | 仓库 1.20.1 | alpha3.0.1 jar | 处理 |
|---|---|---|---|
| improvements | 22 | 20 | 仓库多 `comfused`、`vampire`，**纳入移植** |
| modules | 22 | 15 | 仓库多 `bow/flatbow_stave`、`bow/long_stave`、`bow/recurve_stave`、`sword/bastard_blade`、`sword/executioner_blade`、`sword/rapier_blade`、`sword/saber_blade`，**纳入移植** |
| 类 | 36 个 .java | 34 个顶层类（含 4 个内部类共 38 class） | 仓库多 Confuse、Vampire、Utils/GenerateJSON、VANameMe、VANameRe；仓库少 DataGenerators、ItemModes、Language（datagen/语言辅助，原 jar 有） |
| materials | 3 | 3 | 一致 |
| 物品/实体/效果/音效 | 5/1/1/1 | 5/1/1/1 | 一致 |
| 配置项 | 39 | 约 36（无 Vampire 三项） | 以仓库 39 项为准 |
| 依赖声明 | tetra `[6,)` | tetra `[6.2.0,]` | 1.21.1 统一改 `[7.0.0,)` |

## 3. 移植执行顺序

1. 注册层：主类事件总线、物品/实体/音效/创造标签/配置注册；
2. 数据组件/持久化：本 mod 无物品 NBT；箭实体沿用 EntityData 同步；
3. 网络同步：无自定义包，确认无需迁移；
4. 事件与实体：Forge 事件 → NeoForge 事件、`ForgeRegistries` → `BuiltInRegistries`、`AbstractArrow` 适配；
5. 数据与资源：`data/tetra/**`、`assets/vvaddon/**`、`neoforge.mods.toml`。

## 4. 构建与核对结果

### 4.1 构建

- 工程：`D:\mod移植\2026-11-13\addons\vvaddon`（复用 universalscroll 脚手架；NeoForge 21.1.238 为本地缓存可用版本，工程实际使用该版本，依赖声明仍为 `[21.1.0,)`）。
- 命令：`.\gradlew.bat build --offline --console=plain`。
- 结果：`BUILD SUCCESSFUL`（仅有 1 条 `initializeClient` 过时警告 + 1 条 Vampire 使用过时 API 警告，均为预期）。
- 产物：`build/libs/vvaddon-1.21-3.0.1.jar`，119,605 字节（修复事件总线注册后重新构建的版本）。
- 已复制交付：`D:\mod移植\2026-11-13\deploy-mods\vvaddon-1.21-3.0.1.jar`。

**实测修复（2026-11-14）**：1.21.1 实例首次加载报
`IllegalArgumentException: class com.vv.vvaddon.MainVVAddon has no @SubscribeEvent methods, but register was called anyway`。
原因是原版 `MinecraftForge.EVENT_BUS.register(this)` 注册了自身，而 `MainVVAddon` 没有任何 `@SubscribeEvent`；NeoForge 1.21.1 对此直接抛异常。已移除该行（仅保留 `register(new VVAddonEventHandler())`）并重建。

### 4.2 数量核对（jar 内实际文件，去掉目录项）

| 项 | 基数 | jar 实际 | 结论 |
|---|---|---|---|
| Java 源文件 | 36 | 36（43 个 class，含 7 个内部/匿名类） | 一致 |
| improvements | 22 | 22 | 一致 |
| materials | 3 | 3 | 一致 |
| modules | 22 | 22 | 一致 |
| 物品模型 | 5 | 5 | 一致 |
| 贴图 | 6 | 6 | 一致 |
| 音效 | 1 | 1（sounds.json 1 键） | 一致 |
| 语言键 | 46 | 46 | 一致 |
| mixin | 0 | 0 | 一致 |
| access transformer | 0 | 0 | 一致 |
| 网络包 | 0 | 0 | 一致 |

其他条目：`META-INF/MANIFEST.MF`、`META-INF/neoforge.mods.toml`（`modId=vvaddon`、`version=3.0.1`）；无 `pack.mcmeta`（NeoForge 1.21.1 不要求，与 universalscroll 脚手架一致）。

### 4.3 迁移改动清单（按处理顺序）

**注册层**
- 主类改为 `@Mod` 构造器注入 `IEventBus` / `ModContainer`，事件总线改用 `NeoForge.EVENT_BUS`。
- `DeferredRegister` + `ForgeRegistries.*` → `DeferredRegister.create(Registries.*)` / `DeferredRegister.createItems`，`RegistryObject` → `DeferredHolder` / `DeferredItem`。
- `ForgeConfigSpec` → `ModConfigSpec`；`ForgeConfigSpec#setConfig(CommentedFileConfig)` 在 1.21.1 已移除，改为 `ModContainer#registerConfig(ModConfig.Type.COMMON, spec)`。配置文件由 `config/vvaddon.toml` 变为 `config/vvaddon-common.toml`，39 项与中文注释保持原样。
- `VACoe` 原在构造期读取配置；因 NeoForge 配置在 mod 构造后才加载，改为监听 `ModConfigEvent.Loading` / `Reloading` 后再 `new VACoe()`。

**数据组件 / 持久化**
- 本 mod 无物品 NBT。箭实体沿用 `SynchedEntityData`，`defineSynchedData()` 改为 `defineSynchedData(SynchedEntityData.Builder)`，在 builder 上 `define`。

**网络同步**
- 无自定义 payload，无需迁移。

**事件与实体**
- `TickEvent.PlayerTickEvent`（原仅处理 `Phase.START`）→ `PlayerTickEvent.Pre`。
- `LivingHurtEvent` → `LivingIncomingDamageEvent`。
- `PlayerInteractEvent.RightClickItem` / `LivingDeathEvent` 保持同类，仅包名换为 `net.neoforged.neoforge.event.*`。
- `ForgeRegistries.ITEMS.getKey` → `BuiltInRegistries.ITEM.getKey`（Charge、Homology）。
- 伤害读取：`ModularItem#getDamage(ItemStack)` 在 7.0.0 中不存在，统一改为 `ItemStack#getDamageValue()`；写回仍用 `ModularItem#setDamage`（Evil、Honor、Homology、DamagedProcedure）。
- `Mob#getExperienceReward()` → `getExperienceReward(ServerLevel, Entity)`（Exp）。
- `ItemStack#getEnchantmentLevel` → `EnchantmentHelper.getItemEnchantmentLevel(Holder, ItemStack)`；`Enchantments.MOB_LOOTING` 在 1.21.1 更名为 `LOOTING` 且为 `ResourceKey`，需经注册表取 Holder（Vampire）。
- `ItemStack#hurtAndBreak(int, LivingEntity, Consumer)` → `hurtAndBreak(int, LivingEntity, EquipmentSlot)`（Cheater）。
- `ArrowItem#createArrow` 增加第 4 个参数（发射物品），补 `bowStack`（Phantom、Cheater）。
- `AbstractArrow` 构造器改为携带 `pickup` / `weapon` 两个 `ItemStack`；新增 `getDefaultPickupItem()` 返回 `ItemStack.EMPTY`。
- `MobEffects.*` 为 `Holder<MobEffect>`，`MobEffectInstance` 只接受 Holder，`EffectProcedure` 参数类型改为 `Holder<MobEffect>`。
- `MobEffect#isDurationEffectTick` → `shouldApplyEffectTickThisTick`；`applyEffectTick` 返回值 `void` → `boolean`；`addAttributeModifiers(LivingEntity, AttributeMap, int)` → `(AttributeMap, int)`。
- `new ResourceLocation(ns, path)` → `ResourceLocation.fromNamespaceAndPath`。

**数据与资源**
- `data/tetra/**` 47 个 JSON 原样拷贝（improvements 22 / materials 3 / modules 22），格式与 7.0.0 一致（`ModuleData.improvements` 已是 `ResourceLocation[]`，`"tetra:vvaddon/xxx"` 与 improvements 的 `vvaddon/xxx` 键可对应）。
- `assets/vvaddon/**` 14 个文件原样拷贝；`neoforge.mods.toml` 重写。

### 4.4 已知限制与差异

- **穿透等级不可设置（行为回退）**：1.21.1 的 `AbstractArrow#setPierceLevel(byte)` 为 `private`，子类无法调用，`PIERCE = 64` 的穿透在 1.21.1 不可用（幻影箭默认穿透 0，命中一个实体即停止）。代码中已标【上游 API 已移除，待人工评估】。
- `PhantomPersuitMobEffect` 在 1.20.1 原版即从未注册（死代码），本次仍保留且未注册；`phantom_persuit` 不作为既有功能。
- `Vampire.execute` 在 1.20.1 原版没有任何调用点（死代码），按原样保留并仅做编译适配。
- 5 个物品仍无配方（原包即无）。
- 原 `pack.mcmeta` 未随迁移，NeoForge 1.21.1 下不影响资源加载。
