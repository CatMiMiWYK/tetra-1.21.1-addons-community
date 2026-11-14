# 通用卷轴（Universal Scroll）附属 Mod — 规格说明

工作目录：`D:\mod移植\2026-11-13\addons\universalscroll`
源码依据：`D:\mod移植\2026-11-13\第三方 tetra 1.21.1`（与线上 `tetra-1.21.1-7.0.0.jar` 同源）
目标版本：Minecraft 1.21.1 / NeoForge 21.1.266 / Tetra 7.0.0

## 1. 机制确认（已逐条对照源码，非推测）

### 1.1 卷轴物品与数据载体
- `ScrollItem extends BlockItem implements InitializableItem`，注册名 `scroll_rolled`（`blocks/scroll/ScrollItem.java`）。
- 数据载体是 DataComponent：`ScrollData` 由 `ScrollData.MAP_CODEC/CODEC/STREAM_CODEC` 序列化，存放于 `TetraRegistries.scrollData`（`ScrollData.java#L83-L88`、`TetraRegistries.java#L184、L492`），读写走 `ScrollData.read/write`，不使用物品旧 NBT。
- `ScrollData` 字段：`key / details / intricate / material / ribbon / glyphs / schematics / effects`（`ScrollData.java#L30-L49`）。
- 物品贴图：`assets/tetra/models/item/scroll_rolled.json`（parent `minecraft:item/generated`，`layer0=tetra:item/scroll`、`layer1=tetra:item/scroll_binding`），可直接作为本附属物品的模型父级，无需新贴图。
- `ScrollRenderer` 是**已放置卷轴方块**的 `BlockEntityRenderer`（`ScrollRenderer.java#L36`），与物品“使用”逻辑无关。

### 1.2 卷轴“使用”的真实调用链（关键结论）
- `ScrollItem#use`（`ScrollItem.java#L248-L255`）：仅当 `details != null` 时在**客户端**打开纯文本 `ScrollScreen`，不触发任何 schematic。
- `ScrollItem#useOn`（`ScrollItem.java#L257-L288`）：把卷轴放成方块；若点击面下方是 `AbstractWorkbenchBlock`，`getPlacementState`（#L290-L307）会选择 `OpenScrollBlock`。
- `ScrollBlock implements ISchematicProviderBlock`（`ScrollBlock.java#L37`），`getSchematics` 读取 `ScrollTile#getSchematics`（`ScrollTile.java#L50-L57`，按 `isIntricate` 过滤）。
- `AbstractWorkbenchBlock#getSchematics`（#L157-L170）扫描工作台周围 `(-2,0,-2)~(+2,4,+2)`，收集所有 `ISchematicProviderBlock` 的 schematic，作为 `WorkbenchTile#getUnlockedSchematics()`。
- `GuiSlotDetail#updateSchematicList`（#L174-L181）用 `getUnlockedSchematics()` 构造 `CraftingContext`，再 `SchematicRegistry.getSchematics(context)` 生成工作台图纸列表。

**结论：原版卷轴不具备“直接应用某 schematic”的能力；它的真实作用是“放在工作台旁，使该 schematic 出现在工作台对应槽位的图纸列表里”。真正的应用发生在工作台：`WorkbenchTile#craft`（#L359-L427）。**

### 1.3 schematic 注册表与枚举
- `DataManager.schematicData = new SchematicStore(gson, "tetra", "schematics", this)`（`DataManager.java#L173`）。
- mutil `DataStore#prepare` 只加载 namespace 为 `tetra` 的资源（`mutil/.../DataStore.java#L59-L66`）；`SchematicStore#processData` 把 `definition.key` 设为路径（`SchematicStore.java#L40-L43`）。
  → 所有本体与附属图纸都必须位于 `data/tetra/schematics/**.json`，键为 `tetra:<path>`。
- 枚举 API（全部为 public static，可直接调用）：
  - `SchematicRegistry.getAllSchematics()`（#L49-L51）
  - `SchematicRegistry.getSchematic(ResourceLocation)` / `getSchematic(String)`（#L41-L47）
  - `UpgradeSchematic#getName/getDescription/getSources/getType/getRarity/getGlyph/isRelevant/isApplicableForSlot/matchesRequirements/isHoning`（`UpgradeSchematic.java#L17-L170`）
  - 图标：`GlyphData.textureLocation/textureX/textureY/tint`（`GlyphData.java#L20-L33`），默认 `tetra:textures/gui/glyphs.png`，16×16 单元（`GuiSchematicListItem.java#L39-L60`）。
