# TetraModify Core 1.21.1 NeoForge 移植说明

- 源：`TetraModify Core-6.9.0-1.0.10.jar`（Forge 1.20.1，33 class / 15 mixin 条目）
- 目标：NeoForge 21.1.238 / MC 1.21.1 / Java 21 / MDG 2.0.141
- 产物：`build/libs/tetra_modify_core-1.21-1.21.1-6.9.0-1.0.10.jar`（68027 字节）
- 构建：`.\gradlew --offline clean build --console=plain` → **BUILD SUCCESSFUL**

## 1. 数量核对表

| 项目 | 原 jar / 原 src | 新工程 | 结论 |
|---|---|---|---|
| class 文件 | 33 | 35 | ✅（+2：新增 `HoloCraftRootGuiMixin` 与 `LivingEntityAccessor`，见第 3 节 / 2.1 节） |
| mixins.json 条目 | 15（`mixins` 11 + `client` 4） | 16（`mixins` 10 + `client` 6） | ✅（+1；`ForgeHooksClientMixin` 从 `mixins` 移入 `client`） |
| accesstransformer.cfg 条目 | 4 | 3 | ⚠️（删除 `HoloCraftRootGui itemStack`，该字段在 1.21.1 已不存在） |
| 资源/数据文件 | 8（icon.png、pack.mcmeta、mixins.json、lang×2、scrolls×1、AT、mods.toml） | 8（同左，mods.toml→neoforge.mods.toml） | ✅ 内容原样复制 |
| 未随包内容 | refmap.json、META-INF/jarjar/（mixinextras）、MANIFEST.MF | 不带 | ✅ 按 1.21 规范移除（MixinExtras 由 NeoForge 内置，无 refmap） |

逐项核对：
- ✅ 包名 / 类名 / 目录结构保持原样（`net.tetra.modify.**`）
- ✅ 事件 API（`LivingHurtEvent` / `LivingDamageEvent` / `ModularGetterProjectileEvent`）语义保持
- ✅ 配置类、`MixinConfig`、`MixinConfigPlugin` 保持
- ✅ 数据（`ScrollDataManager`）、工具（`MatchUtils`）、包装（`IModularItemWrapper`）保持
- ✅ 全部 15 个原 mixin 目标均保留
- ⚠️ 全息仪（HoloItemsGui / HoloItemGui）按 1.21.1 新结构重写
- ⚠️ 伤害钩子重锚（ForgeHooks → DamageContainer 管线）
- ⚠️ 卷轴物品栈创建方式改为 1.21.1 数据组件
- ❌ 无（没有功能被整体删除）

## 2. 与 1.20.1 原实现的所有偏离

### 2.1 伤害钩子（重锚）
- `LivingEntityMixin` / `PlayerMixin` 各含两个注入点：
  - `@Inject(method="actuallyHurt", at=HEAD)`：从 `damageContainers.peek()` 取 `DamageContainer`，用 `getNewDamage()` 发 `LivingHurtEvent.Pre`；取消→`setNewDamage(0)` 并返回；否则写回 `getResultAmount()`；紧接着发 `LivingHurtEvent.Post`，同样处理。
  - `@WrapOperation` 拦截 `CommonHooks.onLivingDamagePre(LivingEntity, DamageContainer)F`：先发 `LivingDamageEvent.Pre`（取消→0）→ 写回 → 调 `original` → 结果为 0 直接返回 0 → 用结果发 `Post`（取消→0）→ 写回并返回。
- 语义：`LivingHurtEvent` 位于护甲吸收前，`LivingDamageEvent` 位于护甲/附魔减伤后，与原 Forge 钩子顺序一致。
- 原 `PlayerMixin` 中 1.20 专有的 `@Shadow` 成员（如 `ForgeHooks` 相关）已删除。
- **启动崩溃修复（2026-11-14）**：1.21.1 的 `damageContainers` 字段声明在 `LivingEntity`，`Player` 没有该字段，`PlayerMixin` 上的 `@Shadow` 会直接抛 `InvalidMixinException`。改为新增接口访问器 `LivingEntityAccessor`（`@Mixin(LivingEntity.class)` + `@Accessor("damageContainers")`），`PlayerMixin` 通过 `((LivingEntityAccessor)(Object)this).getDamageContainers()` 读取；两处 HEAD 注入均加 `isEmpty()` 守卫（HEAD 早于方法内的 `isInvulnerableTo` 判断，栈可能为空）。
- `@Shadow` 字段类型为 `java.util.Stack<DamageContainer>`（1.21.1 实际声明，任务描述写的是 `Deque`，以实际类型为准）。

