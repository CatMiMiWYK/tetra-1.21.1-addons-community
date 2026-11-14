package net.yiran.tetrajs.api;

import com.google.gson.JsonObject;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.item.ItemStack;
import se.mickelus.tetra.data.DataManager;
import se.mickelus.tetra.effect.ItemEffect;
import se.mickelus.tetra.module.data.MaterialData;

public class LangUtils {
    public static LangUtils INSTANCE = new LangUtils();

    public JsonObject getLackMaterialLang() {
        JsonObject json = new JsonObject();
        return this.addLackMaterialLang(json);
    }

    public JsonObject getEmptyObject() {
        return new JsonObject();
    }

    public JsonObject addLackMaterialLang(JsonObject json) {
        for (MaterialData materialData : DataManager.instance.materialData.getData().values()) {
            String key = materialData.key;
            ItemStack[] items = materialData.material.getApplicableItemStacks();
            if (items.length <= 0) {
                continue;
            }
            ItemStack item = items[0];
            if (!I18n.exists("tetra.material." + key)) {
                json.addProperty("tetra.material." + key, I18n.get(item.getDescriptionId()));
            }
            if (I18n.exists("tetra.material." + key + ".prefix")) {
                continue;
            }
            json.addProperty("tetra.material." + key + ".prefix", I18n.get(item.getDescriptionId()));
        }
        return json;
    }

    public JsonObject addSchematic(JsonObject json, String path, String name, String description, String... slots) {
        json.addProperty("tetra/schematic/" + path + ".name", name);
        json.addProperty("tetra/schematic/" + path + ".description", description);
        int index = 0;
        for (String slot : slots) {
            json.addProperty("tetra/schematic/" + path + ".slot" + ++index, slot);
        }
        return json;
    }

    public JsonObject addEffect(JsonObject json, ItemEffect effect, String name, String tooltip, String tooltipShort, String tooltipExtended) {
        json.addProperty("tetra.stat." + effect.getKey(), name);
        if (tooltip != null) {
            json.addProperty("tetra.stat." + effect.getKey() + ".tooltip", tooltip);
        }
        if (tooltipShort != null) {
            json.addProperty("tetra.stat." + effect.getKey() + ".tooltip_short", tooltipShort);
        }
        if (tooltipExtended != null) {
            json.addProperty("tetra.stat." + effect.getKey() + ".tooltip_extended", tooltipExtended);
        }
        return json;
    }
}
