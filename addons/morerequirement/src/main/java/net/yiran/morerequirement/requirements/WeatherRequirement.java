package net.yiran.morerequirement.requirements;

import net.minecraft.core.BlockPos;
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

public class WeatherRequirement implements CraftingRequirement, CraftingEffectCondition {
    public String weather;

    @Override
    public boolean test(ResourceLocation[] unlocks, ItemStack upgradedStack, String slot, boolean isReplacing, Player player,
            ItemStack[] materials, Map<ItemAbility, Integer> tools, UpgradeSchematic schematic, Level world, BlockPos pos, BlockState blockState) {
        return matches(world);
    }

    @Override
    public boolean test(CraftingContext cxt) {
        Level level = cxt.world;
        if (level == null) {
            return false;
        }
        return matches(level);
    }

    private boolean matches(Level level) {
        return switch (this.weather) {
            case "clear" -> !level.isRaining() && !level.isThundering();
            case "rain" -> level.isRaining();
            case "thunder" -> level.isThundering();
            default -> false;
        };
    }

    @Nullable
    @Override
    @OnlyIn(Dist.CLIENT)
    public List<Component> getDescription() {
        return List.of(Component.translatable("more_requirement.holo.weather_requirement",
                Component.translatable("more_requirement.holo.weather_requirement." + this.weather)));
    }
}
