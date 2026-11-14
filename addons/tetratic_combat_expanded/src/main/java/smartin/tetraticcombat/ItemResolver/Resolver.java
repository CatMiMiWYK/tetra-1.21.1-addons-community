package smartin.tetraticcombat.ItemResolver;

import com.google.gson.Gson;
import net.bettercombat.api.AttributesContainer;
import net.bettercombat.api.WeaponAttributes;
import net.bettercombat.api.WeaponAttributesHelper;
import net.bettercombat.logic.ItemStackNBTWeaponAttributes;
import net.bettercombat.logic.WeaponRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import se.mickelus.tetra.effect.ItemEffect;
import se.mickelus.tetra.items.modular.ModularItem;
import se.mickelus.tetra.properties.AttributeHelper;
import se.mickelus.tetra.util.ItemStackTagHelper;
import smartin.tetraticcombat.ForgeConfigHolder;
import smartin.tetraticcombat.TetraticCombat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Resolver {
    private static final Logger LOGGER = LogManager.getLogger();
    public static JSONFormat weaponConfig;

    public static void reload(JSONFormat config) {
        weaponConfig = config;
    }

    public static void readConfig(String config) {
        LOGGER.info(config);
        weaponConfig = new Gson().fromJson(config, JSONFormat.class);
    }

    public static ExpandedContainer findWeaponByNBT(ItemStack stack) {
        if (weaponConfig == null) {
            return null;
        }
        if (ItemStackTagHelper.hasTag(stack)) {
            CompoundTag tag = ItemStackTagHelper.getTag(stack);
            if (tag == null) {
                return null;
            }
            for (String key : tag.getAllKeys()) {
                Map<String, Condition> map1;
                if (!Resolver.weaponConfig.attributemap.containsKey(key) || !(map1 = Resolver.weaponConfig.attributemap.get(key)).containsKey(tag.getString(key))) continue;
                return map1.get(tag.getString(key)).resolve(stack);
            }
        }
        return null;
    }

    public static ItemStack generateBetterCombatNBT(ItemStack itemStack) {
        return Resolver.generateBetterCombatNBT(itemStack, false);
    }

    public static void resetBetterCombatNBT(ItemStack itemStack) {
        if (ItemStackTagHelper.hasTag(itemStack)) {
            CompoundTag tag = ItemStackTagHelper.getTag(itemStack);
            if (tag != null && tag.contains(WeaponAttributesHelper.nbtTag)) {
                ItemStackTagHelper.removeTagKey(itemStack, WeaponAttributesHelper.nbtTag);
            }
        }
        if ((Object) itemStack instanceof ItemStackNBTWeaponAttributes attributedItemStack) {
            attributedItemStack.setWeaponAttributes(null);
            attributedItemStack.setInvalidAttributes(false);
        }
    }

    public static ItemStack generateBetterCombatNBT(ItemStack itemStack, boolean force) {
        ExpandedContainer container = Resolver.findWeaponByNBT(itemStack);
        if (container != null) {
            if (force && ItemStackTagHelper.hasTag(itemStack)) {
                CompoundTag existing = ItemStackTagHelper.getTag(itemStack);
                if (existing != null && existing.contains("weapon_attributes")) {
                    ItemStackTagHelper.removeTagKey(itemStack, "weapon_attributes");
                }
            }
            try {
                double range = Resolver.getAttackRange(itemStack);
                WeaponAttributes attributes = WeaponRegistry.resolveAttributes(ResourceLocation.parse("tetratic:generated"), container.attributes);
                if (ForgeConfigHolder.COMMON.enableTetraRange.get()) {
                    attributes = new WeaponAttributes(range, attributes.pose(), attributes.offHandPose(), attributes.isTwoHanded(), attributes.category(), attributes.attacks());
                }
                Resolver.rescaleUpswing(attributes, Resolver.getQuickStat(itemStack));
                WeaponAttributesHelper.validate(attributes);
                AttributesContainer attributesContainer = new AttributesContainer(container.attributes.parent(), attributes);
                Resolver.persistWeaponAttributes(itemStack, attributesContainer);
                Resolver.applyScale(itemStack, container.scaleX, container.scaleY, container.scaleZ);
                Resolver.applyTranslation(itemStack, container.translationX, container.translationY, container.translationZ);
                return itemStack;
            } catch (Exception e) {
                TetraticCombat.LOGGER.warn(e.getMessage());
                e.printStackTrace();
            }
        }
        return itemStack;
    }

    private static void persistWeaponAttributes(ItemStack itemStack, AttributesContainer attributesContainer) {
        // BC 2.0.4 的 writeToNBT 只改 CustomData.getUnsafe()，不调用 ItemStack#set，改动不保证落盘/同步；
        // 这里用 BC 的公开 encode() 拿字符串，经 tetra 的 mutate 真正写入 CUSTOM_DATA，再清掉 BC 的属性缓存。
        String encoded = WeaponAttributesHelper.encode(attributesContainer);
        ItemStackTagHelper.mutate(itemStack, nbt -> nbt.putString(WeaponAttributesHelper.nbtTag, encoded));
        if ((Object) itemStack instanceof ItemStackNBTWeaponAttributes attributedItemStack) {
            attributedItemStack.setInvalidAttributes(false);
            attributedItemStack.setWeaponAttributes(null);
        }
    }

    private static void applyScale(ItemStack stack, float x, float y, float z) {
        ItemStackTagHelper.mutate(stack, nbt -> {
            if (x != 1.0f) {
                nbt.putFloat("tetraticScaleX", x);
            } else if (nbt.contains("tetraticScaleX")) {
                nbt.remove("tetraticScaleX");
            }
            if (y != 1.0f) {
                nbt.putFloat("tetraticScaleY", y);
            } else if (nbt.contains("tetraticScaleY")) {
                nbt.remove("tetraticScaleY");
            }
            if (z != 1.0f) {
                nbt.putFloat("tetraticScaleZ", z);
            } else if (nbt.contains("tetraticScaleZ")) {
                nbt.remove("tetraticScaleZ");
            }
        });
    }

    private static void applyTranslation(ItemStack stack, double x, double y, double z) {
        ItemStackTagHelper.mutate(stack, nbt -> {
            if (x != 0.0) {
                nbt.putDouble("tetraticTranslateX", x);
            } else if (nbt.contains("tetraticTranslateX")) {
                nbt.remove("tetraticTranslateX");
            }
            if (y != 0.0) {
                nbt.putDouble("tetraticTranslateY", y);
            } else if (nbt.contains("tetraticTranslateY")) {
                nbt.remove("tetraticTranslateY");
            }
            if (z != 0.0) {
                nbt.putDouble("tetraticTranslateZ", z);
            } else if (nbt.contains("tetraticTranslateZ")) {
                nbt.remove("tetraticTranslateZ");
            }
        });
    }

    private static double getQuickStat(ItemStack stack) {
        Item item = stack.getItem();
        if (item instanceof ModularItem item2) {
            Map<ItemEffect, Float> map = item2.getEffectData(stack).levelMap;
            if (map.containsKey(ItemEffect.quickStrike)) {
                float level = map.get(ItemEffect.quickStrike);
                return (double) level * 0.05 + 0.2;
            }
        }
        return 0.0;
    }

    private static void rescaleUpswing(WeaponAttributes weaponAttributes, double scale) {
        if (!ForgeConfigHolder.COMMON.quickReducesUpswing.get()) {
            return;
        }
        WeaponAttributes.Attack[] attacks = weaponAttributes.attacks();
        for (int i = 0; i < attacks.length; ++i) {
            double newUpswing = Math.max(0.0, attacks[i].upswing() - attacks[i].upswing() * scale);
            attacks[i] = new WeaponAttributes.Attack(attacks[i].conditions(), attacks[i].hitbox(), attacks[i].damageMultiplier(), attacks[i].angle(), newUpswing, attacks[i].animation(), attacks[i].swingSound(), attacks[i].impactSound());
        }
    }

    private static double getAttackRange(ItemStack itemStack) {
        Item item = itemStack.getItem();
        if (item instanceof ModularItem item2) {
            Attribute entityRange = Attributes.ENTITY_INTERACTION_RANGE.value();
            if (item2.getAttributeValue(itemStack, entityRange) != 0.0) {
                return 3.0 + item2.getAttributeValue(itemStack, entityRange);
            }
            if (ForgeConfigHolder.COMMON.reachFallBack.get()) {
                return 3.0 + item2.getAttributeValue(itemStack, Attributes.BLOCK_INTERACTION_RANGE.value());
            }
            return 3.0;
        }
        try {
            List<AttributeModifier> modifiers = new ArrayList<>();
            itemStack.forEachModifier(itemStack.getEquipmentSlot(), (holder, modifier) -> {
                if (holder.value() == Attributes.ENTITY_INTERACTION_RANGE.value()) {
                    modifiers.add(modifier);
                }
            });
            return AttributeHelper.getMergedAmount(modifiers, 3.0);
        } catch (Exception e) {
            return 3.0;
        }
    }
}

