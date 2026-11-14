package net.yiran.toh.core.mixins;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.tetra.modify.wrapper.IModularItemWrapper;
import net.yiran.toh.OverHoningUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import se.mickelus.tetra.ConfigHandler;
import se.mickelus.tetra.items.modular.IModularItem;

import java.util.List;

@Mixin(value = IModularItemWrapper.class, remap = false)
public class IModularItemWrapperMixin {
    /**
     * 1.20.1 原实现注入在 getOrCreateTag() 调用处，此时已通过 moduleProgression / canGainHoneProgress 检查，
     * 且未因 isHoneable 提前返回。1.21.1 的包装器把 isHoneable 提前 return 掉，故改注入 HEAD 并补回前置条件。
     */
    @Inject(method = "tickHoningProgression", at = @At(value = "HEAD"))
    private static void t$addOverHoning(LivingEntity entity, ItemStack itemStack, int multiplier, CallbackInfo ci) {
        if (!ConfigHandler.moduleProgression.get() || !(itemStack.getItem() instanceof IModularItem item) || !item.canGainHoneProgress(itemStack)) {
            return;
        }
        if (IModularItem.isHoneable(itemStack)) {
            OverHoningUtil.addOverHoning(itemStack, multiplier);
        }
    }

    @Mixin(value = IModularItemWrapper.Client.class, remap = false)
    public static class Client {
        @Inject(method = "addHoningTooltip", at = @At(value = "RETURN"))
        private static void t$addOverHoning(IModularItem item, ItemStack itemStack, List<Component> tooltip, CallbackInfo ci) {
            int amount = OverHoningUtil.getOverHoning(itemStack);
            double conversion = OverHoningUtil.getConversion(itemStack);
            int conversionAmount = (int) (amount * conversion);
            if (conversionAmount > 10) {
                tooltip.add(Component.literal(" > ")
                        .withStyle(IModularItem.isHoneable(itemStack) ? ChatFormatting.DARK_AQUA : ChatFormatting.AQUA)
                        .append(Component.translatable("tetra.hone.overHoning", amount, conversionAmount, (float) (int) (conversion * 1000.0) / 10.0f)
                                .withStyle(ChatFormatting.GRAY)));
            }
        }
    }
}
