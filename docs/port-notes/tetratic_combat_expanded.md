# 移植实施记录：Tetratic Combat Expanded 2.8.3 → 1.21.1 NeoForge

工程：`d:\mod移植\2026-11-13\addons\tetratic-combat-expanded-1.21\`
产物：`build\libs\tetratic_combat_expanded-1.21-1.21.1-2.8.3.jar`（25,566 字节）
验证：`gradlew --offline clean build` BUILD SUCCESSFUL（仅 2 条 `EventBusSubscriber.bus()` 弃用警告）

---

## 1. Better Combat 版本判定（实测，非版本号推测）

判据两条，缺一不可：
- `WeaponAttributesHelper.writeToNBT` 是否存在；
- `WeaponRegistry.getAttributes(ItemStack)` 是否真的读取物品上的 `weapon_attributes` NBT（存在 API ≠ 机制可用）。

| BC 版本（1.21.1-neoforge） | writeToNBT | getAttributes 读 NBT | WeaponAttributes 构造 | Attack 构造 |
| --- | --- | --- | --- | --- |
| 2.0.0 | ✅ | ✅（`WeaponRegistry:64` 调 `readFromNBT`） | 6 参 | 8 参 |
| **2.0.4** | ✅ | ✅ | **6 参** | **8 参** |
| 2.1.0 | ✅ | ❌（`readFromNBT` 无调用方） | 7 参 | 8 参 |
| 2.1.2 / 2.1.3 | ✅ | ❌ | 7 参 | 8 参 |
| 2.2.0 | ❌（已删） | ❌ | 7 参 | 8 参 |
| 2.3.0 / 2.3.2 / 2.4.0 | ❌ | ❌ | 8 参（+trailAppearance） | 11 参 |

**结论：用户点名的 2.2.0 / 2.3.0 均无法保住旧机制（writeToNBT 于 2.2.0 删除）；2.1.x 虽有 API 但上游已不再读取 NBT，属"空壳"。真正能保住逐实例机制的最后一个 1.21.1 构建是 2.0.4，且其 `WeaponAttributes`(6 参)/`Attack`(8 参) 与原版 1.8 完全一致，业务代码零改写。**

最终依赖：Better Combat `2.0.4+1.21.1-neoforge`，版本区间 `[2.0.4,2.1.0)`。

---

## 2. 依赖落位

| 依赖 | 坐标 / 文件 | 说明 |
| --- | --- | --- |
| NeoForge | 21.1.238（MDG 2.0.141） | 与 tetra 参考工程一致，走本机 NFRT 缓存 |
| tetra | `libs/tetra-1.21.1-7.0.0.jar` | 本地源码构建产物 |
| mutil | `libs/mutil-1.21.1-7.0.0.jar` | 官方 1.21.1-7.0.0 |
| Better Combat | `libs/bettercombat-2.0.4-1.21.1-neoforge.jar` | 见 §1；运行期还要求 `playeranimator`、`cloth_config [15.0.0,)` |
| Parchment | 1.21.1 / 2024.11.17 | 同参考工程 |

---

## 3. 逐文件改动

| 文件 | 改动 | 原因 |
| --- | --- | --- |
| `TetraticCombat` | `@Mod(MODID)` + 构造注入 `ModContainer`；`modContainer.registerConfig(COMMON, spec, "…-common.toml")`；`MinecraftForge.EVENT_BUS`→`NeoForge.EVENT_BUS`；`AddReloadListenerEvent` 改 `net.neoforged.neoforge.event` | NeoForge 21.1 配置注册方式（依据 tetra `ConfigHandler:225` / `TetraMod:81`） |
| `ForgeConfigHolder` | `ForgeConfigSpec`→`ModConfigSpec`；7 个键名/默认值原样保留 | 1.21 API 迁移 |
| `ClientEventHandler` | `@Mod.EventBusSubscriber`→`net.neoforged.fml.common.EventBusSubscriber`；`Dist/OnlyIn` 包变更；`event.isCanceled()`→`instanceof ICancellableEvent` 判断；`mc.f_91074_`→`mc.player`、`f_91077_`→`mc.hitResult` | `Event#isCanceled` 在 1.21 移除；LeftClickEmpty 自身不可取消，语义等价 |
| `ItemResolver/Resolver` | NBT 读写全部改 `se.mickelus.tetra.util.ItemStackTagHelper`；写操作统一 `mutate`；`ForgeMod.ENTITY_REACH/BLOCK_REACH`→原版 `Attributes.ENTITY_INTERACTION_RANGE/BLOCK_INTERACTION_RANGE`；非模块分支 `getAttributeModifiers(slot)`→`forEachModifier(slot,…)`；`new ResourceLocation(..)`→`ResourceLocation.parse(..)` | NBT→组件、属性 API 迁移 |
| `ItemResolver/Condition` | `stack.getTag()`→`ItemStackTagHelper.getTag(stack)`（并加 null/空判断）；module/improvement 判定分支原样保留 | 同上 |
| `ItemResolver/ReloadListener` | 显式泛型 `SimplePreparableReloadListener<JSONFormat>`；`pack.listResources` 回调第 2 参 `IoSupplier<InputStream>`；保留原有 `listResources` 空转调用与 `getNamespace()` 判定 | 1.21 重载 API |
| `ItemResolver/ExpandedContainer`、`JSONFormat` | 无改动 | — |
| `mixin/PlayerMixin` | `ItemStack.equals(stack,false)`→`ItemStack.isSameItemSameComponents(...)`；其余不变 | 1.21 移除旧 equals 重载 |
| `mixin/ItemInHandRendererMixin` | NBT 读取改 `ItemStackTagHelper.getTag`；`renderItem` 7 参描述符未变 | 组件化 |
| `mixin/WorkbenchTileMixin` | 无逻辑改动；`craft(Player)` 与 `getTargetItemStack()` 在 tetra 1.21.1 仍存在（`WorkbenchTile.java:359/325`） | — |
| `neoforge.mods.toml` | 新文件；`loaderVersion="[4,)"`，依赖 `neoforge/tetra 7.0.0/bettercombat [2.0.4,2.1.0)`，AoF 仍为可选 | 1.21 元数据 |
| `pack.mcmeta` | `pack_format 15 → 48` | 1.21.1 数据包格式 |
| `tetraticcombat.mixins.json` | `JAVA_17 → JAVA_21`；删除 `refmap` 字段 | NeoForge 1.21 运行于官方名，无需 refmap |
| 数据文件 | 6 个 `data/tetra/stamina_cost/*`、2 个 `data/tetratic/configs/*` 原样复制 | 数据零改动 |