- `ConfigSchematic#getKey` 返回**路径**（#L79-L81），`SchematicRegistry.getSchematic(String)` 自动补 `tetra:` 命名空间（#L45-L47），因此 `WorkbenchPacketUpdate`（#L60-L74）与 `WorkbenchTile#loadAdditional`（#L496-L497）的字符串键回读对所有图纸成立。

### 1.4 打开工作台图纸的可行路径（本附属采用）
- `WorkbenchTile#setCurrentSchematic(schematic, slot)`（#L280-L287）在服务端设置当前图纸并同步方块实体。
- `WorkbenchTile implements MenuProvider`（#L56），`createMenu`（#L574-L576）返回 `WorkbenchContainer`；Tetra 已为它注册 GUI（`ClientSetup.java#L116-L118`）。
- 因此服务端校验后可 `player.openMenu(tile)`，客户端直接进入工作台界面，并已停留在所选图纸的详情/应用页（`GuiSlotDetail#onTileEntityChange`，`GuiSlotDetail.java#L125-L158`）。

## 2. 可行性结论（明确边界）

### 2.1 可做到
1. 一个物品列出**当前已加载的全部** Tetra schematic（本体 + 所有附属，含搜索、图标、名称、类型/来源分类）。
2. 选中后：若玩家对工作台使用本物品，且工作台内有目标物品，则直接打开该工作台 GUI 并预选该图纸，后续材料/工具/合成流程与“卷轴解锁后在工作台选中图纸”完全一致（同一 `WorkbenchTile#craft`）。
3. 图纸兼容性判定复用原版：`isRelevant` + `isApplicableForSlot` + `matchesRequirements(CraftingContext)`。

### 2.2 不可做到（不近似冒充）
1. 单靠物品**直接应用** schematic：应用必须要有工作台方块、槽位 0 的目标物品、材料槽材料、以及工具/经验等条件；原版卷轴同样做不到。
2. `matchesRequirements` 不满足（如需要特定解锁/携带材料/进阶条件）的图纸无法进入应用页——原版工作台列表也不会显示它们，本附属同样拒绝并给出原因。
3. 目标物品为空、或目标物品没有该图纸可用槽位时，只能“列出但不能进入”，与原版 `getSchematics(context)` 过滤一致。
4. 因此产品定位是：**“把你带到对应图纸”——在工作台处一步直达所选图纸的应用界面**，不是“凭空合成”。

## 3. 实现设计

### 3.1 标识
- modid：`universalscroll`（`addons/` 与实例 mods 中均无同名，避免冲突）
- 包名：`ovo.yiran.universalscroll`
- 物品注册名：`universalscroll:universal_scroll`
- 架构：复用 `geotetraarmor` 的 NeoForge ModDev 离线脚手架与 `libs/tetra|mutil` 编译依赖。

### 3.2 物品 `UniversalScrollItem`（非 BlockItem）
- 模型：`assets/universalscroll/models/item/universal_scroll.json`，parent `tetra:item/scroll_rolled`。
- 使用行为：
  - `use(...)`：客户端打开选择界面（`workbenchPos = null`，仅浏览；点击时提示“需对工作台使用”）。
  - `useOn(...)`：若 `context.getLevel().getBlockState(pos).getBlock() instanceof AbstractWorkbenchBlock` 且非潜行，客户端打开选择界面并带上 `pos`；否则退化为浏览。
  - 不消耗物品（可重复使用）。
- DataComponent（1.21.1 规范）：
  - `DeferredRegister<DataComponentType<?>>` 注册 `universalscroll:selected_schematic`（`ResourceLocation`，`persistent(ResourceLocation.CODEC)` + `networkSynchronized(ResourceLocation.STREAM_CODEC)`）。
  - 服务端成功选中后写入手持物品；tooltip 显示“上次选择：<键>”。

