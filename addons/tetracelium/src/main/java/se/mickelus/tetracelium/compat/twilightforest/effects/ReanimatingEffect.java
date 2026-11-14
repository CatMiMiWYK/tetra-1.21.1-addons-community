package se.mickelus.tetracelium.compat.twilightforest.effects;

import java.util.Optional;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import se.mickelus.mutil.util.CastOptional;
import se.mickelus.tetra.blocks.workbench.gui.WorkbenchStatsGui;
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
import twilightforest.entity.monster.LoyalZombie;
import twilightforest.init.TFEntities;
import twilightforest.init.TFSounds;

public class ReanimatingEffect {
    public static final ItemEffect reanimatingEffect = ItemEffect.get("reanimating");

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
            int effectLevel = EffectHelper.getEffectLevel(itemStack, reanimatingEffect);
            if (!level.isClientSide && !event.getEntity().isInvertedHealAndHarm() && effectLevel > 0) {
                ServerPlayer player = CastOptional.cast(killer, ServerPlayer.class).orElse(null);
                spawnZombie(level, killer, event.getEntity().position(), effectLevel);
                itemStack.hurtAndBreak((int) Math.ceil((double) itemStack.getMaxDamage() * 0.01 * (double) EffectHelper.getEffectEfficiency(itemStack, reanimatingEffect)), (ServerLevel) level, player, item -> {
                });
            }
        }
    }

    static void spawnZombie(Level level, Entity owner, Vec3 spawnPosition, int duration) {
        LoyalZombie zombie = TFEntities.LOYAL_ZOMBIE.get().create(level);
        zombie.moveTo(spawnPosition);
        if (level.noCollision(zombie, zombie.getBoundingBox())) {
            zombie.setPersistenceRequired();
            zombie.setTame(true, false);
            zombie.setOwnerUUID(owner.getUUID());
            zombie.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, duration * 20, 1));
            level.addFreshEntity(zombie);
            level.gameEvent(owner, GameEvent.ENTITY_PLACE, spawnPosition);
            zombie.playSound(TFSounds.MINION_SUMMON.get(), 1.0f, zombie.getVoicePitch());
        }
    }

    @OnlyIn(value = Dist.CLIENT)
    public static void clientInit() {
        StatGetterEffectLevel statGetter = new StatGetterEffectLevel(reanimatingEffect, 1.0);
        StatGetterEffectEfficiency efficiencyGetter = new StatGetterEffectEfficiency(reanimatingEffect, 1.0);
        GuiStatBar statBar = new GuiStatBar(0, 0, 59, "tetra.stats.reanimating", 0.0, 120.0, false, false, false, statGetter, LabelGetterBasic.integerLabel, new TooltipGetterMultiValue("tetra.stats.reanimating.tooltip", StatsHelper.withStats(new IStatGetter[]{statGetter, efficiencyGetter}), StatsHelper.withFormat(new IStatFormat[]{StatFormat.noDecimal, StatFormat.noDecimal})));
        WorkbenchStatsGui.addBar(statBar);
        HoloStatsGui.addBar(statBar);
    }
}
