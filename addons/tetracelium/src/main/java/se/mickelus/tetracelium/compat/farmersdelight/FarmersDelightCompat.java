package se.mickelus.tetracelium.compat.farmersdelight;

import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.NeoForge;

public class FarmersDelightCompat {
    public static final String modId = "farmersdelight";
    public static final Boolean isLoaded = ModList.get().isLoaded("farmersdelight");

    public static void init() {
        NeoForge.EVENT_BUS.register(FarmersDelightEvents.class);
    }
}
