package com.hp.tetra_data_kubejs;

import com.hp.tetra_data_kubejs.data.TetraScriptRegistry;
import com.hp.tetra_data_kubejs.network.TetraNetwork;
import com.mojang.logging.LogUtils;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.bus.api.IEventBus;
import org.slf4j.Logger;

@Mod(TetraDataKubejs.MODID)
public class TetraDataKubejs {
    public static final String MODID = "tetra_data_kubejs";
    public static final Logger LOGGER = LogUtils.getLogger();

    public TetraDataKubejs(IEventBus modEventBus, ModContainer modContainer) {
        TetraScriptRegistry.setOnTranslationsChangedCallback(TetraNetwork::broadcastAllTranslations);
    }
}
