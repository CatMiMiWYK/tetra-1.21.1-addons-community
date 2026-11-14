package ovo.yiran.geotetraarmor.compat;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import ovo.yiran.geotetraarmor.compat.ehp.EHPCompat;

public class CompatHandler {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        if (ModList.get().isLoaded("extraholopage")) {
            EHPCompat.onClientCompat();
        }
    }
}
