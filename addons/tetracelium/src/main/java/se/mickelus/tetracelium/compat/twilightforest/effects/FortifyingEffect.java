package se.mickelus.tetracelium.compat.twilightforest.effects;

import java.util.Optional;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import se.mickelus.tetra.blocks.workbench.gui.WorkbenchStatsGui;
import se.mickelus.tetra.effect.EffectHelper;
import se.mickelus.tetra.effect.ItemEffect;
import se.mickelus.tetra.gui.stats.bar.GuiStatBar;
import se.mickelus.tetra.gui.stats.getter.LabelGetterBasic;
import se.mickelus.tetra.gui.stats.getter.StatGetterEffectEfficiency;
import se.mickelus.tetra.gui.stats.getter.StatGetterEffectLevel;
import se.mickelus.tetra.gui.stats.getter.TooltipGetterPercentage;
import se.mickelus.tetra.items.modular.IModularItem;
import se.mickelus.tetra.items.modular.ThrownModularItemEntity;
import se.mickelus.tetra.items.modular.impl.holo.gui.craft.HoloStatsGui;
import twilightforest.components.entity.FortificationShieldAttachment;
import twilightforest.init.TFDataAttachments;

public class FortifyingEffect {
    public static final ItemEffect fortifyingEffect = ItemEffect.get("fortifying");

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        ItemStack itemStack;
        Entity killer;
        if ("trident".equals(event.getSource().getMsgId()) && event.getSource().getDirectEntity() instanceof ThrownModularItemEntity modularEntity) {
            itemStack = modularEntity.getThrownStack();
            killer = modularEntity.getOwner();
        } else {
            Entity sourceEntity = event.getSource().getEntity();
            itemStack = Optional.ofNullable(sourceEntity)
                    .filter(entity -> entity instanceof Player)
                    .map(entity -> ((LivingEntity) entity).getMainHandItem())
                    .filter(stack -> stack.getItem() instanceof IModularItem)
                    .orElse(null);
            killer = sourceEntity;
        }
        if (itemStack != null) {
            Level level = event.getEntity().level();
            if (!level.isClientSide && EffectHelper.getEffectLevel(itemStack, fortifyingEffect) > 0 && killer instanceof ServerPlayer player) {
                FortificationShieldAttachment shields = player.getData(TFDataAttachments.FORTIFICATION_SHIELDS);
                shields.setShields(player, Math.min(shields.temporaryShieldsLeft() + 1, 4), true);
                itemStack.hurtAndBreak((int) Math.ceil((double) itemStack.getMaxDamage() * 0.01 * (double) EffectHelper.getEffectEfficiency(itemStack, fortifyingEffect)), (ServerLevel) level, player, item -> {
                });
            }
        }
    }

    @OnlyIn(value = Dist.CLIENT)
    public static void clientInit() {
        StatGetterEffectLevel statGetter = new StatGetterEffectLevel(fortifyingEffect, 1.0);
        StatGetterEffectEfficiency efficiencyGetter = new StatGetterEffectEfficiency(fortifyingEffect, 1.0);
        GuiStatBar statBar = new GuiStatBar(0, 0, 59, "tetra.stats.fortifying", 0.0, 1.0, false, false, false, statGetter, LabelGetterBasic.noLabel, new TooltipGetterPercentage("tetra.stats.fortifying.tooltip", efficiencyGetter));
        WorkbenchStatsGui.addBar(statBar);
        HoloStatsGui.addBar(statBar);
    }
}
