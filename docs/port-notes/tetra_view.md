# 移植记录：Tetra View 6.9.0-1.0.2 → 1.21.1 NeoForge

源 jar：`d:\mod移植\Tetra View-6.9.0-1.0.2.jar`（19,912 字节，无公开源码，CFR 反编译为基准）
工程：`d:\mod移植\2026-11-13\addons\tetra_view\`
产物：`build\libs\tetra_view-1.21-1.21.1-6.9.0-1.0.2.jar`（19,306 字节）
构建：`gradlew --offline clean build` BUILD SUCCESSFUL

## 1. 数量核对

| 项 | 原 jar | 新 jar |
|---|---|---|
| class | 8（含 2 个匿名内部类） | 8 |
| 语言文件 | 2（en_us / zh_cn） | 2 |
| 贴图 | 4（progress/slot/slot1/slot2） | 4 |
| icon.png | 1 | 1 |
| pack.mcmeta | 1 | 1 |
| mixin | 0 | 0 |
| 注册项 | 2 KeyMapping + 1 client config | 2 KeyMapping + 1 client config |
| 数据文件 / 网络包 | 0 / 0 | 0 / 0 |

## 2. 逐项结论

### ✅（3）
- `KeyMappings`：`KeyMapping(String,int,String)` 构造在 1.21.1 仍存在，键位与分类键不变。
- `KeyMappingUtil`：`InputConstants.isKeyDown` / `getTranslatedKeyMessage` / `getKeyModifier().isActive(getKeyConflictContext())` 在 NeoForge 1.21.1 均有对应实现（IKeyMappingExtension）。
- `Config`：`ForgeConfigSpec` → `net.neoforged.neoforge.common.ModConfigSpec`，`Builder#define` 语义一致。

### ⚠️（4）
- `TetraView`（入口）：`FMLJavaModLoadingContext.get().registerConfig(...)` → 构造注入 `ModContainer` 后 `modContainer.registerConfig(ModConfig.Type.CLIENT, Config.SPEC)`；`RegisterKeyMappingsEvent` 包名改为 `net.neoforged.neoforge.client.event`。原类里未使用的 `LOGGER` 字段未保留。
- `Handler`（事件）：
  - `TickEvent.ClientTickEvent(phase)` → 拆成 `ClientTickEvent.Pre` / `ClientTickEvent.Post` 两个监听（Pre 处理 `consumeClick()`，Post 复位 `isSelected`）。
  - `ScreenEvent.KeyPressed.Pre`、`ItemTooltipEvent` 改为 NeoForge 包路径；`MinecraftForge.EVENT_BUS` → `NeoForge.EVENT_BUS`；`Screen.m_96638_` 经 1.20.1 官方映射核实为 `hasShiftDown()`。
- `ViewScreen`（界面）：`Screen` 方法全部改为 1.21.1 签名 —— `init()`、`render(GuiGraphics,int,int,float)`、`mouseClicked/mouseDragged`、`onClose`、`keyPressed`；`renderBackground` 由单参改为四参（`fillGradient` 重绘半透明遮罩）；`GuiGraphics.renderTooltip` 需走 `Optional<TooltipComponent>` 重载；`ResourceLocation` 改 `fromNamespaceAndPath`；`SoundEvents.UI_BUTTON_CLICK` 由 Supplier 变 Holder（`.value()`）。
- NBT → 数据组件（copy / give 两个按钮）：
  - `ItemStack#save(CompoundTag)` 在 1.21.1 已移除，改为 `stack.save(level.registryAccess(), tag)`，产物为 `{id,count,components}` 的 SNBT。
  - copy 仍生成 `/setblock ~ ~1 ~ minecraft:pink_shulker_box[facing=up]{Items:[...]}` 并写入剪贴板（`minecraft.keyboardHandler.setClipboard`，1.20.1 经 SRG/官方映射核实 `f_91068_`=keyboardHandler、`m_90911_`=setClipboard）。
  - give 从剪贴板读取（`keyboardHandler.getClipboard()`），解析出物品 SNBT、去掉 `Slot` 后发 `give @p {id:...,count:...,components:{...}}`。**注意**：1.21 的物品参数支持该 SNBT 形式，但未实机验证；若不支持，玩家只会收到命令语法错误提示，不会崩客户端。原实现拼接的是 `tag` 子标签，1.21 无此字段，故改用整段 SNBT。
- 统计栏反射：`WorkbenchStatsGui.staticBars` / `bars` 在 1.21.1 仍为同名字段（`private static final List<GuiStatBase>` / `private static List<GuiStatBase>`），反射逻辑不变；`GuiStatBase.shouldShow/update` 五参签名一致；`GuiStatBase.setAlignment` 为抽象方法，`realignBar` 可直接调用。

### ❌（0）
无功能删除。

## 3. 依赖

- 仅 `tetra [7.0.0,)` required（原 mods.toml 未声明前置，补齐）；mutil 随 tetra 提供。
- 纯客户端界面/按键，服务端无内容。

## 4. 待实测

1. 按键 V/C 在 1.21.1 是否与其它模组冲突（分类 `tetra.binding.group`）。
2. copy → give 链路（SNBT 物品参数）。
3. 统计栏在 1.21.1 的排布/对齐外观。
