package ovo.yiran.universalscroll.net;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import ovo.yiran.universalscroll.UniversalScroll;
import ovo.yiran.universalscroll.logic.SchematicService;

/**
 * 客户端选择完图纸后发送给服务端，由服务端校验工作台上下文并打开对应图纸。
 */
public record SelectSchematicPayload(BlockPos pos, ResourceLocation schematic) implements CustomPacketPayload {
    public static final Type<SelectSchematicPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(UniversalScroll.MODID, "select_schematic"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SelectSchematicPayload> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, SelectSchematicPayload::pos,
            ResourceLocation.STREAM_CODEC, SelectSchematicPayload::schematic,
            SelectSchematicPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SelectSchematicPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof net.minecraft.server.level.ServerPlayer player) {
                SchematicService.open(player, payload.pos, payload.schematic);
            }
        });
    }
}
