package net.yiran.extraholopage.util;

import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import se.mickelus.mutil.gui.GuiClickable;
import se.mickelus.mutil.gui.GuiItem;
import se.mickelus.mutil.gui.GuiStringOutline;
import se.mickelus.mutil.gui.GuiTexture;
import se.mickelus.tetra.gui.GuiItemRolling;
import se.mickelus.tetra.gui.GuiTextures;
import se.mickelus.tetra.module.data.MaterialData;

public class HoloMaterialSorterGui
extends GuiClickable {
    protected GuiTexture backdrop;
    protected MaterialData material;
    protected Consumer<MaterialData> onHover;
    protected Consumer<MaterialData> onBlur;
    protected boolean isMuted = false;
    GuiItemRolling icon;
    GuiStringOutline stringOutline;

    public HoloMaterialSorterGui(int x, int y, GuiSorter guiSorter, MaterialData material, Consumer<MaterialData> onHover, Consumer<MaterialData> onBlur, Consumer<MaterialData> onSelect) {
        super(x, y, 16, 16, () -> onSelect.accept(material));
        this.material = material;
        this.onHover = onHover;
        this.onBlur = onBlur;
        this.backdrop = new GuiTexture(0, 0, 16, 16, 52, 16, GuiTextures.workbench);
        this.addChild(this.backdrop);
        this.icon = new GuiItemRolling(0, 0).setTooltip(false).setCountVisibility(GuiItem.CountMode.never).setItems(material.material.getApplicableItemStacks());
        this.addChild(this.icon);
        if (guiSorter != null && guiSorter.getFocusSorter() != MaterialSorter.NONE) {
            String sting = String.valueOf(guiSorter.getFocusSorter().getPriority(material));
            if (sting.endsWith(".0")) {
                sting = sting.substring(0, sting.length() - 2);
            }
            this.stringOutline = new GuiStringOutline(8 - Minecraft.getInstance().font.width(sting) / 2, 4, sting, material.tints.glyph);
            this.addChild(this.stringOutline);
        }
    }

    protected void drawChildren(GuiGraphics graphics, int refX, int refY, int screenWidth, int screenHeight, int mouseX, int mouseY, float opacity) {
        if (this.stringOutline == null || Screen.hasShiftDown()) {
            this.icon.setVisible(true);
        } else {
            this.icon.setVisible(false);
        }
        super.drawChildren(graphics, refX, refY, screenWidth, screenHeight, mouseX, mouseY, opacity);
    }

    public void updateSelection(MaterialData material) {
        this.isMuted = material != null && !this.material.equals(material);
        this.backdrop.setColor(this.isMuted ? 0x7F7F7F : 0xFFFFFF);
    }

    protected void onFocus() {
        super.onFocus();
        this.onHover.accept(this.material);
        this.backdrop.setColor(0xFFFFCC);
    }

    protected void onBlur() {
        super.onBlur();
        this.onBlur.accept(this.material);
        this.backdrop.setColor(this.isMuted ? 0x7F7F7F : 0xFFFFFF);
    }
}
