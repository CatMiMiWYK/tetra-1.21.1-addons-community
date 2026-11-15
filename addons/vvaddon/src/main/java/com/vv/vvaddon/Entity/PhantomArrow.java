package com.vv.vvaddon.Entity;

import com.vv.vvaddon.Init.VVAddonEntityRegistry;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class PhantomArrow extends AbstractArrow{
    private static final EntityDataAccessor<Float> PHANTOM_ARROW_DAMAGE = SynchedEntityData.defineId(PhantomArrow.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> PHANTOM_ARROW_TRIGGER = SynchedEntityData.defineId(PhantomArrow.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> PHANTOM_ARROW_IMMU = SynchedEntityData.defineId(PhantomArrow.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> PHANTOM_ARROW_TICK = SynchedEntityData.defineId(PhantomArrow.class, EntityDataSerializers.INT);
    
    private int tickCount = 0;
    private static final boolean NO_GRAVITY = true;
    private static final byte PIERCE = 64;

    public PhantomArrow(EntityType<? extends PhantomArrow> entityType, Level level) {
        super(entityType, level);
        initArrow();
    }
    public PhantomArrow(Level level, LivingEntity shooter) {
        // 1.21.1：AbstractArrow 不再有 (EntityType, LivingEntity, Level) 构造器，
        // 改为 (EntityType, LivingEntity, Level, ItemStack pickup, ItemStack weapon)
        super(VVAddonEntityRegistry.PHANTOM_ARROW.get(), shooter, level, ItemStack.EMPTY, ItemStack.EMPTY);
        initArrow();
    }

    public PhantomArrow(Level level, double x, double y, double z) {
        // 1.21.1：坐标构造器同样需要 pickup/weapon 两个 ItemStack
        super(VVAddonEntityRegistry.PHANTOM_ARROW.get(), x, y, z, level, ItemStack.EMPTY, ItemStack.EMPTY);
        initArrow();
    }

    private void initArrow() {
        setNoGravity(NO_GRAVITY);
        // 【上游 API 已移除，待人工评估】1.21.1 中 AbstractArrow#setPierceLevel(byte) 为 private，
        // 子类无法再设置穿透等级；原版 PIERCE = 64 的穿透行为在 1.21.1 不可用（默认穿透 0）。
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(PHANTOM_ARROW_IMMU, false);
        builder.define(PHANTOM_ARROW_TICK, 10);
        builder.define(PHANTOM_ARROW_DAMAGE, 2.0F);
        builder.define(PHANTOM_ARROW_TRIGGER, true);
    }


    // 1.21.1：AbstractArrow 用 getDefaultPickupItem() 提供默认拾取物
    @Override
    protected ItemStack getDefaultPickupItem() {
        return ItemStack.EMPTY;
    }

    @Override
    protected ItemStack getPickupItem() {
        return ItemStack.EMPTY;
    }

    @Override
    public void tick() {
        if(tickCount++ == getPhantomArrowTick()){
            this.setDeltaMovement(this.getDeltaMovement().scale(200F));
        }
        super.tick();
        if (this.inGround || tickCount > getPhantomArrowTick() + 100) {
            this.discard();
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        Vec3 motion = getDeltaMovement();
        super.onHitEntity(entityHitResult);
        if (isAlive()) {
            setDeltaMovement(motion);
        }
    }    
    public float getPhantomArrowDamage() {
        return this.entityData.get(PHANTOM_ARROW_DAMAGE);
    }

    public void setPhantomArrowDamage(float f) {
        this.entityData.set(PHANTOM_ARROW_DAMAGE, f);
    }


    public boolean getPhantomArrowImmu(){
        return this.entityData.get(PHANTOM_ARROW_IMMU);
    }

    public boolean getPhantomArrowTrigger() {
        return this.entityData.get(PHANTOM_ARROW_TRIGGER);
    }

    public int getPhantomArrowTick() {
        return this.entityData.get(PHANTOM_ARROW_TICK);
    }

    public void setPhantomArrowTick(int i) {
        this.entityData.set(PHANTOM_ARROW_TICK, i);
    }

    public void setPhantomArrowTrigger(boolean b){
        this.entityData.set(PHANTOM_ARROW_TRIGGER, b);
    }

    public void setPhantomArrowImmu(boolean b){
        this.entityData.set(PHANTOM_ARROW_IMMU, b);
    }
}
