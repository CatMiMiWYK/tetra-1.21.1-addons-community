/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 *  se.mickelus.tetra.module.data.ImprovementData
 *  se.mickelus.tetra.module.data.MaterialData
 *  se.mickelus.tetra.module.data.MaterialMultiplier
 *  se.mickelus.tetra.module.data.MaterialVariantData
 *  se.mickelus.tetra.module.data.VariantData
 */
package net.yiran.tmo.core.mixins;

import java.util.List;
import net.yiran.tmo.ContextData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import se.mickelus.tetra.module.data.ImprovementData;
import se.mickelus.tetra.module.data.MaterialData;
import se.mickelus.tetra.module.data.MaterialMultiplier;
import se.mickelus.tetra.module.data.MaterialVariantData;
import se.mickelus.tetra.module.data.VariantData;

@Mixin(value={MaterialVariantData.class}, remap=false)
public class MaterialVariantDataMixin
extends VariantData {
    @Shadow
    public MaterialMultiplier extract;
    @Unique
    private List<String> contexts = List.of("default");

    @Inject(method={"combine"}, at={@At(value="RETURN")})
    private void combine(MaterialData material, CallbackInfoReturnable<VariantData> cir) {
        ContextData.combineImprovementData(this, (VariantData)cir.getReturnValue(), this.contexts, this.extract, material);
    }
}

