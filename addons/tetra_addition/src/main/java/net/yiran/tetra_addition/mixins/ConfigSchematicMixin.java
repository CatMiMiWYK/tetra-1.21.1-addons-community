package net.yiran.tetra_addition.mixins;

import net.yiran.tetra_addition.util.IConfigSchematic;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import se.mickelus.tetra.module.schematic.ConfigSchematic;
import se.mickelus.tetra.module.schematic.SchematicDefinition;

@Mixin(value = ConfigSchematic.class, remap = false)
public abstract class ConfigSchematicMixin
        implements IConfigSchematic {
    @Shadow
    @Final
    private SchematicDefinition definition;

    @Override
    public SchematicDefinition getDefinition() {
        return this.definition;
    }
}
