package com.vv.vvaddon.Init;

import com.vv.vvaddon.MainVVAddon;

import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class VVAddonItemRegistry {
    // 1.21.1：ForgeRegistries.ITEMS -> DeferredRegister.createItems（内置注册表）
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MainVVAddon.MODID);

    public static final DeferredItem<Item> REFINE_IA = ITEMS.register("refine_ia",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> REFINE_IB = ITEMS.register("refine_ib",
            () -> new Item(new Item.Properties()));
            
    public static final DeferredItem<Item> REFINE_IIA = ITEMS.register("refine_iia",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> REFINE_IIB = ITEMS.register("refine_iib",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> MINE_INGOT = ITEMS.register("mine_ingot",
            () -> new Item(new Item.Properties()));
            
    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }

}
