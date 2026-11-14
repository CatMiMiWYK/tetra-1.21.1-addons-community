package net.yiran.tetrajs.kubejs.builders.items;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.yiran.tetrajs.items.BowModularItem;

public class BowModularItemBuilder extends AbstractWithSlotModularItemBuilder<BowModularItemBuilder> {
    public BowModularItemBuilder(ResourceLocation itemID) {
        super(itemID);
    }

    @Override
    public Item createObject() {
        return new BowModularItem(this.id, this.majorsData, this.minorsData, this.canHone, this.honeBase, this.honeIntegrityMultiplier, this.synergiesPath);
    }
}
