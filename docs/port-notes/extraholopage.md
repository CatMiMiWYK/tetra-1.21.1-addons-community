# ExtraHoloPage 1.21.1 / NeoForge 移植说明

## 1. 源版本与目标

| 项目 | 内容 |
| --- | --- |
| 源 mod | ExtraHoloPage 6.9.0-1.2.13（Forge 1.20.1） |
| 源 jar | `2026-11-13/reference/work/addons/extrahamelopage.jar` |
| 反编译源码 | `2026-11-13/reference/work/addons/extrahamelopage_dec`（48 个 .java） |
| 目标 | Minecraft 1.21.1 / NeoForge 21.1.238 / Java 21 |
| 新工程 | `2026-11-13/addons/extraholopage` |
| 产物 | `build/libs/extraholopage-1.21-1.21.1-6.9.0-1.2.13.jar`（113,640 字节） |
| 构建命令 | `.\gradlew build --offline --console=plain` |

> 说明：任务描述写“58 个 class 文件”，但对给定的原始 jar 实际枚举为 **54** 个 `.class`
> （含内部类，见 `jar tf ... | grep .class`）。本说明以实际 jar 为准，并逐个对照。
> 17 个 mixin 与 5 个资源文件与描述一致。

## 2. 数量核对

### 2.1 class 文件

| 范围 | 原 jar | 新 jar |
| --- | --- | --- |
| `net/yiran/extraholopage/**` 全部 `.class` | 54 | 53 |
| 其中 mixin 包内的 `.class`（含 `$1`/`$MaterialWrapper`） | 18 | 17 |

差异清单：

| 差异 | 原因 |
| --- | --- |
| `core/mixins/HoloPageMixin.class` 缺失 | ❌ 见 §4：NeoForge 1.21.1 无 Forge `IExtensibleEnum` 枚举扩展机制，tetra 7.0.0 的 `HoloPage` 是 final、只有 3 个常量且未接入 NeoForge ExtensibleEnum，无法注入 `ADDON` 常量。改用 `HoloGuiMixin` 以叠加页方式实现同等功能（见下）。 |

其余 53 个 class 一一对应（类名/包名/公开签名保持）。

### 2.2 mixin

| 项目 | 原 jar | 新 jar |
| --- | --- | --- |
| mixins.json 条目 | 17（3 common + 14 client） | 16（3 common + 13 client） |
| 编译生成的 mixin `.class` | 18（含 `GuiStatBarMixin$1`、`HoloVariantDetailGuiMixin$MaterialWrapper`） | 17 |

唯一移除：`HoloPageMixin`（❌，原因同上）。

### 2.3 资源

| 资源 | 原 jar | 新 jar |
| --- | --- | --- |
| `assets/extraholopage/lang/en_us.json` | ✅ | ✅（内容未改） |
| `assets/extraholopage/lang/zh_cn.json` | ✅ | ✅（内容未改） |
| `assets/extraholopage/textures/gui/back.png` | ✅ | ✅（未改） |
| `assets/extraholopage/textures/gui/holo.png` | ✅ | ✅（未改） |
| `pack.mcmeta` | ✅（pack_format 15） | ✅（pack_format 改为 34，1.21.1 资源包版本） |
| `META-INF/mods.toml` | ✅ | → `META-INF/neoforge.mods.toml`（NeoForge 1.21.1 格式） |
| `extraholopage.refmap.json` | ✅ | 移除（NeoForge 不需要 refmap） |
| `extraholopage.mixins.json` | ✅ | ✅（`JAVA_21`、无 refmap、全部 `remap=false`） |

## 3. Mixin 逐条清单

