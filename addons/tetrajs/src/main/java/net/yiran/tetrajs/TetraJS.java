package net.yiran.tetrajs;

import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.List;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.yiran.tetrajs.compat.CompatManager;
import net.yiran.tetrajs.kubejs.events.EnchantAspectRegisterEventJS;
import net.yiran.tetrajs.kubejs.events.TetraJSEvents;
import org.slf4j.Logger;
import se.mickelus.tetra.TetraMod;
import se.mickelus.tetra.items.InitializableItem;
import se.mickelus.tetra.items.modular.impl.bow.ModularBowItem;
import se.mickelus.tetra.items.modular.impl.crossbow.ModularCrossbowItemImpl;
import se.mickelus.tetra.items.modular.impl.shield.ModularShieldItem;
import se.mickelus.tetra.module.SchematicRegistry;
import se.mickelus.tetra.module.schematic.RepairSchematic;

@Mod(TetraJS.MODID)
public class TetraJS {
    public static final String MODID = "tetrajs";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static List<InitializableItem> items = new ArrayList<>();

    public TetraJS(IEventBus modEventBus, ModContainer modContainer) {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            modEventBus.addListener(TetraJSClient::onClientSetup);
        }
        modEventBus.addListener(EventPriority.LOWEST, this::onCommonSetup);
        CompatManager.init(modEventBus);
    }

    public void onCommonSetup(FMLCommonSetupEvent event) {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            TetraJSClient.itemClientInit(items);
        }
        items.forEach(item -> item.commonInit(TetraMod.packetHandler));
        items.clear();
        items = null;
        TetraJSEvents.EnchantAspectRegister.post(new EnchantAspectRegisterEventJS());
        SchematicRegistry.instance.registerSchematic(new RepairSchematic(ModularShieldItem.instance, "modular_shield"));
        SchematicRegistry.instance.registerSchematic(new RepairSchematic(ModularBowItem.instance, "modular_bow"));
        SchematicRegistry.instance.registerSchematic(new RepairSchematic(ModularCrossbowItemImpl.instance, "modular_crossbow"));
    }
}
