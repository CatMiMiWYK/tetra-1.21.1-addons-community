# TetraJS 2001-6.9.0-1.0.2 1.21.1 NeoForge 移植说明

- 源：`Tetra JS-2001-6.9.0-1.0.2.jar`（Forge 1.20.1，59 class / 3 mixin / `kubejs.plugins.txt` 1 条）
- 目标：NeoForge 21.1.238 / MC 1.21.1 / Java 21 / MDG 2.0.141
- 产物：`build/libs/tetrajs-1.21-1.21.1-2001-6.9.0-1.0.2.jar`（97141 字节，SHA256 `A18E67C5...7BA4EF`）
- 构建：`.\gradlew --offline clean build --console=plain` → **BUILD SUCCESSFUL**
- 新增/更新外部依赖（已放入工程 `libs/`）：
  - tetra 1.21.1-7.0.0、mutil 1.21.1-7.0.0、tetra_addition 1.21.1-6.9.0-1.1（本套 #4 产物）
  - kubejs-neoforge-2101.7.2-build.381
  - **rhino-2101.2.7-build.85**（KubeJS 2101 传递依赖，原工程无；从 Modrinth 下载）
  - **curios-neoforge-9.5.1+1.21.1**（从 Modrinth 下载，编译期使用）

## 1. 数量核对表

| 项目 | 原 jar | 新工程 | 结论 |
|---|---|---|---|
| class 文件 | 59 | 58 | ⚠️ -1：`probejs/TetraProbePlugin` 已移除（见 §3）；另 `AttributeStatBarBuilder$1`（旧 javac switch 合成类）换成 `EquipModularItem$1`（新 switch 合成类），相互抵消 |
| mixins.json 条目 | 3（`mixins` 2 + `client` 1） | 3 | ✅ 名称与 package 完全一致 |
| `kubejs.plugins.txt` | 1 条（`net.yiran.tetrajs.kubejs.TetraJSPlugin`） | 1 条 | ✅ |
| 资源文件 | `pack.mcmeta`、`kubejs.plugins.txt` | 同名复制 | ✅ |
| refmap | 有 | 无 | ✅ 按 1.21 规范 |
| `META-INF/mods.toml` | 1.20.1 格式 | `neoforge.mods.toml`，`${version}` / `${loader_version_range}` 已展开 | ✅ |

## 2. 主要适配点

### 2.1 启动/注册（NeoForge 21.1）
- 主类改为构造注入 `(IEventBus modEventBus, ModContainer modContainer)`，mod bus 监听 `FMLCommonSetupEvent`（`EventPriority.LOWEST`），客户端 `FMLClientSetupEvent` 仅在 `Dist.CLIENT` 注册。
- `MinecraftForge.EVENT_BUS` → `NeoForge.EVENT_BUS`（workbench 合成事件监听）。
- `ModList.get().isLoaded`、`TetraMod.packetHandler` 保持不变。

### 2.2 tetra 7.0 API
- `ModuleModel` 已删除 → `IModuleModel`；`ShowModelHelper` 过滤逻辑改为：
  - `FilteredGridTextureModelData#getFilter()` 对应原 `ModuleModel.type`（draw_0/draw_1/draw_2/loaded/item）；
  - 非过滤模型始终显示，与原“showModels 为空则全显示、否则按变体过滤”的语义对齐。
- `ModularCrossbowItem` → `ModularCrossbowItemImpl`（`CrossBowModularItem` 改继承它，`super(Items.CROSSBOW)`）。
- `ItemStack#getOrCreateTag/getTag` → `se.mickelus.tetra.util.ItemStackTagHelper#getTag/mutate`；`IModularItem.updateIdentifier(ItemStack)`。
- `IModularItem#synergyDataSync()` 已由上游删除 → 在 `ITetraJSItem` 内用 `DataManager.instance.synergyData.onReload(...)` 自行实现（按 namespace=`tetra` + 路径前缀过滤），行为与原实现一致。
- `ToolAction` → `net.neoforged.neoforge.common.ItemAbility`；`TetraToolActions.hammer` → `TetraItemAbilities.hammer`。