| # | Mixin | 目标类（1.21.1 实际路径） | 目标方法 | 注入点 | 迁移说明 |
| --- | --- | --- | --- | --- | --- |
| 1 | `GuiElementMixin` | `se.mickelus.mutil.gui.GuiElement` | —（字段 `elements`） | 接口实现 | ✅ 给 `GuiElement` 增加 `IGuiElement`（removeChild/removeIfChild） |
| 2 | `GuiSchematicListMixin` | `se.mickelus.tetra.blocks.workbench.gui.GuiSchematicList` | `<init>` | `TAIL` | ⚠️ 构造器变为 `(int,int,Consumer)`；`buttonBack/buttonForward` 变为 private final（`@Mutable`）；`setPage/getPage` 为 private（shadow 桩） |
| 3 | `GuiStatBarMixin` | `se.mickelus.tetra.gui.stats.bar.GuiStatBar` | `shouldShow` | `HEAD`，可取消 | ✅ 字段 `labelKey` 同名；`shouldShow` 改为 public |
| 4 | `HoloGuiMixin` | `se.mickelus.tetra.items.modular.impl.holo.gui.HoloGui` | `<init>` / `changePage` / `openSchematic` | `TAIL` / `HEAD` / `HEAD`（可取消） | ⚠️ 不再扩展 `HoloPage`；把 `ExtraHoloRootGui` 作为 `defaultGui` 叠加子元素，注册物品的 `openSchematic` 被拦截并显示叠加页；切页时隐藏叠加页 |
| 5 | `HoloImprovementGuiMixin` | `...holo.gui.craft.schematic.HoloImprovementGui` | `updateVariants` | `RETURN` | ✅ 包名迁移；字段 `variants`/`header` 不变 |
| 6 | `HoloImprovementVariantGuiMixin` | `...holo.gui.craft.schematic.HoloImprovementVariantGui` | `<init>` | `TAIL` | ✅ 包名迁移；构造器签名不变 |
| 7 | `HoloItemGuiMixin` | `...holo.gui.craft.item.HoloItemGui` | 接口实现（字段 `icon`） | — | ✅ 包名迁移；构造器改为 `(int,int,HolosphereEntryData,Runnable,Consumer)`，`setIcon` 逻辑不变 |
| 8 | `HoloMaterialApplicableMixin` | `...holo.gui.craft.schematic.HoloMaterialApplicable` | `update` | `INVOKE ModularHolosphereItem.findHolosphere`，可取消 | ✅ 包名迁移；5 个 shadow 字段同名同型 |
| 9 | `HoloMaterialDetailGuiMixin` | `...holo.gui.craft.material.HoloMaterialDetailGui` | `update` | `INVOKE Arrays.asList`，ordinal 0 | ✅ 包名迁移；7.0.0 中 `Arrays.asList` 有两个匹配，ordinal 0 仍对应原锚点 |
| 10 | `HoloMaterialListGuiMixin` | `...holo.gui.craft.material.HoloMaterialListGui` | `<init>` / `updateGroups` / `onSelect` / `onShow` / `reload` / `onMouseClick` | 见下 | ⚠️ 7.0.0 的 `updateGroups` 已重写：用 `WrapOperation` 包裹 `Stream.collect` 追加 3 个过滤器与排序；`addChild` 处用 `@Local` 换成 `MyHoloMaterialGroupGui`；`INVOKE_ASSIGN getChildren` 在 7.0.0 没有 astore，改为 `INVOKE getChildren` |
| 11 | `HoloPageMixin` | `se.mickelus.tetra.items.modular.impl.holo.HoloPage` | — | — | ❌ 删除（NeoForge 无枚举扩展 API；见 §4） |
| 12 | `HoloSchematicGuiMixin` | `...holo.gui.craft.schematic.HoloSchematicGui` | `<init>` | `RETURN` | ✅ 包名迁移；`detail` 仍直接挂在 `this.elements` 上，可移除重建 |
| 13 | `HoloVariantDetailGuiMixin$MaterialWrapper` | `...holo.gui.craft.schematic.HoloVariantDetailGui$MaterialWrapper` | `getTooltipLines` | `HEAD`，可取消 | ✅ 包名迁移；targets 字符串同步更新 |
| 14 | `HoloVariantItemGuiMixin` | `...holo.gui.craft.schematic.HoloVariantItemGui` | `drawChildren` | `INVOKE Screen.hasShiftDown` | ✅ 包名迁移；`GuiStringOutline` 子元素判断不变 |
| 15 | `HoloVariantListGuiMixin` | `...holo.gui.craft.schematic.HoloVariantListGui` | `update()V` / `updateSelection` / `onShow` / `filter` | `WrapOperation addChild` / `HEAD` / `HEAD` / `HEAD`（可取消） | ✅ 包名迁移；`addChild` 换 `MyHoloVariantGroupGui`；`filter` 注入使用 JECH 反射兼容层 |
| 16 | `StatBarStoreMixin` | `se.mickelus.tetra.gui.stats.data.StatBarStore` | `prepareBars` | `RETURN` | ⚠️ 7.0.0 已改用 `stat_bars`；本 mixin 仍扫描 `tool_bars` 以兼容旧附属数据，tetra 自身不再提供该目录，通常为空操作 |
| 17 | `ToolActionIconStoreMixin` | `se.mickelus.tetra.client.ItemAbilityIconStore` | `prepareIcons` | `RETURN` | ⚠️ 上游类由 `ToolActionIconStore` 更名为 `ItemAbilityIconStore`，`ToolAction` → `ItemAbility`；同样扫描 `tool_bars`，通常为空操作 |

