package com.hp.rift;

import com.hp.item.ModItems;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import se.mickelus.tetra.blocks.workbench.WorkbenchTile;
import se.mickelus.tetra.module.schematic.UpgradeSchematic;

import java.util.ArrayList;

public class RiftWorkbenchHooks {
    public static final String RIFT_CORE_SLOT = "rift_core";
    public static final String RIFT_CORE_SCHEMATIC = "rift_gate_pearl/rift_core/rift_core";
    public static final String RIFT_CORE_1_SCHEMATIC = "rift_gate_pearl/rift_core/rift_core_1";
    public static final String RIFT_CORE_2_SCHEMATIC = "rift_gate_pearl/rift_core/rift_core_2";
    public static final String RIFT_CORE_3_SCHEMATIC = "rift_gate_pearl/rift_core/rift_core_3";
    public static final String RIFT_CORE_LOCAL_SPAWN_SCHEMATIC = "rift_gate_pearl/rift_core/local_spawn";

    public static void afterCraft(WorkbenchTile tile, Player player, String slot, UpgradeSchematic schematic, ItemStack[] materials) {
        if (!RIFT_CORE_SLOT.equals(slot) || schematic == null || !isTrackedRiftCoreSchematic(schematic.getKey())) {
            return;
        }
        ItemStack result = tile.getTargetItemStack();
        if (result.getItem() != ModItems.RIFT_GATE_PEARL.get()) {
            return;
        }
        if (RIFT_CORE_LOCAL_SPAWN_SCHEMATIC.equals(schematic.getKey())) {
            RiftPearlData.clearCoreRewards(result);
            return;
        }
        ArrayList<ItemStack> rewards = new ArrayList<>();
        for (ItemStack material : materials) {
            if (material.isEmpty()) {
                continue;
            }
            rewards.add(material.copy());
        }
        if (!rewards.isEmpty()) {
            RiftPearlData.setCoreRewards(result, rewards);
        }
    }

    private static boolean isTrackedRiftCoreSchematic(String key) {
        return RIFT_CORE_SCHEMATIC.equals(key) || RIFT_CORE_1_SCHEMATIC.equals(key) || RIFT_CORE_2_SCHEMATIC.equals(key)
                || RIFT_CORE_3_SCHEMATIC.equals(key) || RIFT_CORE_LOCAL_SPAWN_SCHEMATIC.equals(key);
    }

    private RiftWorkbenchHooks() {
    }
}
