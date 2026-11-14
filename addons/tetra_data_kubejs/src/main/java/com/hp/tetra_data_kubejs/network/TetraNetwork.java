package com.hp.tetra_data_kubejs.network;

import com.hp.tetra_data_kubejs.data.TetraScriptRegistry;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import se.mickelus.mutil.network.PacketHandler;

@EventBusSubscriber(modid="tetra_data_kubejs", bus=EventBusSubscriber.Bus.MOD)
public final class TetraNetwork {
    private static final String PROTOCOL_VERSION = "1";
    private static final PacketHandler HANDLER = new PacketHandler("tetra_data_kubejs", "1", "1");
    private static final Set<ServerPlayer> ONLINE_PLAYERS = Collections.synchronizedSet(new LinkedHashSet<>());

    static {
        HANDLER.registerPacket(TetraTranslationSyncPacket.class, TetraTranslationSyncPacket::new);
    }

    private TetraNetwork() {
    }

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        HANDLER.register(event);
    }

    public static void syncAllTranslationsTo(ServerPlayer player) {
        if (player == null) {
            return;
        }
        Map<String, Map<String, String>> translations = TetraScriptRegistry.getAllTranslations();
        TetraNetwork.sendTranslationsTo(player, translations);
    }

    public static void addOnlinePlayer(ServerPlayer player) {
        if (player != null) {
            ONLINE_PLAYERS.add(player);
        }
    }

    public static void removeOnlinePlayer(ServerPlayer player) {
        if (player != null) {
            ONLINE_PLAYERS.remove(player);
        }
    }

    public static void broadcastAllTranslations() {
        Map<String, Map<String, String>> translations = TetraScriptRegistry.getAllTranslations();
        synchronized (ONLINE_PLAYERS) {
            ServerPlayer[] players = ONLINE_PLAYERS.toArray(ServerPlayer[]::new);
            for (ServerPlayer player : players) {
                TetraNetwork.sendTranslationsTo(player, translations);
            }
        }
    }

    private static void sendTranslationsTo(ServerPlayer player, Map<String, Map<String, String>> translations) {
        HANDLER.sendTo(new TetraTranslationSyncPacket(translations), player);
    }
}
