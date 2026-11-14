package net.yiran.extraholopage.core;

import java.util.function.Predicate;
import se.mickelus.mutil.gui.GuiElement;

public interface IGuiElement {
    public void removeChild(Class<?> var1);

    public void removeIfChild(Predicate<? super GuiElement> var1);
}
