package net.yiran.extraholopage.core;

import net.yiran.extraholopage.util.GuiFilter;
import net.yiran.extraholopage.util.GuiSorter;

public interface IHoloMaterialListGui {
    public GuiSorter getGuiSorter();

    public GuiFilter[] getHoloFilters();

    default public void resetAllSF() {
        this.getGuiSorter().resetSorter();
        for (GuiFilter filter : this.getHoloFilters()) {
            filter.resetFilters();
        }
    }
}
