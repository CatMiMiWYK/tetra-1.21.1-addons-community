package com.sswltzz.tetracompat;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = TetraCompat.MODID, bus = EventBusSubscriber.Bus.MOD)
public class TCConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    private static final ModConfigSpec.BooleanValue TETRACELIUM_KNIFE_COMPAT = BUILDER
            .comment("Enable the tetracelium knife compat.")
            .define("tetraceliumKnifeCompat", true);
    static final ModConfigSpec SPEC = BUILDER.build();

    public static boolean tetraceliumKnifeCompat;

    @SubscribeEvent
    static void onLoad(ModConfigEvent event) {
        tetraceliumKnifeCompat = TETRACELIUM_KNIFE_COMPAT.get();
    }
}
