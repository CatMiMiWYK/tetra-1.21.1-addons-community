package com.vv.vvaddon.Init;

import com.vv.vvaddon.MainVVAddon;
import com.vv.vvaddon.Entity.PhantomArrow;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class VVAddonEntityRegistry {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, MainVVAddon.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<PhantomArrow>> PHANTOM_ARROW = 
    ENTITIES.register("phantom_arrow", () -> EntityType.Builder.<PhantomArrow>of(PhantomArrow::new, MobCategory.MISC).sized(0.5F, 0.5F).build("phantom_arrow"));
}
