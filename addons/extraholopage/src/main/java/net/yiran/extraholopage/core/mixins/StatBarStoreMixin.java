package net.yiran.extraholopage.core.mixins;

import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.yiran.extraholopage.gui.ToolBarHelper;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import se.mickelus.tetra.gui.stats.bar.GuiStatBase;
import se.mickelus.tetra.gui.stats.data.StatBarStore;

@Mixin(value={StatBarStore.class}, remap=false)
public class StatBarStoreMixin {
    @Inject(method={"prepareBars"}, at={@At(value="RETURN")}, cancellable=true)
    private static void prepareBars(CallbackInfoReturnable<GuiStatBase[]> cir) {
        ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
        GuiStatBase[] s = resourceManager.listResources("tool_bars", rl -> rl.getPath().endsWith(".json")).entrySet().stream().filter(entry -> "tetra".equals(entry.getKey().getNamespace())).map(entry -> (GuiStatBase)ToolBarHelper.parseT(entry.getValue())).filter(Objects::nonNull).toArray(GuiStatBase[]::new);
        cir.setReturnValue(ArrayUtils.addAll(cir.getReturnValue(), s));
    }
}
