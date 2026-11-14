package net.tetra.modify.core.mixins.gui.element;

import net.tetra.modify.core.IGuiElementParentProvider;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import se.mickelus.mutil.gui.GuiElement;

@Mixin(value = {GuiElement.class}, remap = false)
public class GuiElementMixin implements IGuiElementParentProvider {
    private GuiElement parent;

    @Inject(method = {"addChild"}, at = {@At(value = "HEAD")})
    private void add(GuiElement child, CallbackInfo ci) {
        ((IGuiElementParentProvider) (Object) child).setParent((GuiElement) (Object) this);
    }

    @Override
    @Nullable
    public GuiElement getParent() {
        return this.parent;
    }

    @Override
    public void setParent(GuiElement guiElement) {
        this.parent = guiElement;
    }
}
