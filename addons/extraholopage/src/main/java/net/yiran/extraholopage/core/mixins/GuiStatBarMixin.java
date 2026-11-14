package net.yiran.extraholopage.core.mixins;

import java.util.Objects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.yiran.extraholopage.Config;
import net.yiran.extraholopage.api.StatBarShowManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import se.mickelus.tetra.gui.stats.bar.GuiStatBar;
import se.mickelus.tetra.items.modular.IModularItem;
import se.mickelus.tetra.module.ItemUpgradeRegistry;

@Mixin(value={GuiStatBar.class}, remap=false)
public class GuiStatBarMixin {
    @Shadow
    protected String labelKey;

    @Inject(method={"shouldShow"}, at={@At(value="HEAD")}, cancellable=true)
    private void shouldShow(Player player, ItemStack currentStack, ItemStack previewStack, String slot, String improvement, CallbackInfoReturnable<Boolean> cir) {
        if (!Config.ENABLE_STATBAR_SHOW_CONTROL.get()) {
            return;
        }
        if (Objects.equals(currentStack.getItem(), previewStack.getItem()) || previewStack.isEmpty()) {
            if (!(currentStack.getItem() instanceof IModularItem)) {
                currentStack = ItemUpgradeRegistry.instance.getReplacement(currentStack);
            }
            StatBarShowManager.ShowResult result = StatBarShowManager.getInstance().handle(this.labelKey, currentStack);
            switch (result) {
                case FAIL: {
                    cir.setReturnValue(false);
                    break;
                }
                case SUCCESS: {
                    cir.setReturnValue(true);
                }
            }
        }
    }
}
