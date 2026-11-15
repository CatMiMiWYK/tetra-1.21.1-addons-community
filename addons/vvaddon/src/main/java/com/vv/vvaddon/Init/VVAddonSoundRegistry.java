package com.vv.vvaddon.Init;

import com.vv.vvaddon.MainVVAddon;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;


public class VVAddonSoundRegistry {

    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT, MainVVAddon.MODID);

    public static final DeferredHolder<SoundEvent, SoundEvent> KATANA_RUSH = registerSoundEvent("katana_rush");

    private static DeferredHolder<SoundEvent, SoundEvent> registerSoundEvent(String name) {
        // 1.21.1：new ResourceLocation(ns, path) 改为 ResourceLocation.fromNamespaceAndPath
        return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(MainVVAddon.MODID, name)));
    }
}
