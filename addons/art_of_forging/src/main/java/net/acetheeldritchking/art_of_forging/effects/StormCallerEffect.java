package net.acetheeldritchking.art_of_forging.effects;

import net.acetheeldritchking.art_of_forging.effects.gui.EffectGuiStats;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import se.mickelus.tetra.items.modular.ModularItem;

import java.util.Objects;

public class StormCallerEffect {
    @SubscribeEvent
    public void onLivingAttackEvent(LivingDamageEvent.Pre event) {
        LivingEntity target = event.getEntity();
        Entity eAttacker = event.getSource().getEntity();
        if (eAttacker instanceof LivingEntity attacker) {
            ItemStack heldStack = attacker.getMainHandItem();
            Item item = heldStack.getItem();
            if (item instanceof ModularItem item2) {
                int level = item2.getEffectLevel(heldStack, EffectGuiStats.stormCaller);
                float eff = item2.getEffectEfficiency(heldStack, EffectGuiStats.stormCaller);
                if (level > 0 && !attacker.level().isClientSide && eff > target.getRandom().nextFloat() * 100.0f) {
                    ServerLevel world = (ServerLevel) attacker.level();
                    ServerPlayer player = (ServerPlayer) attacker;
                    BlockPos position = target.blockPosition();
                    for (int i = 0; i < level; ++i) {
                        world.addFreshEntity(Objects.requireNonNull(EntityType.LIGHTNING_BOLT.create(world, null, position, MobSpawnType.TRIGGERED, true, true)));
                    }
                }
            }
        }
    }
}
