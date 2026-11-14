package net.acetheeldritchking.art_of_forging.effects.curio;

import net.acetheeldritchking.art_of_forging.effects.gui.EffectGuiStats;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import se.mickelus.tetra.items.modular.ModularItem;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class CurioRegenerationEffect
        implements ICurioItem {
    @SubscribeEvent
    public void onPlayerTickEvent(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        CuriosApi.getCuriosInventory(player).ifPresent(inv -> inv.findCurios(itemStack -> itemStack.getItem() instanceof ModularItem).forEach(slotResult -> {
            if (player.tickCount % 20 == 0) {
                ItemStack itemStack = slotResult.stack();
                ModularItem item = (ModularItem) itemStack.getItem();
                int level = item.getEffectLevel(itemStack, EffectGuiStats.healingInfusedEffect);
                int eff = (int) item.getEffectEfficiency(itemStack, EffectGuiStats.healingInfusedEffect);
                if (level > 0) {
                    player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, eff * 20, level - 1, true, true, true));
                }
            }
        }));
    }
}
