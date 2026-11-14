/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.resources.language.I18n
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package com.hp.tetra_data_kubejs.mixin.client;

import com.hp.tetra_data_kubejs.client.TetraClientTranslationHelper;
import net.minecraft.client.resources.language.I18n;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={I18n.class})
public class I18nMixin {
    @Inject(method={"exists(Ljava/lang/String;)Z"}, at={@At(value="HEAD")}, cancellable=true)
    private static void tetraDataKubejs$checkScriptTranslation(String key, CallbackInfoReturnable<Boolean> cir) {
        if (TetraClientTranslationHelper.hasCustomTranslation(key)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method={"get(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;"}, at={@At(value="HEAD")}, cancellable=true)
    private static void tetraDataKubejs$resolveScriptTranslation(String key, Object[] args, CallbackInfoReturnable<String> cir) {
        String translated = TetraClientTranslationHelper.translate(key, args);
        if (translated != null) {
            cir.setReturnValue(translated);
        }
    }
}

