package net.tetra.modify.api.event;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;

public class LivingDamageEvent extends LivingEvent implements ICancellableEvent {
    private final DamageSource source;
    private final float originalAmount;
    private float baseAmount = 0.0f;
    private float extraAmount = 0.0f;
    private float multiplyBaseAmount = 1.0f;
    private float multiplyTotalAmount = 1.0f;

    public LivingDamageEvent(LivingEntity entity, DamageSource source, float amount) {
        super(entity);
        this.source = source;
        this.originalAmount = amount;
    }

    public DamageSource getSource() {
        return this.source;
    }

    public float getOriginalAmount() {
        return this.originalAmount;
    }

    public void addBaseAmount(float amount) {
        this.baseAmount += amount;
    }

    public float getBaseAmount() {
        return this.baseAmount;
    }

    public void addExtraAmount(float amount) {
        this.extraAmount += amount;
    }

    public float getExtraAmount() {
        return this.extraAmount;
    }

    public void addMultiplyBaseAmount(float amount) {
        this.multiplyBaseAmount += amount;
    }

    public float getMultiplyBaseAmount() {
        return this.multiplyBaseAmount;
    }

    public void addMultiplyTotalAmount(float amount) {
        this.multiplyTotalAmount *= amount;
    }

    public float getMultiplyTotalAmount() {
        return this.multiplyTotalAmount;
    }

    public float getResultAmount() {
        return (this.originalAmount + this.baseAmount) * this.multiplyBaseAmount * this.multiplyTotalAmount + this.extraAmount;
    }

    public static class Post extends LivingDamageEvent {
        public Post(LivingEntity entity, DamageSource source, float amount) {
            super(entity, source, amount);
        }
    }

    public static class Pre extends LivingDamageEvent {
        public Pre(LivingEntity entity, DamageSource source, float amount) {
            super(entity, source, amount);
        }
    }
}
