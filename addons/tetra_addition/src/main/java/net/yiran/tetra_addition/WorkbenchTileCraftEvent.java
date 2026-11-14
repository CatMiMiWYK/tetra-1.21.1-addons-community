package net.yiran.tetra_addition;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.Event;
import se.mickelus.tetra.blocks.workbench.WorkbenchTile;
import se.mickelus.tetra.module.schematic.UpgradeSchematic;

public class WorkbenchTileCraftEvent
        extends Event {
    public ItemStack targetStack;
    public ItemStack upgradedStack;
    public Player player;
    public Level level;
    public WorkbenchTile workbenchTile;
    public ItemStack[] materials;
    public ItemStack[] materialsAltered;
    public UpgradeSchematic currentSchematic;
    public String currentSlot;

    public WorkbenchTileCraftEvent(ItemStack targetStack, ItemStack upgradedStack, Player player, WorkbenchTile workbenchTile, ItemStack[] materials, ItemStack[] materialsAltered, UpgradeSchematic currentSchematic, String currentSlot) {
        this.targetStack = targetStack;
        this.upgradedStack = upgradedStack;
        this.player = player;
        this.level = player.level();
        this.workbenchTile = workbenchTile;
        this.materials = materials;
        this.materialsAltered = materialsAltered;
        this.currentSchematic = currentSchematic;
        this.currentSlot = currentSlot;
    }

    public void setUpgradedStack(ItemStack upgradedStack) {
        this.upgradedStack = upgradedStack;
    }

    public ItemStack getTargetStack() {
        return this.targetStack;
    }

    public ItemStack getUpgradedStack() {
        return this.upgradedStack;
    }

    public Player getPlayer() {
        return this.player;
    }

    public Level getLevel() {
        return this.level;
    }

    public WorkbenchTile getWorkbenchTile() {
        return this.workbenchTile;
    }

    public ItemStack[] getMaterials() {
        return this.materials;
    }

    public ItemStack[] getMaterialsAltered() {
        return this.materialsAltered;
    }

    public UpgradeSchematic getCurrentSchematic() {
        return this.currentSchematic;
    }

    public String getCurrentSlot() {
        return this.currentSlot;
    }
}
