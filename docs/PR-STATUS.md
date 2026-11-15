# 上游 PR 流程状态（本轮未提交）

## 1. 环境检查结果

| 检查项 | 结果 |
|---|---|
| `gh` CLI | 已安装 2.104.0，但**未登录**（无法 fork / 创建 PR） |
| `git` | 已安装 2.56.0.4 |
| GitHub 登录 | `gh` 未登录；Web 与推送使用 Edge 登录态及 Git Credential Manager |
| 上游仓库地址 | vvaddon 有可核验地址；yiran1457 名下 10 个工程已补录，其余本地没有记录仓库 URL |

结论：**本轮不提交任何 PR**。`gh` 未登录，且多数 mod 缺少上游地址或只有反编译基准。

## 2. 逐仓库状态

分支名统一规划为 `neoforge/1.21.1`；`—` 表示尚未创建（环境阻塞）。

| mod | 上游仓库 | 规划分支 | PR 链接 | 未提交原因 |
|---|---|---|---|---|
| vvaddon | https://github.com/VV-VittorioVeneto/VVAddon | — | 无 | 源码基准为上游 1.20.1 分支；本轮不提 PR |
| art_of_forging | 本地未记录 | — | 无 | 源码基准为 jar + 反编译；上游地址缺失 |
| extraholopage | https://github.com/yiran1457/ExtraHoloPage | — | 无 | 上游地址已补录；本轮不提 PR |
| fluxoftetra | https://github.com/yiran1457/FluxOfTetra | — | 无 | 上游地址已补录；本轮不提 PR |
| geotetraarmor | https://github.com/yiran1457/GeoTetraArmor | — | 无 | 上游地址已补录；本轮不提 PR |
| morerequirement | https://github.com/yiran1457/MoreRequirement | — | 无 | 上游地址已补录；本轮不提 PR |
| simplejadetetra | 无公开仓库（反编译） | — | 无 | 无源码基准，只记录不提交 |
| tetracelium | 本地未记录 | — | 无 | 源码基准为 jar + 反编译；上游地址缺失 |
| tetracompat | 本地未记录 | — | 无 | 同上 |
| tetrajs | https://github.com/yiran1457/Tetra-JS | — | 无 | 功能已放弃；本轮不提 PR |
| tetra_modify_core | https://github.com/yiran1457/TetraModifyCore | — | 无 | 上游地址已补录；本轮不提 PR |
| tetratic_combat_expanded | 本地未记录 | — | 无 | 同上 |
| tetrawhetstone | 无公开源码（反编译） | — | 无 | 无源码基准 |
| tetra_addition | https://github.com/yiran1457/tetra_addition | — | 无 | 上游地址已补录；本轮不提 PR |
| tetra_attribute_rebalancing | https://github.com/yiran1457/Tetra-Attribute-Rebalancing | — | 无 | 上游 LICENSE 为 GPL-3.0，jar 写 ARR；本轮不提 PR |
| tetra_data_kubejs | 本地未记录 | — | 无 | 已放弃；无源码基准 |
| tetra_gateways | 本地未记录 | — | 无 | 源码基准为 jar + 反编译；上游地址缺失 |
| tetra_inherit_enchant | 待确认（TetraExtend-Enchant / TetraInherit-Improvement） | — | 无 | 上游候选不唯一；本轮不提 PR |
| tetra_material_overhaul | https://github.com/yiran1457/Tetra-Material-Overhaul | — | 无 | 上游 LICENSE 为 LGPL-3.0；本轮不提 PR |
| tetra_over_honing | https://github.com/yiran1457/TetraOverHoning | — | 无 | 上游地址已补录；本轮不提 PR |
| tetra_view | https://github.com/yiran1457/Tetra-View | — | 无 | 上游地址已补录；本轮不提 PR |
| universalscroll | 无（本项目原创） | — | 无 | 无上游仓库，不适用 PR |

## 3. 解除阻塞所需

1. 安装 Git for Windows（或提供 `git.exe` 路径），并配置 `user.name` / `user.email`。
2. 安装 GitHub CLI（`gh`），执行 `gh auth login`，确认对目标仓库有 fork 权限。
3. 为每个准备提交的 mod 提供**拿到了原始源码**的上游仓库地址与许可证信息。
4. VVAddon 因 ARR 未纳入本仓库，不参与 PR 流程。

## 4. PR 描述模板（解除阻塞后直接套用）

```markdown
## Port to Minecraft 1.21.1 / NeoForge

- Target: Minecraft 1.21.1, NeoForge 21.1.238+ (tested 21.1.266), Java 21
- Dependencies: tetra 7.0.0, mutil 7.0.0
- Base: <upstream branch/commit>

## API changes
- Compatible: <list>
- Interface changed: <list>
- Unavailable upstream API: <list + fallback>

## Behavior / degradation
- <none | itemized>

## Testing
- Build: `./gradlew build --offline` (BUILD SUCCESSFUL)
- In-game: <which features tested, which not>

## Notes
- No changes to tetra/mutil itself.
- Mixins (if any) are declared in neoforge.mods.toml.
```
