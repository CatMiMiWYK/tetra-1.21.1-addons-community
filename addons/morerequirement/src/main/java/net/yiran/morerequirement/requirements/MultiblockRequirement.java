package net.yiran.morerequirement.requirements;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.ItemAbility;
import se.mickelus.tetra.craftingeffect.condition.CraftingEffectCondition;
import se.mickelus.tetra.module.schematic.CraftingContext;
import se.mickelus.tetra.module.schematic.UpgradeSchematic;
import se.mickelus.tetra.module.schematic.requirement.CraftingRequirement;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MultiblockRequirement implements CraftingRequirement, CraftingEffectCondition {
    public Map<Block, Integer> blocks;
    public int x = 3;
    public int y = 2;
    public int z = 3;

    @Override
    public boolean test(ResourceLocation[] unlocks, ItemStack upgradedStack, String slot, boolean isReplacing, Player player,
            ItemStack[] materials, Map<ItemAbility, Integer> tools, UpgradeSchematic schematic, Level world, BlockPos pos, BlockState blockState) {
        return matches(world, pos);
    }

    @Override
    public boolean test(CraftingContext cxt) {
        Level level = cxt.world;
        BlockPos pos = cxt.pos;
        if (level == null || pos == null) {
            return false;
        }
        return matches(level, pos);
    }

    private boolean matches(Level level, BlockPos pos) {
        AABB aabb = new AABB(pos).inflate(this.x, this.y, this.z);
        Map<Block, Integer> found = level.getBlockStates(aabb)
                .map(BlockState::getBlock)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.summingInt(i -> 1)));
        for (Map.Entry<Block, Integer> entry : this.blocks.entrySet()) {
            if (found.getOrDefault(entry.getKey(), 0) < entry.getValue()) {
                return false;
            }
        }
        return true;
    }

    @Nullable
    @Override
    @OnlyIn(Dist.CLIENT)
    public List<Component> getDescription() {
        ImmutableList.Builder<Component> builder = ImmutableList.builder();
        int size = this.blocks.size();
        int index = 0;
        builder.add(Component.translatable("more_requirement.holo.multiblock_requirement", this.x * 2 + 1, this.y * 2 + 1, this.z * 2 + 1));
        for (Map.Entry<Block, Integer> entry : this.blocks.entrySet()) {
            if (++index == size) {
                builder.add(Component.literal(" \u00a78\u2514\u00a7r ").append(entry.getKey().getName()).append(" x" + entry.getValue()));
            } else {
                builder.add(Component.literal(" \u00a78\u251c\u00a7r ").append(entry.getKey().getName()).append(" x" + entry.getValue()));
            }
        }
        return builder.build();
    }
}