---

## 4. 降级 / 约束清单

| 项 | 标注 | 说明 |
| --- | --- | --- |
| Better Combat 版本上限 | **【上游能力变更，业务降级】** | 逐实例 `weapon_attributes` NBT 覆写自 BC 2.1.0 起被上游放弃（API 残留但无读取方），2.2.0 起彻底删除。本移植因此把依赖锁在 `[2.0.4,2.1.0)`；无法与 BC 2.1.0+ 共存。若未来必须上 2.2+，只能改走数据包预设 + `WEAPON_PRESET_ID`，届时「攻击距离 / upswing 运行时微调」将不可表达。 |
| `@EventBusSubscriber(bus = Bus.MOD)` | 保留原状（编译警告） | 原实现 modid 写的是 `"tetratic"`（与实际 modId 不符）且总线选错，注解实际不生效；真正注册来自构造函数里的游戏总线。未修改，避免改变既有行为。 |
| 玩家手持比较 | 轻微语义差异 | 原 `equals(stack,false)` 忽略耐久；新 `isSameItemSameComponents` 含 damage 组件，耐久变化也会触发一次重算（只多一次无害计算，不改变结果）。 |
| `data/tetra/stamina_cost/*` | 存疑（非降级） | tetra 1.20/1.21 源码均无消费者，实际读取方未知；数据原样保留，加载期无风险。 |
| `art_of_forging` | 存疑 | 1.21.1 未检索到可用构建；`aof.json` 19 条映射保留但不会命中。依赖仍为 optional。 |
| 运行时验证 | 未执行 | 按既定约定由用户自测；本阶段只完成构建与静态核对。 |

---

## 5. 核对结果（与原 jar 对齐）

| 挂载面 | 原 jar | 新 jar | 结果 |
| --- | --- | --- | --- |
| class | 12 | 12 | ✅ |
| 数据文件 | 8 | 8 | ✅ |
| json 总数（含 mixins） | 9（另含 refmap 1） | 9（无 refmap） | ✅ |
| 配置项 | 7 | 7 | ✅ |
| Mixin | 3 | 3 | ✅ |
| 注册项 / 网络包 / 数据组件 / assets / lang / Tooltip | 0 | 0 | ✅ |
| `net.minecraftforge` 引用 | — | 0 | ✅ |
| `ENTITY_REACH` / `BLOCK_REACH` 引用 | — | 0 | ✅ |
| `neoforge.mods.toml` 的 `[[mixins]]` | 无（原版走 MANIFEST） | **有** | ✅ 见 §6 |

