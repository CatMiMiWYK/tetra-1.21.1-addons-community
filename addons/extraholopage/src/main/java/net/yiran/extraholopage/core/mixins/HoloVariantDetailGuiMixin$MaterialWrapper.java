package net.yiran.extraholopage.core.mixins;

import java.util.List;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import se.mickelus.mutil.gui.GuiElement;

@Mixin(targets={"se.mickelus.tetra.items.modular.impl.holo.gui.craft.schematic.HoloVariantDetailGui$MaterialWrapper"}, remap=false)
public abstract class HoloVariantDetailGuiMixin$MaterialWrapper
extends GuiElement {
    public HoloVariantDetailGuiMixin$MaterialWrapper(int x, int y) {
        super(x, y, 0, 0);
    }

    @Inject(method={"getTooltipLines"}, at={@At(value="HEAD")}, cancellable=true)
    private void ehp$getTooltipLines(CallbackInfoReturnable<List<Component>> cir) {
        List<Component> tooltip;
        if (this.hasFocus() && (tooltip = super.getTooltipLines()) != null && !tooltip.isEmpty()) {
            cir.setReturnValue(tooltip);
            return;
        }
        cir.setReturnValue(null);
    }
}
