package net.yiran.morerequirement;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.yiran.morerequirement.craftefffect.CustomCraftingEffectOutcome;
import net.yiran.morerequirement.data.MRDataManager;
import net.yiran.morerequirement.data.MRUpdateDataPacket;
import net.yiran.morerequirement.requirements.AdvancementRequirement;
import net.yiran.morerequirement.requirements.BiomeRequirement;
import net.yiran.morerequirement.requirements.CustomRequirement;
import net.yiran.morerequirement.requirements.DimensionRequirement;
import net.yiran.morerequirement.requirements.EntitiesRequirement;
import net.yiran.morerequirement.requirements.HeightRequirement;
import net.yiran.morerequirement.requirements.MoonPhaseRequirement;
import net.yiran.morerequirement.requirements.MultiblockRequirement;
import net.yiran.morerequirement.requirements.OtherModuleRequirement;
import net.yiran.morerequirement.requirements.PotionEffectRequirement;
import net.yiran.morerequirement.requirements.SeeSkyRequirement;
import net.yiran.morerequirement.requirements.TimeRequirement;
import net.yiran.morerequirement.requirements.WarpTargetItemRequirement;
import net.yiran.morerequirement.requirements.WeatherRequirement;
import net.yiran.morerequirement.requirements.grouprequirement.GroupRequirement;
import net.yiran.morerequirement.sorter.MyStatRegistry;
import org.slf4j.Logger;
import se.mickelus.mutil.network.PacketHandler;
import se.mickelus.tetra.craftingeffect.CraftingEffectRegistry;
import se.mickelus.tetra.module.schematic.requirement.CraftingRequirementDeserializer;

@Mod(MoreRequirement.MODID)
public class MoreRequirement {
    public static final String MODID = "morerequirement";
    private static final Logger LOGGER = LogUtils.getLogger();
    public static IEventBus ModEventBus;
    public static PacketHandler NETWORK;

    public MoreRequirement(IEventBus modEventBus, ModContainer modContainer) {
        ModEventBus = modEventBus;
        NETWORK = new PacketHandler(MODID, "data", "1");
        NETWORK.registerPacket(MRUpdateDataPacket.class, MRUpdateDataPacket::new);
        modEventBus.addListener(NETWORK::register);

        if (FMLEnvironment.dist.isClient()) {
            MyStatRegistry.init();
        }
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        NeoForge.EVENT_BUS.register(MRDataManager.instance);
        CreativeTabHandler.init();
    }

    public static void onModLoaded() {
        CraftingRequirementDeserializer.registerSupplier("mr:group", GroupRequirement.class);
        CraftingRequirementDeserializer.registerSupplier("mr:advancement", AdvancementRequirement.class);
        CraftingRequirementDeserializer.registerSupplier("mr:biome", BiomeRequirement.class);
        CraftingRequirementDeserializer.registerSupplier("mr:custom", CustomRequirement.class);
        CraftingRequirementDeserializer.registerSupplier("mr:dimension", DimensionRequirement.class);
        CraftingRequirementDeserializer.registerSupplier("mr:entities", EntitiesRequirement.class);
        CraftingRequirementDeserializer.registerSupplier("mr:height", HeightRequirement.class);
        CraftingRequirementDeserializer.registerSupplier("mr:moon_phase", MoonPhaseRequirement.class);
        CraftingRequirementDeserializer.registerSupplier("mr:mbd", MultiblockRequirement.class);
        CraftingRequirementDeserializer.registerSupplier("mr:potion", PotionEffectRequirement.class);
        CraftingRequirementDeserializer.registerSupplier("mr:other_module", OtherModuleRequirement.class);
        CraftingRequirementDeserializer.registerSupplier("mr:see_sky", SeeSkyRequirement.class);
        CraftingRequirementDeserializer.registerSupplier("mr:time", TimeRequirement.class);
        CraftingRequirementDeserializer.registerSupplier("mr:weather", WeatherRequirement.class);

        CraftingEffectRegistry.registerConditionType("mr:advancement", AdvancementRequirement.class);
        CraftingEffectRegistry.registerConditionType("mr:biome", BiomeRequirement.class);
        CraftingEffectRegistry.registerConditionType("mr:custom", CustomRequirement.class);
        CraftingEffectRegistry.registerConditionType("mr:dimension", DimensionRequirement.class);
        CraftingEffectRegistry.registerConditionType("mr:entities", EntitiesRequirement.class);
        CraftingEffectRegistry.registerConditionType("mr:height", HeightRequirement.class);
        CraftingEffectRegistry.registerConditionType("mr:moon_phase", MoonPhaseRequirement.class);
        CraftingEffectRegistry.registerConditionType("mr:mbd", MultiblockRequirement.class);
        CraftingEffectRegistry.registerConditionType("mr:potion", PotionEffectRequirement.class);
        CraftingEffectRegistry.registerConditionType("mr:see_sky", SeeSkyRequirement.class);
        CraftingEffectRegistry.registerConditionType("mr:time", TimeRequirement.class);
        CraftingEffectRegistry.registerConditionType("mr:wrap_target", WarpTargetItemRequirement.class);
        CraftingEffectRegistry.registerConditionType("mr:weather", WeatherRequirement.class);

        CraftingEffectRegistry.registerEffectType("mr:custom", CustomCraftingEffectOutcome.class);
    }
}
