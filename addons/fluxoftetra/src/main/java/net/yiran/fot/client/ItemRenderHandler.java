package net.yiran.fot.client;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Optional;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.client.event.RegisterItemDecorationsEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.yiran.fot.common.TetraFEStore;
import net.yiran.fot.util.FEUtil;
import se.mickelus.tetra.items.modular.IModularItem;

public class ItemRenderHandler {
    public static void onItemTooltip(ItemTooltipEvent event) {
        if (event.getFlags().isAdvanced()) {
            FEUtil.getTetraFEStore(event.getItemStack()).ifPresent(store -> {
                if (store.getMaxEnergyStored() > 0) {
                    event.getToolTip().add(1, Component.literal(I18n.get("tooltip.fot.fe.format", formatInt(store.getEnergyStored()), formatInt(store.getMaxEnergyStored()))));
                }
            });
        }
    }

    public static void onRegisterItemDecorations(RegisterItemDecorationsEvent event) {
        for (Item item : BuiltInRegistries.ITEM) {
            if (!(item instanceof IModularItem)) {
                continue;
            }
            event.register((ItemLike) item, ItemRenderHandler::drawBar);
        }
    }

    public static boolean drawBar(GuiGraphics guiGraphics, Font font, ItemStack itemStack, int x, int y) {
        Optional<TetraFEStore> feStore = FEUtil.getTetraFEStore(itemStack);
        if (feStore.isEmpty()) {
            return false;
        }
        if (feStore.get().getMaxEnergyStored() <= 0) {
            return false;
        }
        PoseStack pose = guiGraphics.pose();
        pose.pushPose();
        pose.translate((float) (x + 2), (float) (y + 12), 200.0f);
        if (itemStack.getDamageValue() == 0) {
            guiGraphics.fillGradient(0, 0, 13, 2, 0, -16777216);
        } else {
            guiGraphics.fillGradient(0, 0, 13, 1, 0, -16777216);
        }
        guiGraphics.fillGradient(0, 0, 13 * feStore.get().getEnergyStored() / feStore.get().getMaxEnergyStored(), 1, 0, -65536);
        pose.popPose();
        return true;
    }

    public static String formatInt(int value) {
        int divisor;
        String suffix;
        if (value < 0) {
            return "-" + formatInt(-value);
        }
        if (value < 1000) {
            return String.valueOf(value);
        }
        if (value >= 1000000000) {
            suffix = "G";
            divisor = 100000000;
        } else if (value >= 1000000) {
            suffix = "M";
            divisor = 100000;
        } else {
            suffix = "K";
            divisor = 100;
        }
        int scaled = value / divisor;
        int whole = scaled / 10;
        int fraction = scaled % 10;
        if (fraction == 0) {
            return whole + suffix;
        }
        return whole + "." + fraction + suffix;
    }
}
