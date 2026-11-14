package net.yiran.extraholopage.api;

import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.world.item.Item;

public class ExtraHoloRegister {
    public static Map<Item, ExtraHoloBuilder> MAP = new LinkedHashMap<Item, ExtraHoloBuilder>();

    public static ExtraHoloBuilder register(Item item) {
        ExtraHoloBuilder builder = new ExtraHoloBuilder(item);
        MAP.put(item, builder);
        return builder;
    }
}
