package ovo.yiran.universalscroll.logic;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import se.mickelus.tetra.data.DataManager;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * 通用卷轴等价于“已经拿到全部卷轴”。
 * <p>
 * 卷轴解锁图纸的真实机制是：放置的卷轴把 {@code ScrollData.schematics} 里的 key 加入
 * {@code WorkbenchTile#getUnlockedSchematics()}，随后 {@code LockedRequirement#test}
 * 用 {@code CraftingContext.unlocks} 判断（见 tetra 源码 requirement/LockedRequirement.java）。
 * <p>
 * 这里从已加载的 schematic 原始 JSON 里收集所有 {@code "type": "tetra:locked"} 的 key，
 * 仅用于 {@code matchesRequirements} 的上下文，不修改任何 tetra 数据。
 */
public final class SchematicUnlocks {
    private static final String LOCKED_TYPE = "tetra:locked";

    private SchematicUnlocks() {
    }

    public static ResourceLocation[] withAllUnlocks(ResourceLocation[] existing) {
        Set<ResourceLocation> keys = new LinkedHashSet<>(Arrays.asList(existing));
        DataManager manager = DataManager.instance;
        if (manager != null && manager.schematicData != null) {
            Map<ResourceLocation, JsonElement> raw = manager.schematicData.getRawData();
            if (raw != null) {
                raw.values().forEach(element -> collect(element, keys));
            }
        }
        return keys.toArray(new ResourceLocation[0]);
    }

    private static void collect(JsonElement element, Set<ResourceLocation> keys) {
        if (element == null) {
            return;
        }

        if (element.isJsonArray()) {
            for (JsonElement child : element.getAsJsonArray()) {
                collect(child, keys);
            }
            return;
        }

        if (!element.isJsonObject()) {
            return;
        }

        JsonObject object = element.getAsJsonObject();
        if (object.has("type") && object.has("key")
                && LOCKED_TYPE.equals(object.get("type").getAsString())) {
            ResourceLocation key = ResourceLocation.tryParse(object.get("key").getAsString());
            if (key != null) {
                keys.add(key);
            }
        }

        for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
            collect(entry.getValue(), keys);
        }
    }
}