## 4. 关于 `HoloPageMixin` / ADDON 页（❌）

- 1.20.1 Forge 通过 `IExtensibleEnum` + `HoloPageMixin.create(String,int,...)` 在运行时给 `HoloPage` 增加 `ADDON` 常量。
- NeoForge 1.21.1 已无 `IExtensibleEnum`；其新枚举扩展机制要求目标枚举带 `@ExtensibleEnum` 元数据，而 tetra 7.0.0 的 `HoloPage` 为 final、未接入该机制，无法合法扩展。
- 采用等价替代：`HoloGuiMixin` 在 `HoloGui` 构造末尾把 `ExtraHoloRootGui` 加到 `defaultGui`，并在 `openSchematic` 命中 `ExtraHoloRegister.MAP` 时隐藏原 3 个页面、显示叠加页并设置 `currentPage`；`changePage` 时隐藏叠加页。
- 结果：**额外全息页的核心功能（工作台快捷打开注册物品的扩展页）完整保留**；差别是不再出现独立的第 4 个 `ADDON` 头部标签页，且重新打开时沿用 vanilla 的“记住上次页面”行为。

## 5. 兼容性统计

按原始 48 个 `.java` 源文件计：

| 等级 | 数量 | 说明 |
| --- | --- | --- |
| ✅ 等价迁移 | 40 | 仅包名/导入/API 重命名，行为等价 |
| ⚠️ 实现差异 | 7 | `HoloGuiMixin`、`ExtraHoloRootGui`、`ExtraHoloItemsGui`、`JECHCompat`、`TooltipHandler`、`StatBarStoreMixin`、`ToolActionIconStoreMixin` |
| ❌ 降级删除 | 1 | `HoloPageMixin`（ADDON 枚举页/标签页） |

### ⚠️ 差异明细

1. **`JECHCompat`**：本机无 `me.towdium.jecharacters`。改为反射调用
   `Match.contains(String, CharSequence)`，保留 `ModList.get().isLoaded("jecharacters")`
   判断与普通 `String.contains` 回退；功能等价。
2. **`TooltipHandler`**：1.21.1 NeoForge 的 `ScreenEvent.MouseScrolled` 不再可取消
   （无 `setCanceled`），因此多材料 Tooltip 翻页时无法拦截底层界面的滚轮；滚动计数
   与翻页逻辑保留。
3. **`StatBarStoreMixin` / `ToolActionIconStoreMixin`**：上游 7.0.0 用 `stat_bars` +
   `ItemAbilityIconStore`/`ItemAbility` 取代 `tool_bars` + `ToolActionIconStore`/`ToolAction`。
   mixin 已迁移到新类名/新类型，但仍扫描 `tool_bars` 目录；tetra 7.0.0 不再自带该目录，
   因此对旧附属的兼容逻辑保留但通常不产生数据。
