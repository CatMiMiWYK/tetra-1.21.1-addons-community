package com.vv.vvaddon.Effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.neoforged.neoforge.client.extensions.common.IClientMobEffectExtensions;

public class PhantomPersuitMobEffect extends MobEffect{
	public PhantomPersuitMobEffect() {
		super(MobEffectCategory.NEUTRAL, -6750055);
	}

	@Override
	public String getDescriptionId() {
		return "effect.vvaddon.phantom_persuit";
	}


    // 1.21.1：addAttributeModifiers 的 LivingEntity 参数被移除，签名改为 (AttributeMap, int)
    public void addAttributeModifiers(AttributeMap map, int level) {
		super.addAttributeModifiers(map, level);
	}

    // 1.21.1：applyEffectTick 返回值由 void 改为 boolean（表示该 tick 是否生效）
    @Override
	public boolean applyEffectTick(LivingEntity entity, int amplifier) {
		return true;
	}

	// 1.21.1：isDurationEffectTick 更名为 shouldApplyEffectTickThisTick
	@Override
	public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
		return duration % 20 == 0;
	}

	@Override
	public void initializeClient(java.util.function.Consumer<IClientMobEffectExtensions> consumer) {
		consumer.accept(new IClientMobEffectExtensions() {
			@Override
			public boolean isVisibleInGui(MobEffectInstance effect) {
				return false;
			}
		});
	}
    
}
