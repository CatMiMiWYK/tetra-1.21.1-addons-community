package net.yiran.tetrajs.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.yiran.tetrajs.util.NbtSlotData;
import se.mickelus.tetra.items.modular.IModularItem;
import se.mickelus.tetra.util.ItemStackTagHelper;

public class DynamicModularHelper {
    public static DynamicModularHelper INSTANCE = new DynamicModularHelper();

    public void addSlot(ItemStack stack, NbtSlotData slot) {
        Map<String, NbtSlotData> slotsData = this.getSlotData(stack);
        slotsData.put(slot.slot(), slot);
        this.saveSlotData(stack, new ArrayList<>(slotsData.values()));
    }

    public void addSlot(ItemStack stack, String slot, int x, int y, boolean isMajor, boolean required) {
        this.addSlot(stack, NbtSlotData.create(slot, x, y, isMajor, required));
    }

    public void addSlot(ItemStack stack, String slot, int x, int y, boolean isMajor) {
        this.addSlot(stack, NbtSlotData.create(slot, x, y, isMajor, isMajor));
    }

    public void removeSlot(ItemStack stack, String slot) {
        Map<String, NbtSlotData> slotsData = this.getSlotData(stack);
        slotsData.remove(slot);
        this.saveSlotData(stack, new ArrayList<>(slotsData.values()));
    }

    public void clearSlot(ItemStack stack) {
        this.saveSlotData(stack, new ArrayList<>());
    }

    public Map<String, NbtSlotData> getSlotData(ItemStack stack) {
        CompoundTag tag = ItemStackTagHelper.getTag(stack);
        if (tag == null) {
            return new java.util.HashMap<>();
        }
        ListTag list = tag.getList("extraslots", 10);
        return NbtSlotData.LCODEC.parse(NbtOps.INSTANCE, list).result().orElse(List.of()).stream()
                .collect(Collectors.toMap(NbtSlotData::slot, Function.identity()));
    }

    public void saveSlotData(ItemStack stack, List<NbtSlotData> slots) {
        Tag encoded = NbtSlotData.LCODEC.encodeStart(NbtOps.INSTANCE, slots).result().orElseGet(ListTag::new);
        ItemStackTagHelper.mutate(stack, tag -> tag.put("extraslots", encoded));
        IModularItem.updateIdentifier(stack);
    }
}
