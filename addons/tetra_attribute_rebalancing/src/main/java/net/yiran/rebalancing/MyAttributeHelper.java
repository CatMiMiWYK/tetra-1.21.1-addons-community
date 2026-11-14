package net.yiran.rebalancing;

import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.Collection;

public class MyAttributeHelper {
    public static MyAttributeHelper INSTANCE = new MyAttributeHelper();

    public double getAdditionAmount(Collection<AttributeModifier> modifiers) {
        return modifiers.stream()
                .filter(modifier -> modifier.operation().equals(AttributeModifier.Operation.ADD_VALUE))
                .mapToDouble(AttributeModifier::amount)
                .sum();
    }

    public double getMultiplyBaseAmount(Collection<AttributeModifier> modifiers) {
        return modifiers.stream()
                .filter(modifier -> modifier.operation().equals(AttributeModifier.Operation.ADD_MULTIPLIED_BASE))
                .mapToDouble(AttributeModifier::amount)
                .sum();
    }

    public double getMultiplyTotalAmount(Collection<AttributeModifier> modifiers) {
        return modifiers.stream()
                .filter(modifier -> modifier.operation().equals(AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                .mapToDouble(AttributeModifier::amount)
                .map(amount -> amount + 1.0)
                .reduce(1.0, (a, b) -> a * b);
    }
}
