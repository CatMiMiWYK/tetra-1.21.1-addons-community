# MoreRequirement 1.21.1 NeoForge 移植说明

- 源：`MoreRequirement-1.2.2.jar`（Forge 1.20.1，34 class / 4 mixin）
- 目标：NeoForge 21.1.238 / MC 1.21.1 / Java 21 / MDG 2.0.141
- 产物：`build/libs/morerequirement-1.21-1.21.1-1.2.2.jar`（64394 字节）
- 构建：`.\gradlew --offline clean build --console=plain` → **BUILD SUCCESSFUL**

## 1. 数量核对表

| 项目 | 原 jar | 新工程 | 结论 |
|---|---|---|---|
| class 文件 | 34 | 34 | ✅（逐名比对，无缺失/多余） |
| mixins.json 条目 | 4（`mixins` 2 + `client` 2） | 4 | ✅ |
| 语言文件 | 2（en_us、zh_cn） | 2 | ✅ |
| 其他资源 | pack.mcmeta、mixins.json、mods.toml | 同左（mods.toml→neoforge.mods.toml） | ✅ |
| 未随包内容 | refmap、MANIFEST | 不带 | ✅（无 refmap） |

## 2. 主要适配点

### 2.1 工具能力类型
`net.minecraftforge.common.ToolAction` 在 1.20.5+ 被 `net.neoforged.neoforge.common.ItemAbility` 取代，因此：
- `CraftingEffectCondition#test(...)` 的 `Map<ToolAction,Integer>` → `Map<ItemAbility,Integer>`；
- `CraftingEffectOutcome#apply(...)` 除同样替换外，1.21.1 接口末尾新增 `float severity` 参数（`CustomCraftingEffectOutcome` 已同步，`Context` record 保持原字段集合）。

### 2.2 进度（Advancement）
1.20.1 的 `AdvancementList` + `AdvancementList#get` 已不存在。改为：
- 服务端：`serverPlayer.server.getAdvancements().get(id)` 取 `AdvancementHolder`，再 `serverPlayer.getAdvancements().getOrStartProgress(holder).isDone()`；
- 客户端：`Minecraft.getInstance().getConnection().getAdvancements().get(id) != null`（`ClientAdvancements`）。

### 2.3 状态效果
`MobEffect` 在 1.21 仍可经 tetra 的 `MobEffectDeserializer` 反序列化，但 `LivingEntity#getEffect` 只接受 `Holder<MobEffect>`，故用 `BuiltInRegistries.MOB_EFFECT.wrapAsHolder(effect)` 包装。

### 2.4 注册表查询
- `ForgeRegistries.ITEMS.getValue` → `BuiltInRegistries.ITEM.get`；`ItemStack#getItem()==item` → `ItemStack#is(item)`；标签 `ItemStack#is(TagKey)`。
- 实体统计改为按 `BuiltInRegistries.ENTITY_TYPE.getKey(type)` 聚合，与 JSON 中的 `ResourceLocation` 键对齐。
- 生物群系改为 `level.getBiome(pos).unwrapKey().map(ResourceKey::location)`。

### 2.5 方块/环境判定
`BlockState#isAir()`、`canOcclude()`（1.20 起 `isSolid` 更名）、`Level#getMaxBuildHeight()`、`Level#getBlockStates(AABB)`、`Level#getMoonPhase()`、`Level#getDayTime()`、`Level#isRaining/isThundering()`、`getBlockEntity(pos, type)` 均按 1.21.1 名称重写。

### 2.6 卷轴（CreativeTabHandler）
- 自定义 `ScrollDataCodec` / `IntClamp` 删除，改用 tetra 1.21.1 内置的 `ScrollData.CODEC`（`ribbon` 仍为 HexCodec，写盘前转回整数，输出格式不变）。
- 手工构造 `BlockEntityTag` 改为 `ScrollData#createItemStack()`（1.21.1 走 `TetraRegistries.scrollData` 数据组件）。
- `CreativeModeTab` 注册改为 `DeferredRegister<CreativeModeTab>` + `CreativeModeTab.builder().icon().title().displayItems()`；`BuildCreativeModeTabContentsEvent` 用 `event.getTabKey()` 与 `Registries.CREATIVE_MODE_TAB` 比较。

### 2.7 网络
1.21.1 的 mutil `AbstractPacket` 实现 `CustomPacketPayload`，必须提供 `type()`：
- `MRUpdateDataPacket` 新增 `TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("morerequirement","update_data"))` 与 `type()`；
- 包注册从 `FMLCommonSetupEvent` 改为构造器内 `NETWORK.registerPacket(...)` + `modEventBus.addListener(NETWORK::register)`（`RegisterPayloadHandlersEvent`）。

### 2.8 数据/配置/事件总线
- `ForgeConfigSpec` → `ModConfigSpec`，`ModLoadingContext#registerConfig` → `ModContainer#registerConfig`。
- `MinecraftForge.EVENT_BUS` → `NeoForge.EVENT_BUS`；`AddReloadListenerEvent` / `PlayerEvent.PlayerLoggedInEvent` / `EventPriority` 换 `net.neoforged.**` 包。
- `net.neoforged.api.distmarker.Dist/OnlyIn` 保留；客户端专属 `MyStatRegistry` 仍以 `FMLEnvironment.dist.isClient()` 守卫。
- `mods.toml` → `neoforge.mods.toml`；**原包未声明 tetra/mutil 依赖，此处补为必需 + `ordering="AFTER"`**（否则加载顺序无保证）。

### 2.9 Mixin 基础设施
- 4 个 mixin 全部保留：`TetraModMixin`（`<init>` RETURN 触发注册）、`TransformationDeserializerMixin`（HEAD 覆盖，支持 `rotationL/rotationR`）、`ConfigSchematicMixin`（`getRequirementDescription`）、`ModuleRequirementMixin`（`getDescription`）。
- mixins.json：`JAVA_17`→`JAVA_21`，去 refmap；`neoforge.mods.toml` 加 `[[mixins]]`。
- `ModuleRequirement` 的 `moduleKey/moduleVariant/materialPattern` 在 1.21.1 为包级私有，`@Shadow private` 仍可用（可见性降级合法）。

无功能删除、无等价物缺失，无降级。

## 3. 待实测项

1. 工作台/全息仪：`mr:*` 需求在 `group`/`crafting_effects` JSON 中正常反序列化并显示描述。
2. `mr:group` 分组数据经 `MRDataManager` 重载与登录同步（`MRUpdateDataPacket` 双向收发）。
3. `mr:custom` 的 `CraftingEffectOutcome` 在合成后按 `key` 调用注册函数。
4. `mr:advancement` 在单人（服务端路径）与客户端提示一致。
5. `mr:potion` 对 1.21 效果 ID 正常匹配。
6. 创造模式页：`CreateSingleCreativeTab=true` 时生成独立「Scroll Items」页；`false` 时并入 tetra 默认页。
7. 统计排序 `mr:item` 在全息仪排序列表中可用。
