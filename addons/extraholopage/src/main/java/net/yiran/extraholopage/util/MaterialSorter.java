package net.yiran.extraholopage.util;

import java.util.function.Function;
import net.minecraft.client.resources.language.I18n;
import se.mickelus.tetra.module.data.MaterialData;

public class MaterialSorter {
    public static MaterialSorter NONE = new MaterialSorter(materialData -> Float.valueOf(0.0f), "ehp.sorter.NONE", 999);
    public Function<MaterialData, Number> priorityFn;
    public String name;
    public int index;

    public MaterialSorter(Function<MaterialData, Number> priorityFn, String nameKey) {
        this(priorityFn, nameKey, 0);
    }

    public MaterialSorter(Function<MaterialData, Number> priorityFn, String nameKey, int index) {
        this.priorityFn = priorityFn;
        this.name = nameKey;
        this.index = index;
    }

    public float getPriority(MaterialData data) {
        return this.priorityFn.apply(data).floatValue();
    }

    public String getName() {
        return I18n.get(this.name);
    }
}
