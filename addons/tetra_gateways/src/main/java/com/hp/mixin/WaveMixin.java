package com.hp.mixin;

import com.hp.rift.RiftGatewayBehavior;
import dev.shadowsoffire.gateways.entity.GatewayEntity;
import dev.shadowsoffire.gateways.gate.Wave;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = Wave.class, remap = false)
public class WaveMixin {
    @Inject(method = "spawnWave", at = @At("HEAD"), cancellable = true)
    private void tetra_gateways$spawnRiftWave(ServerLevel level, Vec3 pos, GatewayEntity gate,
            CallbackInfoReturnable<List<LivingEntity>> cir) {
        List<LivingEntity> spawned = new ArrayList<>();
        if (RiftGatewayBehavior.trySpawnRiftWave(level, gate, spawned)) {
            cir.setReturnValue(spawned);
        }
    }
}
