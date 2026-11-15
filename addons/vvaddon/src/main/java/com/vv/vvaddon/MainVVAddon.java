package com.vv.vvaddon;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.common.NeoForge;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.vv.vvaddon.Config.VVAddonConfig;
import com.vv.vvaddon.Handler.VVAddonEventHandler;
import com.vv.vvaddon.Renderer.PhantomArrowRenderer;
import com.vv.vvaddon.Init.VACoe;

import static com.vv.vvaddon.Init.VVAddonEntityRegistry.ENTITIES;
import static com.vv.vvaddon.Init.VVAddonItemRegistry.ITEMS;
import static com.vv.vvaddon.Init.VVAddonEntityRegistry.PHANTOM_ARROW;
import static com.vv.vvaddon.Init.VVAddonSoundRegistry.SOUNDS;
import static com.vv.vvaddon.Init.VVAddonCreativeTabRegistry.TABS;

@Mod(MainVVAddon.MODID)
public class MainVVAddon
{
    public static final String MODID = "vvaddon";
    public static final Logger LOGGER = LogManager.getLogger();

    // 1.21.1 / NeoForge：事件总线由构造器注入，不再使用 FMLJavaModLoadingContext
    public MainVVAddon(IEventBus modEventBus, ModContainer modContainer)
    {
        // 1.21.1：NeoForge 的事件总线不再允许注册没有任何 @SubscribeEvent 方法的对象，
        // 原版这里对 MainVVAddon 自身的 register(this) 会抛 IllegalArgumentException，已移除。
        NeoForge.EVENT_BUS.register(new VVAddonEventHandler());
        modEventBus.addListener(this::entityRendererSetup);
        ITEMS.register(modEventBus);
        ENTITIES.register(modEventBus);
        SOUNDS.register(modEventBus);
        TABS.register(modEventBus);

        // 1.21.1：ModConfigSpec 不再提供 Forge 的 setConfig(CommentedFileConfig)，
        // 改由 ModContainer 注册；配置文件变为 config/vvaddon-common.toml（原为 vvaddon.toml）。
        modContainer.registerConfig(ModConfig.Type.COMMON, VVAddonConfig.spec);
        // 配置在构造期尚未加载，VACoe 的读取推迟到配置加载/重载事件。
        modEventBus.addListener((ModConfigEvent.Loading event) -> new VACoe());
        modEventBus.addListener((ModConfigEvent.Reloading event) -> new VACoe());
    }
    
    private void entityRendererSetup(final EntityRenderersEvent.RegisterRenderers event) {

        event.registerEntityRenderer(PHANTOM_ARROW.get(), PhantomArrowRenderer::new);
    }

}
