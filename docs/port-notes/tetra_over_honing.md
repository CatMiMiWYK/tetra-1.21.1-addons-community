# TetraOverHoning 1.21.1 NeoForge 移植说明

- 源：`TetraOverHoning-6.9.0-1.0.1-fix.jar`（Forge 1.20.1，7 class / 3 mixin）
- 目标：NeoForge 21.1.238 / MC 1.21.1 / Java 21 / MDG 2.0.141
- 产物：`build/libs/tetra_over_honing-1.21-1.21.1-6.9.0-1.0.1-fix.jar`（13188 字节）
- 构建：`.\gradlew --offline clean build --console=plain` → **BUILD SUCCESSFUL**

## 1. 数量核对表

| 项目 | 原 jar | 新工程 | 结论 |
|---|---|---|---|
| class 文件 | 7（含内部类 `IModularItemWrapperMixin$Client`） | 7 | ✅ |
| mixins.json 条目 | 3（`mixins` 2 + `client` 1） | 3 | ✅ |
| 语言文件 | 2（en_us、zh_cn） | 2 | ✅ |
| 其他资源 | icon.png、pack.mcmeta、mixins.json、mods.toml | 同左（mods.toml→neoforge.mods.toml） | ✅ |
| 未随包内容 | refmap.json、MANIFEST | 不带 | ✅（无 refmap） |

## 2. 适配点

1. **配置**：`ForgeConfigSpec` → `net.neoforged.neoforge.common.ModConfigSpec`；由主类构造注入 `ModContainer` 后 `registerConfig(ModConfig.Type.COMMON, Config.SPEC)`（原 `ModLoadingContext.get().registerConfig`）。`Config` 上的 `@Mod.EventBusSubscriber` 已移除（原类无事件处理方法，纯占位）。
2. **主类**：`@Mod` 换 `net.neoforged.fml.common.Mod`；构造签名 `(IEventBus, ModContainer)`；客户端处理器改用 `FMLEnvironment.dist.isClient()` 判断后 `modEventBus.register(...)`（原 `FMLJavaModLoadingContext`）。
3. **NBT**：`ItemStack#getOrCreateTag()`（`m_41784_`）已移除 → `ItemStackTagHelper.getTag/mutate`（tetra 1.21.1 自带工具），`over_honing` 键名不变。
4. **注入点重锚**（关键）：原 `IModularItemWrapperMixin` 注入在 `tickHoningProgression` 内 `ItemStack.getOrCreateTag()` 的 INVOKE 处；1.21.1 的 `IModularItemWrapper.tickHoningProgression` 在 `isHoneable` 为真时**提前 return**（1.20.1 是先取 tag 再判断），该 INVOKE 在“已可打磨”状态下不再可达。故改为 `@Inject(at = HEAD)`，并显式补回原注入点之前的两项前置条件（`ConfigHandler.moduleProgression`、`canGainHoneProgress`），再以 `isHoneable` 作为累计条件 —— 与原控制流等价。
5. **客户端 Tooltip Mixin**：`IModularItemWrapper.Client#addHoningTooltip` 1.21.1 签名与实现未变，`@At("RETURN")` 保持不变；组件 API 换 `Component.literal/translatable`。
6. **统计条**：`GuiStatBar` 10 参构造在 1.21.1 仍存在（内部转调 12 参）；`StatGetterEffectLevel(ItemEffect)`、`LabelGetterBasic.integerLabel`、`TooltipGetterInteger(String, IStatGetter)`、`WorkbenchStatsGui.addBar` / `HoloStatsGui.addBar` 均未变。
7. **元数据**：`mods.toml`→`neoforge.mods.toml`，`forge`→`neoforge`，`minecraft` 区间 `[1.21.1,1.22)`，`tetra_modify_core` 区间由 `6.9.0-1.0.4` 改 `[6.9.0,)`，新增 `[[mixins]]`；mixins.json `JAVA_17`→`JAVA_21`、删 refmap。

无功能降级，无删除特性。

## 3. 待实测项

1. 可打磨物品继续打磨：`over_honing` 逐步累计（每次 +multiplier）。
2. 工作台合成结束时：按转换率把 `over_honing` 转为打磨进度（`amount = min(getHoningProgress, over_honing*conversion)`），并回扣 `over_honing`。
3. Tooltip：`conversionAmount > 10` 时出现 `> 过量打磨: X -> Y (Z%)` 行，颜色随可打磨状态区分。
4. `over_honing:conversion` effect 提升转换率、上限受 `MaxConversion` 限制。
5. 工作台/全息仪统计栏出现「高效转换」条。
