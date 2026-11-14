package net.yiran.extraholopage.core.mixins;

import net.minecraft.world.item.ItemStack;
import net.yiran.extraholopage.api.ExtraHoloRegister;
import net.yiran.extraholopage.gui.ExtraHoloRootGui;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import se.mickelus.mutil.gui.GuiElement;
import se.mickelus.tetra.items.modular.IModularItem;
import se.mickelus.tetra.items.modular.impl.holo.HoloPage;
import se.mickelus.tetra.items.modular.impl.holo.gui.HoloGui;
import se.mickelus.tetra.items.modular.impl.holo.gui.HoloRootBaseGui;
import se.mickelus.tetra.module.schematic.UpgradeSchematic;

@Mixin(value={HoloGui.class}, remap=false)
public abstract class HoloGuiMixin {
    @Shadow
    @Final
    private HoloRootBaseGui[] pages;
    @Shadow
    @Final
    private GuiElement defaultGui;
    @Shadow
    private Runnable closeCallback;
    @Shadow
    private HoloRootBaseGui currentPage;
    @Unique
    private ExtraHoloRootGui ehp$extraPage;

    @Inject(method={"<init>"}, at={@At(value="TAIL")})
    private void onInit(CallbackInfo ci) {
        this.ehp$extraPage = new ExtraHoloRootGui(0, 18);
        this.ehp$extraPage.setVisible(false);
        this.defaultGui.addChild(this.ehp$extraPage);
    }

    @Inject(method={"changePage"}, at={@At(value="HEAD")})
    private void ehp$onChangePage(HoloPage page, CallbackInfo ci) {
        if (this.ehp$extraPage != null) {
            this.ehp$extraPage.setVisible(false);
        }
    }

    @Inject(method={"openSchematic"}, at={@At(value="HEAD")}, cancellable=true)
    private void sb$openSchematic(IModularItem item, ItemStack itemStack, String slot, UpgradeSchematic schematic, Runnable closeCallback, CallbackInfo ci) {
        if (ExtraHoloRegister.MAP.containsKey(item)) {
            for (HoloRootBaseGui page : this.pages) {
                page.setVisible(false);
            }
            this.currentPage = this.ehp$extraPage;
            this.ehp$extraPage.updateState(item, itemStack, slot, schematic);
            this.ehp$extraPage.setVisible(true);
            this.closeCallback = closeCallback;
            ci.cancel();
        }
    }
}
