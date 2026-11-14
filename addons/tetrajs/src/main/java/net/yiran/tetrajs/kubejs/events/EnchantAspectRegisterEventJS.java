package net.yiran.tetrajs.kubejs.events;

import dev.latvian.mods.kubejs.event.KubeEvent;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import se.mickelus.tetra.aspect.ItemAspect;
import se.mickelus.tetra.aspect.TetraEnchantmentHelper;

public class EnchantAspectRegisterEventJS implements KubeEvent {
    public void register(ItemAspect aspect, ItemStack... supportedItems) {
        TetraEnchantmentHelper.registerMapping(aspect, new TetraEnchantmentHelper.EnchantmentRules(List.of(supportedItems), "additions/" + aspect.getKey(), "exclusions/" + aspect.getKey()));
    }
}
