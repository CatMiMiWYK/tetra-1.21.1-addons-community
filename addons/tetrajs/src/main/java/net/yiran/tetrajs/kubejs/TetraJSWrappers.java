package net.yiran.tetrajs.kubejs;

import com.mojang.serialization.DynamicOps;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.neoforged.neoforge.common.ItemAbility;
import net.yiran.tetrajs.util.NbtSlotData;
import se.mickelus.tetra.aspect.ItemAspect;
import se.mickelus.tetra.effect.ItemEffect;

public class TetraJSWrappers {
    public static ItemEffect toItemEffect(Object o) {
        if (o instanceof ItemEffect effect) {
            return effect;
        }
        if (o instanceof String string) {
            return ItemEffect.get(string);
        }
        return ItemEffect.get(o.toString());
    }

    public static ItemAbility toItemAbility(Object o) {
        if (o instanceof ItemAbility ability) {
            return ability;
        }
        if (o instanceof String string) {
            return ItemAbility.get(string);
        }
        return ItemAbility.get(o.toString());
    }

    public static NbtSlotData toNbtSlotData(Object o) {
        if (o instanceof NbtSlotData slotData) {
            return slotData;
        }
        if (o instanceof CompoundTag tag) {
            return NbtSlotData.CODEC.parse((DynamicOps<Tag>) NbtOps.INSTANCE, tag).result().orElseThrow();
        }
        if (o instanceof Map<?, ?> map) {
            boolean required = !(map.get("required") instanceof Boolean b) || b;
            boolean isMajor = !(map.get("isMajor") instanceof Boolean b) || b;
            String slot = String.valueOf(map.get("slot"));
            int x = map.get("x") instanceof Number n ? n.intValue() : 0;
            int y = map.get("y") instanceof Number n ? n.intValue() : 0;
            return NbtSlotData.create(slot, x, y, isMajor, required);
        }
        throw new IllegalArgumentException("Cannot convert to NbtSlotData: " + o);
    }

    public static Attribute toAttribute(Object o) {
        if (o instanceof Attribute attribute) {
            return attribute;
        }
        if (o instanceof ResourceLocation id) {
            return BuiltInRegistries.ATTRIBUTE.get(id);
        }
        return BuiltInRegistries.ATTRIBUTE.get(ResourceLocation.parse(o.toString()));
    }

    public static ItemAspect toItemAspect(Object o) {
        if (o instanceof ItemAspect aspect) {
            return aspect;
        }
        if (o instanceof String string) {
            return ItemAspect.get(string);
        }
        return ItemAspect.get(o.toString());
    }
}
