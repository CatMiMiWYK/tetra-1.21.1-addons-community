package net.yiran.tetrajs.compat;

import dev.latvian.mods.kubejs.registry.BuilderTypeRegistry;
import java.util.function.BiConsumer;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.NeoForge;
import net.yiran.tetrajs.TetraJS;
import net.yiran.tetrajs.compat.curios.CuriosHelper;
import net.yiran.tetrajs.compat.curios.builders.items.CuriosModularItemBuilder;
import net.yiran.tetrajs.compat.curios.builders.items.DynamicCuriosModularItemBuilder;
import net.yiran.tetrajs.compat.tetra_addition.TetraAdditionEvents;
import net.yiran.tetrajs.compat.tetra_addition.events.WorkbenchTileCraftEventJS;

public class CompatManager {
    public static boolean CuriosLoaded = ModList.get().isLoaded("curios");
    public static boolean TetraAdditionLoaded = ModList.get().isLoaded("tetra_addition");

    public static void init(IEventBus modEventBus) {
        TetraJS.LOGGER.debug("TetraJS Compat Init");
        if (TetraAdditionLoaded) {
            TetraAdditionEvents.init();
            NeoForge.EVENT_BUS.register(WorkbenchTileCraftEventJS.class);
        }
    }

    public static void registerCompatItemBuilder(BuilderTypeRegistry.Callback<Item> registryInfo) {
        if (CuriosLoaded) {
            registryInfo.add("tetrajs:curios_modular_item", CuriosModularItemBuilder.class, CuriosModularItemBuilder::new);
            registryInfo.add("tetrajs:dynamic_curios_modular_item", DynamicCuriosModularItemBuilder.class, DynamicCuriosModularItemBuilder::new);
        }
    }

    public static void registerCompatBindings(BiConsumer<String, Object> register) {
        if (CuriosLoaded) {
            register.accept("CuriosHelper", CuriosHelper.getInstance());
        }
    }
}