### 3.3 选择界面 `SchematicSelectionScreen`（客户端）
- 数据源：`SchematicRegistry.getAllSchematics()`，按 来源(`getSources`) → 类型(`getType`) → 名称 排序。
- 每行：16×16 图标（`getGlyph()` 的 `textureLocation/textureX/textureY`，`GuiGraphics#blit`）、名称（按 `getRarity().tint` 着色）、右侧灰色 `key` 与来源/类型。
- 顶部 `EditBox` 搜索（匹配名称/key/来源，忽略大小写）；鼠标滚轮滚动；仅渲染可见行。
- 悬停 tooltip：名称、key、来源、类型、`getDescription(ItemStack.EMPTY)`、`getRequirementDescription()`（若存在）。
- 点击：有 `workbenchPos` → 发送 `SelectSchematicPayload(pos, schematicRL)` 并关闭；无 → 本地提示“请对着工作台使用”。

### 3.4 网络与服务端校验
- `SelectSchematicPayload(BlockPos, ResourceLocation)`：`CustomPacketPayload` + `StreamCodec`，在 `RegisterPayloadHandlersEvent` 中 `playToServer` 注册。
- 服务端处理：
  1. 方块实体必须是 `WorkbenchTile`；玩家距离 ≤ 6 格。
  2. `SchematicRegistry.getSchematic(rl)` 必须存在。
  3. `tile.getTargetItemStack()` 非空且为 `IModularItem`。
  4. 在 `getMajorModuleKeys + getMinorModuleKeys` 中找第一个满足 `isRelevant(target)` 且 `isApplicableForSlot(slot, target)` 的槽位。
  5. `new CraftingContext(level,pos,blockState,player,target,slot,unlocks)` 下 `matchesRequirements` 为真。
     - `unlocks` = 工作台自身 `getUnlockedSchematics()` ∪ 已加载 schematic 原始 JSON 中所有 `"type": "tetra:locked"` 的 key。
       依据：卷轴解锁的真实实现就是把这些 key 放入 `CraftingContext.unlocks`（`LockedRequirement.java#L19`），
       通用卷轴语义上等价于“已获得全部卷轴”，但仍严格保留 `module/improvement/materialReveal` 等真实条件。
  6. `tile.setCurrentSchematic(schematic, slot)`；写 DataComponent；`player.openMenu(tile, pos)`。
     - 必须使用带 `pos` 的重载：`WorkbenchContainer` 的客户端工厂（`TetraRegistries.java#L428-L429`）会从额外数据读取 BlockPos，
       原版 `AbstractWorkbenchBlock.java#L47` 同样调用 `player.openMenu(te, pos)`。
  7. 任一步失败：`player.displayClientMessage` 给出具体原因（无工作台/无目标物品/无可用槽位/条件不满足/图纸不存在），不改动工作台。

### 3.5 资源与元数据
- `META-INF/neoforge.mods.toml`：`modId=universalscroll`；依赖 `neoforge [21.1.0,)`、`minecraft [1.21.1,1.22)`、`tetra [7.0.0,)`；**本实现不使用 mixin，因此不写 `[[mixins]]`**（规则要求是“若使用 mixin 必须写入”，此处无 mixin）。
- 语言文件：`zh_cn.json` / `en_us.json`（物品名、tooltip、界面标题、搜索提示、失败提示）。
- 创造模式标签：加入 `tetra:default`（与 `geotetraarmor` 相同做法）。

### 3.6 构建与交付
- `build.gradle` 复用 geotetraarmor 脚手架：`net.neoforged.moddev 2.0.141`、工具链 21、`mavenLocal()`、禁用 `neoFormRuntimeExternalTools` 传递依赖、`libs/tetra-1.21.1-7.0.0.jar` + `libs/mutil-1.21.1-7.0.0.jar`。
- 离线构建：`.\gradlew.bat -p D:\mod移植\2026-11-13\addons\universalscroll build --offline --console=plain`。
- 产物交付到 `D:\mod移植\2026-11-13\deploy-mods`（实例 mods 目录只读，不直接写入）。

## 4. 验收标准
1. `build` 成功，jar 内含：主类、物品类、payload 类、客户端 Screen、模型 JSON、两份语言文件、`neoforge.mods.toml`。
2. jar 内**不得**包含 `se/mickelus/tetra/**` 或 `se/mickelus/mutil/**` 的 class（编译依赖不得打包）。
3. `neoforge.mods.toml` 的 modId/版本/依赖正确；版本号不带 `1.21.1-` 前缀（FML Maven 比较规则）。
4. 输出 jar 路径、字节大小、class 数量、资源数量；说明放入 mods 的注意事项（需 tetra 7.0.0、仅客户端/服务端均需安装、选择界面为客户端功能）。

