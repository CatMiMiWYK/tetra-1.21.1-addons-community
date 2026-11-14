package com.hp.tetra_data_kubejs.network;

import com.hp.tetra_data_kubejs.client.TetraClientTranslationHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber(modid="tetra_data_kubejs", bus=EventBusSubscriber.Bus.GAME)
public final class TetraTranslationSyncEvents {
    private TetraTranslationSyncEvents() {
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        if (player instanceof ServerPlayer serverPlayer) {
            TetraNetwork.addOnlinePlayer(serverPlayer);
            TetraNetwork.syncAllTranslationsTo(serverPlayer);
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        Player player = event.getEntity();
        if (player instanceof ServerPlayer serverPlayer) {
            TetraNetwork.removeOnlinePlayer(serverPlayer);
        }
    }

    @EventBusSubscriber(modid="tetra_data_kubejs", bus=EventBusSubscriber.Bus.GAME, value={Dist.CLIENT})
    public static final class ClientEvents {
        private ClientEvents() {
        }

        @SubscribeEvent
        public static void onClientDisconnected(ClientPlayerNetworkEvent.LoggingOut event) {
            TetraClientTranslationHelper.clearClientTranslations();
        }
    }
}
