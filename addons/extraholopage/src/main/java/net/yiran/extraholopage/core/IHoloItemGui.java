package net.yiran.extraholopage.core;

import se.mickelus.mutil.gui.GuiTexture;
import se.mickelus.tetra.items.modular.impl.holo.gui.craft.item.HoloItemGui;

public interface IHoloItemGui {
    public static IHoloItemGui cast(HoloItemGui holoItemGui) {
        return (IHoloItemGui)holoItemGui;
    }

    public void setIcon(GuiTexture var1);
}
