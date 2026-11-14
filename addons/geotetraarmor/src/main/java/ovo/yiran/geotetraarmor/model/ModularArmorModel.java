package ovo.yiran.geotetraarmor.model;

import net.minecraft.resources.ResourceLocation;
import ovo.yiran.geotetraarmor.items.ModularArmorItem;
import software.bernie.geckolib.model.GeoModel;

public class ModularArmorModel<T extends ModularArmorItem> extends GeoModel<T> {
    private final ResourceLocation modelPath;
    private final ResourceLocation texturePath;
    private final ResourceLocation animationsPath;

    public ModularArmorModel(GeckoModelData moduleModel) {
        this.modelPath = this.buildFormattedModelPath(moduleModel.getLocation());
        this.texturePath = this.buildFormattedTexturePath(moduleModel.getLocation());
        this.animationsPath = this.buildFormattedAnimationPath(moduleModel.getLocation());
    }

    @Override
    public ResourceLocation getModelResource(T t) {
        return this.modelPath;
    }

    @Override
    public ResourceLocation getTextureResource(T t) {
        return this.texturePath;
    }

    @Override
    public ResourceLocation getAnimationResource(T t) {
        return this.animationsPath;
    }

    public ResourceLocation buildFormattedModelPath(ResourceLocation basePath) {
        return ResourceLocation.fromNamespaceAndPath(basePath.getNamespace(), "geo/" + basePath.getPath() + ".geo.json");
    }

    public ResourceLocation buildFormattedAnimationPath(ResourceLocation basePath) {
        return ResourceLocation.fromNamespaceAndPath(basePath.getNamespace(), "animations/" + basePath.getPath() + ".animation.json");
    }

    public ResourceLocation buildFormattedTexturePath(ResourceLocation basePath) {
        return ResourceLocation.fromNamespaceAndPath(basePath.getNamespace(), "textures/" + basePath.getPath() + ".png");
    }
}
