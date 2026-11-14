package net.acetheeldritchking.art_of_forging.effects;

import net.acetheeldritchking.art_of_forging.effects.gui.EffectGuiStats;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import se.mickelus.tetra.items.modular.ModularItem;

public class ResolveEffect {
    @SubscribeEvent
    public void onPlayerTickEvent(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        ItemStack heldStack = player.getMainHandItem();
        Item item = heldStack.getItem();
        if (item instanceof ModularItem item2) {
            int level = item2.getEffectLevel(heldStack, EffectGuiStats.resolveEffect);
            float eff = item2.getEffectEfficiency(heldStack, EffectGuiStats.resolveEffect);
            float health = player.getHealth();
            if (level > 0 && eff >= health) {
                this.applyEffects(level, player);
            }
        }
    }

    private void applyEffects(int duration, LivingEntity user) {
        user.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, duration * 20, 1, true, true, true));
        user.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, duration * 20, 1, true, true, true));
        user.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, duration * 20, 1, true, true, true));
    }
}
