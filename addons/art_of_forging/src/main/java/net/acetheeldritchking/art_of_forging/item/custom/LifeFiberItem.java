package net.acetheeldritchking.art_of_forging.item.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LifeFiberItem
        extends Item {
    public LifeFiberItem(Item.Properties pProperties) {
        super(pProperties);
    }

    public void appendHoverText(ItemStack pStack, @Nullable Item.TooltipContext pContext, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        pTooltipComponents.add(Component.translatable("item.life_fiber.tooltip").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
        super.appendHoverText(pStack, pContext, pTooltipComponents, pIsAdvanced);
    }
}
