# tetra_attribute_rebalancing 移植记录（1.21.1 NeoForge）

- 原 jar：`Tetra Attribute Rebalancing-6.9.0-1.0.0-PrecisionFix.jar`（7 KB，3 class，1 mixin）
- 源码：无公开仓库（ARR），**反编译**（CFR + javap），仅 3 个类，无存疑逻辑。
- 新 jar：`build\libs\tetra_attribute_rebalancing-1.21-1.21.1-6.9.0-1.0.0-PrecisionFix.jar`

## 挂载项对照

| 项 | 原 jar | 新 jar |
| --- | --- | --- |
| class | 3 | 3 |
| mixin | 1（`AttributeHelperMixin`，目标 tetra `AttributeHelper`） | 1 |
| 配置项 / 数据 / 注册项 / 网络包 / 组件 / assets | 0 | 0 |

## 兼容性分类

| # | 原实现 | 1.21.1 | 判定 |
| --- | --- | --- | --- |
| 1 | `@Mixin(AttributeHelper.class)` 目标 `collapse/getAdditionAmount/getMultiplyAmount` | tetra 1.21 仍有这三个方法（`AttributeHelper.java:133/162/176`） | ✅ |
| 2 | `AttributeModifier#m_22217_`（getOperation）/`m_22218_`（getAmount） | 记录类访问器改官方名 `operation()` / `amount()` | ⚠️ 接口变更 |
| 3 | `AttributeModifier.Operation.ADDITION/MULTIPLY_BASE/MULTIPLY_TOTAL` | 1.21 改名 `ADD_VALUE` / `ADD_MULTIPLIED_BASE` / `ADD_MULTIPLIED_TOTAL` | ⚠️ 枚举改名 |
| 4 | `new AttributeModifier(String id, double, Operation)` | 1.21 构造改 `AttributeModifier(ResourceLocation, double, Operation)`；原字符串 id `"tetra.stats.addition"` 等改为 `tetra:stats/addition` 等合法 id | ⚠️ 签名变更（id 仅作标识，不影响数值） |
| 5 | `@ModifyVariable(... name="multiplier")` 作用于 `round(Attribute, AttributeModifier)` | 1.21 的 `round` 局部变量名为 **`rounding`**（`AttributeHelper.java:233-236`），且类型为 `double` | ⚠️ 目标变量改名（已同步改名，语义不变：取整因子 1000.0） |
| 6 | `@Mod` 类无行为 | 同 | ✅ |

无业务降级。

## 备注

- 原 jar 靠 MANIFEST `MixinConfigs` 装配；新 jar 改为 `neoforge.mods.toml` 的 `[[mixins]]`（NeoForge 1.21 口径）。
- 依赖仅 tetra（本地 1.21.1-7.0.0）+ mutil（编译期需要）。
