package net.yiran.morerequirement.data;

import com.google.gson.JsonElement;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import se.mickelus.mutil.data.AbstractUpdateDataPacket;

import java.util.Map;

public class MRUpdateDataPacket extends AbstractUpdateDataPacket {
    public static final CustomPacketPayload.Type<MRUpdateDataPacket> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath("morerequirement", "update_data"));

    public MRUpdateDataPacket() {
        super();
    }

    public MRUpdateDataPacket(String directory, Map<ResourceLocation, JsonElement> data) {
        super(directory, data);
    }

    @Override
    public CustomPacketPayload.Type<MRUpdateDataPacket> type() {
        return TYPE;
    }

    @Override
    public void handle(Player player) {
        MRDataManager.instance.onDataReceived(directory, data);
    }
}
