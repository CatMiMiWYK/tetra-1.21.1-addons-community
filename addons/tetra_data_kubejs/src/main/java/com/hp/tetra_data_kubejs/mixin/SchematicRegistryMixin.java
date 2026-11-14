/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  se.mickelus.tetra.module.SchematicRegistry
 *  se.mickelus.tetra.module.schematic.SchematicDefinition
 */
package com.hp.tetra_data_kubejs.mixin;

import java.util.Arrays;
import java.util.LinkedHashSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import se.mickelus.tetra.module.SchematicRegistry;
import se.mickelus.tetra.module.schematic.SchematicDefinition;

@Mixin(value={SchematicRegistry.class}, remap=false)
public abstract class SchematicRegistryMixin {
    @Inject(method={"processDefinition"}, at={@At(value="RETURN")}, remap=false)
    private void tetraDataKubejs$dedupeApplicableMaterials(SchematicDefinition definition, CallbackInfo ci) {
        if (definition.applicableMaterials == null || definition.applicableMaterials.length < 2) {
            return;
        }
        definition.applicableMaterials = (String[])new LinkedHashSet<String>(Arrays.asList(definition.applicableMaterials)).toArray(String[]::new);
    }
}

