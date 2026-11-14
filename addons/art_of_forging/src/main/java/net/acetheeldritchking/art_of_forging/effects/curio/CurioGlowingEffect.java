package net.acetheeldritchking.art_of_forging.effects.curio;

import net.acetheeldritchking.art_of_forging.effects.gui.EffectGuiStats;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import se.mickelus.tetra.items.modular.ModularItem;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class CurioGlowingEffect
        implements ICurioItem {
    @SubscribeEvent
    public void onLivingAttackEvent(LivingDamageEvent.Pre event) {
        Entity entity = event.getSource().getEntity();
        LivingEntity target = event.getEntity();
        if (entity instanceof Player player) {
            CuriosApi.getCuriosInventory(player).ifPresent(inv -> inv.findCurios(itemStack -> itemStack.getItem() instanceof ModularItem).forEach(slotResult -> {
                ItemStack itemStack = slotResult.stack();
                ModularItem item = (ModularItem) itemStack.getItem();
                int level = item.getEffectLevel(itemStack, EffectGuiStats.thirdSightEffect);
                if (level > 0) {
                    target.addEffect(new MobEffectInstance(MobEffects.GLOWING, level * 20, 0, false, false, false));
                }
            }));
        }
    }
}
