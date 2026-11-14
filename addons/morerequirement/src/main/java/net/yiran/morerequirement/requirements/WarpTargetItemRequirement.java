package net.yiran.morerequirement.requirements;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbility;
import se.mickelus.tetra.blocks.workbench.WorkbenchTile;
import se.mickelus.tetra.craftingeffect.condition.CraftingEffectCondition;
import se.mickelus.tetra.module.schematic.UpgradeSchematic;

import java.util.Map;
import java.util.Optional;

public class WarpTargetItemRequirement implements CraftingEffectCondition {
    public CraftingEffectCondition requirement;

    @Override
    public boolean test(ResourceLocation[] unlocks, ItemStack upgradedStack, String slot, boolean isReplacing, Player player,
            ItemStack[] materials, Map<ItemAbility, Integer> tools, UpgradeSchematic schematic, Level world, BlockPos pos, BlockState blockState) {
        Optional<WorkbenchTile> be = world.getBlockEntity(pos, WorkbenchTile.type.get());
        if (be.isEmpty()) {
            return false;
        }
        ItemStack targetStack = be.get().getTargetItemStack();
        return this.requirement.test(unlocks, targetStack, slot, isReplacing, player, materials, tools, schematic, world, pos, blockState);
    }
}
