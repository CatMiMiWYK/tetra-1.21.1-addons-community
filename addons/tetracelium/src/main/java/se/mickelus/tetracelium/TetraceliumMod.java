package se.mickelus.tetracelium;

import java.util.concurrent.CompletableFuture;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import se.mickelus.mutil.network.PacketHandler;
import se.mickelus.mutil.scheduling.AbstractScheduler;
import se.mickelus.mutil.scheduling.ServerScheduler;
import se.mickelus.tetracelium.compat.apotheosis.ApotheosisCompat;
import se.mickelus.tetracelium.compat.botania.BotaniaCompat;
import se.mickelus.tetracelium.compat.farmersdelight.FarmersDelightCompat;
import se.mickelus.tetracelium.compat.farmersdelight.provider.FarmersDelightCuttingRecipeProvider;
import se.mickelus.tetracelium.compat.soul_fire_d.SoulFiredCompat;
import se.mickelus.tetracelium.compat.twilightforest.TwilightForestCompat;
import se.mickelus.tetracelium.compat.twilightforest.effects.SapParticlePacket;
import se.mickelus.tetracelium.compat.twilightforest.effects.TwilightBoltPacket;

@Mod(TetraceliumMod.MOD_ID)
@ParametersAreNonnullByDefault
public class TetraceliumMod {
    public static final String MOD_ID = "tetracelium";
    public static PacketHandler packetHandler;
    public static AbstractScheduler serverScheduler = new ServerScheduler();

    public TetraceliumMod(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.register(this);
        NeoForge.EVENT_BUS.register(this);
        NeoForge.EVENT_BUS.register(serverScheduler);
        TetraceliumRegistries.init(modEventBus);
        if (BotaniaCompat.isLoaded.booleanValue()) {
            BotaniaCompat.init();
        }
        if (FarmersDelightCompat.isLoaded.booleanValue()) {
            FarmersDelightCompat.init();
        }
        if (TwilightForestCompat.isLoaded.booleanValue()) {
            TwilightForestCompat.init();
        }
        if (SoulFiredCompat.isLoaded.booleanValue()) {
            SoulFiredCompat.init();
        }
        packetHandler = new PacketHandler(MOD_ID, "main", "1");
        packetHandler.registerPacket(TwilightBoltPacket.class, TwilightBoltPacket::new);
        packetHandler.registerPacket(SapParticlePacket.class, SapParticlePacket::new);
        modEventBus.addListener(packetHandler::register);
    }

    @SubscribeEvent
    public void setup(FMLCommonSetupEvent event) {
        if (ApotheosisCompat.isLoaded.booleanValue()) {
            ApotheosisCompat.setup();
        }
    }

    @SubscribeEvent
    @OnlyIn(value = Dist.CLIENT)
    public void clientSetup(FMLClientSetupEvent event) {
        if (BotaniaCompat.isLoaded.booleanValue()) {
            BotaniaCompat.clientSetup();
        }
        if (TwilightForestCompat.isLoaded.booleanValue()) {
            TwilightForestCompat.clientInit();
        }
    }

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        DataGenerator dataGenerator = event.getGenerator();
        if (event.includeServer()) {
            PackOutput packOutput = dataGenerator.getPackOutput();
            CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
            dataGenerator.addProvider(true, new FarmersDelightCuttingRecipeProvider(packOutput, lookupProvider));
        }
    }
}
