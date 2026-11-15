package com.vv.vvaddon.TestE;

import net.minecraft.world.item.ItemStack;
import se.mickelus.tetra.items.modular.ModularItem;

public class DamagedProcedure {
    public void executemin(ModularItem mitem , int fix , int pro , ItemStack item){
        // 1.21.1：耐久读取改走 ItemStack#getDamageValue
        if(item.getDamageValue() > fix){
            mitem.setDamage(item , item.getDamageValue()+fix);
        }
    }
}
