package net.yiran.extraholopage.core.mixins;

import java.util.ArrayList;
import java.util.function.Predicate;
import net.yiran.extraholopage.core.IGuiElement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import se.mickelus.mutil.gui.GuiElement;

@Mixin(value={GuiElement.class}, remap=false)
public class GuiElementMixin
implements IGuiElement {
    @Shadow
    protected ArrayList<GuiElement> elements;

    @Override
    public void removeChild(Class<?> type) {
        this.elements.removeIf(type::isInstance);
    }

    @Override
    public void removeIfChild(Predicate<? super GuiElement> filter) {
        this.elements.removeIf(filter);
    }
}
