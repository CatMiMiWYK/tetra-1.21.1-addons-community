# Art of Forging 1.20.1 → 1.21.1 / NeoForge 21.1.238 移植记录

## 1. 源信息

| 项目 | 值 |
| --- | --- |
| 源 jar | `reference/work/addons/artofforging.jar` |
| 源版本 | `1.8.4-1.20.1`（Forge 1.20.1） |
| 目标版本 | `1.21.1-1.8.4`（Minecraft 1.21.1 / NeoForge 21.1.238 / Java 21） |
| modId | `art_of_forging`（未变） |
| 包名 | `net.acetheeldritchking.art_of_forging`（未变） |
| 工程 | `d:\mod移植\2026-11-13\addons\art_of_forging` |
| 产物 | `build\libs\art_of_forging-1.21.1-1.8.4.jar` |
| 构建 | `.\gradlew build --offline --console=plain`（JDK 21.0.12） |

构建按流水线要求处理：复制 `geotetraarmor\build\moddev\artifacts`，强制
`net.neoforged:accesstransformers:10.0.1`，禁用 `createMinecraftArtifacts`，
`processResources` 使用 `replaceProperties`（含 `version`、`loader_version_range`）。

## 2. 数量核对

### 2.1 class / mixin

| 项目 | 原 jar | 新 jar | 差异 | 原因 |
| --- | --- | --- | --- | --- |
| `.class` 总数 | 83 | 77 | -6 | Forge Capability 被 NeoForge 1.21 数据附件（AttachmentType）取代；6 个 provider 内的匿名 `CapabilityToken` 内部类不再存在 |
| 源 `.java` / 顶层类 | 75 | 75 | 0 | 全部保留，公开类名与包名未变 |
| Mixin 类 | 2 | 2 | 0 | `ItemModularHandheldAccessor`、`ItemModularHandheldMixin` |
| `refmap.json` | 1 | 0 | -1 | 按要求去除；Mixin 目标 `remap=false` |
| 匿名/内部类 | 8 | 2 | -6 | 同上（仅剩 `ArtOfForging$ClientModEvents` 等） |

### 2.2 assets（文件数）

| 目录 | 原 | 新 | 说明 |
| --- | --- | --- | --- |
| `assets/art_of_forging/blockstates` | 3 | 3 | 原样 |
| `assets/art_of_forging/lang` | 35 | 35 | 原样 |
| `assets/art_of_forging/models` | 41 | 41 | 其中 `mark_of_the_architect.json` 的 `forge:` 命名空间改为 `neoforge:` |
| `assets/art_of_forging/sounds.json` | 1 | 1 | 原样 |
| `assets/art_of_forging/sounds` | 1 | 1 | 原样 |
| `assets/art_of_forging/textures` | 189 | 189 | 原样（含 5 个 `.png.mcmeta` 动画） |
| `assets/minecraft/texts` | 1 | 1 | 原样 |
| `assets/tetra/textures` | 41 | 41 | 原样（模块贴图、`aof_glyph.png`） |
| **合计** | **270** | **270** | 文件数一致 |

### 2.3 data（文件数）

| 目录 | 原 | 新 | 说明 |
| --- | --- | --- | --- |
| `data/art_of_forging/advancements` → `advancement` | 8 | 8 | 目录单数化 |
| `data/art_of_forging/loot_tables` → `loot_table` | 9 | 9 | 目录单数化 |
| `data/art_of_forging/recipes` → `recipe` | 23 | 23 | 目录单数化 + result 格式迁移 |
| `data/art_of_forging/loot_modifiers` | 9 | 9 | `forge:loot_table_id` → `neoforge:loot_table_id` |
| `data/art_of_forging/tags/entity_types` → `tags/entity_type` | 1 | 1 | 目录单数化 |
| `data/tetra/advancements` → `advancement` | 40 | 40 | 目录单数化 |
| `data/tetra/improvements` | 44 | 44 | 原样（schema 与 tetra 7.0.0 一致） |
| `data/tetra/materials` | 22 | 22 | 原样（schema 一致） |
| `data/tetra/modules` | 52 | 52 | 19 个文件的 `forge:swords` → `minecraft:swords` |
| `data/tetra/recipes` → `recipe` | 8 | 8 | 目录单数化 + scroll result 改为组件 |
| `data/tetra/repairs` | 6 | 6 | 原样 |
| `data/tetra/replacements` | 1 | 1 | 原样 |
| `data/tetra/schematics` | 140 | 140 | 原样 |
| `data/tetra/synergies` | 5 | 5 | 原样 |
| `data/tetra/tags/items` → `tags/item` | 1 | 1 | 目录单数化 |
| `data/forge/...` → `data/neoforge/...` | 1 | 1 | 全局 loot modifier 路径 |
| `data/curios/tags/items` → `tags/item` | 1 | 1 | 目录单数化 |
| `data/bettercombat` | 2 | 2 | 原样（数据格式未静态验证） |
| `data/tetratic` | 1 | 1 | 原样（数据格式未静态验证） |
| **合计** | **374** | **374** | 文件数一致 |

