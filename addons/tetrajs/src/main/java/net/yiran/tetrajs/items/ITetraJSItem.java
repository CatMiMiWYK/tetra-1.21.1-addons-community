package net.yiran.tetrajs.items;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.yiran.tetrajs.util.SlotData;
import se.mickelus.tetra.data.DataManager;
import se.mickelus.tetra.gui.GuiModuleOffsets;
import se.mickelus.tetra.module.data.SynergyData;

public interface ITetraJSItem {
    void setSynergies(SynergyData[] synergies);

    String[] getSynergiesPath();

    default void synergyDataSync() {
        DataManager.instance.synergyData.onReload(() -> this.setSynergies(DataManager.instance.synergyData.getData().entrySet().stream()
                .filter(entry -> entry.getKey().getNamespace().equals("tetra"))
                .filter(entry -> Arrays.stream(this.getSynergiesPath()).anyMatch(s -> entry.getKey().getPath().startsWith(s)))
                .flatMap(entry -> Arrays.stream(entry.getValue()))
                .toArray(SynergyData[]::new)));
    }

    void setMajorOffsets(GuiModuleOffsets majorOffsets);

    void setMinorOffsets(GuiModuleOffsets minorOffsets);

    void setMajorModuleKeys(String[] majorModuleKeys);

    void setMinorModuleKeys(String[] minorModuleKeys);

    void setRequiredModules(String[] requiredModules);

    default void slotInit(List<SlotData> majorsData, List<SlotData> minorsData) {
        ArrayList<String> required = new ArrayList<>();
        ArrayList<String> major = new ArrayList<>();
        IntArrayList majorOffset = new IntArrayList();
        ArrayList<String> minor = new ArrayList<>();
        IntArrayList minorOffset = new IntArrayList();
        for (SlotData slotData : majorsData) {
            major.add(slotData.slot());
            majorOffset.add(slotData.x());
            majorOffset.add(slotData.y());
            if (!slotData.required()) {
                continue;
            }
            required.add(slotData.slot());
        }
        for (SlotData slotData : minorsData) {
            minor.add(slotData.slot());
            minorOffset.add(slotData.x());
            minorOffset.add(slotData.y());
            if (!slotData.required()) {
                continue;
            }
            required.add(slotData.slot());
        }
        this.setMajorModuleKeys(major.toArray(new String[0]));
        this.setMajorOffsets(new GuiModuleOffsets(majorOffset.elements()));
        this.setMinorModuleKeys(minor.toArray(new String[0]));
        this.setMinorOffsets(new GuiModuleOffsets(minorOffset.elements()));
        this.setRequiredModules(required.toArray(new String[0]));
    }
}
