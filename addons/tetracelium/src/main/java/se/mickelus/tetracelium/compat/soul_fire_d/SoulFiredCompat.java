package se.mickelus.tetracelium.compat.soul_fire_d;

import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.NeoForge;

public class SoulFiredCompat {
    public static final String modId = "soul_fire_d";
    public static final Boolean isLoaded = ModList.get().isLoaded("soul_fire_d");

    public static void init() {
        NeoForge.EVENT_BUS.register(SoulFiredEffect.class);
    }
}
