package net.yiran.extraholopage.gui;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.neoforged.neoforge.common.ItemAbility;
import se.mickelus.tetra.gui.GuiTextures;
import se.mickelus.tetra.gui.stats.bar.GuiStatBar;
import se.mickelus.tetra.gui.stats.bar.GuiStatBarTool;
import se.mickelus.tetra.module.data.GlyphData;

public class ToolBarHelper {
    public static Codec<ItemAbility> T_CODEC = Codec.STRING.xmap(ItemAbility::get, ItemAbility::name).fieldOf("toolAction").codec();
    public static Codec<GlyphData> G_CODEC = RecordCodecBuilder.create(instance -> instance.group(ResourceLocation.CODEC.optionalFieldOf("textureLocation", GuiTextures.toolActions).forGetter(i -> i.textureLocation), Codec.INT.optionalFieldOf("textureX", 240).forGetter(i -> i.textureX), Codec.INT.optionalFieldOf("textureY", 0).forGetter(i -> i.textureY), Codec.INT.optionalFieldOf("tint", -1).forGetter(i -> i.textureY)).apply(instance, GlyphData::new));

    public static Map<ItemAbility, GlyphData> parseG(Resource resource) {
        try (BufferedReader reader = resource.openAsReader();) {
            JsonElement json = JsonParser.parseReader(reader);
            Optional<GlyphData> resultG = G_CODEC.parse(JsonOps.INSTANCE, json).result();
            Optional<ItemAbility> resultT = T_CODEC.parse(JsonOps.INSTANCE, json).result();
            if (!resultG.isPresent()) return null;
            if (!resultT.isPresent()) return null;
            Map<ItemAbility, GlyphData> map = Map.of(resultT.get(), resultG.get());
            return map;
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static GuiStatBar parseT(Resource resource) {
        try (BufferedReader reader = resource.openAsReader();) {
            JsonElement json = JsonParser.parseReader(reader);
            Optional<ItemAbility> result = T_CODEC.parse(JsonOps.INSTANCE, json).result();
            if (!result.isPresent()) return null;
            ItemAbility itemAbility = result.get();
            GuiStatBar guiStatBar = new GuiStatBarTool(0, 0, 59, itemAbility).setContexts("tetra:holosphere", "tetra:workbench");
            return guiStatBar;
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
