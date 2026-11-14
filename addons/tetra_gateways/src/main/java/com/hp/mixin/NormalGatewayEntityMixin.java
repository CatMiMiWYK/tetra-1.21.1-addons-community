package com.hp.mixin;

import com.hp.rift.RiftGatewayBehavior;
import dev.shadowsoffire.gateways.entity.NormalGatewayEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = NormalGatewayEntity.class, remap = false)
public class NormalGatewayEntityMixin {
    @Inject(method = "isLastWave", at = @At("HEAD"), cancellable = true)
    private void tetra_gateways$isLastWave(CallbackInfoReturnable<Boolean> cir) {
        NormalGatewayEntity gateway = (NormalGatewayEntity) (Object) this;
        int waveCount = RiftGatewayBehavior.getWaveCount(gateway, gateway.getGateway().getNumWaves());
        cir.setReturnValue(gateway.getWave() >= waveCount);
    }
}
