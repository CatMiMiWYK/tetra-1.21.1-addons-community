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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomRequirement implements CraftingRequirement, CraftingEffectCondition {
    public static CustomFunction DEFAULT = cxt -> true;
    public static CustomCEFunction CE_DEFAULT = cxt -> true;
    public static Map<String, CustomFunction> Functions = new HashMap<>();
    public static Map<String, CustomCEFunction> CEFunctions = new HashMap<>();
    public String key;

    public static void registerCustomCEFunction(String key, CustomCEFunction cxt) {
        CEFunctions.put(key, cxt);
    }

    @Override
    public boolean test(ResourceLocation[] unlocks, ItemStack upgradedStack, String slot, boolean isReplacing, Player player,
            ItemStack[] materials, Map<ItemAbility, Integer> tools, UpgradeSchematic schematic, Level world, BlockPos pos, BlockState blockState) {
        return CEFunctions.getOrDefault(this.key, CE_DEFAULT).test(new CEContext(unlocks, upgradedStack, slot, isReplacing, player, materials, tools,
                schematic, world, pos, blockState));
    }

    public static void registerCustomFunction(String key, CustomFunction cxt) {
        Functions.put(key, cxt);
    }

    @Override
    public boolean test(CraftingContext craftingContext) {
        return Functions.getOrDefault(this.key, DEFAULT).test(craftingContext);
    }

    @Nullable
    @Override
    @OnlyIn(Dist.CLIENT)
    public List<Component> getDescription() {
        return List.of(Component.translatable("more_requirement.holo.custom_requirement" + this.key));
    }

    @FunctionalInterface
    public interface CustomCEFunction {
        boolean test(CEContext context);
    }

    public record CEContext(ResourceLocation[] unlocks, ItemStack upgradedStack, String slot, boolean isReplacing, Player player,
            ItemStack[] materials, Map<ItemAbility, Integer> tools, UpgradeSchematic schematic, Level world, BlockPos pos, BlockState blockState) {
    }

    @FunctionalInterface
    public interface CustomFunction {
        boolean test(CraftingContext context);
    }
}
