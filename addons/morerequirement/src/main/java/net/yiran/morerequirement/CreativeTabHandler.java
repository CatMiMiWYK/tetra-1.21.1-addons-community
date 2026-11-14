package net.yiran.morerequirement;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonWriter;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.registries.DeferredRegister;
import se.mickelus.tetra.blocks.scroll.ScrollData;
import se.mickelus.tetra.blocks.scroll.ScrollItem;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.stream.Stream;
import net.yiran.morerequirement.data.MRDataManager;

public class CreativeTabHandler {
    public static void init() {
        if (needCreateCreativeTab()) {
            registerCreativeModeTab();
        } else {
            MoreRequirement.ModEventBus.addListener(CreativeTabHandler::onCreativeTabsBuild);
        }
    }

    public static boolean needCreateCreativeTab() {
        Path path = FMLPaths.CONFIGDIR.get().resolve("morerequirement-common.toml");
        if (!Files.exists(path)) {
            return true;
        }
        try {
            return Files.readAllLines(path).stream()
                    .filter(line -> !line.isBlank())
                    .filter(line -> !line.startsWith("#"))
                    .filter(line -> line.contains("CreateSingleCreativeTab"))
                    .anyMatch(line -> line.contains("true"));
        } catch (IOException e) {
            return true;
        }
    }

    public static void addScrollItems(CreativeModeTab.Output output) {
        MRDataManager.instance.scrolls.getData().values().stream()
                .flatMap(Stream::of)
                .map(CreativeTabHandler::getScrollItem)
                .forEach(output::accept);
    }

    public static void writeScrollData(ItemStack stack) {
        JsonArray data = new JsonArray();
        JsonObject data1 = getScrollData(stack).getAsJsonObject();
        data1.addProperty("ribbon", Integer.parseInt(data1.get("ribbon").getAsString(), 16));
        data.add(data1);
        Path path = FMLPaths.GAMEDIR.get().resolve("kubejs").resolve("data").resolve("morerequirement")
                .resolve("scrolls").resolve(data1.get("key").getAsString().replace(":", "_").replace("/", "_") + ".json");
        if (!Files.exists(path.getParent())) {
            try {
                Files.createDirectories(path.getParent(), new FileAttribute[0]);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try (BufferedWriter fileWriter = Files.newBufferedWriter(path)) {
            JsonWriter jsonWriter = new JsonWriter((Writer) fileWriter);
            jsonWriter.setIndent("\t");
            jsonWriter.setSerializeNulls(true);
            jsonWriter.setLenient(true);
            Streams.write(data, jsonWriter);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static JsonElement getScrollData(ItemStack stack) {
        return ScrollData.CODEC.encodeStart(JsonOps.INSTANCE, ScrollData.read(stack)).result().get();
    }

    public static ItemStack getScrollItem(ScrollData scrollData) {
        return scrollData.createItemStack();
    }

    public static void onCreativeTabsBuild(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == ResourceKey.create(Registries.CREATIVE_MODE_TAB, ResourceLocation.fromNamespaceAndPath("tetra", "default"))) {
            addScrollItems(event);
        }
    }

    public static void registerCreativeModeTab() {
        DeferredRegister<CreativeModeTab> creativeTabs = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MoreRequirement.MODID);
        creativeTabs.register("scroll", () -> CreativeModeTab.builder()
                .icon(() -> new ItemStack(ScrollItem.instance))
                .title(Component.translatable("more_requirement.scrollGroup"))
                .displayItems((itemDisplayParameters, output) -> {
                    addScrollItems(output);
                    ScrollItem.instance.getCreativeTabItems().forEach(output::accept);
                })
                .build());
        creativeTabs.register(MoreRequirement.ModEventBus);
    }
}