## 5. 已知限制（写进交付说明）
- 图纸数量取决于当前加载的包（本体 389 个 + 各附属，如 art_of_forging 176、geotetraarmor 35 等），界面动态枚举，不做硬编码。
- 需要工作台与目标物品才能“进入图纸”；不在工作台处使用时只能浏览。
- 不提供“无需材料直接合成”，这是 Tetra 机制限制，不是实现取舍。

## 6. 2026-11-14 增补：异次元口袋图标 + 百宝卷轴

### 6.1 物品图标
- `assets/universalscroll/textures/item/universal_scroll.png` 改为 16×16「哆啦A梦异次元口袋」：深蓝描边、哆啦A梦蓝底、白色口袋、深色袋口。

### 6.2 新增方块与物品
| 项 | 注册 id |
|---|---|
| 方块 | `universalscroll:omni_scroll` |
| 物品（BlockItem） | `universalscroll:omni_scroll`（百宝卷轴 / Treasure Scroll） |

- 方块 `OmniScrollBlock implements ISchematicProviderBlock, ICraftingEffectProviderBlock`：
  - `canUnlockSchematics / canUnlockCraftingEffects` 恒为 true；
  - `getSchematics` 返回“全部卷轴解锁键”，收集方式：
    1. 遍历 `DataManager.instance.schematicData` 中所有图纸定义的 `requirement`，递归取 `tetra:locked` 的 `key`
       （And/Or 组合条件的 children 字段为包私有、Tetra 未提供读取接口，此处用反射遍历；反射失败仅跳过该分支）；
    2. 并入全部图纸键 `tetra:<UpgradeSchematic#getKey()>` 作为兜底。
    说明：卷轴 `schematics` 字段里的键（如 `tetra:warforge/axe`）不一定登记为图纸，所以不能只返回图纸键。
    结果按数据包重载监听（`schematicData.onReload`）清空缓存。
  - `getCraftingEffects` 返回 `DataManager.instance.craftingEffectData` 的全部键。
- 依据：`AbstractWorkbenchBlock#getSchematics/getCraftingEffects` 扫描工作台周围 `(-2,0,-2)~(2,4,2)` 内的 provider 方块，把返回值并入工作台的 `unlocks`；工作台 GUI 仍用 `SchematicRegistry.getSchematics(CraftingContext)` 做 `isRelevant / isApplicableForSlot / matchesRequirements` 过滤。
- 放置行为：无 BlockEntity 的静态方块，碰撞/选取形状 `[2,0,5]~[14,3,11]`；`data/universalscroll/loot_table/blocks/omni_scroll.json` 掉落自身。

### 6.3 豁免范围（重要）
- 只豁免“卷轴解锁类”条件：即 `LockedRequirement#test` 对 `context.unlocks` 的包含判断。等价于“玩家已持有全部卷轴”，且只对 `tetra:locked` 这一种条件类型生效。
- 不修改 `matchesRequirements`，不修改 tetra/mutil 本体。以下条件全部保留、照常校验：模块/槽位存在、材料与材料揭示、工具能力与等级、经验等级、工作台范围等。
- 边界：解锁键集合来自“已加载图纸里所有 `tetra:locked` 引用的 key + 全部图纸键”。若某条件引用了既非图纸键、也没有被任何图纸定义引用的自定义键（例如只写在卷轴数据/战利品表里的孤立标记），本方块不会凭空提供该键——这是“只豁免卷轴解锁”的精确范围。

### 6.4 鎏金外观实现
- 物品：`Item.Properties#component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)`，强制附魔光效。
- 方块：`textures/block/omni_scroll.png` 为 8 帧 16×128 动画纹理（`omni_scroll.png.mcmeta`，`frametime=3`），金色底 + 移动虹彩高光；束带用静态 `omni_scroll_band.png`。
- 模型：`models/block/omni_scroll.json`（卷轴主体 + 束带两个 cuboid）；`models/item/omni_scroll.json` 为鎏金卷轴物品图标。

### 6.5 功能降级
1. 仍需工作台与目标物品才能实际应用图纸，本方块只负责“提供图纸”，不代替 `WorkbenchTile#craft`。
2. 只在工作台周围 5×5×5 范围内生效（与原版卷轴一致），不是全图/全局解锁。
3. 不消耗、不消失；破坏后按 loot table 掉落自身。

