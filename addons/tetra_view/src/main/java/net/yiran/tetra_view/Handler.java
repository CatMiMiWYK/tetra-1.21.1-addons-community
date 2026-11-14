package net.yiran.tetra_view;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.yiran.tetra_view.screen.ViewScreen;
import se.mickelus.tetra.items.modular.IModularItem;

public class Handler {
    public boolean isSelected = false;
    public ItemStack viewItem;

    public static void init() {
        NeoForge.EVENT_BUS.register(new Handler());
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onItemTooltip(ItemTooltipEvent event) {
        this.viewItem = event.getItemStack();
        if (!(this.viewItem.getItem() instanceof IModularItem)) {
            return;
        }
        this.isSelected = true;
        if (!Config.EnableTooltip.get()) {
            return;
        }
        if (ViewScreen.FromItem.isEmpty()) {
            event.getToolTip().add(1, Component.translatable("tetra_view.store", KeyMappingUtil.getKeyMappingName(KeyMappings.STORE)));
        } else if (this.viewItem.equals(ViewScreen.FromItem)) {
            event.getToolTip().add(1, Component.translatable("tetra_view.same"));
        } else {
            Component name = ViewScreen.FromItem.getHoverName();
            event.getToolTip().add(1, Component.translatable("tetra_view.tooltip", KeyMappingUtil.getKeyMappingName(KeyMappings.VIEW), name));
        }
    }

    @SubscribeEvent
    public void onKeyPressInGui(ScreenEvent.KeyPressed.Pre event) {
        if (!this.isSelected) {
            return;
        }
        InputConstants.Key key = InputConstants.getKey(event.getKeyCode(), event.getScanCode());
        if (KeyMappings.VIEW.isActiveAndMatches(key)) {
            this.openGui(this.viewItem);
        } else if (KeyMappings.STORE.isActiveAndMatches(key) && !Screen.hasShiftDown()) {
            ViewScreen.FromItem = this.viewItem.getItem() instanceof IModularItem
                    ? (ViewScreen.FromItem.equals(this.viewItem) ? ItemStack.EMPTY : this.viewItem)
                    : ItemStack.EMPTY;
        }
    }

    @SubscribeEvent
    public void onClientTickPre(ClientTickEvent.Pre event) {
        if (KeyMappings.VIEW.consumeClick()) {
            Minecraft mc = Minecraft.getInstance();
            this.openGui(mc.player.getMainHandItem());
        }
    }

    @SubscribeEvent
    public void onClientTickPost(ClientTickEvent.Post event) {
        this.isSelected = false;
    }

    public void openGui(ItemStack itemStack) {
        if (itemStack == null || !(itemStack.getItem() instanceof IModularItem)) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        mc.setScreen(new ViewScreen(itemStack, mc.screen));
    }
}
