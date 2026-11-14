# 测试实例 Tetra 附属移植评估（只评估，不改代码）

评估对象：`D:\PCL 正式版 2.13.0.1\.minecraft\versions\测试\mods`
评估基准：已移植批次（1.21.1 NeoForge 21.1.266 + Tetra 7.0.0 + mutil 7.0.0）
全量 jar 清单：见 [测试包mods全量清单.txt](file:///D:/mod移植/2026-11-13/evaluation/测试包mods全量清单.txt)

## 0. 结论摘要

- 目录内共 **364 个 jar**；与 Tetra 相关的 **7 个**（tetra 本体、mutil、5 个附属）。
- **唯一的新附属是 VVAddon（modid `vvaddon`，版本 3.0.1）**。
- 其余 4 个附属均已完成 1.21.1 移植：art_of_forging、tetracelium、tetracompat、tetratic_combat_expanded。
- VVAddon 的 1.20.1 源码可在 GitHub 找到（分支 `1.20.1`，无 1.21.1 分支）；前置只有 Tetra，1.21.1 侧已齐备；数据格式与 7.0.0 兼容；需要把 Forge 事件/实体/配置迁移到 NeoForge 1.21.1。
- **建议：可以移植，难度“中”；但该 mod 声明 All Rights Reserved，分发前应先取得作者授权。**

## 1. 扫描方法与交叉验证

对 364 个 jar 逐个读取 `META-INF/mods.toml` / `META-INF/neoforge.mods.toml`，解析 `[[mods]]` 与 `[[dependencies.*]]`；另外做了两项交叉验证：

1. 扫描所有 `.class` 常量池中对 `se/mickelus/tetra` 的引用；
2. 扫描 `data/tetra/**`、`assets/tetra/**` 资源条目。

三种方式结果一致：只有下列 7 个 jar 与 Tetra 相关，没有“未声明依赖但引用 Tetra API”的隐藏附属，也没有纯数据型 Tetra 附属。

## 2. Tetra 相关 jar 明细

| Jar | modid | 版本 | 目标 MC / loader | 声明的依赖 | 性质 |
|---|---|---|---|---|---|
| tetra-1.20.1-6.11.0.jar | tetra | 6.11.0 | 1.20.1 / Forge 47+ | mutil `[6.2.0,]`；curios 可选 | Tetra 本体 |
| mutil-1.20.1-6.2.0.jar | mutil | 6.2.0 | 1.20.1 / Forge 47+ | 无 | Tetra 前置库 |
| art_of_forging-1.8.4-1.20.1.jar | art_of_forging | 1.8.4-1.20.1 | 1.20.1 / Forge 47+ | tetra `[6.0,)`、mutil `[6.0,)`、soul_fire_d `[4.0.2,)` | Tetra 附属 |
| tetracelium-1.20.1-1.3.1.jar | tetracelium | 1.3.1 | 1.20.1 / Forge 47+ | tetra `[6.5.0,)`；farmersdelight、soul_fire_d 可选 | Tetra 附属 |
| tetracompat-1.0.0-all.jar | tetracompat | 1.0.0 | 1.20.1 / Forge 47+ | tetra `[0,)`；ftbultimine、alexscaves、farmersdelight、tetracelium 可选 | Tetra 兼容附属 |
| tetratic-combat-expanded-1.20-2.8.3.jar | tetratic_combat_expanded | 2.8.3 | 1.20.x / Forge 47.1.79+ | tetra `[6.1,)`、bettercombat `[1.8,)`；art_of_forging 可选 | Tetra 附属 |
| vvaddon-1.20.1-alpha3.0.1.jar | vvaddon | 3.0.1 | 1.20.1 / Forge 47+ | tetra `[6.2.0,]` | **Tetra 附属（新）** |

## 3. 与已移植批次比对

| 附属 | 本目录是否存在 | 状态 |
|---|---|---|
| art_of_forging | 是（1.8.4） | 已移植：`art_of_forging-1.21.1-1.8.4.jar`（535,741 B），当前 1.21.1 实例已安装 |
| tetracelium | 是（1.3.1） | 已移植：`tetracelium-1.21-1.21.1-1.3.1.jar`（295,281 B），当前 1.21.1 实例未安装 |
| tetracompat | 是（1.0.0） | 已移植：`tetracompat-1.21-1.21.1-1.0.0.jar`（22,703 B），已安装 |
| tetratic_combat_expanded | 是（2.8.3） | 已移植：`tetratic_combat_expanded-1.21-1.21.1-2.8.3.jar`（25,928 B），已安装 |
| **vvaddon** | 是（3.0.1） | **新，未移植** |
| tetrajs | 否 | 此前移植过，已放弃（KubeJS 1.21.1 类型重复注册问题），本目录也没有该包 |
| tetra_data_kubejs | 否 | 同上，已放弃 |
| fluxoftetra / tetrawhetstone / tetra_modify_core / tetra_over_honing / tetra_view / tetra_addition / tetra_material_overhaul / tetra_attribute_rebalancing / tetra_inherit_enchant / morerequirement / simplejadetetra / geotetraarmor / extraholopage / tetra_gateways | 否 | 均已移植，本目录未包含这些 jar，不属于本轮对象 |

## 4. VVAddon 可行性判定（新）

### 4.1 源码可得性

- **GitHub 仓库**：<https://github.com/VV-VittorioVeneto/VVAddon>
- **分支**：`1.19.2`、`1.20.1`（默认分支）、`main`（指向同一提交）；**没有 1.21.1 分支**。
- 最后推送：2024-06-08；`gradle.properties` 中 `mod_id=vvaddon`、`mod_version=3.0.1`，与包内 jar 一致。
- 仓库内容 **比 alpha3.0.1 的 jar 更新**：多了 `Confuse`、`Vampire`、`GenerateJSON`、`VANameMe`、`VANameRe` 等类，以及 `comfused`/`vampire` 改进、`flatbow/long/recurve_stave` 等数据；从仓库移植会得到比包内 jar 更多的内容。
- **授权风险**：包内 `mods.toml` 为 `All Rights Reserved`；仓库根目录 `LICENSE.txt` 是 Forge MDK 的通用许可证，不是该 mod 的授权；GitHub API 显示 `license: Other / NOASSERTION`。若要分发移植包，需先联系作者取得许可。

### 4.2 前置可用性

- 必需前置只有 `forge + minecraft + tetra`，无其他硬依赖。
- 1.21.1 侧：`tetra-1.21.1-7.0.0.jar`（已安装）、`mutil-1.21.1-7.0.0.jar`（已安装）→ **前置全部满足**。
- 无 mixin、无 access transformer、无运行时数据生成依赖（`DataGenerators`/`ItemModes` 是 datagen 类，可不移植）。

### 4.3 内容与 API 依赖

包内容（jar）：38 个 class（CFR 反编译为 34 个顶层 .java）+ 5 个物品（`mine_ingot`、`refine_ia/ib/iia/iib`）+ 1 个自定义箭实体与渲染器 + 1 个 MobEffect 类（未注册）+ 1 个音效 `katana_rush.ogg` + 20 个 improvement + 3 个材料 + 15 个模块的 improvement 合并数据。

Tetra 7.0.0 API 核对结果：

- `se.mickelus.tetra.effect.ItemEffect#get(String)` 存在（`ItemEffect.java`，字符串键）；
- `ModularItem#getEffectLevel(ItemStack, ItemEffect)` 存在（`IModularItem` 默认方法，7.0.0 源码第 899 行）；
- `ModularItem#getDamage/setDamage/getMaxDamage` 存在（`ModularItem.java` 第 179-189 行）；
- 未使用其它 Tetra 内部 API，没有需要 mixin/AT 的部分。

数据格式核对（与 7.0.0 实际数据对比）：

- `data/tetra/improvements/**`：数组 + `key` + `effects`（`ItemEffect` 字符串键）→ 兼容；
- `data/tetra/materials/**`：`key/category/tints/textures/material.items/improvements/requiredTools` → 兼容；`tints` 的十六进制字符串由 `MaterialColors.Deserializer` 按 16 进制解析；
- `data/tetra/modules/**`：`{"replace": false, "improvements": ["tetra:vvaddon/..."]}` → `ModuleData.improvements` 在 7.0.0 已改为 `ResourceLocation[]`，字符串可直接解析，合并语义保留。

需要迁移的 loader/MC API（机械性工作）：

- Forge → NeoForge：`MinecraftForge.EVENT_BUS`、`FMLJavaModLoadingContext`（改为构造器注入）、`DeferredRegister`/`RegistryObject` → `DeferredRegister`/`DeferredHolder`、`ForgeRegistries` → `BuiltInRegistries`；
- 事件：`TickEvent.PlayerTickEvent`（带 phase）→ `PlayerTickEvent.Post`；`LivingHurtEvent` → `LivingIncomingDamageEvent`；`LivingDeathEvent`、`PlayerInteractEvent.RightClickItem` 直接对应；
- 配置：`ForgeConfigSpec` → `ModConfigSpec`（约 50 个配置项，逐项改写）；
- 实体：`PhantomArrow` 继承 `AbstractArrow`，需要适配 1.21.1 的构造器、`defineSynchedData(SynchedEntityData.Builder)`、拾取状态与 `getPickupItem`；`ArrowItem#createArrow` 的参数在 1.21.1 有变化；
- 注册与创造标签：`DeferredRegister.Items`、`CreativeModeTab` 构建方式微调；`SoundEvent.createVariableRangeEvent` 可直接用。

### 4.4 可行性结论

- **可移植**；难度评估 **中**（源码齐全，数据基本直接复用，工作量集中在事件/实体/配置迁移与回归测试）。
- 无“不可实现”的功能；唯一非技术阻碍是 **All Rights Reserved 的授权**。
- 两个需要提前说明的点：
  1. 包内 jar 与仓库都 **没有配方**，5 个物品只能创造模式获取；若要生存可玩需额外补配方（属于新增内容，不是原包行为）。
  2. `VVAddonEffectRegistry`（phantom_persuit 效果）在 `MainVVAddon` 中 **没有被注册**，jar 里实际上是死代码；移植时要么补注册，要么删除，不能当作现有功能。

## 5. 排序表（按“值得先做”）

| 排序 | 附属 | 是否新 | 源码可得性 | 前置可用性 | 预估难度 | 主要风险 | 建议 |
|---|---|---|---|---|---|---|---|
| 1 | VVAddon（vvaddon 3.0.1） | 是 | GitHub `VV-VittorioVeneto/VVAddon`，分支 `1.20.1` | 满足（tetra/mutil 1.21.1 已就绪，无其他硬依赖） | 中 | ① All Rights Reserved，分发需授权；② 事件/实体/配置迁移；③ 自定义箭实体行为需实测；④ 无配方 | **可做，先确认授权** |
| — | art_of_forging | 否（已移植） | 已有 | 满足 | — | — | 无需再做 |
| — | tetracelium | 否（已移植） | 已有 | 满足 | — | — | 无需再做（当前实例未装） |
| — | tetracompat | 否（已移植） | 已有 | 满足（alexscaves/FTB Ultimine/FarmersDelight 均在 1.21.1） | — | — | 无需再做 |
| — | tetratic_combat_expanded | 否（已移植） | 已有 | 满足 | — | — | 无需再做 |
| — | tetrajs / tetra_data_kubejs | 否（已放弃） | 有 | KubeJS 1.21.1 类型冲突 | 大 | 历史崩溃点，不建议重启 | **不建议做** |

**不建议做的情况**：若无法取得 VVAddon 作者授权，则不建议对外分发移植包；除此之外本轮没有其它新的 Tetra 附属需要评估。

## 6. 参考来源

- [VVAddon GitHub 仓库](https://github.com/VV-VittorioVeneto/VVAddon)
- [1.20.1 分支 build.gradle](https://github.com/VV-VittorioVeneto/VVAddon/blob/1.20.1/build.gradle)
- [1.20.1 分支 gradle.properties](https://github.com/VV-VittorioVeneto/VVAddon/blob/1.20.1/gradle.properties)
- [1.20.1 分支 mods.toml](https://github.com/VV-VittorioVeneto/VVAddon/blob/1.20.1/src/main/resources/META-INF/mods.toml)
