package ovo.yiran.geotetraarmor.model;

import net.minecraft.resources.ResourceLocation;
import se.mickelus.mutil.gui.SimpleColor;
import se.mickelus.tetra.items.modular.ItemColors;
import se.mickelus.tetra.module.Priority;
import se.mickelus.tetra.module.data.MaterialData;
import se.mickelus.tetra.module.model.IModuleModel;

import java.util.Arrays;
import java.util.List;

/**
 * tetra 7.0 起模块模型改为注册表驱动的 {@link IModuleModel}，原先的 ModuleModel 已移除。
 * 此类对应原 JSON 中 "type": "gecko" 的模型，由 GeoTetraArmor 在主类中注册到 ModuleModelRegistry。
 */
public class GeckoModelData implements IModuleModel {
    public static final ResourceLocation TYPE = ResourceLocation.fromNamespaceAndPath("geotetraarmor", "gecko");

    private String type = "gecko";
    private ResourceLocation location;
    private String textureSuffix;
    private SimpleColor tint = new SimpleColor(0xffffffff);
    private SimpleColor overlayTint;
    private Priority renderLayer = Priority.BASE;

    public ResourceLocation getLocation() {
        // 1.21.1 移植修复：模块数据里的 location 以 "/" 结尾（如 item/armor/heavy/），
        // 需要拼接纹理名（base/left/right）才能定位到实际的 .geo.json / .png，否则 GeckoLib 会报 Unable to find model
        if (this.textureSuffix != null && this.location.getPath().endsWith("/")) {
            return ResourceLocation.fromNamespaceAndPath(this.location.getNamespace(), this.location.getPath() + this.textureSuffix);
        }
        return this.location;
    }

    public SimpleColor getTint() {
        return this.tint;
    }

    public int getTintRaw() {
        return this.tint.getRaw();
    }

    @Override
    public ResourceLocation getType() {
        return TYPE;
    }

    @Override
    public Priority getRenderLayer() {
        return this.renderLayer;
    }

    @Override
    public IModuleModel forMaterial(List<String> availableTextures, MaterialData material) {
        // 对齐 tetra 的 GridTextureModelData#forMaterial（与 1.20.1 原版 geotetraarmor 的模型选择一致）：
        // 先按 material.textures 的顺序挑出 availableTextures 里存在的纹理，找不到才退回 availableTextures[0]；
        // 同时把模型底色改为材料色，避免盔甲渲染成默认白色。
        if (availableTextures == null || availableTextures.isEmpty()) {
            return this;
        }
        if (material.textureOverrides != null && material.textures != null && material.textures.length > 0
                && Arrays.stream(material.textureOverrides).anyMatch(override -> this.location.getPath().equals(override))) {
            GeckoModelData copy = this.copy();
            copy.textureSuffix = material.textures[0];
            copy.tint = material.tintOverrides ? new SimpleColor(material.tints.texture) : new SimpleColor(0xffffffff);
            copy.overlayTint = new SimpleColor(material.tints.texture);
            return copy;
        }
        String texture = material.textures == null ? availableTextures.get(0) : Arrays.stream(material.textures)
                .filter(availableTextures::contains)
                .findFirst()
                .orElse(availableTextures.get(0));
        GeckoModelData copy = this.copy();
        copy.textureSuffix = texture;
        copy.tint = new SimpleColor(material.tints.texture);
        copy.overlayTint = new SimpleColor(material.tints.texture);
        return copy;
    }

    @Override
    public IModuleModel withSlotSuffix(String suffix) {
        GeckoModelData copy = this.copy();
        copy.location = ResourceLocation.fromNamespaceAndPath(this.location.getNamespace(), this.location.getPath() + suffix);
        return copy;
    }

    @Override
    public IModuleModel inheritTint(SimpleColor parentTint) {
        if (this.tint.getRaw() == ItemColors.inherit) {
            GeckoModelData copy = this.copy();
            copy.tint = parentTint;
            return copy;
        }
        return this;
    }

    @Override
    public SimpleColor getOverlayTint() {
        return this.overlayTint != null ? this.overlayTint : this.tint;
    }

    public GeckoModelData copy() {
        GeckoModelData copy = new GeckoModelData();
        copy.type = this.type;
        copy.location = this.location;
        copy.textureSuffix = this.textureSuffix;
        copy.tint = this.tint;
        copy.overlayTint = this.overlayTint;
        copy.renderLayer = this.renderLayer;
        return copy;
    }
}
