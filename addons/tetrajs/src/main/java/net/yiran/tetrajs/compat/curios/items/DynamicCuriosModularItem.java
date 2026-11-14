package net.yiran.tetrajs.compat.curios.items;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.yiran.tetrajs.compat.curios.CuriosHelper;
import net.yiran.tetrajs.items.DynamicModularItem;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class DynamicCuriosModularItem extends DynamicModularItem implements ICurioItem {
    public boolean providerAttribute;
    public boolean isUnbreakable;

    public DynamicCuriosModularItem(ResourceLocation itemID, boolean canHone, int honeBase, int honeIntegrityMultiplier, String[] synergiesPath, boolean providerAttribute, boolean isUnbreakable) {
        super(itemID, canHone, honeBase, honeIntegrityMultiplier, synergiesPath);
        this.providerAttribute = providerAttribute;
        this.isUnbreakable = isUnbreakable;
    }

    @Override
    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext slotContext, ResourceLocation id, ItemStack stack) {
        if (!this.providerAttribute || this.isBroken(stack)) {
            return ImmutableMultimap.of();
        }
        return CuriosHelper.Curios$fixIdentifiers(slotContext, this.getAttributeModifiersCached(stack));
    }

    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
        return ItemAttributeModifiers.builder().build();
    }

    @Override
    public boolean isDamageable(ItemStack stack) {
        return !this.isUnbreakable && super.isDamageable(stack);
    }

    @Override
    public int getMaxDamage(ItemStack itemStack) {
        return this.isUnbreakable ? -1 : super.getMaxDamage(itemStack);
    }

    @Override
    public boolean isBroken(int damage, int maxDamage) {
        return !this.isUnbreakable && super.isBroken(damage, maxDamage);
    }
}
