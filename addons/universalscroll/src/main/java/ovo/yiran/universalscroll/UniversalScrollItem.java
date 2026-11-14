package ovo.yiran.universalscroll;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import ovo.yiran.universalscroll.client.ClientHooks;
import se.mickelus.tetra.blocks.workbench.AbstractWorkbenchBlock;

import java.util.List;

/**
 * 通用卷轴物品。
 * <p>
 * 行为与原版 {@code ScrollItem} 保持同一目标：把玩家带到某张图纸所在的工作台流程。
 * 区别只在于“选择哪张图纸”由本附属的界面完成，而不是事先发放的固定卷轴。
 */
public class UniversalScrollItem extends Item {
    public UniversalScrollItem() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide) {
            ClientHooks.openSelection(null);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();

        // 只有对着工作台使用才进入“选择并打开工作台图纸”的流程，其余方块保持原版交互
        if (level.getBlockState(pos).getBlock() instanceof AbstractWorkbenchBlock) {
            if (level.isClientSide) {
                ClientHooks.openSelection(pos);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.universalscroll.universal_scroll.description").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("item.universalscroll.universal_scroll.usage").withStyle(ChatFormatting.DARK_GRAY));

        ResourceLocation selected = stack.get(UniversalScroll.SELECTED_SCHEMATIC.get());
        if (selected != null) {
            tooltip.add(Component.translatable("item.universalscroll.universal_scroll.last_selected", selected.toString())
                    .withStyle(ChatFormatting.DARK_PURPLE));
        }
    }
}
