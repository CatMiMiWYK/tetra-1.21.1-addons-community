package net.acetheeldritchking.art_of_forging.effects.curio;

import net.acetheeldritchking.art_of_forging.capabilities.karma.PlayerKarma;
import net.acetheeldritchking.art_of_forging.capabilities.karma.PlayerKarmaProvider;
import net.acetheeldritchking.art_of_forging.effects.gui.EffectGuiStats;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import se.mickelus.tetra.items.modular.ModularItem;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class CurioKarmaEffect
        implements ICurioItem {
    @SubscribeEvent
    public void onLivingDeathEvent(LivingDeathEvent event) {
        Entity entity = event.getSource().getEntity();
        LivingEntity target = event.getEntity();
        if (entity instanceof Player player) {
            CuriosApi.getCuriosInventory(player).ifPresent(inv -> inv.findCurios(itemStack -> itemStack.getItem() instanceof ModularItem).forEach(slotResult -> {
                ItemStack itemStack = slotResult.stack();
                ModularItem item = (ModularItem) itemStack.getItem();
                int level = item.getEffectLevel(itemStack, EffectGuiStats.karmaInfusedEffect);
                if (level > 0 && !player.level().isClientSide) {
                    PlayerKarma karma = player.getData(PlayerKarmaProvider.PLAYER_KARMA);
                    if (target instanceof Monster) {
                        karma.addKarma(1);
                    } else {
                        karma.subKarma(1);
                    }
                }
            }));
        }
    }

    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        CuriosApi.getCuriosInventory(player).ifPresent(inv -> inv.findCurios(itemStack -> itemStack.getItem() instanceof ModularItem).forEach(slotResult -> {
            if (player.tickCount % 20 == 0) {
                ItemStack itemStack = slotResult.stack();
                ModularItem item = (ModularItem) itemStack.getItem();
                int level = item.getEffectLevel(itemStack, EffectGuiStats.karmaInfusedEffect);
                if (level > 0 && !player.level().isClientSide) {
                    PlayerKarma karma = player.getData(PlayerKarmaProvider.PLAYER_KARMA);
                    switch (karma.getKarma()) {
                        case -5:
                            player.addEffect(new MobEffectInstance(MobEffects.UNLUCK, level * 20, 4, true, true, true));
                            break;
                        case -4:
                            player.addEffect(new MobEffectInstance(MobEffects.UNLUCK, level * 20, 3, true, true, true));
                            break;
                        case -3:
                            player.addEffect(new MobEffectInstance(MobEffects.UNLUCK, level * 20, 2, true, true, true));
                            break;
                        case -2:
                            player.addEffect(new MobEffectInstance(MobEffects.UNLUCK, level * 20, 1, true, true, true));
                            break;
                        case -1:
                            player.addEffect(new MobEffectInstance(MobEffects.UNLUCK, level * 20, 0, true, true, true));
                            break;
                        case 0:
                            player.removeEffect(MobEffects.UNLUCK);
                            player.removeEffect(MobEffects.LUCK);
                            break;
                        case 1:
                            player.addEffect(new MobEffectInstance(MobEffects.LUCK, level * 20, 0, true, true, true));
                            break;
                        case 2:
                            player.addEffect(new MobEffectInstance(MobEffects.LUCK, level * 20, 1, true, true, true));
                            break;
                        case 3:
                            player.addEffect(new MobEffectInstance(MobEffects.LUCK, level * 20, 2, true, true, true));
                            break;
                        case 4:
                            player.addEffect(new MobEffectInstance(MobEffects.LUCK, level * 20, 3, true, true, true));
                            break;
                        case 5:
                            player.addEffect(new MobEffectInstance(MobEffects.LUCK, level * 20, 4, true, true, true));
                            break;
                    }
                }
            }
        }));
    }
}
