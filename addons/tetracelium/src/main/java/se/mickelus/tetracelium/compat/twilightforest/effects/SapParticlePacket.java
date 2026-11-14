package se.mickelus.tetracelium.compat.twilightforest.effects;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import se.mickelus.mutil.network.AbstractPacket;

public class SapParticlePacket extends AbstractPacket {
    public static final CustomPacketPayload.Type<SapParticlePacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("tetracelium", "sap_particle"));
    InteractionHand hand;
    Vec3 target;

    public SapParticlePacket() {
    }

    public SapParticlePacket(InteractionHand hand, Vec3 target) {
        this.hand = hand;
        this.target = target;
    }

    @Override
    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeByte(this.hand == null ? 2 : this.hand.ordinal());
        buffer.writeVec3(this.target);
    }

    @Override
    public void fromBytes(FriendlyByteBuf buffer) {
        byte handByte = buffer.readByte();
        if (handByte != 2) {
            this.hand = InteractionHand.values()[handByte];
        }
        this.target = buffer.readVec3();
    }

    @Override
    public void handle(Player player) {
        SappingEffect.makeRedMagicTrail(player.level(), this.hand, player, this.target);
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
