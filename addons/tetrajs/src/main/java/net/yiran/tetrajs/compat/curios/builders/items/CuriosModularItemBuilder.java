package net.yiran.tetrajs.compat.curios.builders.items;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.yiran.tetrajs.compat.curios.items.CuriosModularItem;
import net.yiran.tetrajs.kubejs.builders.items.AbstractWithSlotModularItemBuilder;

public class CuriosModularItemBuilder extends AbstractWithSlotModularItemBuilder<CuriosModularItemBuilder> {
    public boolean providerAttribute = true;
    public boolean isUnbreakable = false;

    public CuriosModularItemBuilder(ResourceLocation itemID) {
        super(itemID);
    }

    public CuriosModularItemBuilder tjs$setProviderAttribute(boolean providerAttribute) {
        this.providerAttribute = providerAttribute;
        return this;
    }

    public CuriosModularItemBuilder tjs$addCuriosSlot(String curiosSlot) {
        this.tag(new ResourceLocation[]{ResourceLocation.fromNamespaceAndPath("curios", curiosSlot)});
        return this;
    }

    public CuriosModularItemBuilder tjs$isUnbreakable() {
        this.isUnbreakable = true;
        return this;
    }

    @Override
    public Item createObject() {
        return new CuriosModularItem(this.id, this.majorsData, this.minorsData, this.canHone, this.honeBase, this.honeIntegrityMultiplier, this.synergiesPath, this.providerAttribute, this.isUnbreakable);
    }
}
