package net.yiran.fot.common;

import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.yiran.fot.Config;
import se.mickelus.tetra.items.modular.IModularItem;

public class AttachCapability {
    public static Object2BooleanOpenHashMap<Item> cache = new Object2BooleanOpenHashMap<>();

    public static void attach(RegisterCapabilitiesEvent event) {
        for (Item item : BuiltInRegistries.ITEM) {
            if (item instanceof IModularItem) {
                event.registerItem(Capabilities.EnergyStorage.ITEM,
                        (ICapabilityProvider<ItemStack, Void, IEnergyStorage>) (stack, context) -> checkInCache(stack.getItem()) ? new TetraFEStore(stack) : null,
                        item);
            }
        }
    }

    public static boolean checkInCache(Item item) {
        if (!cache.containsKey(item)) {
            String key = BuiltInRegistries.ITEM.getKey(item).toString();
            cache.put(item, !Config.NotEnergyItems.get().contains(key));
        }
        return cache.getBoolean(item);
    }
}
