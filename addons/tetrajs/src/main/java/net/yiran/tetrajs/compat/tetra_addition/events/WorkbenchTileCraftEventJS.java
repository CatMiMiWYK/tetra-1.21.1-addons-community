package net.yiran.tetrajs.compat.tetra_addition.events;

import dev.latvian.mods.kubejs.event.KubeEvent;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.script.ScriptTypeHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.yiran.tetra_addition.WorkbenchTileCraftEvent;
import net.yiran.tetrajs.compat.tetra_addition.TetraAdditionEvents;
import se.mickelus.tetra.blocks.workbench.WorkbenchTile;
import se.mickelus.tetra.module.schematic.UpgradeSchematic;

public class WorkbenchTileCraftEventJS implements KubeEvent {
    public ItemStack targetStack;
    public ItemStack upgradedStack;
    public Player player;
    public Level level;
    public WorkbenchTile workbenchTile;
    public ItemStack[] materials;
    public ItemStack[] materialsAltered;
    public UpgradeSchematic currentSchematic;
    public String currentSlot;

    public WorkbenchTileCraftEventJS(WorkbenchTileCraftEvent event) {
        this.targetStack = event.targetStack;
        this.upgradedStack = event.upgradedStack;
        this.player = event.player;
        this.level = event.getLevel();
        this.workbenchTile = event.workbenchTile;
        this.materials = event.materials;
        this.materialsAltered = event.materialsAltered;
        this.currentSchematic = event.currentSchematic;
        this.currentSlot = event.currentSlot;
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

    @SubscribeEvent
    public static void onCraft(WorkbenchTileCraftEvent event) {
        TetraAdditionEvents.Craft.post((ScriptTypeHolder) (event.level.isClientSide() ? ScriptType.CLIENT : ScriptType.SERVER), new WorkbenchTileCraftEventJS(event));
    }
}
