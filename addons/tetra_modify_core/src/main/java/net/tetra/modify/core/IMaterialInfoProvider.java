package net.tetra.modify.core;

import org.jetbrains.annotations.Nullable;
import se.mickelus.tetra.module.data.MaterialData;
import se.mickelus.tetra.module.data.MaterialMultiplier;

public interface IMaterialInfoProvider {
    public boolean hasInfo();

    public void markHasInfo();

    @Nullable
    public MaterialMultiplier getMaterialMultiplier();

    public void setMaterialMultiplier(MaterialMultiplier materialMultiplier);

    @Nullable
    public MaterialData getMaterialData();

    public void setMaterialData(MaterialData materialData);
}
