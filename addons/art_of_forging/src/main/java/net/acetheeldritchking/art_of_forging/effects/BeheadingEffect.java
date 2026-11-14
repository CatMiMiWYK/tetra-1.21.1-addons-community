package net.acetheeldritchking.art_of_forging.effects;

import com.mojang.authlib.GameProfile;
import net.acetheeldritchking.art_of_forging.effects.gui.EffectGuiStats;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ResolvableProfile;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import se.mickelus.tetra.items.modular.ModularItem;

public class BeheadingEffect {
    @SubscribeEvent
    public void onLivingDropEvent(LivingDropsEvent event) {
        LivingEntity target = event.getEntity();
        Entity attackingEntity = event.getSource().getEntity();
        if (attackingEntity instanceof LivingEntity attacker) {
            ItemStack heldStack = attacker.getMainHandItem();
            Item item = heldStack.getItem();
            if (item instanceof ModularItem item2) {
                float level = item2.getEffectLevel(heldStack, EffectGuiStats.beheadingEffect);
                float chance = level / 100.0f;
                if (level > 0.0f) {
                    ItemStack headDrop = ItemStack.EMPTY;
                    if (target instanceof Zombie) {
                        headDrop = new ItemStack(Items.ZOMBIE_HEAD);
                    } else if (target instanceof Creeper) {
                        headDrop = new ItemStack(Items.CREEPER_HEAD);
                    } else if (target instanceof Skeleton) {
                        headDrop = new ItemStack(Items.SKELETON_SKULL);
                    } else if (target instanceof WitherSkeleton || target instanceof WitherBoss) {
                        headDrop = new ItemStack(Items.WITHER_SKELETON_SKULL);
                    } else if (target instanceof EnderDragon) {
                        headDrop = new ItemStack(Items.DRAGON_HEAD);
                    } else if (target instanceof Player player) {
                        headDrop = new ItemStack(Items.PLAYER_HEAD);
                        GameProfile profile = player.getGameProfile();
                        headDrop.set(DataComponents.PROFILE, new ResolvableProfile(profile));
                    }
                    if (!headDrop.isEmpty()) {
                        boolean drop = target.level().random.nextFloat() < chance;
                        if (drop) {
                            ItemEntity itemDrop = new ItemEntity(target.level(), target.getX(), target.getY(), target.getZ(), headDrop);
                            itemDrop.setDefaultPickUpDelay();
                            event.getDrops().add(itemDrop);
                        }
                    }
                }
            }
        }
    }
}
