package smartin.tetraticcombat.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import se.mickelus.tetra.util.ItemStackTagHelper;
import smartin.tetraticcombat.ForgeConfigHolder;

@Mixin(ItemInHandRenderer.class)
public class ItemInHandRendererMixin {
    @Inject(at = @At("HEAD"), method = "renderItem", require = 1)
    private void renderArmWithItem(LivingEntity entity, ItemStack itemstack, ItemDisplayContext displayContext, boolean handSide, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, CallbackInfo ci) {
        if (!ForgeConfigHolder.COMMON.enableRescale.get()) {
            return;
        }
        CompoundTag tag = ItemStackTagHelper.getTag(itemstack);
        if (tag == null) {
            return;
        }
        float xScale = tag.contains("tetraticScaleX") ? tag.getFloat("tetraticScaleX") : 1.0f;
        float yScale = tag.contains("tetraticScaleY") ? tag.getFloat("tetraticScaleY") : 1.0f;
        float zScale = tag.contains("tetraticScaleZ") ? tag.getFloat("tetraticScaleZ") : 1.0f;
        double xTranslate = tag.contains("tetraticTranslateX") ? (double) tag.getFloat("tetraticTranslateX") : 0.0;
        double yTranslate = tag.contains("tetraticTranslateY") ? (double) tag.getFloat("tetraticTranslateY") : 0.0;
        double zTranslate = tag.contains("tetraticTranslateZ") ? (double) tag.getFloat("tetraticTranslateZ") : 0.0;
        poseStack.scale(xScale, yScale, zScale);
        poseStack.translate(xTranslate, yTranslate, zTranslate);
    }
}
