package net.acetheeldritchking.art_of_forging.mixins.tetra;

import net.acetheeldritchking.art_of_forging.effects.gui.EffectGuiStats;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import se.mickelus.tetra.items.modular.ItemModularHandheld;

@Mixin(value = ItemModularHandheld.class, remap = false)
public class ItemModularHandheldMixin {
    @Inject(method = "<clinit>", at = @At(value = "RETURN"), remap = false)
    private static void modifyStaticBlock(CallbackInfo info) {
        EffectGuiStats.setupAbilites();
    }
}
