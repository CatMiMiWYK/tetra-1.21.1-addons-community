package com.vv.vvaddon.Feature;

import com.vv.vvaddon.Init.VACoe;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;

public class Vampire {
    public static boolean execute(float damage, Player player, ItemStack item){
        // 1.21.1：ItemStack#getEnchantmentLevel 已移除；Enchantments.MOB_LOOTING 改名为 LOOTING
        // 且为 ResourceKey，需要先经注册表取 Holder 再查询等级。
        final int level_bonus = EnchantmentHelper.getItemEnchantmentLevel(
                player.level().registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(Enchantments.LOOTING), item) + 1;
        final Level level = player.level();
        final float healamount =(float) (((damage * VACoe.vampire_rate) < VACoe.vampire_maxamount) ? damage * (float)VACoe.vampire_rate : VACoe.vampire_maxamount);
        if(level.random.nextFloat() <= (1.0D - VACoe.vampire_chance)/4.0){
            return false;
        }
        int i;
        for(i = 0 ; i < level_bonus ; i++){
            if(level.random.nextFloat() <= VACoe.vampire_chance){
                player.heal(healamount);
                return true;
            }
        }
        return false;
    }
}
