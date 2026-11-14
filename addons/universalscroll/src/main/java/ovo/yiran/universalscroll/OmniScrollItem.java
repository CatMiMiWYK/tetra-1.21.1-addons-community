package ovo.yiran.universalscroll;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;

import java.util.List;

/**
 * 百宝卷轴物品：放置后成为 {@link OmniScrollBlock}，把全部图纸与合成效果提供给附近工作台。
 * 物品本身强制开启附魔光效（1.21 的 ENCHANTMENT_GLINT_OVERRIDE），对应“鎏金”外观。
 */
public class OmniScrollItem extends BlockItem {
    public OmniScrollItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.universalscroll.omni_scroll.description").withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.translatable("item.universalscroll.omni_scroll.usage").withStyle(ChatFormatting.GRAY));
    }
}
