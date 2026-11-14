package net.acetheeldritchking.art_of_forging.effects;

import net.acetheeldritchking.art_of_forging.AoFRegistry;
import net.acetheeldritchking.art_of_forging.effects.gui.EffectGuiStats;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import se.mickelus.tetra.items.modular.ModularItem;

public class NanoFusedEffect {
    @SubscribeEvent
    public void onLivingDropEvent(LivingDropsEvent event) {
        LivingEntity target = event.getEntity();
        Entity attackingEntity = event.getSource().getEntity();
        if (attackingEntity instanceof LivingEntity attacker) {
            ItemStack heldStack = attacker.getMainHandItem();
            Item item = heldStack.getItem();
            if (item instanceof ModularItem item2) {
                float level = item2.getEffectLevel(heldStack, EffectGuiStats.nanoFusedEffect);
                float chance = level / 100.0f;
                if (level > 0.0f) {
                    ItemStack nanoDrop = ItemStack.EMPTY;
                    if (target instanceof Creeper) {
                        nanoDrop = new ItemStack(AoFRegistry.POTENT_MIXTURE.get());
                    } else if (target instanceof EnderMan) {
                        nanoDrop = new ItemStack(AoFRegistry.HEART_OF_ENDER.get());
                    } else if (target instanceof Warden) {
                        nanoDrop = new ItemStack(AoFRegistry.EERIE_SHARD.get());
                    } else if (target instanceof WitherBoss) {
                        nanoDrop = new ItemStack(AoFRegistry.DEVILS_SOUL_GEM.get());
                    }
                    if (!nanoDrop.isEmpty()) {
                        boolean drop = target.level().random.nextFloat() < chance;
                        if (drop) {
                            ItemEntity itemDrop = new ItemEntity(target.level(), target.getX(), target.getY(), target.getZ(), nanoDrop);
                            itemDrop.setDefaultPickUpDelay();
                            event.getDrops().add(itemDrop);
                        }
                    }
                }
            }
        }
    }
}
