package net.yiran.tetra_addition.mixins;

import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.yiran.tetra_addition.util.ISchematicRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import se.mickelus.tetra.module.SchematicRegistry;
import se.mickelus.tetra.module.schematic.UpgradeSchematic;

@Mixin(value = SchematicRegistry.class, remap = false)
public class SchematicRegistryMixin
        implements ISchematicRegistry {
    @Shadow
    private Map<ResourceLocation, UpgradeSchematic> schematicMap;

    @Override
    public Map<ResourceLocation, UpgradeSchematic> getSchematicMap() {
        return this.schematicMap;
    }
}
