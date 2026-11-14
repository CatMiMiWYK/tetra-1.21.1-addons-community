package ovo.yiran.geotetraarmor.core.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import ovo.yiran.geotetraarmor.items.ModularArmorItem;
import ovo.yiran.geotetraarmor.model.ModularGeoArmorRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = CustomHeadLayer.class, remap = false)
public abstract class CustomHeadLayerMixin<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {
    public CustomHeadLayerMixin(RenderLayerParent<T, M> renderer) {
        super(renderer);
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/layers/CustomHeadLayer;translateToHead(Lcom/mojang/blaze3d/vertex/PoseStack;Z)V"))
    private void t$render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T livingEntity, float limbSwing, float limbSwingAmount,
            float partialTick, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        ItemStack itemStack = livingEntity.getItemBySlot(EquipmentSlot.HEAD);
        if (itemStack.getItem() instanceof ModularArmorItem armorItem && armorItem.getEquipmentSlot() == EquipmentSlot.HEAD) {
            for (ModularGeoArmorRenderer renderer : armorItem.geoArmorRenderers(itemStack, livingEntity)) {
                poseStack.pushPose();
                renderer.prepForRender(livingEntity, itemStack, EquipmentSlot.HEAD, null);
                net.minecraft.client.model.HumanoidModel<?> parentModel = (net.minecraft.client.model.HumanoidModel<?>) this.getParentModel();
                renderer.applyBoneVisibilityByPart(EquipmentSlot.HEAD, parentModel.head, parentModel);
                CustomHeadLayer.translateToHead(poseStack, false);
                renderer.renderToBuffer(poseStack,
                        buffer.getBuffer(net.minecraft.client.renderer.RenderType.armorCutoutNoCull(renderer.getGeoModel().getTextureResource(armorItem))),
                        packedLight, net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);
                poseStack.popPose();
            }
        }
    }
}