### 2.4 其它资源

| 文件 | 原 | 新 | 说明 |
| --- | --- | --- | --- |
| `META-INF/mods.toml` | 1 | 0 | 改为 `META-INF/neoforge.mods.toml` |
| `META-INF/neoforge.mods.toml` | 0 | 1 | 新增 `[[mixins]]`，版本由 Gradle 展开 |
| `art_of_forging.mixins.json` | 1 | 1 | 去 refmap、`JAVA_21`、包名 |
| `pack.mcmeta` | 1 | 1 | `pack_format` 15 → 34 |
| `pack.png` | 1 | 1 | 原样 |

## 3. 数据格式迁移（逐类）

1. **原版配方（23 个 `data/art_of_forging/recipe/*.json`）**
   - `"result": "id"` → `"result": {"id": "id"}`；
   - `"result": {"item": "id", "count": n}` → `"result": {"id": "id", "count": n}`；
   - 其余 `type`（crafting_shaped/shapeless/smelting/blasting/smithing_transform）与 ingredient `{"item": ...}` / `{"tag": ...}` 在 1.21.1 仍有效。
2. **Tetra 卷轴配方（8 个 `data/tetra/recipe/*_scroll.json`）**
   - 1.20.1 的 result NBT（`nbt.BlockEntityTag.data[0]`）改为 1.21.1 数据组件：
     `result.components."tetra:scroll_data"`，字段名与 tetra 7.0.0 `ScrollData.CODEC` 完全一致（key/details/intricate/material/ribbon/glyphs/schematics/effects）。
3. **Loot modifier（9 个）**：条件 `forge:loot_table_id` → `neoforge:loot_table_id`；序列化器 id 仍为 `art_of_forging:add_to_table`。
4. **全局 loot modifier（1 个）**：`data/forge/loot_modifiers/global_loot_modifiers.json` → `data/neoforge/loot_modifiers/global_loot_modifiers.json`。
5. **Tetra 模块（19 个文件）**：`forge:swords` → `minecraft:swords`（与 tetra 7.0.0 自身模块一致）。
6. **目录单数化**：`advancements`→`advancement`、`recipes`→`recipe`、`loot_tables`→`loot_table`、`tags/entity_types`→`tags/entity_type`、`tags/items`→`tags/item`。
7. **物品模型**：`mark_of_the_architect.json` 的 `forge:item/default` → `neoforge:item/default`、`forge:separate_transforms` → `neoforge:separate_transforms`。
8. **pack.mcmeta**：`pack_format` 15 → 34。

## 4. Mixin 逐条清单（已用 javap 对照 tetra 7.0.0 字节码）

| Mixin | 目标 | 注入/访问 | 核对结果 |
| --- | --- | --- | --- |
| `mixins.tetra.ItemModularHandheldAccessor` | `se.mickelus.tetra.items.modular.ItemModularHandheld` | `@Accessor("abilities")` 静态 getter + `@Mutable @Accessor` 静态 setter，类型 `ChargedAbilityEffect[]` | ✅ tetra 7.0.0 中字段 `abilities` 存在且为 `ChargedAbilityEffect[]` |
| `mixins.tetra.ItemModularHandheldMixin` | 同上 | `@Inject(method="<clinit>", at=@At("RETURN"))` 调用 `EffectGuiStats.setupAbilites()` | ✅ 类存在静态初始化器，字段在 `<clinit>` 中赋值 |

