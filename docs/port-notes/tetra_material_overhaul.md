# tetra_material_overhaul 移植记录（1.21.1 NeoForge）

- 原 jar：`Tetra Material Overhaul-6.9.0-1.0.1.jar`（13 KB，10 class，1 mixin 配置含 5 条）
- 源码：无公开仓库（ARR），**反编译**（CFR），逻辑完整可读，无存疑处。
- 新 jar：`build\libs\tetra_material_overhaul-1.21-1.21.1-6.9.0-1.0.1.jar`

## 挂载项对照

| 项 | 原 jar | 新 jar |
| --- | --- | --- |
| class | 10（8 源文件 + 2 内部类） | 10 |
| mixin 配置 | 1（5 条目：MaterialDataMixin、$DeserializerMixin、MaterialImprovementDataMixin、MaterialMultiplierMixin、MaterialVariantDataMixin） | 同 |
| 数据 / 配置 / 注册项 / 网络包 / 组件 / assets | 0 | 0 |

## 兼容性分类

| # | 原实现 | 1.21.1 | 判定 |
| --- | --- | --- | --- |
| 1 | `@Mod(value="tetra_material_overhaul")`（forge 包） | `net.neoforged.fml.common.Mod` | ⚠️ 包名 |
| 2 | `MaterialData.copyFields(MaterialData, MaterialData)` + `@At("RETURN")` | 1.21 仍为 public static（`MaterialData.java:102`） | ✅ |
| 3 | `MaterialData.Deserializer#deserialize(...)` `LocalCapture.CAPTURE_FAILHARD` | 方法仍在，局部变量顺序仍为 `jsonObject`(JsonObject)、`data`(MaterialData)（`MaterialData.java:233-235`） | ✅ |
| 4 | `MaterialVariantData#combine(MaterialData)` 返回 `ImprovementData` | 1.21 返回类型改为 **`VariantData`**（`MaterialVariantData.java:21`），mixin 回调泛型同步改为 `CallbackInfoReturnable<VariantData>` | ⚠️ 签名变更（已适配） |
| 5 | `MaterialImprovementData#combine(MaterialData)` 返回 `ImprovementData` | 不变 | ✅ |
| 6 | `extract` 字段（MaterialMultiplier）、`MaterialMultiplier` 可被 mixin 注入字段 | 不变 | ✅ |
| 7 | `EffectData.multiply(EffectData, float, float)` / `merge(Collection)` | 不变（`EffectData.java:33/59`） | ✅ |
| 8 | `AttributeHelper.multiplyModifiers(Multimap, double)` / `collapseRound` / `merge(Collection<Multimap>)` | 不变 | ✅ |
| 9 | `ArrayList<Object> attributeList`（反编译产物） | 必须显式泛型 `ArrayList<Multimap<Attribute, AttributeModifier>>` 才能通过 1.21 编译 | ⚠️ 编译修正（无语义变化） |

无业务降级。

## 备注

- 原 jar 靠 MANIFEST `MixinConfigs`；新 jar 改为 toml `[[mixins]]`，mixins.json 去掉 refmap、`JAVA_21`。
- 原元数据未声明 tetra 依赖；新元数据补 `tetra [7.0.0,)`（正确性修复）。
