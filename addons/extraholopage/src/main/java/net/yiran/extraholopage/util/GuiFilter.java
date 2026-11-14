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

public class GuiFilter
extends GuiElement {
    public static ResourceLocation texture = ResourceLocation.fromNamespaceAndPath("extraholopage", "textures/gui/back.png");
    public MaterialFilter focusFiler;
    public GuiFilter afterFilter;
    public Runnable updateGroups;
    public int focusPage = 0;
    public boolean isSelected = false;
    public IHoloMaterialListGui gui;

    public GuiFilter(int x, int y, int width, int height, Runnable runnable, GuiFilter beforeFilter, IHoloMaterialListGui holoMaterialListGui) {
        super(x, y, width, height);
        this.gui = holoMaterialListGui;
        this.focusFiler = MaterialFilter.NONE;
        this.afterFilter = beforeFilter;
        this.updateGroups = runnable;
        this.resetFilters();
    }

    public void resetFilters() {
        this.clearChildren();
        String focusString = I18n.get("ehp.filter.desc") + " : " + this.focusFiler.getName();
        int wight1 = Minecraft.getInstance().font.width(focusString);
        if (this.afterFilter != null) {
            this.afterFilter.setX(this.getX() + wight1 + 10);
            this.afterFilter.resetFilters();
        }
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
        int totalPage = (int)Math.ceil((float)MaterialManager.INSTANCE.getFilters().size() / (float)Config.SHOW_FILTER_AND_SORTER_ELEMENTS.get().intValue());
        ArrayList<MaterialFilter> list = new ArrayList<MaterialFilter>();
        for (int i = page * Config.SHOW_FILTER_AND_SORTER_ELEMENTS.get(); i < (page + 1) * Config.SHOW_FILTER_AND_SORTER_ELEMENTS.get() && i < MaterialManager.INSTANCE.getFilters().size(); ++i) {
            list.add(MaterialManager.INSTANCE.getFilters().get(i));
        }
        this.clearChildren();
        int index = -10;
        int wight = 0;
        for (MaterialFilter filter : list) {
            final MaterialFilter selectedFilter = filter;
            this.addChild(new GuiButton(0, index += 10, filter.getName(), () -> {
                this.isSelected = false;
                this.setFocusFiler(selectedFilter);
                this.updateGroups.run();
                this.resetFilters();
            }));
            wight = Math.max(wight, Minecraft.getInstance().font.width(filter.getName()));
        }
        String ss = I18n.get("ehp.page_info.format", this.focusPage + 1, totalPage);
        int ww = Minecraft.getInstance().font.width(ss);
        if (totalPage > 1) {
            this.addChild(new GuiString(0, index += 10, ss));
            wight = Math.max(wight, ww);
        }
        if (this.afterFilter != null) {
            this.afterFilter.setX(this.getX() + wight + 10);
            this.afterFilter.resetFilters();
        }
        int wight2 = wight;
        this.getChildren(GuiButton.class).forEach(button -> button.setWidth(wight2));
        this.getChildren(GuiString.class).forEach(string -> string.setX((wight2 - ww) / 2));
        this.addChild(new GuiTexture(-2, -2, wight + 4, index + 10 + 4, texture));
    }

    public boolean onKeyPress(int keyCode, int scanCode, int modifiers) {
        if (this.isSelected) {
            int totalPage = (int)Math.ceil((float)MaterialManager.INSTANCE.getFilters().size() / (float)Config.SHOW_FILTER_AND_SORTER_ELEMENTS.get().intValue());
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

    public void setFocusFiler(MaterialFilter focusFiler) {
        this.focusFiler = focusFiler;
    }

    public MaterialFilter getFocusFiler() {
        return this.focusFiler;
    }
}
