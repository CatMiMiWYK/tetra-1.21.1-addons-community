package net.tetra.modify.core;

import org.jetbrains.annotations.Nullable;
import se.mickelus.mutil.gui.GuiElement;

public interface IGuiElementParentProvider {
    @Nullable
    public GuiElement getParent();

    public void setParent(GuiElement guiElement);
}
