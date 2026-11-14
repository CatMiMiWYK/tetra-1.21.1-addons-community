package net.acetheeldritchking.art_of_forging.networking;

import net.acetheeldritchking.art_of_forging.networking.packet.LifeStealParticlesS2CPacket;
import net.acetheeldritchking.art_of_forging.networking.packet.SoulChargedParticlesS2CPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.PacketDistributor;
import se.mickelus.mutil.network.AbstractPacket;
import se.mickelus.mutil.network.PacketHandler;

public class AoFPackets {
    private static final PacketHandler INSTANCE = new PacketHandler("art_of_forging", "main", "1");

    public static void register() {
        INSTANCE.registerPacket(LifeStealParticlesS2CPacket.class, LifeStealParticlesS2CPacket::new);
        INSTANCE.registerPacket(SoulChargedParticlesS2CPacket.class, SoulChargedParticlesS2CPacket::new);
    }

    public static void sendToClient(AbstractPacket message) {
        INSTANCE.sendToServer(message);
    }

    public static void sendToPlayer(AbstractPacket message, ServerPlayer player) {
        INSTANCE.sendTo(message, player);
    }

    public static void sendToEntity(AbstractPacket message, Entity entity) {
        PacketDistributor.sendToPlayersTrackingEntity(entity, message);
    }
}