### 2.3 属性修饰符（1.21 数据组件化）
- `getAttributeModifiers(EquipmentSlot, ItemStack)` 已移除 → `EquipModularItem` 覆写 `Item#getDefaultAttributeModifiers(ItemStack)`，用 `ItemAttributeModifiers.builder().add(Holder<Attribute>, AttributeModifier, EquipmentSlotGroup)` 输出。
- `EquipmentSlotGroup.bySlot` 不存在 → 按 `EquipmentSlot` 枚举 switch 到 `HEAD/CHEST/LEGS/FEET/MAINHAND/OFFHAND`。
- `AttributeModifier` 的 UUID 构造移除 → `(ResourceLocation id, double amount, Operation)`；id 路径追加 `_<slot>` 保持多槽位不冲突。
- `TwoHandedModularItem` 的 `getAttributeModifiers` 覆写删除：tetra 7.0 的 `ItemModularHandheld` 已按 mainhand/offhand 分组输出属性，原覆写等价于默认行为（见 §3.4）。

### 2.4 Curios 9.5.1（1.21.1）
- `ICurioItem#getAttributeModifiers(SlotContext, UUID, ItemStack)` → `(SlotContext, ResourceLocation, ItemStack)`，返回值改为 `Multimap<Holder<Attribute>, AttributeModifier>`。
- `CuriosHelper` 改为通过 `CuriosApi.getCuriosHelper()` 获取（`top.theillusivec4.curios.api.type.util.ICuriosHelper`），`findCurios(LivingEntity, Predicate)` 仍可用（deprecated）。
- `CuriosModularItem/DynamicCuriosModularItem` 覆写 `getDefaultAttributeModifiers` 返回空，避免普通装备槽重复结算。

### 2.5 KubeJS 2101
- `KubeJSPlugin` 由抽象类变接口 → `implements KubeJSPlugin`，覆写 `registerBuilderTypes/registerTypeWrappers/registerBindings/registerEvents`。
- `RegistryInfo.ITEM.addType` → `BuilderTypeRegistry#of(Registries.ITEM, callback)`。
- `ScriptType + TypeWrappers` → `TypeWrapperRegistry`；`registerSimple` 不存在 → `register(ContextFromFunction)`；`NbtSlotData` 用 `registerCodec(NbtSlotData.CODEC)`。
- `EventJS` → `KubeEvent`（3 个事件类 + workbench 事件类）；`GROUP.register()` → `registerEvents(EventGroupRegistry)`。
- `BindingsEvent` → `BindingRegistry`。
- `ItemBuilder.modelJson(...)` 已移除 → 改为覆写 `generateItemModels(KubeAssetGenerator)`，用 `ModelGenerator#custom` 写入原模型 JSON（`tetra:modular_loader` + display/variants/overrides 原样保留）。
- `BuilderBase.tag(ResourceLocation)` 变为数组参数 → `tag(new ResourceLocation[]{...})`。

### 2.6 其它 1.21 改动
- `I18n.m_118936_/m_118938_` → `I18n.exists/I18n.get`；`ItemStack.m_41778_` → `getDescriptionId()`。
- `ResourceLocation` 构造全部改为 `fromNamespaceAndPath` / `parse`。
- `net.minecraft.world.item.ItemAttributeModifiers` → `net.minecraft.world.item.component.ItemAttributeModifiers`。
- `GuiStatBar` 12 参构造、`StatGetter*`、`LabelGetterBasic`、`StatSorters` 在 7.0 中保持兼容。

### 2.7 Mixin
- `compatibilityLevel: JAVA_21`，去掉 `refmap`，保留原 package 与 3 个条目；tetra 为目标 mod，所有 `@Mixin`/`@Shadow` 标 `remap = false`。
- 3 个注入目标已在 1.21.1 tetra jar 中核对存在：
  - `ItemAspect.map`：`private static final Map<String, ItemAspect>`
  - `ItemEffect.effectMap`：`private static final Map<String, ItemEffect>`
  - `ThrownModularItemRenderer#render(ThrownModularItemEntity,float,float,PoseStack,MultiBufferSource,int)` + 方法体内 `Minecraft.getInstance()` 调用；`transformSingleHeaded(...)` 为 `private`（mixin 中改为 private shadow stub）。

## 3. 偏离 / 降级（均为显式处理，未静默丢弃）

