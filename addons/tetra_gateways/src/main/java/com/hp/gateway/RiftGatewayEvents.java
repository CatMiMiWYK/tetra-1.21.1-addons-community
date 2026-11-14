package com.hp.gateway;

import com.hp.effect.RiftEffects;
import com.hp.item.ModItems;
import com.hp.rift.RiftConfigBridge;
import com.hp.rift.RiftGatewayBehavior;
import com.hp.rift.RiftPearlData;
import dev.shadowsoffire.gateways.entity.GatewayEntity;
import dev.shadowsoffire.gateways.event.GateEvent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import se.mickelus.tetra.effect.EffectHelper;

public class RiftGatewayEvents {
    @SubscribeEvent
    public void onWaveEnd(GateEvent.WaveEnd event) {
        GatewayEntity gateway = (GatewayEntity) event.getEntity();
        ItemStack source = RiftPearlData.readSourcePearl(gateway.getPersistentData(), gateway.registryAccess());
        if (source.isEmpty() || source.getItem() != ModItems.RIFT_GATE_PEARL.get()) {
            return;
        }
        for (ItemStack reward : RiftPearlData.getCoreRewards(source)) {
            reward.setCount(RiftGatewayBehavior.getWaveRewardCount(source, gateway));
            gateway.spawnCompletionItem(reward);
        }
    }

    @SubscribeEvent
    public void onWaveEntitySpawned(GateEvent.WaveEntitySpawned event) {
        GatewayEntity gateway = (GatewayEntity) event.getEntity();
        ItemStack source = RiftPearlData.readSourcePearl(gateway.getPersistentData(), gateway.registryAccess());
        if (source.isEmpty() || source.getItem() != ModItems.RIFT_GATE_PEARL.get()) {
            return;
        }
        int waveNumber = RiftGatewayBehavior.getCurrentWaveNumber(gateway);
        this.applyAttributeEffects(source, event.getWaveEntity(), waveNumber);
        this.applyDamageBonus(source, event.getWaveEntity(), waveNumber);
        int health = EffectHelper.getEffectLevel(source, RiftEffects.RIFT_MOB_BASE_HEALTH);
        AttributeInstance maxHealth = event.getWaveEntity().getAttribute(Attributes.MAX_HEALTH);
        if (maxHealth != null) {
            if (health > 0) {
                maxHealth.setBaseValue(health);
            }
            this.applyWaveHealthMultiplier(maxHealth, waveNumber);
            event.getWaveEntity().setHealth(event.getWaveEntity().getMaxHealth());
        }
    }

    @SubscribeEvent
    public void onLivingDrops(LivingDropsEvent event) {
        if (event.getEntity().getPersistentData().getBoolean(RiftGatewayBehavior.RIFT_WAVE_MOB_TAG)) {
            event.getDrops().clear();
        }
    }

    @SubscribeEvent
    public void onLivingDamage(LivingDamageEvent.Pre event) {
        Entity entity = event.getSource().getEntity();
        if (!(entity instanceof LivingEntity attacker) || !attacker.getPersistentData().getBoolean(RiftGatewayBehavior.RIFT_WAVE_MOB_TAG)) {
            return;
        }
        double addition = attacker.getPersistentData().getDouble(RiftGatewayBehavior.RIFT_DAMAGE_ADDITION_TAG);
        double multiplier = attacker.getPersistentData().getDouble(RiftGatewayBehavior.RIFT_DAMAGE_MULTIPLIER_TAG);
        if (addition > 0.0 || multiplier > 0.0) {
            event.setNewDamage((float) (((double) event.getNewDamage() + addition) * multiplier));
        }
    }

    private void applyAttributeEffects(ItemStack source, LivingEntity target, int waveNumber) {
        double scale = RiftPearlData.getAttributeScale(source);
        for (Attribute attribute : BuiltInRegistries.ATTRIBUTE) {
            ResourceLocation attributeId = BuiltInRegistries.ATTRIBUTE.getKey(attribute);
            if (RiftConfigBridge.isAttributeBlocked(attributeId)) {
                continue;
            }
            String name = RiftEffects.attributeName(attribute);
            double addition = (double) EffectHelper.getEffectLevel(source, RiftEffects.attributeEffect(attribute)) * scale * (double) waveNumber;
            double multiplier = (double) EffectHelper.getEffectEfficiency(source, RiftEffects.attributeEffect(attribute)) * scale;
            AttributeInstance instance = target.getAttribute(BuiltInRegistries.ATTRIBUTE.wrapAsHolder(attribute));
            if (instance == null) {
                continue;
            }
            if (addition > 0.0) {
                ResourceLocation id = ResourceLocation.fromNamespaceAndPath("tetra_gateways", "rift_attribute_addition/" + name);
                if (instance.getModifier(id) == null) {
                    instance.addTransientModifier(new AttributeModifier(id, addition, AttributeModifier.Operation.ADD_VALUE));
                }
            }
            for (int wave = 1; wave <= waveNumber && multiplier > 0.0; ++wave) {
                ResourceLocation id = ResourceLocation.fromNamespaceAndPath("tetra_gateways", "rift_attribute_multiplier/" + name + "/" + wave);
                if (instance.getModifier(id) != null) {
                    continue;
                }
                instance.addTransientModifier(new AttributeModifier(id, multiplier, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
            }
        }
    }

    private void applyDamageBonus(ItemStack source, LivingEntity target, int waveNumber) {
        double scale = RiftPearlData.getAttributeScale(source);
        double addition = (double) EffectHelper.getEffectLevel(source, RiftEffects.RIFT_DAMAGE_BONUS) * scale * (double) waveNumber;
        double multiplierPerWave = (double) EffectHelper.getEffectEfficiency(source, RiftEffects.RIFT_DAMAGE_BONUS) * scale;
        double multiplier = 1.0 + multiplierPerWave * (double) waveNumber;
        target.getPersistentData().putDouble(RiftGatewayBehavior.RIFT_DAMAGE_ADDITION_TAG, addition);
        target.getPersistentData().putDouble(RiftGatewayBehavior.RIFT_DAMAGE_MULTIPLIER_TAG, multiplier);
    }

    private void applyWaveHealthMultiplier(AttributeInstance maxHealth, int waveNumber) {
        for (int wave = 2; wave <= waveNumber; ++wave) {
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath("tetra_gateways", "rift_wave_health/" + wave);
            if (maxHealth.getModifier(id) != null) {
                continue;
            }
            maxHealth.addTransientModifier(new AttributeModifier(id, 1.0, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        }
    }
}
