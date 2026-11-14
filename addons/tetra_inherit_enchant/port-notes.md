# TetraInherit Enchant 1.21.1 NeoForge 移植说明

- 源：`TetraInherit Enchant-6.9.0-1.0.0.jar`（Forge 1.20.1，3 class / 2 mixin）
- 目标：NeoForge 21.1.238 / MC 1.21.1 / Java 21 / MDG 2.0.141
- 产物：`build/libs/tetra_inherit_enchant-1.21-1.21.1-6.9.0-1.0.0.jar`（25966 字节）
- 构建：`.\gradlew --offline clean build --console=plain` → **BUILD SUCCESSFUL**

## 1. 数量核对表

| 项目 | 原 jar | 新工程 | 结论 |
|---|---|---|---|
| class 文件 | 3 | 3 | ✅ |
| mixins.json 条目 | 2（`mixins` 1 + `client` 1） | 2（`mixins` 1 + `client` 1） | ✅ |
| 资源文件 | icon.png、pack.mcmeta、mixins.json、mods.toml | 同左（mods.toml→neoforge.mods.toml） | ✅ |
| 未随包内容 | refmap.json、MANIFEST | 不带 | ✅ 按 1.21 规范移除（无 refmap） |

## 2. 功能与原实现对应

原 mod 目的：工作台替换模块时，只移除「被映射到该槽位、且新模块不再接受」的附魔，而不是整槽清空。

- `WorkbenchTileMixin`（服务端/逻辑侧）
  - `@WrapOperation` 拦截 `TetraEnchantmentHelper.removeEnchantments(ItemStack,String)` 并置空（取消整槽移除）。
  - `@Inject` 于 `craft` 中 `UpgradeSchematic.getExperienceCost(ItemStack, ItemStack[], String)` 调用前，取 `@Local(ordinal=1) ItemStack`（即 `upgradedStack`）与 `@Local boolean willReplace`，执行精确移除。
- `WorkbenchScreenMixin`（客户端预览）
  - 同上 `@WrapOperation` 置空 `buildPreviewStack` 中的整槽移除。
  - `@Inject` 于 `WorkbenchTile.applyCraftingBonusEffects(...)` 调用前（1.21.1 该签名新增 `float severity`，描述符已按 13 参重写），取 `@Local(ordinal=1)`（即 `result`）。

## 3. 与 1.20.1 原实现的偏离

1. 附魔读取：`ItemStack#getOrCreateTag()/getTag()` + 手写 `ListTag "Enchantments"` → `ItemStack#getTagEnchantments()`（`ItemEnchantments`，1.21.1 数据组件）。
2. 附魔移除：`ListTag#removeIf` + `ForgeRegistries.ENCHANTMENTS.getValue(...)` → `EnchantmentHelper.updateEnchantments(stack, mutable -> mutable.removeIf(...))`；附魔 id 由 `TetraEnchantmentHelper.getEnchantmentKey(Holder)` 取得，不再依赖 Forge 注册表。
3. `EnchantmentMapping` 自定义 NBT：改用 tetra 自带 `ItemStackTagHelper.getTagElement/mutate`（1.21.1 `getOrCreateTag` 已移除）。
4. 附魔 `Holder` 解析：优先取物品栈上已有的 `Holder`；栈上没有时再经 `CommonHooks.resolveLookup(Registries.ENCHANTMENT)` 兜底（1.21 附魔为数据驱动，无 ForgeRegistries 静态表）。
5. `mods.toml` → `neoforge.mods.toml`；依赖 `forge` → `neoforge`，`minecraft` 区间改 `[1.21.1,1.22)`，新增 `[[mixins]]`；`tetra_modify_core` 依赖区间由 `0` 改为 `[6.9.0,)`（本套移植产物版本号体系）。
6. mixins.json：`JAVA_17`→`JAVA_21`，删除 refmap。
7. 主类 `@Mod` 由 `net.minecraftforge.fml.common.Mod` 改为 `net.neoforged.fml.common.Mod`，构造注入 `IEventBus`（无事件注册，仅占位）。

行为等价性：逻辑（只移除“该槽位映射且新模块不接受”的附魔，同时清掉对应映射项）与原实现一致，无功能降级。

## 4. 待实测项

1. 工作台替换模块（`willReplace=true`）时：不被新模块接受的附魔被移除，仍被接受的保留；`EnchantmentMapping` 同步清理。
2. 客户端预览态与真正合成后的附魔结果一致。
3. 无 `EnchantmentMapping` 数据、附魔为空、非 `IModularItem` 三种边界不报错。
4. 两个 mixin 在服务端/客户端分别只加载对应一侧（`client` 段的 `WorkbenchScreenMixin` 不应出现在专用服务器）。