Mixin 注入点核对（1.21.1 实测）：
- `Player#tick()V` 存在；
- `ItemInHandRenderer#renderItem(LivingEntity, ItemStack, ItemDisplayContext, boolean, PoseStack, MultiBufferSource, int)` 7 参未变（1.21.1 仅此一个重载，无需补描述符）；
- `WorkbenchTile#craft(Lnet/minecraft/world/entity/player/Player;)V` 存在（`remap=false` 保持）。

---

## 6. 实测回归修复（"tetra 武器无攻击动画"）

### 6.1 根因：三个 Mixin 从未加载

| 证据 | 结果 |
| --- | --- |
| 原 `2.8.3` MANIFEST | `MixinConfigs: tetraticcombat.mixins.json`（Forge 1.20 + Loom 写法） |
| 修复前我们构建的 MANIFEST | 只有 `Manifest-Version: 1.0`，无 `MixinConfigs` |
| 修复前我们的 `neoforge.mods.toml` | 无 `[[mixins]]` |
| tetra 1.21.1（可用参照） | `[[mixins]] config="tetra.mixins.json"`，其 MANIFEST 同样无 `MixinConfigs` |

→ NeoForge 1.21 只按 toml 的 `[[mixins]]` 装配 mixin，MANIFEST 的 `MixinConfigs` 不生效。3 个 mixin 全未加载 → `PlayerMixin` 从不写 `weapon_attributes` → BC 读不到属性 → 无攻击动画。

修复：`neoforge.mods.toml` 增加

```toml
[[mixins]]
config="tetraticcombat.mixins.json"
```

核对 `tetraticcombat.mixins.json`：`package` 与类实际包一致、`compatibilityLevel: JAVA_21`、`required: true`、无 `refmap`（NeoForge 1.21 运行于官方名，不需要）。

### 6.2 加固：不再依赖 BC 的 `writeToNBT` 落盘

BC 2.0.4 的 `writeToNBT` 只改 `CustomData.getUnsafe()`，**未调用 `ItemStack#set`**，是否持久/同步取决于后续是否有人把 tag 写回。改为：

```java
String encoded = WeaponAttributesHelper.encode(attributesContainer);
ItemStackTagHelper.mutate(itemStack, nbt -> nbt.putString(WeaponAttributesHelper.nbtTag, encoded));
if ((Object) itemStack instanceof ItemStackNBTWeaponAttributes attributed) {  // BC 的 mixin 接口，编译期需经 Object 转型
    attributed.setInvalidAttributes(false);
    attributed.setWeaponAttributes(null);                                     // 清缓存，避免换材料后读到旧属性
}
```

`resetBetterCombatNBT` 同步改为使用 `WeaponAttributesHelper.nbtTag` 常量并清缓存。缩放/平移的写入逻辑与数值未改动。

字节码核对：`Resolver` 已无 `writeToNBT`，只余 `WeaponAttributesHelper.encode` + `ItemStackTagHelper.mutate` 与缓存清理。

### 6.3 构建产物自检

`build\libs\tetratic_combat_expanded-1.21-1.21.1-2.8.3.jar`（25,928 字节）

| 检查项 | 结果 |
| --- | --- |
| `neoforge.mods.toml` 含 `[[mixins]] config="tetraticcombat.mixins.json"` | ✅ |
| `tetraticcombat.mixins.json` 位于 jar 根目录 | ✅ |
| MANIFEST 无 `MixinConfigs` | ✅（与 tetra 一致，避免重复装配） |
| class / 数据文件 / json | 12 / 8 / 9 ✅ |

加载后验证：`logs/latest.log` 搜 `tetraticcombat.mixins.json` 应能命中；手持 tetra 武器执行 `/data get entity @s SelectedItem`，`minecraft:custom_data` 内应出现 `weapon_attributes`。

### 6.4 遗留（未改，仅记录）

- `ClientEventHandler` 的 `@EventBusSubscriber(modid = "tetratic", bus = Bus.MOD)`：modId 与实际不符、总线选错，实际注册来自构造函数；NeoForge 下 `bus()` 已标记待删除。按要求先不动。
- `data/tetra/stamina_cost/*` 仍无已知消费者。
