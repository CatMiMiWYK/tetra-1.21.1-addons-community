# 上游来源、许可证与排除清单

本仓库为**非官方社区版（Community Edition）**，只收录我们自己编写的移植代码与文档，不打包任何第三方 mod 的二进制文件。

## 1. 政策

- 仅内部使用：在取得各原作者明确许可前，不对外分发源码或构建产物。
- 不收录：Tetra、mutil、KubeJS、暮色森林（Twilight Forest）、GeckoLib、Jade、Better Combat、Gateways、Placebo、Curios 等第三方 jar。
- 不收录：`build/`、`.gradle/`、`libs/`、本地 maven 补件、日志、运行目录。
- 每个 mod 的上游来源与许可证状态如下；**标注“未确认”的，默认按 All Rights Reserved 处理**。

## 2. ARR / 仅内部使用（重点）

| 对象 | 说明 |
|---|---|
| **Tetra（第三方 tetra 1.21.1）** | 本仓库所有移植的目标 API 来源。该分支为 All Rights Reserved，本仓库**不包含**其源码或 jar；仅内部编译引用。 |
| **VVAddon** | 上游：<https://github.com/VV-VittorioVeneto/VVAddon>（分支 `1.20.1`），All Rights Reserved。**因 ARR 未纳入本仓库**：源码与 jar 仅本地保留，不在本仓库提交、不对外分发。 |

## 3. 各 mod 来源与许可状态

| 目录 | 源基准 | 上游仓库 | 许可证状态 |
|---|---|---|---|
| art_of_forging | 1.20.1 jar + 反编译 | 未记录 | 未确认（默认 ARR） |
| extraholopage | 1.20.1 jar + 反编译 | 未记录 | 未确认（默认 ARR） |
| fluxoftetra | 反编译 | 无公开源码 | 未确认（默认 ARR） |
| geotetraarmor | 1.20.1 jar + 反编译 | 未记录 | 未确认（默认 ARR） |
| morerequirement | 1.20.1 jar + 反编译 | 未记录 | 未确认（默认 ARR） |
| simplejadetetra | 反编译（javap） | 无公开仓库 | ARR（笔记记录） |
| tetracelium | 1.20.1 jar + 反编译 | 未记录 | 未确认（默认 ARR） |
| tetracompat | 1.20.1 jar + 反编译 | 未记录 | 未确认（默认 ARR） |
| tetrajs | 1.20.1 jar + 反编译 | 未记录 | 未确认（默认 ARR）；已放弃 |
| tetra_modify_core | 1.20.1 jar + 反编译 | 未记录 | 未确认（默认 ARR） |
| tetratic_combat_expanded | 1.20.1 jar + 反编译 | 未记录（Better Combat 为独立上游依赖） | 未确认（默认 ARR） |
| tetrawhetstone | 反编译 | 无公开源码 | 未确认（默认 ARR） |
| tetra_addition | 反编译（CFR） | 无公开源码 | 未确认（默认 ARR） |
| tetra_attribute_rebalancing | 反编译 | 无公开仓库 | ARR（笔记记录） |
| tetra_data_kubejs | 1.20.1 jar + 反编译 | 未记录 | 未确认（默认 ARR）；已放弃 |
| tetra_gateways | 1.20.1 jar + 反编译 | 未记录 | 未确认（默认 ARR） |
| tetra_inherit_enchant | 1.20.1 jar + 反编译 | 未记录 | 未确认（默认 ARR） |
| tetra_material_overhaul | 反编译（CFR） | 无公开仓库 | ARR（笔记记录） |
| tetra_over_honing | 1.20.1 jar + 反编译 | 未记录 | 未确认（默认 ARR） |
| tetra_view | 反编译（CFR） | 无公开源码 | 未确认（默认 ARR） |
| universalscroll | 本项目原创（基于 Tetra 公开 API） | 无上游 | 自有代码，随本仓库策略 |
| vvaddon（未纳入） | GitHub 源码（分支 1.20.1） | <https://github.com/VV-VittorioVeneto/VVAddon> | **ARR，因许可未纳入本仓库** |

“上游仓库未记录”表示本地没有保存可核验的仓库地址，不代表上游一定没有仓库；补全前不得对外分发。

## 4. 因许可/体积原因排除的文件

- 所有 `libs/*.jar`：`tetra-1.21.1-7.0.0.jar`、`mutil-1.21.1-7.0.0.jar`、GeckoLib、Jade、KubeJS、Rhino、Curios、Gateways、Placebo、Better Combat 等。
- 所有 `build/`、`.gradle/`、`run/`、`logs/`、`local-maven/`。
- 反编译产物与原始上游 jar（`reference/work/**`、`*.jar`）。
- 例外：Gradle Wrapper 的 `gradle-wrapper.jar` 是构建工具，保留在每个工程内。
