package com.vv.vvaddon.Feature;

import com.vv.vvaddon.Config.VVAddonConfig;
import com.vv.vvaddon.Init.VACoe;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class Charge {
    
    public static float execute(Player player){
        int num = 0 , i = 0;
        float bonus = 0.0F;
        for(i = 1 ; i <= player.getInventory().getContainerSize() ; i++) {
            ItemStack itemstack = player.getInventory().getItem(i);
            // 1.21.1：ForgeRegistries.ITEMS -> BuiltInRegistries.ITEM
            ResourceLocation resourceLocation = BuiltInRegistries.ITEM.getKey(itemstack.getItem());
            String itemName = resourceLocation.toString();
            if (VVAddonConfig.Charge_item.get().contains(itemName)){
                num = itemstack.getCount();
                itemstack.setCount(num-1);
                bonus = (float)VACoe.charge_bonus;
                break;
            }
        }
        return bonus;
    }
}
