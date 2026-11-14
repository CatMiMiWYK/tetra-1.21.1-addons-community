# 1.21.1 NeoForge 移植包 测试清单

适用实例：`D:\PCL 正式版 2.13.0.1\.minecraft\versions\1.21.1-NeoForge_21.1.266`
部署包暂存：`D:\mod移植\2026-11-13\deploy-mods`（执行其中的 `deploy.ps1` 铺入 mods）

标记说明：
- 必测：进游戏后要实际验证的功能点（具体到操作）。
- 存疑：按新 API 推断、未实机验证过的实现；给出失败表现与是否会崩。
- 未实现/降级：没有落地的功能、原因、玩家可感知的差别。

---

## 0. 版本约束（先看这一节）

| 组件 | 版本 | 说明 |
| --- | --- | --- |
| Minecraft | 1.21.1 | 全部附属只声明 1.21.1 区间 |
| NeoForge | ≥ 21.1.238（实例 21.1.266） | 各包声明 `[21.1.0,)`；KubeJS 要求 ≥21.1.199 |
| tetra | 1.21.1-7.0.0 | 本地构建；不要换 6.x |
| mutil | 1.21.1-7.0.0 | 本地构建 |
| Better Combat | **只能 2.0.4**（2.1.0+ 逐实例机制失效） | tetratic 声明 `[2.0.4,2.1.0)` |
| player-animation-lib | 2.0.4+1.21.1 | BC 前置 |
| cloth-config | 15.0.140 | BC 前置 |
| curios | 9.5.1+1.21.1 | tetra 前置 |
| GeckoLib | 4.9.3 | geotetraarmor 前置 |
| KubeJS / Rhino | 2101.7.2-build.381 / 2101.2.7-build.85 | tetrajs、tetra_data_kubejs 前置 |
| Gateways / Placebo / Apothic Attributes | 5.1.0 / 9.9.4 / 2.11.0 | tetra_gateways 前置（gateways 5.1.0 强制要求 Apothic Attributes ≥2.7.0） |
| Farmer's Delight | 1.21.1-1.3.4 | tetracelium、tetracompat 前置 |
| Soul Fire'd / Prometheus / Cobweb | 6.1.0 / 1.2.5 / 1.4.0 | art_of_forging、tetracelium（灵魂火） |
| FTB Ultimine / FTB Library | 2101.1.16 / 2101.1.39 | tetracompat |
| Architectury | 13.0.11 | FTB Ultimine 前置 |
| Ex Deorum | 3.12 | tetracompat |
| Alex's Caves / Citadel | 2.0.10 / 2.7.1 | tetracompat（Citadel 必须 2.7.x） |
| Jade | 1.21.1-15.10.6 | simplejadetetra 前置 |

> **#17 已取消**：Twilight Forest、Botania、Patchouli 均不装载；tetracelium 仅保留在 `addons\tetracelium` 归档，不部署、不测试。

装载注意：
- 本套只用 **tetra 7.0.0 + mutil 7.0.0**，不要混入 6.9.x 的 tetra/mutil。
- 目录里若出现 Better Combat 2.1.0+，必须删掉，否则逐实例（per-instance）机制失效、tetratic 也会拒载。
- #17 tetracelium 已取消：不装 tetracelium / Twilight Forest / Botania / Patchouli。

---

## 1. simplejadetetra（Jade 显示）

必测：
1. 装 Jade 后准星对准工作台/锻造台，Jade 信息框应显示 tetra 方块信息，而不是原版名称。
2. 准星对准已装配的 tetra 工具实体展示（物品展示框/掉落物）时，信息应与 1.20.1 一致（模块、打磨等）。
3. 挖掉/放置工作台，Jade 数据刷新正常。

存疑：
- Jade 插件注册方式在 15.10.x 改为 `IWailaPlugin` 新接口，若注册失败，表现是 Jade 里完全不显示 tetra 信息（不崩）。
- 若 NBT/组件读取路径不一致，可能显示空模块列表（不崩）。

未实现/降级：无。

---

## 2. tetra_attribute_rebalancing（属性数值重平衡）

