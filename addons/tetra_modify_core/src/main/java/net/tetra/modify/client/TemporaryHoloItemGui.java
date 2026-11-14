package net.tetra.modify.client;

import net.minecraft.world.item.ItemStack;
import se.mickelus.tetra.items.modular.IModularItem;
import se.mickelus.tetra.items.modular.impl.dynamic.DynamicModularItem;
import se.mickelus.tetra.items.modular.impl.holo.gui.craft.HolosphereEntryData;
import se.mickelus.tetra.items.modular.impl.holo.gui.craft.HolosphereEntryStore;
import se.mickelus.tetra.module.data.GlyphData;

import java.util.Map;
import java.util.Objects;

/**
 * Holds the stack the holosphere has been opened from and builds a temporary holosphere entry for it.
 * 1.21.1 的全息仪条目是数据驱动（HolosphereEntryData），因此这里不再构造自定义 Gui，只提供数据。
 */
public final class TemporaryHoloItemGui {
    private static final String FALLBACK_KEY = "tetra_modify_core:temporary";

    private static ItemStack workbenchStack = ItemStack.EMPTY;

    private TemporaryHoloItemGui() {
    }

    public static void setWorkbenchStack(ItemStack stack) {
        workbenchStack = stack == null ? ItemStack.EMPTY : stack.copy();
    }

    public static ItemStack getWorkbenchStack() {
        return workbenchStack;
    }

    public static String findKey(ItemStack stack) {
        if (HolosphereEntryStore.instance == null || stack.isEmpty() || !(stack.getItem() instanceof IModularItem)) {
            return null;
        }
        String archetype = DynamicModularItem.getArchetypeKey(stack);
        return HolosphereEntryStore.instance.getEntries().entrySet().stream()
                .filter(entry -> entry.getValue().item == stack.getItem())
                .filter(entry -> entry.getValue().archetype == null || entry.getValue().archetype.equals(archetype))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }

    public static HolosphereEntryData createEntryData(ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof IModularItem)) {
            return null;
        }

        String key = findKey(stack);

        HolosphereEntryData data = new HolosphereEntryData();
        data.key = key != null ? key : FALLBACK_KEY;
        data.item = stack.getItem();
        data.archetype = DynamicModularItem.getArchetypeKey(stack);
        data.icon = findIcon(stack);

        return data;
    }

    private static GlyphData findIcon(ItemStack stack) {
        if (HolosphereEntryStore.instance != null) {
            GlyphData icon = HolosphereEntryStore.instance.getEntries().values().stream()
                    .filter(entry -> entry.item == stack.getItem())
                    .map(entry -> entry.icon)
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElse(null);
            if (icon != null) {
                return icon;
            }
        }
        return new GlyphData();
    }
}
