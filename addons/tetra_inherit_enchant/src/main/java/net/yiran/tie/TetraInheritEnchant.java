package net.yiran.tie;

import com.mojang.logging.LogUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.CommonHooks;
import org.slf4j.Logger;
import se.mickelus.tetra.aspect.TetraEnchantmentHelper;
import se.mickelus.tetra.items.modular.IModularItem;
import se.mickelus.tetra.module.ItemModule;
import se.mickelus.tetra.module.ItemModuleMajor;
import se.mickelus.tetra.util.ItemStackTagHelper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Mod(TetraInheritEnchant.MODID)
public class TetraInheritEnchant {
    public static final String MODID = "tetra_inherit_enchant";
    private static final Logger LOGGER = LogUtils.getLogger();

    public TetraInheritEnchant(IEventBus modEventBus) {
    }

    /**
     * 1.20.1 原实现：模块被替换时，仅移除那些被映射到该槽位、且新模块不再接受的附魔。
     * 1.21.1 中附魔改由数据组件承载，故改用 {@link ItemEnchantments} + ItemStackTagHelper 实现。
     */
    public static void removeUnacceptedEnchantments(ItemStack stack, String slot) {
        if (stack.isEmpty() || !(stack.getItem() instanceof IModularItem modularItem)) {
            return;
        }

        CompoundTag map = ItemStackTagHelper.getTagElement(stack, "EnchantmentMapping");
        if (map == null) {
            return;
        }

        ItemModule module = modularItem.getModuleFromSlot(stack, slot);
        if (!(module instanceof ItemModuleMajor majorModule)) {
            return;
        }

        ItemEnchantments enchantments = stack.getTagEnchantments();
        Map<String, Holder<Enchantment>> holders = new HashMap<>();
        enchantments.entrySet().forEach(entry -> TetraEnchantmentHelper.getEnchantmentKey(entry.getKey())
                .ifPresent(key -> holders.put(key.toString(), entry.getKey())));

        Set<String> matching = new HashSet<>();
        for (String enchantmentKey : map.getAllKeys()) {
            if (!slot.equals(map.getString(enchantmentKey))) {
                continue;
            }
            Holder<Enchantment> holder = holders.get(enchantmentKey);
            if (holder == null) {
                holder = resolveEnchantment(enchantmentKey);
            }
            if (holder == null || !majorModule.acceptsEnchantment(stack, holder, false)) {
                matching.add(enchantmentKey);
            }
        }

        if (matching.isEmpty()) {
            return;
        }

        EnchantmentHelper.updateEnchantments(stack, mutable -> mutable.removeIf(holder -> TetraEnchantmentHelper.getEnchantmentKey(holder)
                .map(ResourceLocation::toString)
                .filter(matching::contains)
                .isPresent()));

        ItemStackTagHelper.mutate(stack, tag -> {
            if (tag.contains("EnchantmentMapping", Tag.TAG_COMPOUND)) {
                CompoundTag liveMap = tag.getCompound("EnchantmentMapping");
                matching.forEach(liveMap::remove);
            }
        });
    }

    private static Holder<Enchantment> resolveEnchantment(String key) {
        HolderLookup.RegistryLookup<Enchantment> lookup = CommonHooks.resolveLookup(Registries.ENCHANTMENT);
        if (lookup == null) {
            return null;
        }
        return lookup.get(ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.parse(key))).orElse(null);
    }
}
