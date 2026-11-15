# 上游来源与许可记录

本仓库为**非官方社区版（Community Edition）**，只收录移植源码与文档，不打包任何第三方 mod 的二进制文件。

## 1. 记录原则

- 下表的 `license` 均为各工程 1.20.1 原始 jar 内 `META-INF/mods.toml` 的字段**原文**，逐字抄录，不做解释、不做删留判断。
- `authors` 同为 jar 字段原文；上游使用模板未改的，保留模板占位（如 `YourNameHere`）。
- “未记录上游仓库”表示本地没有保存可核验地址，不代表上游一定没有仓库。
- 未列出的许可证文件、作者口头许可等，不在此表推断。

## 2. 第三方依赖（不随仓库分发）

| 对象 | 说明 |
|---|---|
| tetra 1.21.1 | 第三方授权受限内容，仅作编译依赖，不随本仓库分发。 |
| mutil 7.0.0 | tetra 必需前置，不随本仓库分发。 |

## 3. 各工程来源与许可字段

| 工程 | authors（jar 原文） | 上游仓库 | license（jar 原文） |
|---|---|---|---|
| art_of_forging | `Acetheeldritchking, MindFaer` | 未记录 | `MIT` |
| extraholopage | `_yi_ran_` | <https://github.com/yiran1457/ExtraHoloPage> | `GNU LGPL 3.0` |
| fluxoftetra | `_yi_ran_` | <https://github.com/yiran1457/FluxOfTetra> | `APGL 3.0` |
| geotetraarmor | `_yi_ran_` | <https://github.com/yiran1457/GeoTetraArmor> | `LGPL 3.0` |
| morerequirement | `_yi_ran_` | <https://github.com/yiran1457/MoreRequirement> | `LGPL 3.0` |
| simplejadetetra | `_yi_ran_` | 未找到 | `All Rights Reserved` |
| tetracelium | `Mickelus` | 未找到 | `ARR` |
| tetracompat | `SSWTLZZ` | 未记录 | `MIT` |
| tetrajs | `_yi_ran_` | <https://github.com/yiran1457/Tetra-JS> | `LGPL 3.0` |
| tetra_modify_core | `_yi_ran_` | <https://github.com/yiran1457/TetraModifyCore> | `All Rights Reserved` |
| tetratic_combat_expanded | `Firelight Studios (BluSpring, Kiran), Smartin, Ace` | 未记录 | `MiT` |
| tetrawhetstone | `hp` | 未找到 | `All Rights Reserved` |
| tetra_addition | `_yi_ran_` | <https://github.com/yiran1457/tetra_addition> | `GPL-3.0-or-later` |
| tetra_attribute_rebalancing | `YourNameHere, OtherNameHere` | <https://github.com/yiran1457/Tetra-Attribute-Rebalancing> | `All Rights Reserved` |
| tetra_data_kubejs | （空） | 未找到 | `All Rights Reserved` |
| tetra_gateways | `hp` | 未找到 | `All Rights Reserved` |
| tetra_inherit_enchant | `_yi_ran_` | 待确认（候选：TetraExtend-Enchant / TetraInherit-Improvement） | `All Rights Reserved` |
| tetra_material_overhaul | `_yi_ran_` | <https://github.com/yiran1457/Tetra-Material-Overhaul> | `LGPL 3.0` |
| tetra_over_honing | `_yi_ran_` | <https://github.com/yiran1457/TetraOverHoning> | `LGPL 3.0` |
| tetra_view | `_yi_ran_` | <https://github.com/yiran1457/Tetra-View> | `LGPL 3.0` |
| universalscroll | 本项目 | 无上游 | 未声明 |
| vvaddon | `Vittorio_Veneto` | <https://github.com/VV-VittorioVeneto/VVAddon> | `All Rights Reserved` |

## 4. 排除的文件

- 所有 `libs/*.jar`：`tetra-1.21.1-7.0.0.jar`、`mutil-1.21.1-7.0.0.jar`、GeckoLib、Jade、KubeJS、Rhino、Curios、Gateways、Placebo、Better Combat 等。
- 所有 `build/`、`.gradle/`、`run/`、`logs/`、`local-maven/`。
- 反编译产物与原始上游 jar（`reference/work/**`、`*.jar`）。
- 例外：Gradle Wrapper 的 `gradle-wrapper.jar` 是构建工具，保留在每个工程内。
