# GeoTetraArmor 1.21.1 NeoForge 移植说明

- 源：`GeoTetraArmor-6.9.0-1.0.4-fix.jar`（Forge 1.20.1，17 class / 3 mixin / 内嵌 jarjar Tetra Material Overhaul 6.9.0-0.0.4）
- 目标：NeoForge 21.1.238 / MC 1.21.1 / Java 21 / MDG 2.0.141
- 产物：`build/libs/geotetraarmor-1.21-1.21.1-6.9.0-1.0.4-fix.jar`（101796 字节）
- 构建：`.\gradlew --offline clean build --console=plain` → **BUILD SUCCESSFUL**
- 新增外部依赖：**GeckoLib 4.9.3**（`geckolib-neoforge-1.21.1-4.9.3.jar`，已下载到 `reference\work\deps\` 并复制到工程 libs）

## 1. 数量核对表

| 项目 | 原 jar | 新工程 | 结论 |
|---|---|---|---|
| class 文件 | 17 | 19 | ⚠️ +2：`GeckoModelData`（新增，替代已移除的 tetra `ModuleModel`）、`ModularArmorItem$1`（switch 合成类） |
| mixins.json 条目 | 3（`mixins` 1 + `client` 2） | 3 | ✅ |
| assets/data | 163 个文件 | 163 个文件（全量原样复制） | ✅ |
| 内嵌 jarjar | Tetra Material Overhaul 0.0.4 | 不带，改 `neoforge.mods.toml` 必需依赖 `tetra_material_overhaul` | ⚠️ 用 1.21.1 移植版替代 |
| refmap / mods.toml | 有 | 去掉 refmap，`mods.toml`→`neoforge.mods.toml` | ✅ 按 1.21 规范 |

## 2. 主要适配点

### 2.1 模块模型系统（tetra 7.0 重构）
tetra 1.20.1 的 `ModuleModel`（含 `type` / `location` / `tint` / `renderLayer`）在 7.0 被移除，改为注册表驱动的 `IModuleModel`：
- 新增 `model/GeckoModelData implements IModuleModel`，字段与 JSON 对应（`type` / `location` / `tint` / `renderLayer`）；
- 主类构造期 `ModuleModelRegistry.register("gecko", GeckoModelData.class)`，对应数据里 `"type": "gecko"`；
- 实现 `withSlotSuffix`（把槽位后缀拼到 location 末尾，生成 `.../vanilla/base` 这类路径）、`inheritTint`（`ItemColors.inherit` 时继承父染色）、`forMaterial`（原样返回）；
- `ModularArmorItem#getModels/getGecModels` 从"按 type 字符串判断"改为 `instanceof GeckoModelData`。

### 2.2 属性修饰符（1.21 数据组件化）
- 1.20.1 的 `getAttributeModifiers(EquipmentSlot, ItemStack)` 已不存在，改为覆写 `Item#getDefaultAttributeModifiers(ItemStack)`，用 `ItemAttributeModifiers.builder().add(holder, modifier, EquipmentSlotGroup)` 输出；
- `EquipmentSlotGroup` 无 `bySlot` 静态方法，按槽位 switch 到 `HEAD/CHEST/LEGS/FEET`；
- `fixIdentifiers` 改为基于 1.21 的 `AttributeModifier.id()`（`ResourceLocation`）：在 id 路径后追加 `/<槽位名>`，维持"同模块不同部位 id 不冲突"的原意。

### 2.3 护甲耐久伤害
- 原 `applyDamage(int, ItemStack, LivingEntity, int slot)` 的 4 参版本在 1.21.1 已无对应（`IModularItem` 仅保留 3 参默认实现），故删除覆写，直接使用 tetra 默认实现。
- `ModularItemDamageEvent` 在 1.21.1 变为 `(LivingEntity, ItemStack, int amount)` 且**不再可取消**（仅可调 amount），`PlayerMixin` 相应改造；`Item#isFireResistant()` 改为 `ItemStack#has(DataComponents.FIRE_RESISTANT)`。

### 2.4 渲染（GeckoLib 4.9 + MC 1.21）
- 包名扁平化：`GeoModel` / `GeoBone` / `Color` / `DataTicket` / `RenderUtil`（原 `RenderUtils`）；
- `GeoModel#getBone` 返回 `Optional<GeoBone>`；`GeoArmorRenderer#getHeadBone()` 等改为 `getHeadBone(this.model)`；
- `Model#renderToBuffer` 的 8 参（RGBA float）改为 5 参带 `int` 颜色，统一传 `0xFFFFFFFF`；
- `HumanoidArmorLayer` 在 1.21.1 拆成 6 参 / 11 参两个 `renderArmorPiece`（`render` 调 11 参），mixin 注入 11 参重载；`usesInnerModel` 变 private，改为直接用等价判断（LEGS）；
- `CustomHeadLayerMixin` 沿用 `translateToHead` 注入点，`getParentModel()` 返回泛型 `EntityModel`，显示时强转 `HumanoidModel` 取 `head`。

### 2.5 其他
- 物品注册改 `DeferredRegister.Items` + `DeferredItem`；创造页 `event.getTabKey()` 比较 tetra 默认页。
- `ForgeConfigSpec` → `ModConfigSpec`，`ModLoadingContext` → `ModContainer`。
- `EventBusSubscriber` 改用 `net.neoforged.fml.common.EventBusSubscriber`（`FlyEffect` 走 game bus；`ClientStatBar` 走 `Bus.MOD` + `Dist.CLIENT`）。
- `ExtraHoloPage` 兼容改为**反射**调用 `net.yiran.extraholopage.api.ExtraHoloRegister`，避免对尚未移植的 #15 产生编译依赖；未安装或 API 不匹配时静默跳过（原实现为编译期直接调用）。
- 内嵌 `Tetra Material Overhaul 0.0.4` 移除，改为必需依赖 `tetra_material_overhaul >= 6.9.0`（本套 #3 移植产物）。
- mixins.json：`JAVA_17`→`JAVA_21`，去 refmap；toml 加 `[[mixins]]`。

## 3. 已知行为差异

1. 护甲耐久事件不再携带"槽位"信息（上游事件已删除该字段），因此其他监听器无法按槽位区分；伤害本身仍按物品栈正常结算。
2. `ItemColors.inherit` 染色继承仅在 JSON 显式写 `tint` 时生效（原实现同样逻辑），无新增差异。
3. ExtraHoloPage 兼容为反射实现，若其 1.21.1 版 API 名称变化则静默降级（不影响本体功能）。

## 4. 待实测项

1. 四件套 GeckoLib 模型/贴图/动画路径正确（`geo/.../<slot>.geo.json`、`textures/...`、`animations/...`）。
2. 护甲穿戴后属性生效且四件套叠加不冲突（id 槽位后缀）。
3. 受伤时四件套耐久按次掉 1 点；火焰伤害时防火物品不掉耐久。
4. `armorBelt` 骨骼仅在 LEGS 显示；飞行时播放 FLY 动画。
5. `gta:fly` 效果：穿上后获得飞行、脱下后非创造模式收回。
6. 工作台/全息仪统计栏出现飞行条目；修复配方可用。
7. 安装 ExtraHoloPage 后全息页出现四件套图标（未安装不得报错）。