1. **ProbeJS 类型导出移除**：`net.yiran.tetrajs.probejs.TetraProbePlugin` 依赖 ProbeJS 1.20.1 API，本机无 1.21.1 版 ProbeJS，按任务约定删除该类与主类中的注册代码。`kubejs.plugins.txt` 原本只引用主插件，不受影响。KubeJS 运行期绑定（`TetraJS$*`、`StatBarHelper` 等）仍完整保留，仅缺少 ProbeJS 的 TS 类型文件导出。
2. **KubeJS builder type id 改为小写蛇形**：KubeJS 2101 用 `ResourceLocation` 解析 type 名，原大写 id（`TetraJS:BaseModularItem` 等）不合法，会抛异常。现注册为：
   `tetrajs:base_modular_item`、`tetrajs:two_handed_modular_item`、`tetrajs:dynamic_modular_item`、`tetrajs:bow_modular_item`、`tetrajs:shield_modular_item`、`tetrajs:crossbow_modular_item`、`tetrajs:equip_modular_item`、`tetrajs:curios_modular_item`、`tetrajs:dynamic_curios_modular_item`。
   **旧脚本中的 type 字符串必须同步改名**（破坏性变更）。
3. **附魔 aspect 注册 API 变更**：1.21 删除 `EnchantmentCategory`，`TetraEnchantmentHelper.EnchantmentRules` 改为接收 `List<ItemStack> supportedItems`。事件方法由 `register(ItemAspect, EnchantmentCategory...)` 改为 `register(ItemAspect, ItemStack... supportedItems)`；脚本需传入“该 aspect 允许附魔的物品栈”，不能再传分类。
4. **TwoHanded 属性覆写删除**：1.21 tetra 基类已统一处理主手/副手属性组，保留原覆写无意义；双手武器仍可在主/副手获得属性，与原行为一致（不是功能丢失）。
5. **AttributeModifier id 方案变更**：1.21 移除了 UUID 构造，改用 `ResourceLocation`；同一物品多槽位的 modifier id 通过追加 `_<slot>` / `_<curiosIdentifier>_<index>` 保证唯一。语义等价，但 id 字符串与旧版不同。
6. **Curios 依赖改为编译期真实依赖**：已成功从 Modrinth 下载 9.5.1，未使用反射降级；`neoforge.mods.toml` 中 curios 为 `optional`，未安装时 `CompatManager` 跳过注册。

## 4. 待实测项

1. 启动期 3 个 mixin 是否全部成功应用（`ItemAspectMixin` / `ItemEffectMixin` 的 `<clinit>` 注入、客户端 `ThrownModularItemRendererMixin` 的 `@Local Item`）。
2. KubeJS 脚本（startup/client）加载：
   - `TetraJSEvents.registerEnchantAspect` 新签名；
   - `TetraJSEvents.registerStatBar` / `registerStatSorter` 绑定可用；
   - 9 个新 builder type id 能正确创建物品。
3. 动态物品模型：KubeJS 资产生成是否输出 `assets/<namespace>/models/item/<path>.json`，且 `tetra:modular_loader` 能识别 display/variants；盾牌 `builtin/entity` 是否正常渲染。
4. 动态槽位（`DynamicModularHelper` + `extraslots`）与 `showModels` 在 1.21.1 的 UI 中生效（依赖 `ItemStackTagHelper` 写入是否触发 `updateIdentifier`）。
5. Bow/Crossbow 拉弓变体（`item/draw_0/draw_1/draw_2/loaded`）与 `FilteredGridTextureModelData#getFilter` 是否一一对应。
6. Curios 饰品属性：`getAttributeModifiers(SlotContext, ResourceLocation, ItemStack)` 的 Holder 化 id 是否被 Curios 正确叠加；`providerAttribute=false` / `isUnbreakable` 分支。
7. `EquipModularItem` 在 1.21 的 `getDefaultAttributeModifiers` 是否只对配置的 `EquipmentSlot` 生效（`EquipmentSlotGroup` switch）。
8. `tetra_addition` 的工作台合成事件（`TetraJSEvents.workbenchCraft`）在 1.21.1 端口是否正常 `post`，且 `upgradedStack` 修改可回写。
9. 主类 `onCommonSetup` 中三次 `RepairSchematic` 注册与动态物品的 repair 原理图无重复/覆盖。