必测：
1. 做一把默认长剑/战斧，查看 tooltip 的攻击伤害/攻速，应与配置中的重平衡数值一致。
2. 装配不同手柄/刀刃，属性应按新材料表变化。
3. 客户端与服务端属性显示一致（联机时另一名玩家看到同样的数值）。
4. 用 `/give` 拿预设工具（SNBT），属性 modifier 应正确附加。

存疑：
- 1.21.1 改为 `ItemAttributeModifiers` + `AttributeModifier(ResourceLocation,...)`，若 modifier id 冲突，表现为属性叠加/不生效（不崩）。
- SNBT give 的组件格式若写错，物品会是空工具（不崩）。

未实现/降级：无。

---

## 3. tetra_material_overhaul（材料数值重做）

必测：
1. 分别用铁/钻石/下界合金级材料制作同一模块，tooltip 数值应体现新表。
2. 全息球（holosphere）/工作台里材料预览数值与实际成品一致。
3. 打磨、修复时材料效率符合新表。
4. 旧存档中的工具（若从 1.20.1 升级）应正确重算，不应变成 0 属性。

存疑：
- 材料表通过 `ContextData`/`IMaterialData` 注入；若注入时机晚于工具构建，可能出现"首次打开 tooltip 数值为空、重开正常"（不崩）。
- 数据驱动替换对 JEI/REI 显示可能不生效（不崩）。

未实现/降级：无。

---

## 4. tetra_addition（新增模块/材料）

必测：
1. 工作台/锻造台中出现新增模块，可正常装配与拆卸。
2. 新增材料可打磨、可修复。
3. 新增模块的模型/贴图正常（不出现紫黑格）。
4. 创造模式标签页能找到新增物品/模块。

存疑：
- 数据 JSON 若字段名在 7.0.0 变更，模块会静默不注册（表现为工作台里看不到，不崩）。

未实现/降级：无。

---

## 5. tetra_view（第一人称视角）

必测：
1. 手持 tetra 双手武器，第一人称视角下模型位置/旋转正常，不遮挡准星。
2. 切换第一/第三人称、骑马、游泳时视角稳定。
3. 与 Better Combat 同时开启时，攻击动画不抖动。

存疑：
- 1.21.1 渲染管线（`PoseStack`/`Camera`）变化，若注入失败，表现是第一人称模型消失或错位（不崩）。
- 与 BC 的动画叠加可能闪烁（不崩）。

未实现/降级：无。

---

## 6. tetrawhetstone（磨刀石）

必测：
1. 放置磨刀石，手持可打磨工具右键，打磨进度/打磨等级上升。
2. 打磨后 tooltip 的打磨值变化，且消耗正确。
3. 不同工具等级（石/铁/钻石）打磨上限不同。

存疑：
- 方块实体 tick/交互 API 迁移，若失败表现为右键无反应（不崩）。

未实现/降级：无。

---

## 7. FluxOfTetra（通量/能量）

必测：
1. 给支持能量的 tetra 工具充能（与能量源/充电器交互），能量条上升。
2. 使用耗能模块时能量下降，耗尽后功能停止。
3. tooltip 能量显示与 NBT/组件一致。
4. 死亡/重进世界后能量保留。

存疑：
- Forge Energy capability 在 1.21.1 改为 `IEnergyStorage` 数据组件/附件注册；若注册遗漏，表现为无法充能（不崩）。
- 能量单位（FE）显示若有偏差，仅显示问题。

未实现/降级：无。

---

## 8. tetra_modify_core（核心库）

必测：
1. 依赖它的附属（inherit_enchant、over_honing）功能正常。
2. 工作台升级流程不报错，日志无 `NoClassDefFoundError`。
3. 与 tetrajs 同时存在时，模块注册顺序无冲突。

存疑：
- 这是库 mod，本身无交互；若 API 不匹配，依赖它的附属会功能失效或加载报错（会崩，需看日志）。

未实现/降级：无。

---

## 9. tetra_inherit_enchant（升级保留附魔）

