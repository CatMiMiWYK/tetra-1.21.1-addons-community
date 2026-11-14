package net.tetra.modify.core.mixins.client;

import net.minecraft.world.item.ItemStack;
import net.tetra.modify.client.TemporaryHoloItemGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import se.mickelus.tetra.items.modular.IModularItem;
import se.mickelus.tetra.items.modular.impl.holo.gui.craft.HolosphereEntryData;
import se.mickelus.tetra.items.modular.impl.holo.gui.craft.item.HoloItemsGui;

import java.util.ArrayList;
import java.util.List;

/**
 * 1.21.1 的全息仪条目为数据驱动：向 loadEntries 的列表末尾追加一个临时条目，
 * 让「从工作台打开的全息仪」也能显示当前手持的模块化物品。
 */
@Mixin(value = {HoloItemsGui.class}, remap = false)
public class HoloItemsGuiMixin {
    @ModifyVariable(method = {"loadEntries"}, at = @At(value = "HEAD"), argsOnly = true)
    private List<HolosphereEntryData> tmc$addTemporary(List<HolosphereEntryData> entries) {
        ItemStack stack = TemporaryHoloItemGui.getWorkbenchStack();
        if (stack.isEmpty() || !(stack.getItem() instanceof IModularItem)) {
            return entries;
        }

        HolosphereEntryData data = TemporaryHoloItemGui.createEntryData(stack);
        if (data == null) {
            return entries;
        }

        String key = data.key;
        boolean exists = entries.stream().anyMatch(entry -> entry != null && key.equals(entry.key));
        if (exists) {
            return entries;
        }

        ArrayList<HolosphereEntryData> result = new ArrayList<>(entries);
        result.add(data);
        return result;
    }
}
