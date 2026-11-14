package net.yiran.morerequirement.sorter;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import se.mickelus.tetra.gui.stats.getter.IStatGetter;

import java.util.Arrays;

public class StatGetterItem implements IStatGetter {
    IStatGetter stat;
    Item[] items;
    TagKey<Item> tag;

    public StatGetterItem(IStatGetter stat, String[] items, String tag) {
        this.stat = stat;
        if (items != null) {
            this.items = Arrays.stream(items)
                    .map(ResourceLocation::parse)
                    .map(BuiltInRegistries.ITEM::get)
                    .toArray(Item[]::new);
        }
        if (tag != null) {
            this.tag = ItemTags.create(ResourceLocation.parse(tag));
        }
    }

    public boolean check(ItemStack stack) {
        if (this.items != null) {
            for (Item item : this.items) {
                if (stack.is(item)) {
                    return true;
                }
            }
        }
        if (this.tag != null) {
            return stack.is(this.tag);
        }
        return false;
    }

    @Override
    public double getValue(Player player, ItemStack itemStack) {
        return this.check(itemStack) ? this.stat.getValue(player, itemStack) : 0.0;
    }

    @Override
    public double getValue(Player player, ItemStack itemStack, String s) {
        return this.check(itemStack) ? this.stat.getValue(player, itemStack, s) : 0.0;
    }

    @Override
    public double getValue(Player player, ItemStack itemStack, String s, String s1) {
        return this.check(itemStack) ? this.stat.getValue(player, itemStack, s, s1) : 0.0;
    }

    @Override
    public boolean shouldShow(Player player, ItemStack currentStack, ItemStack previewStack) {
        return IStatGetter.super.shouldShow(player, currentStack, previewStack);
    }
}