必测：
1. 给 tetra 武器附魔（锋利/耐久等），在工作台升级刃/柄，升级后附魔应保留。
2. 升级到不接受该附魔的模块时，不兼容附魔应被移除（与原逻辑一致）。
3. 附魔等级不丢失、不叠加。
4. 工作台界面（WorkbenchScreen）与服务端结果一致。

存疑：
- mixin 注入点从 `applyCraftingBonusEffects`（13 参）与 `getExperienceCost` 进入；若上游签名再变，注入失败会导致附魔被清空（不崩，但功能丢失）。
- 附魔读写改用 `getTagEnchantments`/`updateEnchantments`，若组件路径不对，可能附魔显示但实际无效（不崩）。

未实现/降级：无。

---

## 10. tetra_over_honing（过量打磨）

必测：
1. 把工具打磨到超过常规上限，tooltip 出现过量打磨数值。
2. 过量打磨带来的属性加成/惩罚符合配置。
3. 在工作台进行转换（over-honing conversion）后数值正确结算。
4. 重进世界后过量打磨值保留。

存疑：
- tick 注入点已从 `getOrCreateTag` 迁到 `tickHoningProgression` 头部；若注入失败，表现为打磨不增长（不崩）。
- 客户端 tooltip mixin 若失败，仅 tooltip 少一行（不崩）。

未实现/降级：无。

---

## 11. morerequirement（更多合成要求）

必测：
1. 需要条件的配方（经验、工具等级、材料、维度等）在条件不满足时不能合成，满足时可以。
2. 自定义 crafting effect outcome（额外产出/消耗）生效。
3. 卷轴（scroll）使用的 `ScrollData` 组件读写在 1.21.1 正常（放置/拾取不丢数据）。
4. 创造标签页里附加卷轴物品存在。
5. 联机时条件由服务端判定，客户端不越权合成。

存疑：
- 网络包 `MRUpdateDataPacket` 已补 `TYPE/type()`；若注册遗漏，联机时数据不同步（不崩，但条件显示错误）。
- 1.21.1 `ScrollData` codec 若有字段差异，旧卷轴可能读不出来（不崩）。

未实现/降级：无。

---

## 12. geotetraarmor（GeckoLib 模块化护甲）

必测：
1. 制作并穿戴整套 modular 护甲，模型/贴图正确，无紫黑格。
2. 头盔/胸甲/护腿/靴子分别提供正确护甲值与韧性。
3. 飞行（鞘翅/创造飞行）时播放 FLY 动画，落地切回 IDLE。
4. 受伤时护甲耐久按模块结算，附魔（保护/耐久）生效。
5. 第一人称下头盔不遮挡视野；戴头盔时自定义头颅层正常。
6. 与 Curios 饰品栏同时显示不冲突。

存疑：
- GeckoLib 4.9 骨骼可见性/渲染改由 `ModularGeoArmorRenderer` 处理；若失败表现为护甲不可见或只有部分骨骼（不崩）。
- `HumanoidArmorLayer` mixin 若失效，护甲会渲染成原版模型（不崩）。
- 飞行动画依赖 `DataTicket("entity")` 判断，若 ticket 名不对，飞行时不切动画（不崩）。

未实现/降级：无（`ModuleModel` 被移除，改用自建 `IModuleModel` 实现 `GeckoModelData`，行为等价）。

---

## 13. tetrajs（KubeJS 脚本 API）

必测：
1. 启动日志出现 tetrajs 的 KubeJS 插件注册，无 `ClassNotFound`。
2. 写一个 `startup_scripts` 脚本用 tetrajs API 注册模块/材料，`/reload` 后生效。
3. 写一个 `server_scripts` 脚本注册配方/事件，重载后生效。
4. 脚本报错时只影响该脚本，不导致游戏崩溃。

存疑：
- KubeJS 2101 的 type id 必须全小写蛇形；旧脚本里的大写/驼峰 id 会注册失败（不崩）。
- `EnchantmentCategory` 已移除，旧脚本里给物品加附魔分类的写法需改写（报脚本错误，不崩）。
- ProbeJS 未安装时没有类型补全（不影响运行）。

未实现/降级：
- ProbeJS 类型导出（`TetraProbePlugin`）未移植：仅开发期代码补全缺失，游戏内功能无差别。

