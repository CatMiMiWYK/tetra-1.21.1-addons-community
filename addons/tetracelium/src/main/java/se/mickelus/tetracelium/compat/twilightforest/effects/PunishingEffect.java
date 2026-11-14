package se.mickelus.tetracelium.compat.twilightforest.effects;

import net.minecraft.network.protocol.game.ClientboundAnimatePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import se.mickelus.tetra.blocks.workbench.gui.WorkbenchStatsGui;
import se.mickelus.tetra.effect.ItemEffect;
import se.mickelus.tetra.gui.stats.bar.GuiStatBar;
import se.mickelus.tetra.gui.stats.getter.LabelGetterBasic;
import se.mickelus.tetra.gui.stats.getter.StatGetterEffectLevel;
import se.mickelus.tetra.gui.stats.getter.TooltipGetterInteger;
import se.mickelus.tetra.items.modular.ItemModularHandheld;
import se.mickelus.tetra.items.modular.impl.holo.gui.craft.HoloStatsGui;

public class PunishingEffect {
    public static final ItemEffect punishingEffect = ItemEffect.get("punishing");

    @SubscribeEvent
    public static void onKnightmetalToolDamage(LivingDamageEvent.Pre event) {
        LivingEntity target = event.getEntity();
        if (!target.level().isClientSide() && event.getSource().getDirectEntity() instanceof LivingEntity living) {
            ItemStack itemStack = living.getMainHandItem();
            Item item = itemStack.getItem();
            if (!itemStack.isEmpty() && item instanceof ItemModularHandheld modularItem) {
                int effectLevel = modularItem.getEffectLevel(itemStack, punishingEffect);
                if (effectLevel > 0 && target.getArmorValue() > 0) {
                    if (target.getAbsorptionAmount() > 0.0f) {
                        event.setNewDamage(event.getNewDamage() + (float) effectLevel * target.getAbsorptionAmount());
                    } else {
                        event.setNewDamage(event.getNewDamage() + (float) effectLevel);
                    }
                    ((ServerLevel) target.level()).getChunkSource().broadcastAndSend(target, new ClientboundAnimatePacket(target, 5));
                }
            }
        }
    }

    @OnlyIn(value = Dist.CLIENT)
    public static void clientInit() {
        StatGetterEffectLevel statGetter = new StatGetterEffectLevel(punishingEffect, 1.0);
        GuiStatBar statBar = new GuiStatBar(0, 0, 59, "tetra.stats.punishing", 0.0, 10.0, false, false, false, statGetter, LabelGetterBasic.integerLabel, new TooltipGetterInteger("tetra.stats.punishing.tooltip", statGetter));
        WorkbenchStatsGui.addBar(statBar);
        HoloStatsGui.addBar(statBar);
    }
}
