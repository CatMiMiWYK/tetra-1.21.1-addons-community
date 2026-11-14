package net.tetra.modify;

import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.tetra.modify.data.ScrollDataManager;
import org.slf4j.Logger;
import se.mickelus.tetra.blocks.scroll.ScrollItem;

@Mod(value = TetraModifyCore.MODID)
public class TetraModifyCore {
    public static final String MODID = "tetra_modify_core";
    private static final Logger LOGGER = LogUtils.getLogger();
    DeferredRegister<CreativeModeTab> creativeTabs = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    DeferredHolder<CreativeModeTab, CreativeModeTab> creativeTab = this.creativeTabs.register("scroll", () -> CreativeModeTab.builder()
            .icon(() -> new ItemStack(ScrollItem.instance))
            .title(Component.translatable("tetra_modify_core.scrollGroup"))
            .displayItems((itemDisplayParameters, output) -> {
                ScrollDataManager.addScrollItems(output);
                ScrollItem.instance.getCreativeTabItems().forEach(output::accept);
            })
            .build());

    public TetraModifyCore(IEventBus modBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        if (FMLEnvironment.dist.isClient()) {
            modBus.register(ScrollDataManager.class);
            NeoForge.EVENT_BUS.addListener(TetraModifyCore::onClientTick);
        }
        this.creativeTabs.register(modBus);
    }

    private static void onClientTick(ClientTickEvent.Post event) {
        String ignored = Component.translatable("item.tetra.scroll.omniscient.description").getString();
    }
}
