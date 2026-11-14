package com.hp.item;

import com.hp.gateway.RiftGatewaySpawner;
import com.hp.rift.RiftPearlData;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.Item.TooltipContext;
import net.yiran.tetrajs.items.BaseModularItem;
import net.yiran.tetrajs.util.SlotData;

import java.util.List;

public class RiftGatePearlItem extends BaseModularItem {
    public RiftGatePearlItem(ResourceLocation id, List<SlotData> majorSlots, List<SlotData> minorSlots) {
        super(id, majorSlots, minorSlots, false, 0, 0, new String[0]);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        return RiftGatewaySpawner.open(context);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        RiftPearlData.ensureGateway(stack);
        super.appendHoverText(stack, context, tooltip, flag);
        for (ItemStack reward : RiftPearlData.getCoreRewards(stack)) {
            tooltip.add(Component.translatable("tooltip.tetra_gateways.rift_core_reward", reward.getHoverName(), RiftPearlData.getRewardCount(stack))
                    .withStyle(ChatFormatting.GRAY));
        }
        tooltip.add(Component.translatable("tooltip.tetra_gateways.rift_uses", RiftPearlData.getUses(stack)).withStyle(ChatFormatting.DARK_AQUA));
    }
}
