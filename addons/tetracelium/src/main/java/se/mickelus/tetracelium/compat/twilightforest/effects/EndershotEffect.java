package se.mickelus.tetracelium.compat.twilightforest.effects;

import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import se.mickelus.tetra.effect.ItemEffect;
import se.mickelus.tetra.event.ModularProjectileSpawnEvent;
import se.mickelus.tetra.items.modular.IModularItem;

public class EndershotEffect {
    public static final ItemEffect enderShotEffect = ItemEffect.get("enderShot");

    @SubscribeEvent
    public static void onArrowLoose(ModularProjectileSpawnEvent event) {
        Item item = event.getFiringStack().getItem();
        if (item instanceof IModularItem modularItem && modularItem.getEffectLevel(event.getFiringStack(), enderShotEffect) > 0) {
            event.getProjectileEntity().getPersistentData().putBoolean("twilightforest:ender", true);
        }
    }
}
