package com.hp.tetra_data_kubejs.network;

import com.hp.tetra_data_kubejs.client.TetraClientTranslationHelper;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import se.mickelus.mutil.network.AbstractPacket;

public class TetraTranslationSyncPacket extends AbstractPacket {
    public static final CustomPacketPayload.Type<TetraTranslationSyncPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("tetra_data_kubejs", "translation_sync"));

    private Map<String, Map<String, String>> translations = new LinkedHashMap<>();

    public TetraTranslationSyncPacket() {
    }

    public TetraTranslationSyncPacket(Map<String, Map<String, String>> translations) {
        this.translations = TetraTranslationSyncPacket.copyTranslations(translations);
    }

    @Override
    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeVarInt(this.translations.size());
        this.translations.forEach((language, values) -> {
            buffer.writeUtf(language);
            buffer.writeVarInt(values.size());
            values.forEach((key, value) -> {
                buffer.writeUtf(key);
                buffer.writeUtf(value);
            });
        });
    }

    @Override
    public void fromBytes(FriendlyByteBuf buffer) {
        int languageCount = buffer.readVarInt();
        LinkedHashMap<String, Map<String, String>> decoded = new LinkedHashMap<>();
        for (int languageIndex = 0; languageIndex < languageCount; ++languageIndex) {
            String language = buffer.readUtf();
            int entryCount = buffer.readVarInt();
            LinkedHashMap<String, String> values = new LinkedHashMap<>();
            for (int entryIndex = 0; entryIndex < entryCount; ++entryIndex) {
                values.put(buffer.readUtf(), buffer.readUtf());
            }
            decoded.put(language, values);
        }
        this.translations = decoded;
    }

    @Override
    public void handle(Player player) {
        if (player != null && player.level().isClientSide()) {
            ClientHandler.apply(this.translations);
        }
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    private static final class ClientHandler {
        private static void apply(Map<String, Map<String, String>> translations) {
            TetraClientTranslationHelper.replaceClientTranslations(translations);
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft != null) {
                minecraft.execute(() -> {});
            }
        }
    }

    private static Map<String, Map<String, String>> copyTranslations(Map<String, Map<String, String>> source) {
        LinkedHashMap<String, Map<String, String>> copy = new LinkedHashMap<>();
        if (source == null || source.isEmpty()) {
            return copy;
        }
        source.forEach((language, values) -> copy.put(language, values == null ? Map.of() : new LinkedHashMap<>(values)));
        return copy;
    }
}
