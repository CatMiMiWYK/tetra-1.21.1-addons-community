package net.yiran.extraholopage.compat;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.fml.ModList;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.yiran.extraholopage.api.ExtraHoloRegister;

public class CompatHandler {
    public static ResourceLocation HOLO = ResourceLocation.fromNamespaceAndPath("extraholopage", "textures/gui/holo.png");
    public static boolean OverhaulIsLoaded = ModList.get().isLoaded("tetra_material_overhaul");

    public static void onClientSetup(FMLClientSetupEvent event) {
        if (ModList.get().isLoaded("art_of_forging")) {
            CompatHandler.AoF_Compat();
        }
        if (ModList.get().isLoaded("secrets_of_forging_revelations")) {
            CompatHandler.SoF_Compat();
        }
    }

    public static void AoF_Compat() {
        Item item = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("art_of_forging", "modular_artifact"));
        ExtraHoloRegister.register(item).setTexture(HOLO, 0, 0);
    }

    public static void SoF_Compat() {
        Item item = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("tetra", "modular_polearm"));
        ExtraHoloRegister.register(item).setTexture(HOLO, 38, 0);
    }
}
