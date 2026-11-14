package com.hp.mixin;

import dev.shadowsoffire.gateways.client.NormalGateClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = NormalGateClient.class, remap = false)
public class NormalGateClientMixin {
    private static final int MAX_BAR_WIDTH = 183;

    @Redirect(method = "renderBossBar", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIIIIIII)V"))
    private static void tetra_gateways$clampBossBarWidth(GuiGraphics graphics, ResourceLocation sprite,
            int spriteWidth, int spriteHeight, int uOffset, int vOffset, int x, int y, int width, int height) {
        graphics.blitSprite(sprite, spriteWidth, spriteHeight, uOffset, vOffset, x, y,
                Math.min(width, MAX_BAR_WIDTH), height);
    }
}
