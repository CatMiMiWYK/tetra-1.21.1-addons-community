# 移植记录：FluxOfTetra 6.9.0-1.0.3-fix → 1.21.1 NeoForge

源 jar：`d:\mod移植\FluxOfTetra-6.9.0-1.0.3-fix.jar`（无公开源码，CFR 反编译为基准）
工程：`d:\mod移植\2026-11-13\addons\fluxoftetra\`
产物：`build\libs\fluxoftetra-1.21-1.21.1-6.9.0-1.0.3-fix.jar`（19,791 字节）
构建：`gradlew --offline clean build` BUILD SUCCESSFUL

## 1. 数量核对

| 项 | 原 jar | 新 jar |
|---|---|---|
| class | 12 | 11（见 ⚠️1） |
| 语言文件 | 2 | 2 |
| tetra 材料数据 | 2（rod/lightning_rod、socket/end_crystal） | 2 |
| icon.png / pack.mcmeta | 1 / 1 | 1 / 1 |
| 配置项 | 2（ForgeEnergy2Durability、NotEnergyItems） | 2 |
| 物品能力注册 | 全部 IModularItem 的 FE 存储 | 同（改为注册期枚举） |
| mixin / 网络包 / 方块 | 0 / 0 / 0 | 0 / 0 / 0 |

## 2. 逐项结论

### ⚠️ 核心：Forge 能力系统 → NeoForge Capability API
1.21 的 NeoForge 已经完全移除 `ICapabilityProvider`/`ICapabilitySerializable`/`LazyOptional`/`AttachCapabilitiesEvent`/`INBTSerializable`：
- `AttachCapability`：`forgeBus.addGenericListener(ItemStack.class, ...)` → 在 mod 总线监听 `RegisterCapabilitiesEvent`，遍历 `BuiltInRegistries.ITEM` 把所有 `IModularItem` 逐个 `registerItem(Capabilities.EnergyStorage.ITEM, provider, item)`。`ForgeRegistries.ITEMS` → `BuiltInRegistries.ITEM`。
- `TetraFEProvider` **删除**：新 API 的 provider 只是一个函数式接口（`ICapabilityProvider<ItemStack,Void,IEnergyStorage>`），不再承载 `serializeNBT`，包装类没有存在意义；缓存判断与原 `LazyOptional` 分支逻辑并入注册 lambda（不通过时返回 null 表示无能力）。**这是结构性简化，不是功能删除**。
- `FEUtil`：`stack.getCapability(ForgeCapabilities.ENERGY)` → `stack.getCapability(Capabilities.EnergyStorage.ITEM)`。
- `TetraFEStore`：仅实现 `IEnergyStorage`；由于新 API 没有自动序列化钩子，改为**每次 `setEnergyStored` 立即持久化**（`ItemStackTagHelper.mutate` 写入 `FOT$energy:{energy:int}`，键名与格式与原版一致，旧存档可读）。构造时用 `ItemStackTagHelper.getOrCreateTag` 读取（副本，只读安全）。

### ⚠️ 入口/事件（4）
- `FluxOfTetra`：`FMLJavaModLoadingContext` → 构造注入 `IEventBus`+`ModContainer`；`registerConfig(COMMON)` 走 `ModContainer`；`MinecraftForge.EVENT_BUS` → `NeoForge.EVENT_BUS`，`DamageHook::onModularDamage` 仍以 `EventPriority.LOWEST` 注册。
- `Config`：`ForgeConfigSpec` → `ModConfigSpec`（`defineList` 签名一致，编译有 1 条弃用提示，不影响功能）。
- `FluxOfTetraClient`：保持「mod 总线注册装饰器/客户端 setup，forge 总线注册 tooltip」的划分，仅换 `NeoForge.EVENT_BUS`。
- `ItemRenderHandler`：`ForgeRegistries.ITEMS` → `BuiltInRegistries.ITEM`；`RegisterItemDecorationsEvent` 包名改 NeoForge；`GuiGraphics.m_280168_().m_85836_/m_252880_/m_85849_` → `pose().pushPose()/translate()/popPose()`；`m_41773_` → `getDamageValue()`；`ItemTooltipEvent.getFlags().m_7050_()` → `isAdvanced()`。
- `FEItemEffectBars` / `TooltipGetterFormatInt`：仅换 import（`FMLClientSetupEvent` 包名），`WorkbenchStatsGui.addBar` / `HoloStatsGui.addBar` / `GuiStatBar` 12 参构造 / `StatGetterEffectLevel` / `LabelGetterBasic.integerLabel` 在 1.21.1 全部同名同签名。

### ✅ 数据（2）
两份 tetra 材料 JSON 无 `attributes` 字段（1.21 属性 id 改名不影响），`effects` 的 `fot:fe_store` 与 `requiredTools.hammer_dig` 在 1.21.1 tetra 中键名不变，原样保留。

### ❌（0）
无功能删除。

## 3. 依赖

`tetra [7.0.0,)`、`mutil [7.0.0,)` required（原 toml 只写了 `tetra 6.9.0`，且缺失 mutil，已按 1.21.1 修正）。

## 4. 待实测

1. 给 tetra 工具充能（外部 FE 机器/物品）后，耐久条上方是否出现红色能量条。
2. 高级 tooltip（F3+H）是否显示 `x / y FE`。
3. 使用时按 `ForgeEnergy2Durability`（默认 320 FE = 1 耐久）抵扣耐久，且能量消耗后重进世界仍保留（NBT 落盘）。
4. `NotEnergyItems` 配置排除某些物品时不附加能力。
