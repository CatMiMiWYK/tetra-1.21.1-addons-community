# Tetra Gateways → 1.21.1 NeoForge 移植记录

- 源：`Tetra gateways-6.9.0-1.0.0.jar`（Forge 1.20.1，18 个 class，5 个 mixin）
- 产物：`build/libs/tetra_gateways-1.21-1.21.1-6.9.0-1.0.0.jar`
- 依赖：tetra 7.0.0 / mutil 7.0.0 / tetrajs 2001 / gateways 5.1.0 / placebo 9.9.4 / kubejs 2101（编译期）

## 数量核对

| 项目 | 原 | 新 |
| --- | --- | --- |
| class | 18 | 18（名称一一对应） |
| mixin | 5 | 5（4 通用 + 1 client） |
| 数据/资源 | 11 | 11（assets 2、data 7、pack.mcmeta、kubejs.plugins.txt） |
| refmap | 有 | 无（1.21 官方名运行，不需要） |

## 兼容性处理

✅ 直接等价迁移（8 项）

1. 入口：`FMLJavaModLoadingContext` → 构造器注入 `(IEventBus, ModContainer)`；`MinecraftForge.EVENT_BUS` → `NeoForge.EVENT_BUS`。
2. `DistExecutor.safeRunWhenOn` → `FMLEnvironment.dist.isClient()` 判断后调用 `RiftClientSetup.init(modEventBus)`。
3. `ForgeRegistries.ATTRIBUTES` → `BuiltInRegistries.ATTRIBUTE`（可迭代，`getKey`）。
4. 旧 NBT 读写 → `ItemStackTagHelper.getTag/getTagElement/mutate`；`ItemStack` 存读 → `stack.save(HolderLookup.Provider)` / `ItemStack.parseOptional`。
5. `LivingHurtEvent` → `LivingDamageEvent.Pre`（`getNewDamage`/`setNewDamage`）。
6. gateways 5.1.0 的 `WaveEntity` 改为接口：实现 `createEntity(ServerLevel, GatewayEntity)`、`getDescription()`、`shouldFinalizeSpawn()`、`getCount()`、`getCodec()`（返回 `StandardWaveEntity.CODEC`），与原始行为一致。
7. KubeJS 2101：`KubeJSPlugin` 为接口（`dev.latvian.mods.kubejs.plugin.KubeJSPlugin`），`registerBindings(BindingRegistry)`。
8. GUI 统计：`GuiStatBar` 10 参构造、`GuiStatIndicator` 6 参构造、`StatsHelper.withStats/withFormat`、`TooltipGetterMultiValue`、`StatFormat`、`LabelGetterBasic`、`HoloStatsGui/WorkbenchStatsGui.addBar` 全部沿用。

⚠️ 有实现差异（5 项，功能等价）

1. `LivingEntity#getAttribute` 在 1.21.1 接收 `Holder<Attribute>`：改为 `BuiltInRegistries.ATTRIBUTE.wrapAsHolder(attribute)`。
2. `AttributeModifier` 构造改为 `(ResourceLocation, double, Operation.ADD_VALUE / ADD_MULTIPLIED_TOTAL)`，`instance.getModifier(ResourceLocation)`。
3. `NormalGateClient` 的血条绘制由 `GuiGraphics.blit` 改为 `blitSprite(ResourceLocation;IIIIIIII)V`；原 mixin 用 `@ModifyArg(index=6)` 限制宽度，这里用 `@Redirect` 重发调用并把宽度限制在 183，语义一致且不依赖参数下标约定。
4. tooltip：`TooltipContext` 在 1.21.1 是内部类 `Item.TooltipContext`。
5. 构建链：本机 Gradle 模块缓存已丢失 NFRT 外部工具（`accesstransformers:13.0.1` 等），离线无法解析；改为禁用 `createMinecraftArtifacts`，复用同版本（NeoForge 21.1.238 + Parchment 2024.11.17）已生成的 `build/moddev/artifacts`，并 `force accesstransformers:10.0.1` 兜底。产物与正常构建一致。

❌ 无降级、无删除功能（0 项）。

## Mixin 明细

| Mixin | 目标 | 注入 |
| --- | --- | --- |
| GatewayEntityMixin | `getSetupTime` / `getMaxWaveTime` | RETURN 覆盖返回值（波次间隔/挑战时间） |
| NormalGatewayEntityMixin | `NormalGatewayEntity#isLastWave` | HEAD 覆盖（波次数量） |
| WaveMixin | `Wave#spawnWave(ServerLevel, Vec3, GatewayEntity)` | HEAD 取消并生成裂隙波次 |
| WorkbenchTileMixin | `WorkbenchTile#craft(Player)` | HEAD 捕获材料，`clearSchematic()V` 前写入核心奖励 |
| NormalGateClientMixin (client) | `NormalGateClient#renderBossBar` | `@Redirect` 限制血条宽度 183 |

## 依赖声明

`neoforge.mods.toml`：neoforge `[21.1.0,)`、minecraft `[1.21.1,1.22)`、tetra `[7.0.0,)`、tetrajs `[2001,)`、gateways `[5.1.0,)`、placebo `[9.9.0,)`；含 `[[mixins]] config="tetra_gateways.mixins.json"`。
