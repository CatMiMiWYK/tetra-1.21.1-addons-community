package net.yiran.extraholopage.core.mixins;

import com.google.common.collect.Multimap;
import java.util.Set;
import java.util.stream.Stream;
import net.yiran.extraholopage.compat.CompatHandler;
import net.yiran.extraholopage.compat.HoloMaterialContextEffectGui;
import net.yiran.extraholopage.compat.OverhaulCompat;
import net.yiran.extraholopage.core.IGuiElement;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import se.mickelus.mutil.gui.GuiElement;
import se.mickelus.tetra.effect.ItemEffect;
import se.mickelus.tetra.items.modular.impl.holo.gui.craft.material.HoloMaterialDetailGui;
import se.mickelus.tetra.items.modular.impl.holo.gui.craft.material.HoloMaterialEffectGui;
import se.mickelus.tetra.module.data.MaterialData;

@Mixin(value={HoloMaterialDetailGui.class}, remap=false)
public abstract class HoloMaterialDetailGuiMixin
extends GuiElement {
    @Shadow
    @Final
    private GuiElement modifiers;

    public HoloMaterialDetailGuiMixin(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    @Inject(method={"update"}, at={@At(value="INVOKE", target="Ljava/util/Arrays;asList([Ljava/lang/Object;)Ljava/util/List;", ordinal=0)})
    public void update(MaterialData selected, MaterialData hovered, CallbackInfo ci) {
        if (CompatHandler.OverhaulIsLoaded) {
            ((IGuiElement)this.modifiers).removeChild(HoloMaterialEffectGui.class);
            MaterialData current = selected != null ? selected : hovered;
            MaterialData preview = hovered != null ? hovered : current;
            Multimap<ItemEffect, String> currentContextItemEffects = OverhaulCompat.getContextItemEffects(current);
            Multimap<ItemEffect, String> previewContextItemEffects = OverhaulCompat.getContextItemEffects(preview);
            Set<ItemEffect> currentFeatures = currentContextItemEffects.keySet();
            Set<ItemEffect> previewFeatures = previewContextItemEffects.keySet();
            Stream.concat(currentFeatures.stream(), previewFeatures.stream()).distinct().map(effect -> new HoloMaterialContextEffectGui(0, 0, effect.getKey(), currentFeatures.contains(effect), previewFeatures.contains(effect), currentContextItemEffects.get(effect).equals(previewContextItemEffects.get(effect)), currentContextItemEffects.get(effect))).forEach(effect -> this.modifiers.addChild(effect));
        }
    }
}