4. **`HoloGuiMixin`**：见 §4（叠加页替代 ADDON 枚举页）。
5. **`ExtraHoloRootGui`**：7.0.0 的 `HoloSchematicGui.update(ItemStack,String,UpgradeSchematic)`
   不接受 null schematic；`updateState` 增加空判断（null 时回退到物品列表），避免 NPE。
6. **`ExtraHoloItemsGui`**：7.0.0 的 `HoloItemGui` 改由 `HolosphereEntryData` 驱动，
   因此为每个注册项构造 `key/item/icon`；自定义图标仍通过 `IHoloItemGui.setIcon` 替换，
   尺寸/UV 保持 `ExtraHoloBuilder` 的行为。

### 其它迁移点（✅）

- Forge → NeoForge API：`@Mod(value, dist=Dist.CLIENT)` + 构造器注入
  `(IEventBus, ModContainer)`；`NeoForge.EVENT_BUS`；`ModConfigSpec`；
  `RegisterKeyMappingsEvent`；`ClientTickEvent.Pre`（替代 `TickEvent` + `Phase.START`）。
- `ToolAction` → NeoForge `ItemAbility`；`TetraToolActions` → `TetraItemAbilities`；
  `ToolActions.AXE_DIG` → `ItemAbilities.AXE_DIG`。
- tetra 7.0.0 包结构重构：`...holo.gui.craft.HoloItemGui` → `...craft.item.HoloItemGui`；
  `HoloSchematicGui/HoloSchematicListGui/HoloVariant*/HoloImprovement*/HoloMaterialApplicable`
  → `...craft.schematic.*`；`HoloMaterialListGui/HoloMaterialDetailGui/HoloMaterialsButtonGui`
  → `...craft.material.*`；`HoloGui` 改用 `HoloScanRootGui` 等新页面。
- `GuiGraphics.blit` 相关未在本 mod 使用；`AttributeModifier` 使用 `amount()`/`operation().id()`。
- `ItemTooltipFlag.isAdvanced()`、`ItemStack.is(Item)`、`ResourceLocation.fromNamespaceAndPath` 等 1.21.1 写法。
- `pack_format` 15 → 34。

## 6. 依赖声明（neoforge.mods.toml）

| modId | 类型 | 版本范围 | 说明 |
| --- | --- | --- | --- |
| `neoforge` | required | `[21.1,)` | 21.1.238 |
| `minecraft` | required | `[1.21.1,1.22)` | |
| `tetra` | required | `[7.0.0,)` | 编译用 `libs/tetra-1.21.1-7.0.0.jar` |
| `tetra_material_overhaul` | required | `[1.0.0,)` | 编译用 `libs/tetra_material_overhaul-1.21-1.21.1-6.9.0-1.0.1.jar`，`ContextData`/`IMaterialData` |

可选运行时检测：`art_of_forging`、`secrets_of_forging_revelations`（注册额外全息页条目）、
`jecharacters`（拼音搜索，反射）。

## 7. 构建环境说明

- JDK：`C:\Program Files\Java\jdk-21.0.12`
- 离线构建：`.\gradlew build --offline --console=plain`
- 为解决本机 NFRT 外部工具缓存缺失：
  - `configurations.configureEach { resolutionStrategy { force 'net.neoforged:accesstransformers:10.0.1' } }`
  - `tasks.named('createMinecraftArtifacts') { enabled = false }`
  - 预置 `build/moddev/artifacts/*`（从 `geotetraarmor` 复制）
- `processResources` 的 `expand` 包含 `version` 与 `loader_version_range`。
- 未执行 `gradlew clean`。

## 8. 已知不可在本机验证项

- 未实际启动游戏/客户端，Mixin 的运行时应用仅通过 `javap` 逐项核对目标类、字段名、
  方法名与描述符；所有注入锚点均已对照 tetra 7.0.0 字节码确认存在。
- `GuiSchematicListMixin` 对 private final `buttonBack/buttonForward` 使用 `@Mutable` 重建
  按钮；`setPage/getPage` 为 private，使用带桩体的 `@Shadow` 调用。
