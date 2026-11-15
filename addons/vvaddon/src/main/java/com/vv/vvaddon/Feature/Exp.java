package com.vv.vvaddon.Feature;

import com.vv.vvaddon.Init.VACoe;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;

public class Exp {
    public static void execute(Player player , Entity entity , int level){
        // 1.21.1：Mob#getExperienceReward() 改为 getExperienceReward(ServerLevel, Entity)
        if(entity instanceof Mob mob && entity.level() instanceof ServerLevel serverLevel){
            double reward = mob.getExperienceReward(serverLevel, player);
            switch (level) {
                case 1:  reward *= VACoe.exp_bonus_1;break;
                case 2:  reward *= VACoe.exp_bonus_2;break;
                case 3:  reward *= VACoe.exp_bonus_3;break;
                default: reward *= 0;
            }
            player.giveExperiencePoints((int)Math.ceil(reward));
        }
    }
}
