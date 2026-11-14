package se.mickelus.tetracelium;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import se.mickelus.tetracelium.compat.farmersdelight.DummyKnifeBladeItem;

public class TetraceliumRegistries {
    public static final DeferredRegister<Item> items = DeferredRegister.create(BuiltInRegistries.ITEM, "tetracelium");

    public static void init(IEventBus bus) {
        bus.register(TetraceliumRegistries.class);
        items.register(bus);
        items.register("dummy_knife_blade", DummyKnifeBladeItem::new);
    }
}
