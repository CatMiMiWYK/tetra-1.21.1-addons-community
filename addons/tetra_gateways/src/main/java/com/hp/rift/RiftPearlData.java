package com.hp.rift;

import com.hp.effect.RiftEffects;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import se.mickelus.tetra.effect.EffectHelper;
import se.mickelus.tetra.util.ItemStackTagHelper;

import java.util.ArrayList;
import java.util.List;

public class RiftPearlData {
    public static final String ROOT = "tetra_gateways";
    public static final String CORE_REWARDS = "rift_core_rewards";
    public static final String SOURCE_GATE = "source_gate";
    public static final String USES = "rift_uses";
    public static final String ATTRIBUTE_SCALE = "rift_attribute_scale";
    public static final String DEFAULT_GATEWAY = "tetra_gateways:rift";

    public static void ensureGateway(ItemStack stack) {
        CompoundTag root = ItemStackTagHelper.getTag(stack);
        if (root == null || !root.contains("gateway")) {
            ItemStackTagHelper.mutate(stack, tag -> {
                if (!tag.contains("gateway")) {
                    tag.putString("gateway", DEFAULT_GATEWAY);
                }
            });
        }
    }

    public static void setCoreRewards(ItemStack stack, List<ItemStack> materials) {
        ListTag rewards = new ListTag();
        for (ItemStack material : materials) {
            ResourceLocation id = BuiltInRegistries.ITEM.getKey(material.getItem());
            if (id == null) {
                continue;
            }
            CompoundTag entry = new CompoundTag();
            entry.putString("id", id.toString());
            rewards.add(entry);
        }
        ItemStackTagHelper.mutate(stack, tag -> tag.getCompound(ROOT).put(CORE_REWARDS, rewards));
    }

    public static void clearCoreRewards(ItemStack stack) {
        CompoundTag root = ItemStackTagHelper.getTagElement(stack, ROOT);
        if (root != null) {
            root.remove(CORE_REWARDS);
        }
    }

    public static List<ItemStack> getCoreRewards(ItemStack stack) {
        CompoundTag root = ItemStackTagHelper.getTagElement(stack, ROOT);
        if (root == null || !root.contains(CORE_REWARDS)) {
            return List.of();
        }
        List<ItemStack> result = new ArrayList<>();
        ListTag rewards = root.getList(CORE_REWARDS, 10);
        for (int i = 0; i < rewards.size(); ++i) {
            ResourceLocation id = ResourceLocation.tryParse(rewards.getCompound(i).getString("id"));
            if (id == null || !BuiltInRegistries.ITEM.containsKey(id)) {
                continue;
            }
            result.add(new ItemStack(BuiltInRegistries.ITEM.get(id)));
        }
        return result;
    }

    public static int getRewardCount(ItemStack stack) {
        return Math.max(1, EffectHelper.getEffectLevel(stack, RiftEffects.RIFT_REWARD_COUNT));
    }

    public static int getUses(ItemStack stack) {
        CompoundTag root = ItemStackTagHelper.getTagElement(stack, ROOT);
        if (root == null || !root.contains(USES)) {
            return 0;
        }
        return Math.max(0, root.getInt(USES));
    }

    public static void setUses(ItemStack stack, int uses) {
        ItemStackTagHelper.mutate(stack, tag -> tag.getCompound(ROOT).putInt(USES, Math.max(0, uses)));
    }

    public static void consumeUse(ItemStack stack) {
        int uses = getUses(stack);
        setUses(stack, Math.max(0, uses - 1));
    }

    public static boolean canUse(ItemStack stack) {
        return getUses(stack) > 0;
    }

    public static void setAttributeScale(ItemStack stack, float scale) {
        ItemStackTagHelper.mutate(stack, tag -> tag.getCompound(ROOT).putFloat(ATTRIBUTE_SCALE, Math.max(0.0f, scale)));
    }

    public static float getAttributeScale(ItemStack stack) {
        CompoundTag root = ItemStackTagHelper.getTagElement(stack, ROOT);
        if (root == null || !root.contains(ATTRIBUTE_SCALE)) {
            return 1.0f;
        }
        return Math.max(0.0f, root.getFloat(ATTRIBUTE_SCALE));
    }

    public static void copyRiftData(ItemStack from, ItemStack to) {
        CompoundTag root = ItemStackTagHelper.getTagElement(from, ROOT);
        if (root != null) {
            ItemStackTagHelper.mutate(to, tag -> tag.put(ROOT, root.copy()));
        }
    }

    public static void writeSourcePearl(CompoundTag target, ItemStack stack, HolderLookup.Provider registries) {
        target.put(SOURCE_GATE, stack.save(registries));
    }

    public static ItemStack readSourcePearl(CompoundTag source, HolderLookup.Provider registries) {
        if (!source.contains(SOURCE_GATE)) {
            return ItemStack.EMPTY;
        }
        return ItemStack.parseOptional(registries, source.getCompound(SOURCE_GATE));
    }

    private RiftPearlData() {
    }
}
