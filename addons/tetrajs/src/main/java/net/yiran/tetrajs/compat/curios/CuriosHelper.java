package net.yiran.tetrajs.compat.curios;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import dev.latvian.mods.rhino.util.HideFromJS;
import java.util.List;
import java.util.Map;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import se.mickelus.tetra.effect.ItemEffect;
import se.mickelus.tetra.items.modular.IModularItem;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.type.util.ICuriosHelper;

public class CuriosHelper {
    public static CuriosHelper INSTANCE;
    public ICuriosHelper curiosHelper = CuriosApi.getCuriosHelper();

    public static CuriosHelper getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CuriosHelper();
        }
        return INSTANCE;
    }

    public List<ItemStack> getAllModularItemsInCurios(LivingEntity entity) {
        return this.curiosHelper.findCurios(entity, stack -> stack.getItem() instanceof IModularItem).stream().map(SlotResult::stack).toList();
    }

    public boolean hasCuriosEffectLevel(LivingEntity entity, ItemEffect effect) {
        return this.getAllModularItemsInCurios(entity).stream().anyMatch(stack -> ((IModularItem) stack.getItem()).getEffects(stack).contains(effect));
    }

    public int getCuriosEffectLevel(LivingEntity entity, ItemEffect effect) {
        return this.getAllModularItemsInCurios(entity).stream().mapToInt(stack -> ((IModularItem) stack.getItem()).getEffectLevel(stack, effect)).sum();
    }

    public int getCuriosEffectMaxLevel(LivingEntity entity, ItemEffect effect) {
        return this.getAllModularItemsInCurios(entity).stream().mapToInt(stack -> ((IModularItem) stack.getItem()).getEffectLevel(stack, effect)).reduce(0, Math::max);
    }

    public float getCuriosEffectEfficiency(LivingEntity entity, ItemEffect effect) {
        return this.getAllModularItemsInCurios(entity).stream()
                .map(stack -> ((IModularItem) stack.getItem()).getEffectEfficiency(stack, effect))
                .reduce(0.0f, Float::sum);
    }

    public float getCuriosEffectMaxEfficiency(LivingEntity entity, ItemEffect effect) {
        return this.getAllModularItemsInCurios(entity).stream()
                .map(stack -> ((IModularItem) stack.getItem()).getEffectEfficiency(stack, effect))
                .reduce(0.0f, Math::max);
    }

    @HideFromJS
    public static Multimap<Holder<Attribute>, AttributeModifier> Curios$fixIdentifiers(SlotContext slotContext, Multimap<Attribute, AttributeModifier> modifiers) {
        Multimap<Holder<Attribute>, AttributeModifier> result = ArrayListMultimap.create();
        if (modifiers == null) {
            return result;
        }
        for (Map.Entry<Attribute, AttributeModifier> entry : modifiers.entries()) {
            AttributeModifier modifier = entry.getValue();
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath(
                    modifier.id().getNamespace(),
                    modifier.id().getPath() + "_" + slotContext.identifier() + "_" + slotContext.index());
            result.put(BuiltInRegistries.ATTRIBUTE.wrapAsHolder(entry.getKey()), new AttributeModifier(id, modifier.amount(), modifier.operation()));
        }
        return result;
    }
}
