package net.tetra.modify.utils;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import se.mickelus.tetra.items.modular.IModularItem;
import se.mickelus.tetra.util.ItemStackTagHelper;

public class MatchUtils {
    public static boolean matchItem(ItemStack stack, ItemStack other) {
        if (!(stack.getItem() instanceof IModularItem)) {
            return false;
        }
        if (!(other.getItem() instanceof IModularItem)) {
            return false;
        }
        if (stack == other) {
            return true;
        }
        if (stack.getItem() != other.getItem()) {
            return false;
        }
        CompoundTag tag = ItemStackTagHelper.getTag(stack);
        CompoundTag otherTag = ItemStackTagHelper.getTag(other);
        if (tag != null && otherTag != null) {
            return tag.getString("id").equals(otherTag.getString("id"));
        }
        return true;
    }
}