---

## 14. tetra_gateways（裂隙传送门）

必测：
1. 用裂隙珍珠在方块上右键，生成传送门（gateway），结构/位置正确。
2. 触发传送门后按波次刷怪，波次间隔/挑战时间符合珍珠数据。
3. 完成波次后获得奖励（核心奖励物品与材料一致）。
4. 工作台制作/修改裂隙珍珠：核心槽位、奖励写入正确。
5. 传送门血条在屏幕上方显示，进度条不超出边框。
6. 不同珍珠等级（1/2/3 核心）波数与怪物强度不同。

存疑：
- 血条绘制由 `blit` 改为 `blitSprite`，用 `@Redirect` 限制宽度 183；若失败，血条可能溢出 1px 或位置偏移（不崩）。
- `WaveEntity` 改为接口 + `StandardWaveEntity.CODEC`；若 codec 不匹配，刷怪时可能不生成（不崩，看日志）。
- KubeJS 绑定 `TetraGatewaysRift` 需在脚本里能取到；取不到则脚本报错（不崩）。

未实现/降级：无。

---

## 15. ExtraHoloPage（全息页扩展）

必测：
1. 打开全息球（holosphere），界面正常打开、无崩溃。
2. 对注册了扩展页的物品（如特定 tetra 工具）执行"打开原理图/全息"操作，应弹出扩展页并可交互。
3. 扩展页内的物品/材料列表、过滤、搜索可用。
4. 与 TMO（材料重做）同时存在时，材料数值显示正确。
5. 关闭扩展页后能回到原全息界面。

存疑：
- 由于 `HoloPage` 是 final 枚举，扩展页改为 defaultGui 叠加页；若叠加层绘制顺序不对，可能出现背景穿透/按钮点击偏移（不崩）。
- 1.21.1 `ScreenEvent.MouseScrolled` 不可取消：多材料 tooltip 上滚轮会同时滚动底层界面（不崩，体验差异）。
- JECH 拼音搜索改为反射；未装 jecharacters 时退回普通 `contains`（不崩）。
- `StatBarStore`/`ItemAbilityIconStore` 的统计条扫描沿用旧 `tool_bars` 路径，7.0.0 已用 `stat_bars`，通常为空操作（不崩，统计条可能不显示）。

未实现/降级：
- **独立 ADDON 标签页未实现**（原因：tetra 7.0.0 的 `HoloPage` 为 final 枚举，NeoForge 1.21 无 `IExtensibleEnum` 扩展机制；已用叠加页等价替代）。玩家可感知差别：全息界面里没有第 4 个独立标签，改为在打开相关物品时弹出扩展页。

---

## 16. tetra_data_kubejs（KubeJS 数据 API）

必测：
1. 启动无脚本错误；`kubejs.plugins.txt` 插件被加载。
2. `server_scripts` 里通过 tetra_data_kubejs API 注册/修改材料、模块、替换（replacement），`/reload` 后生效。
3. 数据热重载：`/reload` 后新数据立即生效，且不重复叠加。
4. 脚本抛异常时只该脚本失败，不影响其它脚本与游戏。
5. 翻译同步：客户端重载后 tooltip 文案同步（联机时新加入的玩家也能看到）。

存疑：
- 数据包注入改为 `virtualPacks(GeneratedDataStage.LAST)` + `ReloadableServerResourcesMixin`；若注入失败，表现为 `/reload` 后数据不生效（不崩）。
- `mergeExisting/copyFrom` 依赖 `ResourceManager` 捕获；若捕获为空，可能吞掉原版数据（会崩或数据丢失，重点看日志）。
- mutil `fromBytes` 两次 `readUtf` 的 ordinal 重定向若错位，网络包解析会异常（可能崩）。

未实现/降级：
- ProbeJS 类型导出与 `ProbeEventCompilerMixin` 未移植（本机无 ProbeJS 1.21.1）：仅开发期类型提示缺失，游戏内功能无差别。

---

## 17. tetracelium（已取消，不测试）

