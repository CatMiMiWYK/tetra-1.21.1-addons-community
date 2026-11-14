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
import se.mickelus.tetra.gui.stats.getter.TooltipGetterDecimalSingle;
import se.mickelus.tetra.items.modular.IModularItem;
import se.mickelus.tetra.items.modular.impl.holo.gui.craft.HoloStatsGui;

public class ChargingEffect {
    public static final ItemEffect chargingEffect = ItemEffect.get("charging");

    @SubscribeEvent
    public static void onLivingHurt(LivingDamageEvent.Pre event) {
        LivingEntity target = event.getEntity();
        Entity source = event.getSource().getDirectEntity();
        if (!target.level().isClientSide() && source instanceof LivingEntity attacker) {
            if (source.isSprinting() && (event.getSource().getMsgId().equals("player") || event.getSource().getMsgId().equals("mob"))) {
                ItemStack weaponStack = attacker.getMainHandItem();
                Item item = weaponStack.getItem();
                if (!weaponStack.isEmpty() && item instanceof IModularItem modularItem) {
                    float chargingLevel = (float) modularItem.getEffectLevel(weaponStack, chargingEffect);
                    if (chargingLevel > 0.0f) {
                        event.setNewDamage(event.getNewDamage() + chargingLevel * 0.5f);
                        ((ServerLevel) target.level()).getChunkSource().broadcastAndSend(target, new ClientboundAnimatePacket(target, 5));
                    }
                }
            }
        }
    }

    @OnlyIn(value = Dist.CLIENT)
    public static void clientInit() {
        StatGetterEffectLevel statGetter = new StatGetterEffectLevel(chargingEffect, 0.5);
        GuiStatBar statBar = new GuiStatBar(0, 0, 59, "tetra.stats.charging", 0.0, 10.0, false, false, false, statGetter, LabelGetterBasic.singleDecimalLabel, new TooltipGetterDecimalSingle("tetra.stats.charging.tooltip", statGetter));
        WorkbenchStatsGui.addBar(statBar);
        HoloStatsGui.addBar(statBar);
    }
}