### 2.2 事件取消模型
- 1.21.1 的 `IEventBus.post(Event)` 返回事件本身而非 boolean，且无 `@Cancelable`。
- 三个事件类改为 `implements ICancellableEvent`（`net.neoforged.bus.api.ICancellableEvent`），取消判断改为 `event.isCanceled()`；对外 `setCanceled()` 语义不变。

### 2.3 全息仪（按新结构重写）
- `HoloItemsGuiMixin`：1.21.1 的 `HoloItemsGui` 不再持有 `IModularItem`/`ItemStack`，条目完全数据驱动（`loadEntries(List<HolosphereEntryData>)`），且**没有** `elements` 字段。重写为 `@ModifyVariable(method="loadEntries", at=HEAD, argsOnly=true)`：当捕获到的工作台物品栈存在、且列表中没有对应 key 的条目时，复制一份列表并追加一个临时 `HolosphereEntryData`。
- 新增 `client.HoloCraftRootGuiMixin`：在 `HoloCraftRootGui.openFromWorkbench(IModularItem, ItemStack, String, UpgradeSchematic)` HEAD 捕获 `ItemStack` 到 `TemporaryHoloItemGui.setWorkbenchStack(...)`（1.21.1 没有公开的“当前栈”入口，这是唯一可靠来源）。
- `TemporaryHoloItemGui`：由「HoloItemGui 子类」改为纯数据助手（保留类名与静态入口），负责保存工作台栈、按 item+archetype 反查已注册 key（找不到用 `tetra_modify_core:temporary`）、复用同名条目的 icon。
- 行为差异：临时条目由 tetra 原生 `HoloItemGui` 渲染（不再有自定义名称浮层/38×38 图标移除），点击行为与普通条目一致。

### 2.4 弓 / 弩
- 弩 mixin 目标类 `ModularCrossbowItem` → `ModularCrossbowItemImpl`（1.21.1 拆分），`shootableDummy`（protected 字段）与 `findAmmo(LivingEntity)`（private）均在该类。
- 1.21.1 的 `ModularCrossbowItemImpl`、`ModularBowItem` 局部变量表中没有原 mixin 引用的 `itemstack`/`bowStack` 名字，故去掉 `@Local(name=...)`：
  - 弩 `use`：用 `@Local(argsOnly=true)` 取 `Player` + `InteractionHand`，以 `player.getItemInHand(hand)` 得到弩栈；
  - 弩 `reload`：`@Local(argsOnly=true) ItemStack`（唯一 ItemStack 参数）；
  - 弓 `fireArrow` / `use`：`Player.getProjectile(ItemStack)` 的实参本身就是弓栈，直接使用包裹参数，不再捕获局部变量。

### 2.5 Tooltip 钩子
- `ForgeHooksClient.gatherTooltipComponentsFromElements` → `net.neoforged.neoforge.client.ClientHooks.gatherTooltipComponentsFromElements`（同签名、静态）；`needsWrap` 局部变量名在 1.21.1 仍存在。
- 该 mixin 从 `mixins` 段移入 `client` 段（目标类只在客户端存在，避免专用服务器加载失败）。

### 2.6 卷轴
- `ItemStack.setTagElement` 已移除。1.21.1 中卷轴内容由数据组件承载，故 `ScrollDataManager.getScrollItem` 改为 `ScrollData.createItemStack()`（等价于 `ScrollData.write` 写入 `TetraRegistries.scrollData` 组件），**没有**使用 `DataComponents.BLOCK_ENTITY_DATA`——因为该组件在 1.21.1 已不参与卷轴渲染，用它会产生空白卷轴。解码使用 `DataManager.gson.fromJson(...)`（沿用原实现，`ScrollData` 为普通可反射类）。

### 2.7 IModularItemWrapper
- 1.20 的 `ItemStack.getOrCreateTag()` 改为 `ItemStackTagHelper`：
  - `tickHoningProgression`：`ItemStackTagHelper.mutate(stack, tag -> ...)` 写入 `honing_progress` / `honing_available`；`HonePacket` 在 mutate 之后发送（保证包内是写入后的栈，与原实现一致）。
  - `HonePacket` 包路径改为 `se.mickelus.tetra.module.improvement.HonePacket`。