- `art_of_forging.mixins.json`：`compatibilityLevel: "JAVA_21"`，无 `refmap`；
- 两个 Mixin 均 `remap = false`（tetra 目标不是原版映射类）；
- `neoforge.mods.toml` 已声明 `[[mixins]] config="art_of_forging.mixins.json"`。

## 5. 兼容性统计（按 75 个源 `.java`）

| 等级 | 数量 | 含义 |
| --- | --- | --- |
| ✅ | 56 | API 直接等价，行为保持 |
| ⚠️ | 19 | 已编译通过，但存在 API 替代、推断值或无法静态验证的运行时行为 |
| ❌ | 0 | 无功能删除 |

⚠️ 明细：

1. `ArtOfForging`：Forge 构造器 → `(IEventBus, ModContainer)`；`tetraScroll` 由 `RegistryObject` 改为运行时 `BuiltInRegistries.ITEM.get`；新增 `CuriosApi.registerCurio`（1.21 需显式注册）。
2. `events.AoFEvents`：Forge Capability 事件（`AttachCapabilitiesEvent`/`PlayerEvent.Clone`/`RegisterCapabilitiesEvent`）整体替换为 NeoForge 数据附件 DeferredRegister。
3–8. `capabilities.*.PlayerDevouring/PlayerCarnage/PlayerSoulCharge/PlayerKarma/PlayerConquer/PlayerSubjugation`：实现 `INBTSerializable<CompoundTag>`（带 `HolderLookup.Provider` 参数），NBT 键保持不变，`copyOnDeath()` 保留死亡复制语义。
9–14. `capabilities.*.*Provider`：由 `Capability<T>` 改为 `Supplier<AttachmentType<T>>` 别名，字段名保持不变；调用点改为 `player.getData(...)`。
15. `effects.SoulChargedEffect`：soul_fire_d 6.1.0 已把 `FireManager` 移到传递依赖 Prometheus（`it.crystalnest.prometheus.api.FireManager`，未提供编译 jar）。改为：先 `setRemainingFireTicks(seconds*20)`，再通过反射调用 Prometheus 的 `FireTypeChanger#setFireType(FireRegistry.SOUL_FIRE_TYPE)`；若反射失败则仅保留普通火焰 tick。未伪造 stub。
16. `effects.LifeStealEffect`：音效 `SoundEvents.f_12557_` 在 1.20.1 映射中无法离线确认，采用语义最接近的 `PLAYER_ATTACK_CRIT`（仅音效差异）。
17. `effects.CarnageEffect`：`f_12363_` → `PLAYER_ATTACK_STRONG`（推断）。
18. `effects.RetributionEffect`：`f_12511_` → `PLAYER_HURT`（推断），并用 `LivingIncomingDamageEvent` 替代已删除的 `LivingHurtEvent`。
19. `effects.SubjugationEffect`：`f_12557_` → `PLAYER_ATTACK_CRIT`（推断，与 LifeSteal 同源 SRG）。

其余要点（不单独计入 ⚠️ 的机械替换）：
- `ToolAction`→`ItemAbility` 未在本附属直接使用；tetra 模块数据仍使用 `tools` 字段（与 tetra 7.0.0 一致）。
- `ForgeRegistries.ENTITY_TYPES.tags()` → `EntityType#is(TagKey)`。
- `LivingHurtEvent`/`LivingAttackEvent`（NeoForge 已删除）→ `LivingIncomingDamageEvent`；`LivingDamageEvent` → `LivingDamageEvent.Pre`（`getNewDamage/setNewDamage`）。
- `TickEvent.PlayerTickEvent` → `net.neoforged.neoforge.event.tick.PlayerTickEvent`，`event.player` → `event.getEntity()`。
- `Item#appendHoverText` 使用 `Item.TooltipContext`。
- `MobEffects` 官方名：`STRENGTH`→`DAMAGE_BOOST`、`HASTE`→`DIG_SPEED`、`NAUSEA`→`CONFUSION`、`SLOWNESS`→`MOVEMENT_SLOWDOWN`、`INSTANT_DAMAGE`→`HARM`、`RESISTANCE`→`DAMAGE_RESISTANCE`。
- `MobType.UNDEAD` 已移除 → `LivingEntity#isInvertedHealAndHarm()`。
- `AttributeModifier` 使用 `ResourceLocation` id 与 `ADD_MULTIPLIED_TOTAL`。
- Curios 9.5.1：`CuriosApi.getCuriosInventory(...).findCurios(...)`，`SlotResult#stack()`。
- `IGlobalLootModifier#codec()` 返回 `MapCodec`；LootTable 通过 `ReloadableServerRegistries.Holder#getLootTable(ResourceKey)` 获取。
- 网络包：mutil `AbstractPacket` 补 `CustomPacketPayload.Type` 与 `type()`；tracking 发送改用 `PacketDistributor.sendToPlayersTrackingEntity`。

