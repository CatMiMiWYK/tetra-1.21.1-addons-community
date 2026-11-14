package ovo.yiran.simplejadetetra;

import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import se.mickelus.tetra.items.modular.impl.ModularDoubleHeadedItem;
import se.mickelus.tetra.util.ItemAbilityHelper;
import snownee.jade.addon.harvest.ToolHandler;

import java.util.List;

public class TetraHammerToolHandler implements ToolHandler {
    public List<ItemStack> tools = Lists.newArrayList(ModularDoubleHeadedItem.getCreativeTabItemStacks());

    @Override
    public ItemStack test(BlockState state, Level level, BlockPos pos) {
        if (matchesBlock(state)) {
            for (ItemStack tool : tools) {
                if (tool.isCorrectToolForDrops(state)) {
                    return tool;
                }
            }
            return tools.get(tools.size() - 1);
        }
        return ItemStack.EMPTY;
    }

    public boolean matchesBlock(BlockState state) {
        return state.is(ItemAbilityHelper.hammerMineable);
    }

    @Override
    public List<ItemStack> getTools() {
        return tools;
    }

    @Override
    public ResourceLocation getUid() {
        return ResourceLocation.fromNamespaceAndPath(SimpleJadeTetra.MODID, "hammer");
    }
}
