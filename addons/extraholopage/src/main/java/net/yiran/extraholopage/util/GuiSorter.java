package net.yiran.extraholopage.util;

import com.mojang.blaze3d.platform.InputConstants;
import java.util.ArrayList;
import java.util.Iterator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;
import net.yiran.extraholopage.Config;
import net.yiran.extraholopage.KeyMappingHandler;
import net.yiran.extraholopage.api.MaterialManager;
import net.yiran.extraholopage.core.IHoloMaterialListGui;
import se.mickelus.mutil.gui.GuiButton;
import se.mickelus.mutil.gui.GuiElement;
import se.mickelus.mutil.gui.GuiString;
import se.mickelus.mutil.gui.GuiTexture;

public class GuiSorter
extends GuiElement {
    public static ResourceLocation texture = ResourceLocation.fromNamespaceAndPath("extraholopage", "textures/gui/back.png");
    public MaterialSorter focusSorter;
    public Runnable updateGroups;
    public GuiFilter guiFilter;
    public int focusPage = 0;
    public boolean isSelected = false;
    public IHoloMaterialListGui gui;

    public GuiSorter(int x, int y, int width, int height, Runnable runnable, GuiFilter guiFilter, IHoloMaterialListGui holoMaterialListGui) {
        super(x, y, width, height);
        this.gui = holoMaterialListGui;
        this.focusSorter = MaterialSorter.NONE;
        this.guiFilter = guiFilter;
        this.updateGroups = runnable;
        this.resetSorter();
    }

    public void resetSorter() {
        this.clearChildren();
        String focusString = I18n.get("ehp.sorter.desc") + " : " + this.focusSorter.getName();
        int wight1 = Minecraft.getInstance().font.width(focusString);
        this.guiFilter.setX(this.getX() + wight1 + 10);
        this.guiFilter.resetFilters();
        this.isSelected = false;
        this.addChild(new GuiTexture(-2, -2, wight1 + 4, 14, texture));
        this.addChild(new GuiButton(0, 0, focusString, () -> {
            this.gui.resetAllSF();
            this.isSelected = true;
            this.setPage(this.focusPage);
        }));
    }

    public void setPage(int page) {
        this.focusPage = page;
        int totalPage = (int)Math.ceil((float)MaterialManager.INSTANCE.getSorters().size() / (float)Config.SHOW_FILTER_AND_SORTER_ELEMENTS.get().intValue());
        ArrayList<MaterialSorter> list = new ArrayList<MaterialSorter>();
        for (int i = page * Config.SHOW_FILTER_AND_SORTER_ELEMENTS.get(); i < (page + 1) * Config.SHOW_FILTER_AND_SORTER_ELEMENTS.get() && i < MaterialManager.INSTANCE.getSorters().size(); ++i) {
            list.add(MaterialManager.INSTANCE.getSorters().get(i));
        }
        this.clearChildren();
        int index = -10;
        int wight = 0;
        for (MaterialSorter sorter : list) {
            final MaterialSorter selectedSorter = sorter;
            this.addChild(new GuiButton(0, index += 10, sorter.getName(), () -> {
                this.setFocusSorter(selectedSorter);
                this.updateGroups.run();
                this.resetSorter();
            }));
            wight = Math.max(wight, Minecraft.getInstance().font.width(sorter.getName()));
        }
        String ss = I18n.get("ehp.page_info.format", this.focusPage + 1, totalPage);
        int ww = Minecraft.getInstance().font.width(ss);
        if (totalPage > 1) {
            this.addChild(new GuiString(0, index += 10, ss));
            wight = Math.max(wight, ww);
        }
        this.guiFilter.setX(this.getX() + wight + 10);
        this.guiFilter.resetFilters();
        int wight2 = wight;
        this.getChildren(GuiButton.class).forEach(button -> button.setWidth(wight2));
        this.getChildren(GuiString.class).forEach(string -> string.setX((wight2 - ww) / 2));
        this.addChild(new GuiTexture(-2, -2, wight + 4, index + 10 + 4, texture));
    }

    public boolean onKeyPress(int keyCode, int scanCode, int modifiers) {
        if (this.isSelected) {
            int totalPage = (int)Math.ceil((float)MaterialManager.INSTANCE.getSorters().size() / (float)Config.SHOW_FILTER_AND_SORTER_ELEMENTS.get().intValue());
            InputConstants.Key inputKey = InputConstants.getKey(keyCode, scanCode);
            if (KeyMappingHandler.nextElement.isActiveAndMatches(inputKey)) {
                this.setPage((this.focusPage + 1) % totalPage);
                return true;
            }
            if (KeyMappingHandler.previousElement.isActiveAndMatches(inputKey)) {
                this.setPage((this.focusPage + totalPage - 1) % totalPage);
                return true;
            }
        }
        return super.onKeyPress(keyCode, scanCode, modifiers);
    }

    public void draw(GuiGraphics graphics, int refX, int refY, int screenWidth, int screenHeight, int mouseX, int mouseY, float opacity) {
        graphics.pose().translate(0.0f, 0.0f, 1000.0f);
        super.draw(graphics, refX, refY, screenWidth, screenHeight, mouseX, mouseY, opacity);
        graphics.pose().translate(0.0f, 0.0f, -1000.0f);
    }

    public void setFocusSorter(MaterialSorter focusSorter) {
        this.focusSorter = focusSorter;
    }

    public MaterialSorter getFocusSorter() {
        return this.focusSorter;
    }
}