## 6. 依赖声明

`META-INF/neoforge.mods.toml`：

| modId | 类型 | 版本范围 | 顺序 |
| --- | --- | --- | --- |
| `neoforge` | required | `[21.1.238,)` | NONE |
| `minecraft` | required | `[1.21.1,1.22)` | NONE |
| `tetra` | required | `[7.0.0,)` | AFTER |
| `mutil` | required | `[7.0.0,)` | AFTER |
| `soul_fire_d` | required | `[6.1.0,)` | AFTER |

编译期 `libs/`：`tetra-1.21.1-7.0.0.jar`、`mutil-1.21.1-7.0.0.jar`、
`soul-fire-d-neoforge-1.21-6.1.0.jar`、`curios-neoforge-9.5.1+1.21.1.jar`。

说明：
- curios 与原版一样未写入 `mods.toml` 依赖（原 jar 也没有）；但代码直接引用 Curios API，实际运行仍需要 Curios。
- soul_fire_d 6.1.0 运行时还需要 Prometheus 与 Cobweb（其自身依赖），本次未提供编译 jar；唯一调用点见第 5 节第 15 条。

## 7. 无法静态验证的部分

1. **Tetra 7.0.0 数据 schema**：modules / improvements / materials / schematics / repairs / replacements / synergies 沿用原 JSON，仅做命名空间与目录迁移；已抽样对照 tetra 7.0.0 自带 JSON（module、material、scroll recipe、advancement），字段一致，但 500+ 文件未逐项运行验证。
2. **BetterCombat 数据**（2 个 `weapon_attributes`）与 **Tetratic 配置**（1 个）：未提供对应 1.21.1 mod jar，格式按原样保留。
3. **音效 SRG 常量**：`f_12557_`、`f_12363_`、`f_12511_` 无法离线解析（1.20.1 mcp_config 下载未完成），采用语义近似音效，仅影响声音。
4. **Mixin 运行时**：`<clinit>` 注入与静态 `@Accessor` 已按字节码核对，但未启动游戏做运行时验证。
5. **Soul fire 反射路径**：Prometheus 的 `FireTypeChanger#setFireType` 依据上游源码核对；未提供 jar，无法编译期验证。
6. **配方/战利品表加载**：全部 JSON 已通过语法校验，未运行 `/reload` 验证。

## 8. 汉化补充

- 新增 `src/main/resources/assets/art_of_forging/lang/zh_cn.json`（UTF-8 无 BOM，2 空格缩进，键顺序与 `en_us.json` 完全一致）。
- 键数：`en_us.json` 901 个键，`zh_cn.json` 901 个键；`ConvertFrom-Json` 校验键集合完全一致、顺序一致，无增无减。
- 未翻译残留：0。全部值均为简体中文；`%s`/`%d`/`%1$s` 等格式占位符与 `§` 颜色代码逐一比对与原文件一致；`item.tetra.scroll.*.details` 中的 `\n`、`\r` 转义按原样保留。
- 已离线重建 `build/libs/art_of_forging-1.21.1-1.8.4.jar`（BUILD SUCCESSFUL，约 523 KB），jar 内已含 `assets/art_of_forging/lang/zh_cn.json`（80478 字节）。
- 部署：新 jar 已覆盖 `deploy-mods/art_of_forging-1.21.1-1.8.4.jar`；覆盖前的旧 jar 备份至 `deploy-mods/backup-20261114-lang/`（`backup-20261114` 下已有同名备份，故另建 `-lang` 目录）。
- 未改动 `en_us.json` 任何键名、未修改 Java 代码、未触碰其它模组工程与实例 mods 目录。
