package com.vv.vvaddon.Feature;

import com.vv.vvaddon.Config.VVAddonConfig;
import com.vv.vvaddon.Init.VACoe;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import se.mickelus.tetra.items.modular.ModularItem;

public class Homology {
    
    public static void execute(Player player , ModularItem mitem , ItemStack item){
        int i = 0;
        for(i = 1 ; i <= player.getInventory().getContainerSize() ; i++) {
            ItemStack itemstack = player.getInventory().getItem(i);
            // 1.21.1：ForgeRegistries.ITEMS -> BuiltInRegistries.ITEM
            ResourceLocation resourceLocation = BuiltInRegistries.ITEM.getKey(itemstack.getItem());
            String itemName = resourceLocation.toString();
            if (VVAddonConfig.Homology_item.get().contains(itemName)){
                int effnum = itemstack.getCount() > VACoe.homology_maxnum ? VACoe.homology_maxnum : itemstack.getCount();
                int efffix = Mth.ceil(effnum * VACoe.homology_speed);
                // 1.21.1：耐久读取改走 ItemStack#getDamageValue
                int damage = item.getDamageValue();
                efffix = damage>efffix ? efffix : damage;
                mitem.setDamage(item, (int)damage - efffix);
                break;
            }
        }
    }
}
