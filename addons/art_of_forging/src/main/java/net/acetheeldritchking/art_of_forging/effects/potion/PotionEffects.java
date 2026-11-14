package net.acetheeldritchking.art_of_forging.effects.potion;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class PotionEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, "art_of_forging");
    public static final DeferredHolder<MobEffect, MobEffect> EVOKING_MAW = MOB_EFFECTS.register("evoking_maw", EvokingMawPotionEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> DEFUSE_CREEPER = MOB_EFFECTS.register("defuse_creeper", DefuseCreeperPotionEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> DEVOURING = MOB_EFFECTS.register("devouring", DevouringPotionEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> MORTAL_WOUNDS = MOB_EFFECTS.register("mortal_wounds", MortalWoundsPotionEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> TARGETED = MOB_EFFECTS.register("targeted", TargetedPotionEffect::new);

    public static void register(IEventBus bus) {
        MOB_EFFECTS.register(bus);
    }
}
