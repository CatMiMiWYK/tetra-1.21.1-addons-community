package net.yiran.tetra_view;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

@Mod(TetraView.MODID)
public class TetraView {
    public static final String MODID = "tetra_view";

    public TetraView(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.CLIENT, Config.SPEC);
        modEventBus.addListener(this::regKey);
        Handler.init();
    }

    public void regKey(RegisterKeyMappingsEvent event) {
        event.register(KeyMappings.VIEW);
        event.register(KeyMappings.STORE);
    }
}
