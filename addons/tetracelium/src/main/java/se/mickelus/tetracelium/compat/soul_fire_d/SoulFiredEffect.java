package se.mickelus.tetracelium.compat.soul_fire_d;

import it.crystalnest.soul_fire_d.fire.FireRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.neoforged.bus.api.SubscribeEvent;
import se.mickelus.tetra.event.ModularProjectileSpawnEvent;

public class SoulFiredEffect {
    private static final ResourceKey<Enchantment> SOUL_FLAME = ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.withDefaultNamespace("soul_flame"));
    private static final ResourceKey<Enchantment> SOUL_FIRE_ASPECT = ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.withDefaultNamespace("soul_fire_aspect"));

    @SubscribeEvent
    public static void onModularProjectileSpawn(ModularProjectileSpawnEvent event) {
        ItemStack firingStack = event.getFiringStack();
        if (hasSoulFlame(firingStack)) {
            Entity projectile = event.getProjectileEntity();
            projectile.setRemainingFireTicks(100);
            try {
                Class<?> changer = Class.forName("it.crystalnest.prometheus.api.type.FireTypeChanger");
                changer.getMethod("setFireType", ResourceLocation.class).invoke(projectile, FireRegistry.SOUL_FIRE_TYPE);
            } catch (ReflectiveOperationException ignored) {
            }
        }
    }

    private static boolean hasSoulFlame(ItemStack stack) {
        ItemEnchantments enchantments = stack.getEnchantments();
        for (Holder<Enchantment> enchantment : enchantments.keySet()) {
            if (enchantment.is(SOUL_FLAME) || enchantment.is(SOUL_FIRE_ASPECT)) {
                return true;
            }
        }
        return false;
    }
}
