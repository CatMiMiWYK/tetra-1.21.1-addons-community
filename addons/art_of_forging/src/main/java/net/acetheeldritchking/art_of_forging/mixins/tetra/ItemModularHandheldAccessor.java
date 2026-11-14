package net.acetheeldritchking.art_of_forging.mixins.tetra;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import se.mickelus.tetra.effect.ChargedAbilityEffect;
import se.mickelus.tetra.items.modular.ItemModularHandheld;

@Mixin(value = ItemModularHandheld.class, remap = false)
public interface ItemModularHandheldAccessor {
    @Accessor("abilities")
    static ChargedAbilityEffect[] getAbilities() {
        throw new UnsupportedOperationException();
    }

    @Mutable
    @Accessor("abilities")
    static void setAbilities(ChargedAbilityEffect[] abilities) {
        throw new UnsupportedOperationException();
    }
}
