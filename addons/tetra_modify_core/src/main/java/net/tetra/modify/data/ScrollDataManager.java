package net.tetra.modify.data;

import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.bus.api.SubscribeEvent;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import se.mickelus.tetra.blocks.scroll.ScrollData;
import se.mickelus.tetra.data.DataManager;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class ScrollDataManager extends SimpleJsonResourceReloadListener {
    public static ScrollDataManager instance = new ScrollDataManager();
    public static List<ScrollData[]> scrollData = new ObjectArrayList<>();

    public ScrollDataManager() {
        super(DataManager.gson, "scrolls");
    }

    @SubscribeEvent
    public static void registerClientReloadListeners(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(instance);
    }

    public static void addScrollItems(CreativeModeTab.Output output) {
        scrollData.stream().flatMap(Stream::of).map(ScrollDataManager::getScrollItem).forEach(output::accept);
    }

    public static ItemStack getScrollItem(ScrollData data) {
        return data.createItemStack();
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> jsonElementMap, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        scrollData.clear();
        for (JsonElement value : jsonElementMap.values()) {
            scrollData.add(DataManager.gson.fromJson(value, ScrollData[].class));
        }
    }
}
