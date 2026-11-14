package net.yiran.toh;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import se.mickelus.tetra.effect.ItemEffect;
import se.mickelus.tetra.items.modular.IModularItem;
import se.mickelus.tetra.util.ItemStackTagHelper;

public class OverHoningUtil {
    public static final ItemEffect Conversion = ItemEffect.get("over_honing:conversion");

    public static int getOverHoning(ItemStack itemStack) {
        CompoundTag tag = ItemStackTagHelper.getTag(itemStack);
        return tag == null ? 0 : tag.getInt(TetraOverHoning.NBT_KEY);
    }

    public static void addOverHoning(ItemStack itemStack, int amount) {
        ItemStackTagHelper.mutate(itemStack, tag -> tag.putInt(TetraOverHoning.NBT_KEY, tag.getInt(TetraOverHoning.NBT_KEY) + amount));
    }

    public static double getConversion(ItemStack itemStack) {
        double result = Config.Conversion.get();
        if (itemStack.getItem() instanceof IModularItem item) {
            result += (double) item.getEffectLevel(itemStack, Conversion) / 100.0;
        }
        return Math.min(Config.MaxConversion.get(), result);
    }
}
