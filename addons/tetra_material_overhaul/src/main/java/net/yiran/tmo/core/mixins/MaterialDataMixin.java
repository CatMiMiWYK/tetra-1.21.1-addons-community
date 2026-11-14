/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 *  org.spongepowered.asm.mixin.injection.callback.LocalCapture
 *  se.mickelus.tetra.module.data.MaterialData
 *  se.mickelus.tetra.module.data.MaterialData$Deserializer
 */
package net.yiran.tmo.core.mixins;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import net.yiran.tmo.ContextData;
import net.yiran.tmo.core.IMaterialData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import se.mickelus.tetra.module.data.MaterialData;

@Mixin(value={MaterialData.class})
public class MaterialDataMixin
implements IMaterialData {
    private Map<String, ContextData> contextData;

    @Override
    public void setContextData(Map<String, ContextData> contextData) {
        this.contextData = contextData;
    }

    @Override
    public Map<String, ContextData> getContextData() {
        return this.contextData;
    }

    @Inject(method={"copyFields"}, at={@At(value="RETURN")}, remap=false)
    private static void copyFields(MaterialData from, MaterialData to, CallbackInfo ci) {
        if (((IMaterialData)from).getContextData() != null && ((IMaterialData)to).getContextData() != null) {
            ((IMaterialData)to).getContextData().putAll(((IMaterialData)from).getContextData());
        }
    }

    @Mixin(value={MaterialData.Deserializer.class})
    public static abstract class DeserializerMixin
    implements JsonDeserializer<MaterialData> {
        @Inject(method={"deserialize(Lcom/google/gson/JsonElement;Ljava/lang/reflect/Type;Lcom/google/gson/JsonDeserializationContext;)Lse/mickelus/tetra/module/data/MaterialData;"}, at={@At(value="RETURN")}, locals=LocalCapture.CAPTURE_FAILHARD, remap=false)
        private void deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context, CallbackInfoReturnable<MaterialData> cir, JsonObject jsonObject, MaterialData data) {
            if (json.getAsJsonObject().has("contexts")) {
                Map contexts = (Map)context.deserialize(json.getAsJsonObject().get("contexts"), ContextData.TYPE);
                if (!contexts.containsKey("default")) {
                    contexts.put("default", ContextData.buildDefaultContextData(data));
                }
                ((IMaterialData)data).setContextData(contexts);
            } else {
                HashMap<String, ContextData> contextData = new HashMap<String, ContextData>();
                contextData.put("default", ContextData.buildDefaultContextData(data));
                ((IMaterialData)data).setContextData(contextData);
            }
        }
    }
}

