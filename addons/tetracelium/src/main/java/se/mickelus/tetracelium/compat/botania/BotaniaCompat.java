package se.mickelus.tetracelium.compat.botania;

import javax.annotation.ParametersAreNonnullByDefault;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.NeoForge;

@ParametersAreNonnullByDefault
public class BotaniaCompat {
    public static final String modId = "botania";
    public static final Boolean isLoaded = ModList.get().isLoaded("botania");

    public static void init() {
        NeoForge.EVENT_BUS.register(ManaRepair.class);
    }

    public static void clientSetup() {
        ManaRepair.clientInit();
    }
}
