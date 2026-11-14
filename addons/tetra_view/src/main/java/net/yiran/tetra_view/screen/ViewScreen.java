package net.yiran.tetra_view.screen;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import java.awt.Color;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import se.mickelus.mutil.gui.GuiAlignment;
import se.mickelus.mutil.gui.GuiAttachment;
import se.mickelus.mutil.gui.GuiElement;
import se.mickelus.mutil.gui.GuiItem;
import se.mickelus.mutil.gui.GuiTexture;
import se.mickelus.tetra.blocks.workbench.gui.GuiButtonOutlined;
import se.mickelus.tetra.blocks.workbench.gui.WorkbenchStatsGui;
import se.mickelus.tetra.gui.stats.bar.GuiStatBarTool;
import se.mickelus.tetra.gui.stats.bar.GuiStatBase;

public class ViewScreen
        extends Screen {
    public static ItemStack FromItem = ItemStack.EMPTY;
    public ItemStack stack;
    public ItemStack to;
    public Screen lastScreen;
    public GuiElement statsBarGroup;
    public GuiElement statsToolBarGroup;
    public GuiElement statsCommonBarGroup;
    public GuiElement statsItemGroup;
    public double xOffset;
    public double yOffset;

    public ViewScreen(ItemStack stack, Screen lastScreen) {
        super(Component.literal(""));
        Minecraft mc = Minecraft.getInstance();
        this.statsBarGroup = new GuiElement(0, 0, 0, 0);
        this.statsToolBarGroup = new GuiElement(0, 0, 0, 0);
        this.statsBarGroup.addChild(this.statsToolBarGroup);
        this.statsCommonBarGroup = new GuiElement(0, 0, 0, 0);
        this.statsBarGroup.addChild(this.statsCommonBarGroup);
        this.statsItemGroup = new GuiElement(0, 0, 0, 0);
        this.statsBarGroup.addChild(this.statsItemGroup);
        this.init(mc, mc.getWindow().getGuiScaledWidth(), mc.getWindow().getGuiScaledHeight());
        if (FromItem.isEmpty()) {
            this.stack = stack;
            this.to = FromItem;
        } else {
            this.stack = FromItem;
            this.to = stack;
        }
        this.lastScreen = lastScreen;
        this.updateBars();
        this.xOffset = 0.0;
        this.yOffset = 0.0;
    }

    @Override
    protected void init() {
        super.init();
        this.statsBarGroup.setX(this.width / 2);
        this.statsBarGroup.setY(this.height / 2);
    }

    void updateBars() {
        GuiTexture gt = new GuiTexture(-9, -9, 34, 34, ResourceLocation.fromNamespaceAndPath("tetra_view", "textures/slot1.png"));
        gt.setSpriteSize(34, 34);
        if (FromItem.isEmpty() || this.stack.equals(this.to)) {
            GuiItem item = new GuiItem(-8, -8);
            item.setItem(this.stack);
            item.addChild(gt);
            this.statsItemGroup.addChild(new GuiButtonOutlined(17, -6, I18n.get("tetra_view.copy"), () -> {
                CompoundTag tag = new CompoundTag();
                tag.putByte("Slot", (byte) 13);
                this.stack.save(this.minecraft.level.registryAccess(), tag);
                this.minecraft.keyboardHandler.setClipboard("/setblock ~ ~1 ~ minecraft:pink_shulker_box[facing=up]{Items:[" + tag + "]}");
                this.minecraft.player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 0.2f, 1.0f);
            }) {

                @Override
                public List<Component> getTooltipLines() {
                    return this.hasFocus() ? Arrays.asList(Component.translatable("tetra_view.copy.tooltip")) : null;
                }
            });
            String label = I18n.get("tetra_view.give");
            this.statsItemGroup.addChild(new GuiButtonOutlined(-27 - this.font.width(label) - 8, -6, label, () -> {
                String str = this.minecraft.keyboardHandler.getClipboard();
                if (str.startsWith("/setblock ~ ~1 ~ minecraft:pink_shulker_box[facing=up]{Items:[")) {
                    if (str.endsWith("\n")) {
                        str = str.substring(0, str.length() - 1);
                    }
                    try {
                        CompoundTag tag = TagParser.parseTag(str.substring(62, str.length() - 2));
                        tag.remove("Slot");
                        this.minecraft.player.connection.sendCommand("give @p " + tag);
                    } catch (CommandSyntaxException e) {
                        LogUtils.getLogger().error("Failed to parse item tag", e);
                    }
                } else {
                    this.minecraft.player.displayClientMessage(Component.translatable("tetra_view.failed"), false);
                }
                this.minecraft.player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 0.2f, 1.0f);
            }) {

                @Override
                public List<Component> getTooltipLines() {
                    return this.hasFocus() ? Arrays.asList(Component.translatable("tetra_view.give.tooltip")) : null;
                }
            });
            this.statsItemGroup.addChild(item);
        } else {
            GuiTexture pr = new GuiTexture(-25, -10, 50, 20, ResourceLocation.fromNamespaceAndPath("tetra_view", "textures/progress.png"));
            pr.setSpriteSize(50, 20);
            this.statsItemGroup.addChild(pr);
            GuiItem item = new GuiItem(-31, -8);
            item.setItem(this.stack);
            this.statsItemGroup.addChild(item);
            item.addChild(gt);
            GuiItem item2 = new GuiItem(15, -8);
            item2.setItem(this.to);
            item2.addChild(gt);
            this.statsItemGroup.addChild(item2);
        }
        try {
            Class<WorkbenchStatsGui> clazz = WorkbenchStatsGui.class;
            Field staticBarsField = clazz.getDeclaredField("staticBars");
            Field barsField = clazz.getDeclaredField("bars");
            staticBarsField.setAccessible(true);
            barsField.setAccessible(true);
            @SuppressWarnings("unchecked")
            Stream<GuiStatBase> stream = Stream.concat(((List<GuiStatBase>) staticBarsField.get(clazz)).stream(),
                    ((List<GuiStatBase>) barsField.get(clazz)).stream());
            stream.filter(guiStatBase -> guiStatBase.shouldShow(this.minecraft.player, this.stack, this.to, null, null))
                    .forEach(guiStatBase -> {
                        guiStatBase.update(this.minecraft.player, this.stack, this.to, null, null);
                        if (guiStatBase instanceof GuiStatBarTool) {
                            this.realignBar(guiStatBase, this.statsToolBarGroup.getNumChildren(), true);
                            this.statsToolBarGroup.addChild(guiStatBase);
                        } else {
                            this.realignBar(guiStatBase, this.statsCommonBarGroup.getNumChildren(), false);
                            this.statsCommonBarGroup.addChild(guiStatBase);
                        }
                    });
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    int indexToY(int index) {
        index /= 2;
        int line = 2;
        int extra = 1;
        while (index >= line + extra) {
            index -= line + extra;
            ++line;
            ++extra;
        }
        int result = index > line ? line : index;
        return result;
    }

    int indexToX(int index) {
        index /= 2;
        int line = 2;
        int extra = 1;
        while (index >= line + extra) {
            index -= line + extra;
            ++line;
            ++extra;
        }
        int result = (index -= line) >= 0 ? line - extra + index : extra;
        return result - 1;
    }

    void realignBar(GuiStatBase bar, int index, boolean isTool) {
        int y = -17 * this.indexToY(index) - 3;
        bar.setY(isTool ? -y + 30 : y - 12);
        bar.setAttachmentAnchor(GuiAttachment.bottomCenter);
        int xOffset = 3 + this.indexToX(index) * 62;
        if (index % 2 == 0) {
            bar.setX(xOffset);
            bar.setAttachmentPoint(GuiAttachment.bottomLeft);
            bar.setAlignment(GuiAlignment.left);
        } else {
            bar.setX(-xOffset);
            bar.setAttachmentPoint(GuiAttachment.bottomRight);
            bar.setAlignment(GuiAlignment.right);
        }
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        this.statsBarGroup.updateFocusState((int) this.xOffset, (int) this.yOffset, pMouseX, pMouseY);
        this.statsBarGroup.draw(pGuiGraphics, (int) this.xOffset, (int) this.yOffset, this.width, this.height, pMouseX, pMouseY, 1.0f);
        List<Component> tooltipLines = this.statsBarGroup.getTooltipLines();
        if (tooltipLines != null) {
            pGuiGraphics.renderTooltip(this.font, tooltipLines, Optional.empty(), pMouseX, Math.max(pMouseY, 14));
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.statsItemGroup.onMouseClick((int) mouseX, (int) mouseY, button)) {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        this.xOffset += pDragX;
        this.yOffset += pDragY;
        return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
    }

    @Override
    public void renderBackground(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        pGuiGraphics.fillGradient(0, 0, this.width, this.height, -1, new Color(0, 0, 0, 145).getRGB());
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.lastScreen);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (this.minecraft.options.keyInventory.isActiveAndMatches(InputConstants.getKey(keyCode, scanCode))) {
            this.onClose();
            return true;
        }
        return false;
    }
}
