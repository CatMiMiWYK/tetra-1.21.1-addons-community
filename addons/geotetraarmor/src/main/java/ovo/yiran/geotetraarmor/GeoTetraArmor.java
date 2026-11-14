package ovo.yiran.geotetraarmor;

import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import ovo.yiran.geotetraarmor.compat.CompatHandler;
import ovo.yiran.geotetraarmor.items.ModularChestItem;
import ovo.yiran.geotetraarmor.items.ModularFeetItem;
import ovo.yiran.geotetraarmor.items.ModularHeadItem;
import ovo.yiran.geotetraarmor.items.ModularLegsItem;
import ovo.yiran.geotetraarmor.model.GeckoModelData;
import org.slf4j.Logger;
import se.mickelus.tetra.TetraMod;
import se.mickelus.tetra.items.InitializableItem;
import se.mickelus.tetra.module.model.ModuleModelRegistry;

import java.util.ArrayList;
import java.util.List;

@Mod(GeoTetraArmor.MODID)
public class GeoTetraArmor {
    public static final String MODID = "geotetraarmor";
    private static final Logger LOGGER = LogUtils.getLogger();
    public static List<InitializableItem> ITEM = new ArrayList<>();
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    public static final DeferredItem<Item> HEAD = ITEMS.register("head", ModularHeadItem::new);
    public static final DeferredItem<Item> FEET = ITEMS.register("feet", ModularFeetItem::new);
    public static final DeferredItem<Item> CHEST = ITEMS.register("chest", ModularChestItem::new);
    public static final DeferredItem<Item> LEGS = ITEMS.register("legs", ModularLegsItem::new);

    public GeoTetraArmor(IEventBus modEventBus, ModContainer modContainer) {
        ModuleModelRegistry.register("gecko", GeckoModelData.class);
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        ITEMS.register(modEventBus);
        modEventBus.register(CompatHandler.class);
        modEventBus.addListener(this::onCommonSetup);
        modEventBus.addListener(this::onBuildCreativeModeTab);
    }

    public void onCommonSetup(FMLCommonSetupEvent event) {
        for (InitializableItem item : ITEM) {
            item.commonInit(TetraMod.packetHandler);
        }
        ITEM.clear();
        ITEM = null;
    }

    public void onBuildCreativeModeTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == ResourceKey.create(Registries.CREATIVE_MODE_TAB, ResourceLocation.fromNamespaceAndPath("tetra", "default"))) {
            event.accept(HEAD.get().getDefaultInstance());
            event.accept(CHEST.get().getDefaultInstance());
            event.accept(LEGS.get().getDefaultInstance());
            event.accept(FEET.get().getDefaultInstance());
        }
    }
}
