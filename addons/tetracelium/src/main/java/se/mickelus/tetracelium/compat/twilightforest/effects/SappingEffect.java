package se.mickelus.tetracelium.compat.twilightforest.effects;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import se.mickelus.mutil.util.CastOptional;
import se.mickelus.tetra.blocks.workbench.gui.WorkbenchStatsGui;
import se.mickelus.tetra.effect.ApplyHitTargetEffectsEvent;
import se.mickelus.tetra.effect.ApplyUsageEffectsEvent;
import se.mickelus.tetra.effect.EffectHelper;
import se.mickelus.tetra.effect.ItemEffect;
import se.mickelus.tetra.gui.stats.StatsHelper;
import se.mickelus.tetra.gui.stats.bar.GuiStatBar;
import se.mickelus.tetra.gui.stats.getter.IStatFormat;
import se.mickelus.tetra.gui.stats.getter.IStatGetter;
import se.mickelus.tetra.gui.stats.getter.LabelGetterBasic;
import se.mickelus.tetra.gui.stats.getter.StatFormat;
import se.mickelus.tetra.gui.stats.getter.StatGetterEffectEfficiency;
import se.mickelus.tetra.gui.stats.getter.StatGetterEffectLevel;
import se.mickelus.tetra.gui.stats.getter.TooltipGetterMultiValue;
import se.mickelus.tetra.items.modular.IModularItem;
import se.mickelus.tetra.items.modular.ThrownModularItemEntity;
import se.mickelus.tetra.items.modular.impl.holo.gui.craft.HoloStatsGui;
import se.mickelus.tetracelium.TetraceliumMod;
import twilightforest.data.tags.EntityTagGenerator;
import twilightforest.item.LifedrainScepterItem;

public class SappingEffect {
    public static final ItemEffect sappingEffect = ItemEffect.get("sapping");

    @SubscribeEvent
    public static void onApplyHitTargetEffects(ApplyHitTargetEffectsEvent event) {
        ItemStack itemStack = event.getUsedItemStack();
        int effectLevel = EffectHelper.getEffectLevel(itemStack, sappingEffect);
        LivingEntity attacker = event.getAttacker();
        if (effectLevel > 0) {
            attacker.heal((float) effectLevel);
            if (attacker instanceof Player player) {
                player.getFoodData().eat(effectLevel, 0.1f);
            }
            Level level = event.getTarget().level();
            InteractionHand hand = getHand(itemStack, attacker);
            Vec3 targetPosition = event.getTarget().position();
            if (level.isClientSide) {
                makeRedMagicTrail(level, hand, attacker, targetPosition);
            } else {
                TetraceliumMod.packetHandler.sendToAllPlayersNear(new SapParticlePacket(hand, targetPosition), (ServerLevel) level, attacker.blockPosition(), 64.0);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        ItemStack itemStack;
        if ("trident".equals(event.getSource().getMsgId()) && event.getSource().getDirectEntity() instanceof ThrownModularItemEntity modularEntity) {
            itemStack = modularEntity.getThrownStack();
        } else {
            Entity sourceEntity = event.getSource().getEntity();
            itemStack = Optional.ofNullable(sourceEntity)
                    .filter(entity -> entity instanceof Player)
                    .map(entity -> ((LivingEntity) entity).getMainHandItem())
                    .filter(stack -> stack.getItem() instanceof IModularItem)
                    .orElse(null);
        }
        if (itemStack != null) {
            Level level = event.getEntity().level();
            if (!level.isClientSide
                    && !event.getEntity().getType().is(EntityTagGenerator.LIFEDRAIN_DROPS_NO_FLESH)
                    && EffectHelper.getEffectLevel(itemStack, sappingEffect) > 0) {
                LifedrainScepterItem.animateTargetShatter((ServerLevel) level, event.getEntity());
            }
        }
    }

    @SubscribeEvent
    public static void onApplyUsageEffects(ApplyUsageEffectsEvent event) {
        ItemStack itemStack = event.getItemStack();
        double effectEfficiency = EffectHelper.getEffectEfficiency(itemStack, sappingEffect);
        if (effectEfficiency > 0.0) {
            ServerPlayer player = CastOptional.cast(event.getUsingEntity(), ServerPlayer.class).orElse(null);
            itemStack.hurtAndBreak((int) Math.ceil((double) itemStack.getMaxDamage() * 0.01 * effectEfficiency), (ServerLevel) event.getUsingEntity().level(), player, item -> {
            });
        }
    }

    private static String getIdentifier(ItemStack itemStack) {
        return CastOptional.cast(itemStack.getItem(), IModularItem.class).map(item -> item.getIdentifier(itemStack)).orElse(null);
    }

    @Nullable
    private static InteractionHand getHand(ItemStack itemStack, LivingEntity entity) {
        String usedIdentifier = getIdentifier(itemStack);
        if (usedIdentifier.equals(getIdentifier(entity.getItemInHand(InteractionHand.MAIN_HAND)))) {
            return InteractionHand.MAIN_HAND;
        }
        if (usedIdentifier.equals(getIdentifier(entity.getItemInHand(InteractionHand.OFF_HAND)))) {
            return InteractionHand.OFF_HAND;
        }
        return null;
    }

    public static void makeRedMagicTrail(Level level, InteractionHand hand, LivingEntity source, Vec3 target) {
        double handOffset = 0.0;
        if (hand == InteractionHand.MAIN_HAND) {
            handOffset = 0.35;
        }
        if (hand == InteractionHand.OFF_HAND) {
            handOffset = -0.35;
        }
        int particles = 32;
        for (int i = 0; i < particles; ++i) {
            double trailFactor = (double) i / ((double) particles - 1.0);
            float f = 1.0f;
            float f1 = 0.5f;
            float f2 = 0.5f;
            double tx = source.getX() + (target.x - source.getX()) * trailFactor + level.random.nextGaussian() * 0.005 + handOffset * (double) Direction.fromYRot(source.yHeadRot).getStepX();
            double ty = source.getY(0.5) - 0.1 + (target.y - source.getY(0.5)) * trailFactor + level.random.nextGaussian() * 0.005 - 0.1;
            double tz = source.getZ() + (target.z - source.getZ()) * trailFactor + level.random.nextGaussian() * 0.005 + handOffset * (double) Direction.fromYRot(source.yHeadRot).getStepX();
            level.addParticle(ParticleTypes.CRIT, tx, ty, tz, (double) f, (double) f1, (double) f2);
        }
    }

    @OnlyIn(value = Dist.CLIENT)
    public static void clientInit() {
        StatGetterEffectLevel statGetter = new StatGetterEffectLevel(sappingEffect, 1.0);
        StatGetterEffectLevel saturationGetter = new StatGetterEffectLevel(sappingEffect, (double) 0.2f);
        StatGetterEffectEfficiency durabilityGetter = new StatGetterEffectEfficiency(sappingEffect, 1.0);
        GuiStatBar statBar = new GuiStatBar(0, 0, 59, "tetra.stats.sapping", 0.0, 4.0, false, false, false, statGetter, LabelGetterBasic.integerLabel, new TooltipGetterMultiValue("tetra.stats.sapping.tooltip", StatsHelper.withStats(new IStatGetter[]{statGetter, saturationGetter, durabilityGetter}), StatsHelper.withFormat(new IStatFormat[]{StatFormat.noDecimal, StatFormat.oneDecimal, StatFormat.noDecimal})));
        WorkbenchStatsGui.addBar(statBar);
        HoloStatsGui.addBar(statBar);
    }
}
