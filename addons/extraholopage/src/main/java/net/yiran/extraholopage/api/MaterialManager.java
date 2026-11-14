package net.yiran.extraholopage.api;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.neoforged.neoforge.common.ItemAbilities;
import net.yiran.extraholopage.util.MaterialFilter;
import net.yiran.extraholopage.util.MaterialSorter;
import se.mickelus.tetra.TetraItemAbilities;
import se.mickelus.tetra.module.data.MaterialData;

public class MaterialManager {
    public static final MaterialManager INSTANCE = new MaterialManager();
    public List<MaterialSorter> sorters = new ArrayList<MaterialSorter>();
    public boolean needReSortSorters = true;
    public List<MaterialFilter> filters = new ArrayList<MaterialFilter>();
    public boolean needReSortFilters = true;

    public MaterialManager() {
        this.registerSorter(MaterialSorter.NONE);
        this.registerSorter(materialData -> materialData.primary, "ehp.sorter.primary");
        this.registerSorter(materialData -> materialData.secondary, "ehp.sorter.secondary");
        this.registerSorter(materialData -> materialData.tertiary, "ehp.sorter.tertiary");
        this.registerSorter(materialData -> materialData.magicCapacity, "ehp.sorter.magicCapacity");
        this.registerSorter(materialData -> Float.valueOf(materialData.durability), "ehp.sorter.durability");
        this.registerSorter(materialData -> materialData.toolLevel, "ehp.sorter.toolLevel");
        this.registerSorter(materialData -> Float.valueOf(materialData.experienceCost), "ehp.sorter.experienceCost");
        this.registerSorter(materialData -> materialData.requiredTools == null ? 0 : materialData.requiredTools.getLevel(TetraItemAbilities.hammer), "ehp.sorter.requiredTools.hammer");
        this.registerSorter(materialData -> materialData.requiredTools == null ? 0 : materialData.requiredTools.getLevel(TetraItemAbilities.cut), "ehp.sorter.requiredTools.cut");
        this.registerSorter(materialData -> materialData.requiredTools == null ? 0 : materialData.requiredTools.getLevel(ItemAbilities.AXE_DIG), "ehp.sorter.requiredTools.axe");
        this.registerFilter(MaterialFilter.NONE);
        this.registerFilter(materialData -> !materialData.effects.getValues().isEmpty(), "ehp.filter.hasEffect");
        this.registerFilter(materialData -> materialData.attributes != null, "ehp.filter.hasAttribute");
    }

    public void removeFilter(String name) {
        this.filters.removeIf(filter -> filter.name.equals(name));
    }

    public void registerFilter(MaterialFilter filter) {
        this.filters.add(filter);
        this.needReSortFilters = true;
    }

    public void registerFilter(Function<MaterialData, Boolean> filter, String name, int index) {
        this.registerFilter(new MaterialFilter(filter, name, index));
    }

    public void registerFilter(Function<MaterialData, Boolean> filter, String name) {
        this.registerFilter(filter, name, 0);
    }

    public void removeSorter(String name) {
        this.sorters.removeIf(sorter -> sorter.name.equals(name));
    }

    public void registerSorter(MaterialSorter sorter) {
        this.sorters.add(sorter);
        this.needReSortSorters = true;
    }

    public void registerSorter(Function<MaterialData, Number> sorter, String name, int index) {
        this.registerSorter(new MaterialSorter(sorter, name, index));
    }

    public void registerSorter(Function<MaterialData, Number> sorter, String name) {
        this.registerSorter(sorter, name, 0);
    }

    public List<MaterialSorter> getSorters() {
        if (this.needReSortSorters) {
            this.sorters = this.sorters.stream().sorted((a, b) -> b.index - a.index).collect(Collectors.toList());
            this.needReSortSorters = false;
        }
        return this.sorters;
    }

    public List<MaterialFilter> getFilters() {
        if (this.needReSortFilters) {
            this.filters = this.filters.stream().sorted((a, b) -> b.index - a.index).collect(Collectors.toList());
            this.needReSortFilters = false;
        }
        return this.filters;
    }
}
