# 上游 PR 流程状态（本轮未提交）

## 1. 环境检查结果

| 检查项 | 结果 |
|---|---|
| `gh` CLI | **未安装**（`gh` 不在 PATH，无法 `gh auth status` / fork / 创建 PR） |
| `git` | **未安装**（`git` 不在 PATH；全盘常见目录未找到 `git.exe`），本地分支与初始提交也无法完成 |
| GitHub 登录 | 未知（无 gh，无法确认 token 与 fork 权限） |
| 上游仓库地址 | 仅 VVAddon 有可核验地址；其余 20 个 mod 本地没有记录仓库 URL |

结论：**本轮不提交任何 PR**。仓库文件已整理完成，但 `git init` / 分支 / 提交需要先安装 Git；PR 还需要安装 gh、登录并补全上游地址。

## 2. 逐仓库状态

分支名统一规划为 `neoforge/1.21.1`；`—` 表示尚未创建（环境阻塞）。

| mod | 上游仓库 | 规划分支 | PR 链接 | 未提交原因 |
|---|---|---|---|---|
| vvaddon | https://github.com/VV-VittorioVeneto/VVAddon | neoforge/1.21.1 | 无 | **ARR**：需作者书面许可；且 gh/git 未安装 |
| art_of_forging | 本地未记录 | — | 无 | 源码基准为 jar + 反编译；上游地址缺失 |
| extraholopage | 本地未记录 | — | 无 | 同上 |
| fluxoftetra | 无公开源码（反编译） | — | 无 | 无源码基准，按规则只记录不提交 |
| geotetraarmor | 本地未记录 | — | 无 | 源码基准为 jar + 反编译；上游地址缺失 |
| morerequirement | 本地未记录 | — | 无 | 同上 |
| simplejadetetra | 无公开仓库（反编译） | — | 无 | 无源码基准；且笔记记录为 ARR |
| tetracelium | 本地未记录 | — | 无 | 源码基准为 jar + 反编译；上游地址缺失 |
| tetracompat | 本地未记录 | — | 无 | 同上 |
| tetrajs | 本地未记录 | — | 无 | 已放弃；无源码基准 |
| tetra_modify_core | 本地未记录 | — | 无 | 源码基准为 jar + 反编译；上游地址缺失 |
| tetratic_combat_expanded | 本地未记录 | — | 无 | 同上 |
| tetrawhetstone | 无公开源码（反编译） | — | 无 | 无源码基准 |
| tetra_addition | 无公开源码（反编译） | — | 无 | 无源码基准 |
| tetra_attribute_rebalancing | 无公开仓库（反编译） | — | 无 | 无源码基准；ARR |
| tetra_data_kubejs | 本地未记录 | — | 无 | 已放弃；无源码基准 |
| tetra_gateways | 本地未记录 | — | 无 | 源码基准为 jar + 反编译；上游地址缺失 |
| tetra_inherit_enchant | 本地未记录 | — | 无 | 同上 |
| tetra_material_overhaul | 无公开仓库（反编译） | — | 无 | 无源码基准；ARR |
| tetra_over_honing | 本地未记录 | — | 无 | 源码基准为 jar + 反编译；上游地址缺失 |
| tetra_view | 无公开源码（反编译） | — | 无 | 无源码基准 |
| universalscroll | 无（本项目原创） | — | 无 | 无上游仓库，不适用 PR |

## 3. 解除阻塞所需

1. 安装 Git for Windows（或提供 `git.exe` 路径），并配置 `user.name` / `user.email`。
2. 安装 GitHub CLI（`gh`），执行 `gh auth login`，确认对目标仓库有 fork 权限。
3. 为每个准备提交的 mod 提供**拿到了原始源码**的上游仓库地址与许可证信息。
4. vvaddon 需先取得作者许可（ARR）。

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
