package net.tetra.modify.core.mixins;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.tetra.modify.wrapper.IModularItemWrapper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import se.mickelus.tetra.items.modular.IModularItem;

import java.util.List;

@Mixin(value = {IModularItem.class})
public interface IModularItemMixin extends IModularItem {
    @Override
    default public void tickHoningProgression(LivingEntity entity, ItemStack itemStack, int multiplier) {
        IModularItemWrapper.tickHoningProgression(entity, itemStack, multiplier);
    }

    @Override
    @OnlyIn(value = Dist.CLIENT)
    default public List<Component> getTooltip(ItemStack itemStack, @Nullable Level world, TooltipFlag advanced) {
        return IModularItemWrapper.Client.getTooltip(itemStack, world, advanced);
    }
}
