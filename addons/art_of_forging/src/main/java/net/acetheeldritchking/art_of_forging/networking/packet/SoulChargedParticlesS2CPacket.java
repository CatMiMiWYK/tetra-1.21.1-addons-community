package net.acetheeldritchking.art_of_forging.networking.packet;

import net.acetheeldritchking.art_of_forging.ArtOfForging;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import se.mickelus.mutil.network.AbstractPacket;

public class SoulChargedParticlesS2CPacket
        extends AbstractPacket {
    public static final CustomPacketPayload.Type<SoulChargedParticlesS2CPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(ArtOfForging.MOD_ID, "soul_charged_particles"));
    private double xPos;
    private double yPos;
    private double zPos;

    public SoulChargedParticlesS2CPacket() {
    }

    public SoulChargedParticlesS2CPacket(double xPos, double yPos, double zPos) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.zPos = zPos;
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeDouble(this.xPos);
        buf.writeDouble(this.yPos);
        buf.writeDouble(this.zPos);
    }

    public void fromBytes(FriendlyByteBuf buf) {
        this.xPos = buf.readDouble();
        this.yPos = buf.readDouble();
        this.zPos = buf.readDouble();
    }

    public void handle(Player player) {
        if (player.level().isClientSide) {
            SoulChargedPacketHandler.doSoulParticles(this.xPos, this.yPos, this.zPos);
        }
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
