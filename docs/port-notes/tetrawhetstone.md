# 移植记录：tetrawhetstone 1.1.0 → 1.21.1 NeoForge

源 jar：`d:\mod移植\tetrawhetstone-1.1.0.jar`（18,564 字节，无公开源码，CFR 反编译为基准）
工程：`d:\mod移植\2026-11-13\addons\tetrawhetstone\`
产物：`build\libs\tetrawhetstone-1.21-1.21.1-1.1.0.jar`（15,818 字节）
构建：`gradlew --offline clean build` BUILD SUCCESSFUL

## 1. 数量核对

| 项 | 原 jar | 新 jar |
|---|---|---|
| class | 4（含 ClientModEvents 内部类） | 4 |
| 物品注册 | 2（whetstone / overwhetstone） | 2 |
| 创造模式标签页 | 1（whetstone_tab） | 1 |
| 语言文件 | 2 | 2 |
| 物品模型 / 贴图 | 2 / 2 | 2 / 2 |
| tetra schematic | 4（whetstone / overwhetstone / hpsettled / overhpsettled） | 4 |
| tetra improvement | 1 | 1 |
| 配方 | 2 | 2 |
| mixin | 0（原 mixins.json 为空） | 0 |
| pack.mcmeta | 1 | 1 |

原 jar 里 `assets/tetrawhetstone/models/item/desktop.ini` 是 Windows 系统垃圾文件，未保留。

## 2. 逐项结论

### ✅（2）
- `WhetStoneUse`：`IModularItem.getModuleFromSlot` / `setHoningProgress` / `updateIdentifier(static)`、`ItemModuleMajor.addImprovement/removeImprovement/getImprovementLevel`、`UpgradeSchematic.getKey` 在 1.21.1 签名全部一致；`inventory.add` / `player.drop(stack,false)` 亦一致。
- tetra 数据（4 schematic + 1 improvement）格式与 1.21.1 tetra 自带数据一致（`materialSlotCount`/`materialRevealSlot`/`rarity:temporary`/`displayType:improvement`/`glyph.textureX|Y`），未改动。

### ⚠️（4）
- `TetraWhetstone`（注册）：`DeferredRegister.create(ForgeRegistries.ITEMS,...)` + `RegistryObject` → `DeferredRegister.createItems(MODID)` + `DeferredItem`；创造页 `CreativeModeTab.builder()` 的 `title` 之外，`withTabsBefore(CreativeModeTabs.COMBAT)`、`icon`、`displayItems`、`build` 一一对应。原 `f_256797_`/`f_256968_` 经 1.20.1 官方映射核实为 `COMBAT`（前置页）与 `INGREDIENTS`（投放页）。
- `@Mod.EventBusSubscriber(bus=MOD)` → NeoForge 顶层 `@EventBusSubscriber(modid, value=Dist.CLIENT)`（1.21 起该注解固定挂 mod 总线，`bus` 参数已移除）；`Minecraft.getInstance().getUser().getName()` 替代 `m_91094_().m_92546_()`。
- 事件总线：`MinecraftForge.EVENT_BUS` → `NeoForge.EVENT_BUS`；`BuildCreativeModeTabContentsEvent` / `ServerStartingEvent` 包路径改为 `net.neoforged.neoforge.event`。
- `OverWhetstoneUse`（NBT→数据组件）：`upgradedStack.getOrCreateTag().putInt/contains/getInt` 全部改为 tetra 的 `ItemStackTagHelper` —— 读取用 `getOrCreateTag`（返回副本），写入统一走 `mutate(stack, tag -> ...)`，否则写不落盘。逻辑与数值（`over_hone`=9、`honing_available`）保持不变。
- 注册方式微调：两个事件处理类由「静态方法 + register(Class)」改为「实例方法 + register(new ...)」，避免对事件总线 Class 注册支持的依赖，行为一致。

### ⚠️ 数据：配方格式（已改，必须项）
- `result.item` 在 1.20.5+ 已改名 `result.id`，两份配方均已改为 `{"id": "...", "count": n}`；原料 `{"item": "..."}` 形式在 1.21.1 仍有效。
- 原 jar 两份配方文件名与产物交叉（`tetrawhetstone.json` 产出 overwhetstone、`overtetrawhetstone.json` 产出 whetstone），属原作者写法，原样保留。

### ❌（0）
无功能删除。

## 3. 依赖

`tetra [7.0.0,)`、`mutil [7.0.0,)`、`tetra_addition [1.0,)` 均 required/AFTER（原 toml 已声明前三项，版本区间按 1.21.1 本地构建调整）。

## 4. 待实测

1. 磨刀石/强化磨刀石配方与创造页位置。
2. 用 whetstone 修复「settled」后返还 3 个磨刀石、用 overwhetstone 后 `over_hone` 计数递减（9→…）是否生效。
