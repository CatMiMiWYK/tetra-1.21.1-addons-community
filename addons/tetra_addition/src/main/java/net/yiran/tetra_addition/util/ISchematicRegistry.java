package net.yiran.tetra_addition.util;

import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import se.mickelus.tetra.module.schematic.UpgradeSchematic;

public interface ISchematicRegistry {
    Map<ResourceLocation, UpgradeSchematic> getSchematicMap();
}
