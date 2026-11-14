package net.yiran.morerequirement.sorter;

import com.google.gson.JsonElement;
import se.mickelus.tetra.gui.stats.data.StatRegistry;
import se.mickelus.tetra.gui.stats.getter.IStatGetter;

public class MyStatRegistry {
    public static void init() {
        StatRegistry.registerStatGetter("mr:item", MyStatRegistry::ItemGetter);
    }

    public static IStatGetter ItemGetter(JsonElement json) {
        ItemData itemData = StatRegistry.gson.fromJson(json, ItemData.class);
        return new StatGetterItem(itemData.stat, itemData.items, itemData.tag);
    }

    public record ItemData(IStatGetter stat, String[] items, String tag) {
    }
}