- 状态：**已取消移植/部署**。用户决定不做暮色森林内容；jar 与工程保留在 `addons\tetracelium` 归档，不装入实例。
- 连带不装：Twilight Forest（仅为它服务）、Botania、Patchouli。
- 测试项：无。
- 对其它 mod 的影响：tetracompat 的 FD 刀类配方注入分支依赖 tetracelium，未安装时不激活（设计如此，不崩）。

---


## 18. tetracompat（跨 mod 兼容）

必测：
1. Farmer's Delight：tetra 刀刃在砧板上切割正常，配方产出正确；刀类工具可作为 FD 刀使用。
2. Alex's Caves：磁铁手套（Galena Gauntlet）与 tetra 工具交互正常，结晶附魔（crystallization）生效。
3. Ex Deorum：用 tetra 锤子在筛网/锤子配方中正常工作，产出正确。
4. FTB Ultimine：手持 tetra 斧/锄/锹，按住连锁键右键，可连锁剥皮/耕地/铲平（由 FTB Ultimine 2101 新管线调用 `useOn` 实现）。
5. 未安装某个兼容 mod 时，本 mod 不报错、不崩溃。

存疑：
- 原 3 个 FTB Ultimine mixin 已删除（2101 移除 `RightClickHandlers`/`BrokenItemHandler`），改由上游 `FTBUltimine#blockRightClick` + `PlatformUtil` 处理；若上游未覆盖某种动作，表现为该动作不能连锁（不崩）。
- `RecipeManager` mixin 目标为 1.21.1 `byType` Multimap；若签名不符，配方注入静默失败（不崩，配方不出现）。
- `hammer.json` 改为 1.21 子谓词格式，若格式错误，锤子配方不加载（不崩）。

未实现/降级：
- FTB Ultimine 旧注入点不再存在，功能由上游原生覆盖（等价替代，非降级）。
- tetracelium 已取消，刀类配方注入分支不会激活（设计如此，不崩）。

---

## 19. art_of_forging（锻造艺术）

必测：
1. 新增物品/模块可在工作台制作与装配，模型贴图正常。
2. 卷轴（scroll）物品使用 `tetra:scroll_data` 组件，放置/拾取/重进世界不丢数据。
3. 新增能力/效果（招架、反击、特殊攻击等）按描述触发。
4. 新增附魔/词缀与 Apotheosis（若装）兼容。
5. 灵魂火相关效果：命中后目标燃烧为灵魂火。
6. 创造模式标签页内容完整。
7. 数据包结构（advancement/recipe/loot_table/tags 单数目录）被 1.21.1 正确加载，日志无数据包错误。

存疑：
- soul_fire_d 6.1.0 的 `FireManager` 已迁至 Prometheus 且未提供编译 jar，改为 `setRemainingFireTicks` + 反射 `FireTypeChanger#setFireType`；反射失败时退化为普通火焰（不崩）。
- 4 处 1.20.1 SRG 音效常量无法离线反查，改用近似音效（仅声音差异）。
- Forge Capability 全量改为 NeoForge 数据附件（`AttachmentType` + `copyOnDeath`）；若附件未随死亡复制，表现为死亡后状态清零（不崩，重点测 2/3）。
- 网络包 tracking 发送改用 `sendToPlayersTrackingEntity`；若遗漏，多人时他人看不到特效（不崩）。

未实现/降级：无（❌0）。

---

## 附：tetratic_combat_expanded（已实机验证，回归即可）

必测：手持 tetra 武器连续攻击，逐实例（per-instance）连击/伤害衰减逻辑与之前一致；BC 仍为 2.0.4。
存疑：无。
未实现：无。

---

## 建议的测试顺序

1. 先只放本体+前置（tetra/mutil/curios/BC 2.0.4/playeranimator/cloth-config），确认能进游戏。
2. 加 18 个附属 + 外部依赖（#17 不装），确认无 mod 加载错误（看 `logs/latest.log` 的 "Missing or unsupported"）。
3. 按上面 1→18 顺序逐项测（#17 跳过）；每项测完记录失败点与日志行号。
4. 重点回归：ExtraHoloPage 叠加页、tetra_data_kubejs 的 `/reload`、tetracompat 的 FTB Ultimine 连锁。
