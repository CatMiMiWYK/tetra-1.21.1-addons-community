package com.hp.rift;

import com.hp.effect.RiftEffects;
import com.hp.item.ModItems;
import com.mojang.serialization.Codec;
import dev.shadowsoffire.gateways.entity.GatewayEntity;
import dev.shadowsoffire.gateways.gate.StandardWaveEntity;
import dev.shadowsoffire.gateways.gate.Wave;
import dev.shadowsoffire.gateways.gate.WaveEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import se.mickelus.tetra.effect.EffectHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RiftGatewayBehavior {
    public static final String RIFT_WAVE_MOB_TAG = "tetra_gateways.rift_wave_mob";
    public static final String RIFT_DAMAGE_ADDITION_TAG = "tetra_gateways.rift_damage_addition";
    public static final String RIFT_DAMAGE_MULTIPLIER_TAG = "tetra_gateways.rift_damage_multiplier";
    private static final int TICKS_PER_SECOND = 20;

    public static int getChallengeTime(GatewayEntity gateway, int original) {
        ItemStack source = getSourcePearl(gateway);
        if (source.isEmpty()) {
            return original;
        }
        int value = EffectHelper.getEffectLevel(source, RiftEffects.RIFT_CHALLENGE_TIME);
        if (value <= 0) {
            return original;
        }
        return value * TICKS_PER_SECOND;
    }

    public static int getWaveInterval(GatewayEntity gateway, int original) {
        ItemStack source = getSourcePearl(gateway);
        if (source.isEmpty()) {
            return original;
        }
        int value = EffectHelper.getEffectLevel(source, RiftEffects.RIFT_WAVE_INTERVAL);
        if (value <= 0) {
            return original;
        }
        return value * TICKS_PER_SECOND;
    }

    public static int getWaveCount(GatewayEntity gateway, int original) {
        ItemStack source = getSourcePearl(gateway);
        if (source.isEmpty()) {
            return original;
        }
        int value = EffectHelper.getEffectLevel(source, RiftEffects.RIFT_WAVE_COUNT);
        if (value <= 0) {
            return original;
        }
        return Math.max(1, value);
    }

    public static int getCurrentWaveNumber(GatewayEntity gateway) {
        return Math.max(1, gateway.getWave() + 1);
    }

    public static int getWaveRewardCount(ItemStack source, GatewayEntity gateway) {
        int baseCount = RiftPearlData.getRewardCount(source);
        int completedWaves = Math.max(1, gateway.getWave() + 1);
        double multiplier = 1.0 + (double) completedWaves * 0.3;
        return Math.max(1, (int) Math.ceil((double) baseCount * multiplier));
    }

    public static boolean trySpawnRiftWave(ServerLevel level, GatewayEntity gateway, List<LivingEntity> spawned) {
        ItemStack source = getSourcePearl(gateway);
        if (source.isEmpty()) {
            return false;
        }
        List<EntityType<?>> pool = getEntityPool(level, gateway, source);
        if (pool.isEmpty()) {
            return false;
        }
        int pickCount = Math.max(1, EffectHelper.getEffectLevel(source, RiftEffects.RIFT_MOB_SPAWN));
        int countPerType = Math.max(1, Math.round(EffectHelper.getEffectEfficiency(source, RiftEffects.RIFT_MOB_SPAWN)));
        Collections.shuffle(pool);
        for (EntityType<?> type : pool.stream().limit(pickCount).toList()) {
            for (int i = 0; i < countPerType; ++i) {
                LivingEntity entity = createEntity(type, level);
                if (entity == null) {
                    continue;
                }
                entity.getPersistentData().putBoolean(RIFT_WAVE_MOB_TAG, true);
                LivingEntity spawnedEntity = Wave.spawnWaveEntity(level, gateway.position(), gateway, gateway.getCurrentWave(), new RiftWaveEntity(entity));
                if (spawnedEntity == null) {
                    continue;
                }
                spawned.add(spawnedEntity);
            }
        }
        return !spawned.isEmpty();
    }

    private static ItemStack getSourcePearl(GatewayEntity gateway) {
        ItemStack source = RiftPearlData.readSourcePearl(gateway.getPersistentData(), gateway.registryAccess());
        if (source.isEmpty() || source.getItem() != ModItems.RIFT_GATE_PEARL.get()) {
            return ItemStack.EMPTY;
        }
        return source;
    }

    private static List<EntityType<?>> getEntityPool(ServerLevel level, GatewayEntity gateway, ItemStack source) {
        if (EffectHelper.getEffectLevel(source, RiftEffects.RIFT_LOCAL_SPAWN_MODE) > 0) {
            return getLocalPool(level, gateway.blockPosition());
        }
        ArrayList<EntityType<?>> result = new ArrayList<>();
        if (EffectHelper.getEffectLevel(source, RiftEffects.RIFT_POOL_A) > 0) {
            appendConfiguredPool(result, "a");
        }
        if (EffectHelper.getEffectLevel(source, RiftEffects.RIFT_POOL_B) > 0) {
            appendConfiguredPool(result, "b");
        }
        if (EffectHelper.getEffectLevel(source, RiftEffects.RIFT_POOL_C) > 0) {
            appendConfiguredPool(result, "c");
        }
        return result;
    }

    private static List<EntityType<?>> getLocalPool(ServerLevel level, BlockPos pos) {
        ArrayList<EntityType<?>> result = new ArrayList<>();
        for (MobCategory category : MobCategory.values()) {
            level.getBiome(pos).value().getMobSettings().getMobs(category).unwrap().forEach(data -> {
                if (data.type != null && data.type.getCategory() != MobCategory.MISC) {
                    result.add(data.type);
                }
            });
        }
        return result;
    }

    private static void appendConfiguredPool(List<EntityType<?>> result, String pool) {
        for (ResourceLocation id : RiftConfigBridge.getPool(pool)) {
            EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(id);
            if (type == null || type.getCategory() == MobCategory.MISC) {
                continue;
            }
            result.add(type);
        }
    }

    private static LivingEntity createEntity(EntityType<?> type, ServerLevel level) {
        Entity entity = type.create(level);
        return entity instanceof LivingEntity living ? living : null;
    }

    private RiftGatewayBehavior() {
    }

    private record RiftWaveEntity(LivingEntity entity) implements WaveEntity {
        @Override
        public LivingEntity createEntity(ServerLevel level, GatewayEntity gateway) {
            return this.entity;
        }

        @Override
        public MutableComponent getDescription() {
            return this.entity.getType().getDescription().copy();
        }

        @Override
        public boolean shouldFinalizeSpawn() {
            return true;
        }

        @Override
        public int getCount() {
            return 1;
        }

        @Override
        public Codec<? extends WaveEntity> getCodec() {
            return StandardWaveEntity.CODEC;
        }
    }
}
