package net.yiran.tetrajs.items;

import java.util.List;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.yiran.tetrajs.util.SlotData;

public class EquipModularItem extends BaseModularItem implements Equipable {
    public EquipmentSlot slot;
    public boolean providerAttribute;

    public EquipModularItem(ResourceLocation itemID, List<SlotData> majorsData, List<SlotData> minorsData, boolean canHone, int honeBase, int honeIntegrityMultiplier, String[] synergiesPath, EquipmentSlot slot, boolean providerAttribute) {
        super(itemID, majorsData, minorsData, canHone, honeBase, honeIntegrityMultiplier, synergiesPath);
        this.slot = slot;
        this.providerAttribute = providerAttribute;
    }

    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
        if (!this.providerAttribute || this.isBroken(stack)) {
            return ItemAttributeModifiers.builder().build();
        }
        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();
        EquipmentSlotGroup group = getSlotGroup(this.slot);
        for (Map.Entry<Attribute, AttributeModifier> entry : this.getAttributeModifiersCached(stack).entries()) {
            builder.add(BuiltInRegistries.ATTRIBUTE.wrapAsHolder(entry.getKey()), this.Equip$fixIdentifiers(this.slot, entry.getValue()), group);
        }
        return builder.build();
    }

    @Override
    public EquipmentSlot getEquipmentSlot(ItemStack stack) {
        return this.slot;
    }

    @Override
    public EquipmentSlot getEquipmentSlot() {
        return this.slot;
    }

    public AttributeModifier Equip$fixIdentifiers(EquipmentSlot slot, AttributeModifier modifier) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(modifier.id().getNamespace(), modifier.id().getPath() + "_" + slot.getName());
        return new AttributeModifier(id, modifier.amount(), modifier.operation());
    }

    private static EquipmentSlotGroup getSlotGroup(EquipmentSlot slot) {
        return switch (slot) {
            case MAINHAND -> EquipmentSlotGroup.MAINHAND;
            case OFFHAND -> EquipmentSlotGroup.OFFHAND;
            case HEAD -> EquipmentSlotGroup.HEAD;
            case CHEST -> EquipmentSlotGroup.CHEST;
            case LEGS -> EquipmentSlotGroup.LEGS;
            case FEET -> EquipmentSlotGroup.FEET;
            default -> EquipmentSlotGroup.ANY;
        };
    }
}
