# Tetra 1.21.1 / NeoForge 附属移植集（社区版 / Community Edition）

> **许可与再分发提醒（重要）**
> 本仓库包含 **All Rights Reserved（ARR）** 上游内容的衍生移植。
> **VVAddon 因 ARR 未纳入本仓库**（源码与 jar 仅本地保留，不对外分发）；
> tetra 1.21.1 为第三方授权受限内容，仅作编译依赖，不随本仓库分发。
> 其余未确认许可证的附属默认同样按 ARR 处理，**不得再分发、不得用于商业用途**。
> 详见 [ATTRIBUTION.md](ATTRIBUTION.md)。


本仓库是 **非官方社区版（Community Edition）**，收录将 1.20.1（Forge）Tetra 附属移植到
**Minecraft 1.21.1 / NeoForge** 的源码与文档。**不包含**任何第三方 mod 的二进制 jar
（tetra、mutil、KubeJS、暮色森林等），也不包含构建产物。

社区版标识约定：由本仓库构建的产物统一命名为
`<modid>-1.21-1.21.1-<上游版本>-community.jar`，文档与发布页标注“社区版 / Community Edition”；
modid 与游戏内注册名保持不变，以兼容既有存档。

> 许可提示：本仓库中的代码是上游 mod 的衍生移植。多数上游没有公开仓库或许可证，
> 默认按“保留所有权利、仅内部使用”处理；对外分发前必须取得各原作者许可。详见 [ATTRIBUTION.md](ATTRIBUTION.md)。

## 目标环境

| 项 | 版本 |
|---|---|
| Minecraft | 1.21.1 |
| NeoForge | 21.1.238+（实测 21.1.266） |
| Java | 21 |
| Tetra | 7.0.0（`tetra-1.21.1-7.0.0.jar`） |
| mutil | 7.0.0（`mutil-1.21.1-7.0.0.jar`，Tetra 必需前置，未打包进 Tetra） |
| 构建插件 | ModDevGradle 2.0.141（各工程 `build.gradle` 内已固定） |

## 目录结构

```
.
├── README.md
├── ATTRIBUTION.md          # 上游来源、许可证状态、排除清单
├── TEST-CHECKLIST.md       # 逐 mod 测试清单
├── .gitignore
├── docs/
│   ├── evaluation.md       # 附属评估与可行性记录
│   ├── PR-STATUS.md        # 上游 PR 流程状态（未提交原因）
│   └── port-notes/         # 每个 mod 的移植笔记（变更/兼容性/风险/降级）
└── addons/
    └── <modid>/            # 独立 Gradle 工程（源码 + 资源 + 构建脚本）
```

每个 `addons/<modid>` 都是可独立构建的工程，目录名统一为 modid（snake_case）。

## mod 一览

| 目录 | modid | 版本 | 状态 | 用途（简注） |
|---|---|---|---|---|
| art_of_forging | art_of_forging | 1.8.4 | 已移植 | 追加锻炉/模块内容，含 Soul Fire 联动 |
| extraholopage | extraholopage | 6.9.0-1.2.13 | 已移植 | 扩展 Tetra 全息界面（额外页面、图标） |
| fluxoftetra | fluxoftetra | 6.9.0-1.0.3-fix | 已移植 | Flux 能量与 Tetra 工具行为联动 |
| geotetraarmor | geotetraarmor | 6.9.0-1.0.4-fix | 已移植 | 用 GeckoLib 为模块化盔甲提供 3D 模型 |
| morerequirement | morerequirement | 1.2.2 | 已移植 | 为 Tetra 图纸增加更多条件类型 |
| simplejadetetra | simplejadetetra | 6.9.0-1.0.0 | 已移植 | 在 Jade 提示中显示 Tetra 工具信息 |
| tetracelium | tetracelium | 1.3.1 | 已移植 | Tetra 与 Celium 材料/模块的联动扩展 |
| tetracompat | tetracompat | 1.0.0 | 已移植 | 与 FTB Ultimine、Alex's Caves、Farmer's Delight 等兼容 |
| tetrajs | tetrajs | 2001-6.9.0-1.0.2 | **已放弃** | KubeJS 脚本接口（KubeJS 2101 类型冲突） |
| tetra_modify_core | tetra_modify_core | 6.9.0-1.0.10 | 已移植 | 通过 mixin 修改 Tetra 核心行为 |
| tetratic_combat_expanded | tetratic_combat_expanded | 2.8.3 | 已移植 | 扩展 Tetra 战斗（Better Combat、武器属性） |
| tetrawhetstone | tetrawhetstone | 1.1.0 | 已移植 | 磨刀石物品与相关机制 |
| tetra_addition | tetra_addition | 6.9.0-1.1 | 已移植 | 追加 Tetra 内容，附 KubeJS 可选集成 |
| tetra_attribute_rebalancing | tetra_attribute_rebalancing | 6.9.0-1.0.0-PrecisionFix | 已移植 | 重平衡 Tetra 属性数值 |
| tetra_data_kubejs | tetra_data_kubejs | 1.3.2 | **已放弃** | 用 KubeJS 提供 Tetra 数据（同上冲突） |
| tetra_gateways | tetra_gateways | 6.9.0-1.0.0 | 已移植 | 与 Gateways to Eternity 联动 |
| tetra_inherit_enchant | tetra_inherit_enchant | 6.9.0-1.0.0 | 已移植 | 模块替换时继承附魔 |
| tetra_material_overhaul | tetra_material_overhaul | 6.9.0-1.0.1 | 已移植 | 材料系统大修 |
| tetra_over_honing | tetra_over_honing | 6.9.0-1.0.1-fix | 已移植 | 扩展淬炼/强化上限 |
| tetra_view | tetra_view | 6.9.0-1.0.2 | 已移植 | 工具信息/视图显示调整 |
| universalscroll | universalscroll | 1.0.0 | 新增（自有） | 万能的卷轴 + 百宝卷轴（汇总全部图纸） |
| vvaddon | vvaddon | 3.0.1 | **因 ARR 未纳入** | VVAddon 移植（仅本地保留，不随本仓库分发） |

