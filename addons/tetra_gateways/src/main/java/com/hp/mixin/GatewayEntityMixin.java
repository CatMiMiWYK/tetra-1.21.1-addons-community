package com.hp.mixin;

import com.hp.rift.RiftGatewayBehavior;
import dev.shadowsoffire.gateways.entity.GatewayEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = GatewayEntity.class, remap = false)
public class GatewayEntityMixin {
    @Inject(method = "getSetupTime", at = @At("RETURN"), cancellable = true)
    private void tetra_gateways$getSetupTime(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(RiftGatewayBehavior.getWaveInterval((GatewayEntity) (Object) this, cir.getReturnValue()));
    }

    @Inject(method = "getMaxWaveTime", at = @At("RETURN"), cancellable = true)
    private void tetra_gateways$getMaxWaveTime(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(RiftGatewayBehavior.getChallengeTime((GatewayEntity) (Object) this, cir.getReturnValue()));
    }
}
