package net.yiran.morerequirement.requirements;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
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

public class SeeSkyRequirement implements CraftingRequirement, CraftingEffectCondition {
    @Override
    public boolean test(ResourceLocation[] unlocks, ItemStack upgradedStack, String slot, boolean isReplacing, Player player,
            ItemStack[] materials, Map<ItemAbility, Integer> tools, UpgradeSchematic schematic, Level world, BlockPos pos, BlockState blockState) {
        return isExposedToSky(pos, world);
    }

    @Override
    public boolean test(CraftingContext cxt) {
        Level level = cxt.world;
        BlockPos pos = cxt.pos;
        if (level == null || pos == null) {
            return false;
        }
        return isExposedToSky(pos, level);
    }

    public static boolean isExposedToSky(BlockPos pos, Level level) {
        BlockPos.MutableBlockPos mut = pos.mutable();
        while (mut.getY() < level.getMaxBuildHeight()) {
            BlockState state = level.getBlockState(mut);
            if (state.isAir()) {
                mut.move(Direction.UP);
                continue;
            }
            if (state.canOcclude()) {
                return false;
            }
            mut.move(Direction.UP);
        }
        return true;
    }

    @Nullable
    @Override
    @OnlyIn(Dist.CLIENT)
    public List<Component> getDescription() {
        return List.of(Component.translatable("more_requirement.holo.see_sky_requirement"));
    }
}
