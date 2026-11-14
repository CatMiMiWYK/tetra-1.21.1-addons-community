package net.yiran.extraholopage.core.mixins;

import java.util.Map;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.resources.ResourceManager;
import net.neoforged.neoforge.common.ItemAbility;
import net.yiran.extraholopage.gui.ToolBarHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import se.mickelus.tetra.client.ItemAbilityIconStore;
import se.mickelus.tetra.module.data.GlyphData;

@Mixin(value={ItemAbilityIconStore.class}, remap=false)
public class ToolActionIconStoreMixin {
    @Inject(method={"prepareIcons"}, at={@At(value="RETURN")})
    public void prepareIcons(CallbackInfoReturnable<Map<ItemAbility, GlyphData>> cir) {
        ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
        resourceManager.listResources("tool_bars", rl -> rl.getPath().endsWith(".json")).entrySet().stream().filter(entry -> "tetra".equals(entry.getKey().getNamespace())).map(entry -> ToolBarHelper.parseG(entry.getValue())).filter(Objects::nonNull).forEach(map -> cir.getReturnValue().putAll(map));
    }
}
