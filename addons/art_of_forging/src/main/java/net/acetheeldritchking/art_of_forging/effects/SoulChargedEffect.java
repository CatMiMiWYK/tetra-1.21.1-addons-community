package net.acetheeldritchking.art_of_forging.effects;

import it.crystalnest.soul_fire_d.fire.FireRegistry;
import net.acetheeldritchking.art_of_forging.capabilities.soulCharge.PlayerSoulCharge;
import net.acetheeldritchking.art_of_forging.capabilities.soulCharge.PlayerSoulChargeProvider;
import net.acetheeldritchking.art_of_forging.effects.gui.EffectGuiStats;
import net.acetheeldritchking.art_of_forging.networking.AoFPackets;
import net.acetheeldritchking.art_of_forging.networking.packet.SoulChargedParticlesS2CPacket;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import se.mickelus.tetra.effect.AbilityUseResult;
import se.mickelus.tetra.effect.ChargedAbilityEffect;
import se.mickelus.tetra.items.modular.ItemModularHandheld;
import se.mickelus.tetra.items.modular.ModularItem;

import java.util.List;

public class SoulChargedEffect
        extends ChargedAbilityEffect {
    public static final SoulChargedEffect instance = new SoulChargedEffect();

    public SoulChargedEffect() {
        super(20, 0.15, 50, 8.0, EffectGuiStats.soulChargedEffect, ChargedAbilityEffect.TargetRequirement.either, UseAnim.BOW, "raised");
    }

    @SubscribeEvent
    public void onLivingDeathEvent(LivingDeathEvent event) {
        Entity attackingEntity = event.getSource().getEntity();
        if (attackingEntity instanceof LivingEntity attacker) {
            ItemStack heldStack = attacker.getMainHandItem();
            Item item2 = heldStack.getItem();
            if (item2 instanceof ModularItem item) {
                int level = item.getEffectLevel(heldStack, EffectGuiStats.soulChargedEffect);
                if (level >= 2 && attacker instanceof Player player) {
                    PlayerSoulCharge soul_charge = player.getData(PlayerSoulChargeProvider.PLAYER_SOUL_CHARGE);
                    soul_charge.addSoulCharge(1);
                    if (soul_charge.getSoulCharge() >= 5) {
                        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BLAZE_HURT, SoundSource.PLAYERS, 1.0f, 0.8f);
                    }
                }
            }
        }
    }

    public void perform(Player attacker, InteractionHand hand, ItemModularHandheld item, ItemStack itemStack, LivingEntity target, Vec3 hitVec, int chargedTicks) {
        if (!target.level().isClientSide) {
            PlayerSoulCharge soul_charge = attacker.getData(PlayerSoulChargeProvider.PLAYER_SOUL_CHARGE);
            double radius = 5.0;
            double height = 2.0;
            double attackerX = attacker.getX();
            double attackerY = attacker.getY(0.5);
            double attackerZ = attacker.getZ();
            int level = item.getEffectLevel(itemStack, EffectGuiStats.soulChargedEffect);
            float eff = item.getEffectEfficiency(itemStack, EffectGuiStats.soulChargedEffect);
            if (soul_charge.getSoulCharge() >= 5) {
                soul_charge.resetSoulCharge();
                this.doAoeAttack(radius, height, radius, level * 2, eff, attacker, hand, item, itemStack, target, hitVec);
                this.playSoundsAndParticles(attackerX, attackerY, attackerZ, (ServerPlayer) attacker);
            }
        }
        attacker.swing(hand, false);
        attacker.getCooldowns().addCooldown(item, this.getCooldown(item, itemStack));
        item.tickProgression(attacker, itemStack, 2);
        item.applyDamage(2, itemStack, attacker);
    }

    private AbilityUseResult doAoeAttack(double x, double y, double z, int seconds, float damage, Player attacker, InteractionHand hand, ItemModularHandheld item, ItemStack itemStack, LivingEntity target, Vec3 hitVec) {
        AbilityUseResult result = item.hitEntity(itemStack, attacker, target, 1.0, 1.0, 1.0f, 1.0f);
        if (result != AbilityUseResult.fail) {
            AABB aoeAttack = new AABB(hitVec, hitVec).inflate(x * 2.0, y, z * 2.0);
            List<Entity> targets = attacker.level().getEntities(attacker, aoeAttack);
            for (Entity entity : targets) {
                if (entity instanceof LivingEntity livingTargets) {
                    setSoulFire(livingTargets, seconds);
                    livingTargets.hurt(livingTargets.damageSources().magic(), damage);
                }
            }
        }
        return result;
    }

    @SubscribeEvent
    public void onPlayerTickEvent(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        ItemStack heldStack = player.getMainHandItem();
        Item item = heldStack.getItem();
        if (item instanceof ModularItem item2) {
            int level = item2.getEffectLevel(heldStack, EffectGuiStats.soulChargedEffect);
            double posX = player.getX();
            double posY = player.getY(0.5);
            double posZ = player.getZ();
            if (level >= 2) {
                PlayerSoulCharge soul_charge = player.getData(PlayerSoulChargeProvider.PLAYER_SOUL_CHARGE);
                if (soul_charge.getSoulCharge() >= 5) {
                    ServerLevel world = (ServerLevel) player.level();
                    world.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, posX, posY, posZ, 1, 0.5, 0.5, 0.5, 0.0);
                }
            }
        }
    }

    @SubscribeEvent
    public void onLivingAttackEvent(LivingIncomingDamageEvent event) {
        Entity attackingEntity = event.getSource().getEntity();
        LivingEntity target = event.getEntity();
        if (attackingEntity instanceof LivingEntity attacker) {
            ItemStack heldStack = attacker.getMainHandItem();
            Item item = heldStack.getItem();
            if (item instanceof ModularItem item2) {
                float level = item2.getEffectLevel(heldStack, EffectGuiStats.soulChargedEffect);
                int seconds = (int) (level * 2.0f);
                if (level >= 2.0f && attacker instanceof Player player) {
                    PlayerSoulCharge soul_charge = player.getData(PlayerSoulChargeProvider.PLAYER_SOUL_CHARGE);
                    if (soul_charge.getSoulCharge() >= 5) {
                        setSoulFire(target, seconds);
                    }
                }
            }
        }
    }

    /**
     * Applies the soul fire type through Prometheus (the fire API used by soul_fire_d 6.x).
     * Prometheus is a required dependency of soul_fire_d but is not present on the compile classpath,
     * so the type change is performed reflectively. Fire ticks are always applied.
     */
    private static void setSoulFire(Entity entity, int seconds) {
        entity.setRemainingFireTicks(seconds * 20);
        try {
            Class<?> changer = Class.forName("it.crystalnest.prometheus.api.type.FireTypeChanger");
            changer.getMethod("setFireType", ResourceLocation.class).invoke(entity, FireRegistry.SOUL_FIRE_TYPE);
        } catch (ReflectiveOperationException ignored) {
        }
    }

    private void playSoundsAndParticles(double x, double y, double z, ServerPlayer player) {
        AoFPackets.sendToPlayer(new SoulChargedParticlesS2CPacket(x, y, z), player);
        player.level().playSound(null, x, y, z, SoundEvents.BLAZE_SHOOT, SoundSource.PLAYERS, 1.0f, 1.0f);
    }
}
