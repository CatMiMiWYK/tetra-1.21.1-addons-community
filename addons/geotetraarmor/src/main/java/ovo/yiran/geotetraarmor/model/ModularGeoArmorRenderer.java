package ovo.yiran.geotetraarmor.model;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.EquipmentSlot;
import ovo.yiran.geotetraarmor.items.ModularArmorItem;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.util.Color;
import software.bernie.geckolib.util.RenderUtil;

import javax.annotation.Nullable;
import java.util.Objects;

public class ModularGeoArmorRenderer extends GeoArmorRenderer<ModularArmorItem> {
    public Color color;
    protected GeoBone belt = null;

    public ModularGeoArmorRenderer(GeckoModelData moduleModel) {
        super(new ModularArmorModel<>(moduleModel));
        this.color = new Color(moduleModel.getTintRaw() & 0xFFFFFF | 0xFF000000);
    }

    @Override
    public Color getRenderColor(ModularArmorItem animatable, float partialTick, int packedLight) {
        return this.color;
    }

    @Nullable
    public GeoBone getBeltBone() {
        return this.model.getBone("armorBelt").orElse(null);
    }

    @Override
    protected void grabRelevantBones(BakedGeoModel bakedModel) {
        if (this.lastModel != bakedModel) {
            this.lastModel = bakedModel;
            this.head = this.getHeadBone(this.model);
            this.body = this.getBodyBone(this.model);
            this.rightArm = this.getRightArmBone(this.model);
            this.leftArm = this.getLeftArmBone(this.model);
            this.rightLeg = this.getRightLegBone(this.model);
            this.leftLeg = this.getLeftLegBone(this.model);
            this.rightBoot = this.getRightBootBone(this.model);
            this.leftBoot = this.getLeftBootBone(this.model);
            this.belt = this.getBeltBone();
        }
    }

    @Override
    public void applyBoneVisibilityByPart(EquipmentSlot currentSlot, ModelPart currentPart, HumanoidModel<?> model) {
        this.setAllVisible(false);
        currentPart.skipDraw = true;
        GeoBone bone = null;
        if (currentPart != model.head && currentPart != model.hat) {
            if (currentPart == model.body) {
                bone = this.body;
                if (this.belt != null) {
                    this.belt.setHidden(false);
                }
            } else if (currentPart == model.leftArm) {
                bone = this.leftArm;
            } else if (currentPart == model.rightArm) {
                bone = this.rightArm;
            } else if (currentPart == model.leftLeg) {
                bone = currentSlot == EquipmentSlot.FEET ? this.leftBoot : this.leftLeg;
            } else if (currentPart == model.rightLeg) {
                bone = currentSlot == EquipmentSlot.FEET ? this.rightBoot : this.rightLeg;
            }
        } else {
            bone = this.head;
        }
        if (bone != null) {
            bone.setHidden(false);
        }
    }

    @Override
    protected void applyBaseTransformations(HumanoidModel<?> baseModel) {
        if (this.head != null) {
            ModelPart headPart = baseModel.head;
            RenderUtil.matchModelPartRot(headPart, this.head);
            this.head.updatePosition(headPart.x, -headPart.y, headPart.z);
        }
        if (this.body != null) {
            ModelPart bodyPart = baseModel.body;
            RenderUtil.matchModelPartRot(bodyPart, this.body);
            this.body.updatePosition(bodyPart.x, -bodyPart.y, bodyPart.z);
            if (this.belt != null) {
                RenderUtil.matchModelPartRot(bodyPart, this.belt);
                this.belt.updatePosition(bodyPart.x, -bodyPart.y, bodyPart.z);
            }
        }
        if (this.rightArm != null) {
            ModelPart rightArmPart = baseModel.rightArm;
            RenderUtil.matchModelPartRot(rightArmPart, this.rightArm);
            this.rightArm.updatePosition(rightArmPart.x + 5.0f, 2.0f - rightArmPart.y, rightArmPart.z);
        }
        if (this.leftArm != null) {
            ModelPart leftArmPart = baseModel.leftArm;
            RenderUtil.matchModelPartRot(leftArmPart, this.leftArm);
            this.leftArm.updatePosition(leftArmPart.x - 5.0f, 2.0f - leftArmPart.y, leftArmPart.z);
        }
        if (this.rightLeg != null) {
            ModelPart rightLegPart = baseModel.rightLeg;
            RenderUtil.matchModelPartRot(rightLegPart, this.rightLeg);
            this.rightLeg.updatePosition(rightLegPart.x + 2.0f, 12.0f - rightLegPart.y, rightLegPart.z);
            if (this.rightBoot != null) {
                RenderUtil.matchModelPartRot(rightLegPart, this.rightBoot);
                this.rightBoot.updatePosition(rightLegPart.x + 2.0f, 12.0f - rightLegPart.y, rightLegPart.z);
            }
        }
        if (this.leftLeg != null) {
            ModelPart leftLegPart = baseModel.leftLeg;
            RenderUtil.matchModelPartRot(leftLegPart, this.leftLeg);
            this.leftLeg.updatePosition(leftLegPart.x - 2.0f, 12.0f - leftLegPart.y, leftLegPart.z);
            if (this.leftBoot != null) {
                RenderUtil.matchModelPartRot(leftLegPart, this.leftBoot);
                this.leftBoot.updatePosition(leftLegPart.x - 2.0f, 12.0f - leftLegPart.y, leftLegPart.z);
            }
        }
    }

    @Override
    protected void applyBoneVisibilityBySlot(EquipmentSlot currentSlot) {
        super.applyBoneVisibilityBySlot(currentSlot);
        if (Objects.requireNonNull(currentSlot) == EquipmentSlot.LEGS) {
            this.setBoneVisible(this.belt, true);
        }
    }

    @Override
    protected void setAllBonesVisible(boolean visible) {
        super.setAllBonesVisible(visible);
        this.setBoneVisible(this.belt, visible);
    }
}