- `Client.getTooltip`：`module.getEnchantments` → `module.getEnchantmentHolders`（`Map<Holder<Enchantment>, Integer>`，配合 `Enchantment.getFullname(Holder, int)`）；折叠态附魔改用 `itemStack.getTagEnchantments().entrySet()`（`Object2IntMap.Entry`）。

### 2.8 注册 / 配置 / 生命周期
- `ForgeConfigSpec` → `ModConfigSpec`；入口构造注入 `IEventBus` + `ModContainer`，`modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC)`。
- `MinecraftForge.EVENT_BUS` → `NeoForge.EVENT_BUS`。
- `RegisterClientReloadListenersEvent` / `ClientTickEvent.Post` 均只在 `FMLEnvironment.dist.isClient()` 时注册（客户端专属事件在专用服务器加载会 NoClassDefFoundError）。原实现无条件注册；该空转的 tick 监听器逻辑（只构造一个未使用字符串）保留。
- 创造模式标签使用 `DeferredRegister<CreativeModeTab>` + `CreativeModeTab.builder()`。

### 2.9 Mixin / AT 基础设施
- `tetra_modify_core.mixins.json`：`compatibilityLevel: "JAVA_21"`，**无 refmap**，保留 `plugin`；`neoforge.mods.toml` 加了 `[[mixins]]` 与 `[[accessTransformers]]`（缺 mixins 段 1.21 不加载 mixin）。
- AT 保留 3 条：`HoloItemGui icon`、`HoloItemGui isSelected`、`HoloGui pages`（1.21.1 均存在）；删除 `HoloCraftRootGui itemStack`（1.21.1 已无该字段）。新代码不再在编译期依赖这些 AT（AT 只作用于 MC/NeoForge 产物，不会作用于 tetra 依赖 jar），AT 仅为运行时兼容保留。
- `GuiStatBarMixin` 不再 `extends GuiStatBase`（1.21.1 的 `GuiStatBar` 只有 11/13 参构造，mixin 声明的 4 参构造无法对应），改为不继承的抽象 mixin，仅 `@Shadow` 字段/方法；线程池用 `Math.max(thread, 1)` 防 `thread=0` 时抛异常（默认 `thread=0` 时该 mixin 由 plugin 关闭，行为不变）。
- 全部 mixin 类对 mod 目标使用 `remap = false`（tetra/mutil 已在官方名，无 refmap）。
- **版本号修正（2026-11-14）**：原 `build.gradle` 用 `version = "${minecraft_version}-${mod_version}"`，jar 内声明版本为 `1.21.1-6.9.0-1.0.10`；Maven 版本比较会认为它低于 `6.9.0`，导致 `tetra_over_honing` / `tetra_inherit_enchant` 声明的 `tetra_modify_core [6.9.0,)` 校验失败（模组加载前置阶段崩溃）。改为 `version = "${mod_version}"`，MC 前缀只保留在 `archivesName`（文件名不变）。jar 内现为 `version = "6.9.0-1.0.10"`。

## 3. 待实测项
1. 服务端/客户端启动：mixin 全部成功应用（`PlayerMixin` 已改用 `LivingEntityAccessor` 读取继承字段；仍待实测 `GuiElementMixin` 的 `parent`、`HoloItemsGuiMixin` 的 `@Final` 私有字段 shadow）。
2. 伤害事件数值：护甲前后顺序、取消后是否归零、`LivingDamageEvent.Post` 结果写回是否被 `actuallyHurt` 后续逻辑正确使用。
3. 全息仪：从工作台打开后根节点是否出现临时条目（名称/图标/选中态），点击后是否跳转到对应条目；无对应 `holosphere_entries` 数据时是否不崩溃。
4. 弓/弩：`ModularGetterProjectileEvent` 的发射物替换是否生效（自定义弹药判定、创造模式自动箭）；弩的 `use`/`reload` 两处 `findAmmo` 均被包裹。
5. 卷轴：创造模式标签中 `omniscient` 卷轴是否正常显示与使用（`ScrollData.createItemStack` 组件写入）。
6. Tooltip：`ClientHooks` 换行注入是否使 `needsWrap` 恒为 true；`IModularItemMixin` 覆盖的 tooltip 是否覆盖 tetra 原版 tooltip。
7. `ScrollDataManager` 客户端资源重载是否读到 `assets/tetra_modify_core/scrolls/omniscient.json`。
8. `MixinConfig` 生成的 `config/TetraModifyCoreMixinConfig.json` 是否正常创建，`thread > 0` 时 `GuiStatBarMixin` 生效。