用途简注依据移植笔记与 mod 名称整理，未逐项对照上游说明。

## 构建

各工程可独立构建，命令一致：

```powershell
cd addons\<modid>
.\gradlew.bat build --offline --console=plain
```

前置要求：

1. **Java 21**。
2. 在工程内创建 `libs/`，放入以下第三方 jar（仓库不提供）：
   - `tetra-1.21.1-7.0.0.jar`
   - `mutil-1.21.1-7.0.0.jar`
   - 个别工程另需：GeckoLib 4.9.3、Jade 15.10.6、KubeJS 2101、Gateways 5.1.0、Placebo 9.9.4、Curios 9.5.1 等，
     具体见对应 `docs/port-notes/<modid>.md` 的“依赖/构建”章节。
3. 离线构建依赖本地 Gradle 缓存与本地 maven 补件；换机器时需先联网完成一次依赖解析。

产物：`addons/<modid>/build/libs/*.jar`，社区版命名约定
`<modid>-1.21-1.21.1-<上游版本>-community.jar`。

## 已产出的 jar（历史交付副本）

> 以下文件名产生于社区版命名约定之前；重新构建时请按 `-community` 后缀命名。

| jar | 字节 |
|---|---|
| art_of_forging-1.21.1-1.8.4.jar | 535,741 |
| extraholopage-1.21-1.21.1-6.9.0-1.2.13.jar | 113,663 |
| fluxoftetra-1.21-1.21.1-6.9.0-1.0.3-fix.jar | 19,797 |
| geotetraarmor-1.21-1.21.1-6.9.0-1.0.4-fix.jar | 102,748 |
| morerequirement-1.21-1.21.1-1.2.2.jar | 64,392 |
| simplejadetetra-1.21-1.21.1-6.9.0-1.0.0.jar | 3,634 |
| tetracompat-1.21-1.21.1-1.0.0.jar | 22,703 |
| tetrajs-1.21-1.21.1-2001-6.9.0-1.0.2.jar | 97,039（已放弃，仅供留档） |
| tetrawhetstone-1.21-1.21.1-1.1.0.jar | 15,805 |
| tetra_addition-1.21-1.21.1-6.9.0-1.1.jar | 11,121 |
| tetra_attribute_rebalancing-1.21-1.21.1-6.9.0-1.0.0-PrecisionFix.jar | 6,254 |
| tetra_data_kubejs-1.21-1.21.1-1.3.2.jar | 186,344（已放弃，仅供留档） |
| tetra_gateways-1.21-1.21.1-6.9.0-1.0.0.jar | 42,686 |
| tetra_inherit_enchant-1.21-1.21.1-6.9.0-1.0.0.jar | 25,962 |
| tetra_material_overhaul-1.21-1.21.1-6.9.0-1.0.1.jar | 10,792 |
| tetra_modify_core-1.21-1.21.1-6.9.0-1.0.10.jar | 67,724 |
| tetra_over_honing-1.21-1.21.1-6.9.0-1.0.1-fix.jar | 13,185 |
| tetra_view-1.21-1.21.1-6.9.0-1.0.2.jar | 19,302 |
| universalscroll-1.21-1.0.0.jar | 42,397 |

`tetracelium` 与 `tetratic_combat_expanded` 的产物当前不在交付目录（需在各自工程重新构建或从原构建目录复制）。

## 已知限制

- **vvaddon（因 ARR 未纳入本仓库）**：1.21.1 的 `AbstractArrow#setPierceLevel(byte)` 为 private，幻影箭原本的 64 穿透不可用（行为回退，已标注待评估）；`phantom_persuit` 效果仍未注册（原包即死代码）；5 个物品无配方。
- **tetrajs / tetra_data_kubejs**：因 KubeJS 2101 类型重复注册问题放弃，源码仅留档，不建议装入实例。
- **tetratic_combat_expanded**：Better Combat 依赖锁在 `[2.0.4,2.1.0)`，无法与 BC 2.1.0+ 共存（逐实例武器属性 NBT 被上游移除）。
- **geotetraarmor**：护甲耐久事件不再携带槽位信息（上游字段删除）。
- **universalscroll**：图纸仍需在工作台 + 目标物品上应用；“百宝卷轴”只豁免 `tetra:locked` 解锁条件，模块/材料/工具/经验等条件照常校验。
- 配置文件名：NeoForge 下多为 `<modid>-common.toml`，与 1.20.1 的 `<modid>.toml` 不同。

## 文档

- 每个 mod 的变更清单、兼容性分类、风险与降级：[docs/port-notes/](docs/port-notes/)
- 测试清单：[TEST-CHECKLIST.md](TEST-CHECKLIST.md)
- 上游 PR 流程状态：[docs/PR-STATUS.md](docs/PR-STATUS.md)
- 许可与归属：[ATTRIBUTION.md](ATTRIBUTION.md)
