package com.hp;

import com.hp.gateway.RiftGatewayEvents;
import com.hp.item.ModItems;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

@Mod(Tetra_gateways.MODID)
public class Tetra_gateways {
    public static final String MODID = "tetra_gateways";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Tetra_gateways(IEventBus modEventBus, ModContainer modContainer) {
        ModItems.ITEMS.register(modEventBus);
        if (FMLEnvironment.dist.isClient()) {
            com.hp.client.RiftClientSetup.init(modEventBus);
        }
        NeoForge.EVENT_BUS.register(new RiftGatewayEvents());
    }
}
