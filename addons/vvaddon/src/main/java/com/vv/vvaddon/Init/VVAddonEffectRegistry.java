package com.vv.vvaddon.Init;

import com.vv.vvaddon.MainVVAddon;
import com.vv.vvaddon.Effect.PhantomPersuitMobEffect;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

// 注意：该类在 1.20.1 原版中从未在 MainVVAddon 中注册，phantom_persuit 实际不存在。
// 按原样保留（不注册），不要当作既有功能使用。
public class VVAddonEffectRegistry {
    public static final DeferredRegister<MobEffect> REGISTRY = DeferredRegister.create(Registries.MOB_EFFECT, MainVVAddon.MODID);
	public static final DeferredHolder<MobEffect, MobEffect> PHANTOM_PERSUIT = REGISTRY.register("phantom_persuit", PhantomPersuitMobEffect::new);  
}
