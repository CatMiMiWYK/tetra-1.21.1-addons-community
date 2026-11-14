package ovo.yiran.geotetraarmor.core.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import ovo.yiran.geotetraarmor.items.ModularArmorItem;
import ovo.yiran.geotetraarmor.model.ModularGeoArmorRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = HumanoidArmorLayer.class, remap = false)
public abstract class HumanoidArmorLayerMixin<T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>> extends RenderLayer<T, M> {
    public HumanoidArmorLayerMixin(RenderLayerParent<T, M> renderer) {
        super(renderer);
    }

    @Shadow
    protected abstract void setPartVisibility(A model, EquipmentSlot slot);

    // 1.21.1 有两个 renderArmorPiece 重载：6 参版本会转发到带 6 个 float 的版本，
    // render() 直接调用带 float 的版本，因此必须用完整描述符锁定带 float 的目标。
    @Inject(method = "renderArmorPiece(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/EquipmentSlot;ILnet/minecraft/client/model/HumanoidModel;FFFFFF)V", at = @At(value = "HEAD"))
    private void t$renderArmorPiece(PoseStack poseStack, MultiBufferSource buffer, T livingEntity, EquipmentSlot slot, int packedLight, A model,
            float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        ItemStack itemStack = livingEntity.getItemBySlot(slot);
        if (itemStack.getItem() instanceof ModularArmorItem armorItem && armorItem.getEquipmentSlot() == slot) {
            for (ModularGeoArmorRenderer renderer : armorItem.geoArmorRenderers(itemStack, livingEntity)) {
                this.getParentModel().copyPropertiesTo(model);
                this.setPartVisibility(model, slot);
                renderer.prepForRender(livingEntity, itemStack, slot, model);
                renderer.renderToBuffer(poseStack,
                        buffer.getBuffer(RenderType.armorCutoutNoCull(renderer.getGeoModel().getTextureResource(armorItem))),
                        packedLight, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);
            }
        }
    }
}
