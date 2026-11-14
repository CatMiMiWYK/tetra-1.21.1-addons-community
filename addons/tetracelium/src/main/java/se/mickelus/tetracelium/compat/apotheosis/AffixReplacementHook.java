package se.mickelus.tetracelium.compat.apotheosis;

import java.util.function.BiFunction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

public class AffixReplacementHook implements BiFunction<ItemStack, ItemStack, ItemStack> {
    @Override
    public ItemStack apply(ItemStack original, ItemStack replacementStack) {
        CompoundTag originalTag = original.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (originalTag.contains("affix_data", Tag.TAG_COMPOUND)) {
            CompoundTag affixData = originalTag.getCompound("affix_data").copy();
            CompoundTag replacementTag = replacementStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
            replacementTag.put("affix_data", affixData);
            replacementTag.put("apoth_boss", affixData.copy());
            replacementStack.set(DataComponents.CUSTOM_DATA, CustomData.of(replacementTag));
        }
        return replacementStack;
    }
}
