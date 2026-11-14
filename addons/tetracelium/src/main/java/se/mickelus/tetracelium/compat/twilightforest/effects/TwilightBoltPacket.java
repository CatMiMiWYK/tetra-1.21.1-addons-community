package se.mickelus.tetracelium.compat.twilightforest.effects;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import se.mickelus.mutil.network.AbstractPacket;

public class TwilightBoltPacket extends AbstractPacket {
    public static final CustomPacketPayload.Type<TwilightBoltPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("tetracelium", "twilight_bolt"));

    @Override
    public void toBytes(FriendlyByteBuf buffer) {
    }

    @Override
    public void fromBytes(FriendlyByteBuf buffer) {
    }

    @Override
    public void handle(Player player) {
        TwilightBoltEffect.handleServer(player);
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
