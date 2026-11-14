package net.yiran.extraholopage.util;

import java.util.function.Function;
import net.minecraft.client.resources.language.I18n;
import se.mickelus.tetra.module.data.MaterialData;

public class MaterialFilter {
    public static MaterialFilter NONE = new MaterialFilter(materialData -> true, "ehp.filter.NONE", 999);
    public Function<MaterialData, Boolean> filterFn;
    public String name;
    public int index;

    public MaterialFilter(Function<MaterialData, Boolean> filterFn, String nameKey) {
        this(filterFn, nameKey, 0);
    }

    public MaterialFilter(Function<MaterialData, Boolean> filterFn, String nameKey, int index) {
        this.filterFn = filterFn;
        this.name = nameKey;
        this.index = index;
    }

    public boolean shouldShow(MaterialData data) {
        return this.filterFn.apply(data);
    }

    public String getName() {
        return I18n.get(this.name);
    }
}
