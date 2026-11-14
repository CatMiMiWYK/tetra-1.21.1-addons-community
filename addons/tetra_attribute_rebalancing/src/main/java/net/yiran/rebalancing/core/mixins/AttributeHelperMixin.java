package net.yiran.rebalancing.core.mixins;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.yiran.rebalancing.MyAttributeHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import se.mickelus.tetra.properties.AttributeHelper;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mixin(value = AttributeHelper.class, remap = false)
public class AttributeHelperMixin {
    @Inject(method = "collapse", at = @At("HEAD"), cancellable = true, remap = false)
    private static void reCollapse(Collection<AttributeModifier> modifiers, CallbackInfoReturnable<Collection<AttributeModifier>> cir) {
        cir.setReturnValue(Stream.of(
                Optional.of(MyAttributeHelper.INSTANCE.getAdditionAmount(modifiers))
                        .filter(amount -> amount != 0.0)
                        .map(amount -> new AttributeModifier(
                                ResourceLocation.fromNamespaceAndPath("tetra", "stats/addition"),
                                amount,
                                AttributeModifier.Operation.ADD_VALUE)),
                Optional.of(MyAttributeHelper.INSTANCE.getMultiplyBaseAmount(modifiers))
                        .filter(amount -> amount != 0.0)
                        .map(amount -> new AttributeModifier(
                                ResourceLocation.fromNamespaceAndPath("tetra", "stats/multiply_base"),
                                amount,
                                AttributeModifier.Operation.ADD_MULTIPLIED_BASE)),
                Optional.of(MyAttributeHelper.INSTANCE.getMultiplyTotalAmount(modifiers))
                        .map(amount -> amount - 1.0)
                        .filter(amount -> amount != 0.0)
                        .map(amount -> new AttributeModifier(
                                ResourceLocation.fromNamespaceAndPath("tetra", "stats/multiply_total"),
                                amount,
                                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
        ).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList()));
    }

    @Inject(method = "getAdditionAmount", at = @At("HEAD"), cancellable = true, remap = false)
    private static void reGetAdditionAmount(Collection<AttributeModifier> modifiers, CallbackInfoReturnable<Double> cir) {
        cir.setReturnValue(MyAttributeHelper.INSTANCE.getAdditionAmount(modifiers));
    }

    @Inject(method = "getMultiplyAmount", at = @At("HEAD"), cancellable = true, remap = false)
    private static void reGetMultiplyAmount(Collection<AttributeModifier> modifiers, CallbackInfoReturnable<Double> cir) {
        cir.setReturnValue((1.0 + MyAttributeHelper.INSTANCE.getMultiplyBaseAmount(modifiers)) * MyAttributeHelper.INSTANCE.getMultiplyTotalAmount(modifiers));
    }

    @ModifyVariable(
            method = "round(Lnet/minecraft/world/entity/ai/attributes/Attribute;Lnet/minecraft/world/entity/ai/attributes/AttributeModifier;)Lnet/minecraft/world/entity/ai/attributes/AttributeModifier;",
            at = @At("STORE"),
            name = "rounding",
            remap = false)
    private static double multiplier(double original) {
        return 1000.0;
    }
}
