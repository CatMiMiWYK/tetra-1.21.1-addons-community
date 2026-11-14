package net.acetheeldritchking.art_of_forging.effects;

import net.acetheeldritchking.art_of_forging.capabilities.carnage.PlayerCarnage;
import net.acetheeldritchking.art_of_forging.capabilities.carnage.PlayerCarnageProvider;
import net.acetheeldritchking.art_of_forging.effects.gui.EffectGuiStats;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import se.mickelus.tetra.items.modular.ModularItem;

public class CarnageEffect {
    @SubscribeEvent
    public void onLivingAttackEvent(LivingDamageEvent.Pre event) {
        Entity attackingEntity = event.getSource().getEntity();
        if (attackingEntity instanceof LivingEntity attacker) {
            ItemStack heldStack = attacker.getMainHandItem();
            Item item = heldStack.getItem();
            if (item instanceof ModularItem item2) {
                int level = item2.getEffectLevel(heldStack, EffectGuiStats.carnageEffect);
                float eff = item2.getEffectEfficiency(heldStack, EffectGuiStats.carnageEffect);
                float baseAmount = event.getNewDamage();
                float bonusDamage = baseAmount * (eff / 100.0f);
                if (level > 0 && attacker instanceof Player player) {
                    PlayerCarnage carnage = player.getData(PlayerCarnageProvider.PLAYER_CARNAGE);
                    carnage.addCarnage(1);
                    switch (carnage.getCarnage()) {
                        case 1:
                            event.setNewDamage(baseAmount);
                            break;
                        case 2:
                            event.setNewDamage(baseAmount + bonusDamage);
                            break;
                        case 3:
                            event.setNewDamage(baseAmount + bonusDamage + this.addedPercent(baseAmount, 0.1f));
                            break;
                        case 4:
                            event.setNewDamage(baseAmount + bonusDamage + this.addedPercent(baseAmount, 0.2f));
                            break;
                        case 5:
                            event.setNewDamage(baseAmount + bonusDamage + this.addedPercent(baseAmount, 0.3f));
                            player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_STRONG, SoundSource.PLAYERS, 1.0f, 0.5f);
                    }
                }
            }
        }
    }

    private float addedPercent(float base, float added) {
        float total = base * added;
        return total;
    }
}
