package net.acetheeldritchking.art_of_forging.effects;

import net.acetheeldritchking.art_of_forging.capabilities.subjugation.PlayerSubjugation;
import net.acetheeldritchking.art_of_forging.capabilities.subjugation.PlayerSubjugationProvider;
import net.acetheeldritchking.art_of_forging.effects.gui.EffectGuiStats;
import net.acetheeldritchking.art_of_forging.effects.potion.PotionEffects;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import se.mickelus.tetra.effect.AbilityUseResult;
import se.mickelus.tetra.effect.ChargedAbilityEffect;
import se.mickelus.tetra.items.modular.ItemModularHandheld;
import se.mickelus.tetra.items.modular.ModularItem;

public class SubjugationEffect
        extends ChargedAbilityEffect {
    public static final SubjugationEffect instance = new SubjugationEffect();

    public SubjugationEffect() {
        super(20, 0.15, 100, 1.0, EffectGuiStats.subjugationEffect, ChargedAbilityEffect.TargetRequirement.entity, UseAnim.SPEAR, "raised");
    }

    @SubscribeEvent
    public void onLivingAttackEvent(LivingDamageEvent.Pre event) {
        Entity attackingEntity = event.getSource().getEntity();
        LivingEntity target = event.getEntity();
        if (attackingEntity instanceof LivingEntity attacker) {
            ItemStack heldStack = attacker.getMainHandItem();
            Item item = heldStack.getItem();
            if (item instanceof ModularItem item2) {
                int level = item2.getEffectLevel(heldStack, EffectGuiStats.subjugationEffect);
                int eff = (int) item2.getEffectEfficiency(heldStack, EffectGuiStats.subjugationEffect);
                if (level > 0 && !attacker.level().isClientSide && attacker instanceof Player player) {
                    PlayerSubjugation subjugation = player.getData(PlayerSubjugationProvider.PLAYER_SUBJUGATION);
                    subjugation.addSubjugation(1);
                    if (subjugation.getSubjugation() == 10) {
                        target.addEffect(new MobEffectInstance(PotionEffects.TARGETED, eff * 20, 0, false, false, true));
                    }
                }
            }
        }
    }

    public void perform(Player attacker, InteractionHand hand, ItemModularHandheld item, ItemStack itemStack, LivingEntity target, Vec3 hitVec, int chargedTicks) {
        if (!target.level().isClientSide) {
            PlayerSubjugation subjugation = attacker.getData(PlayerSubjugationProvider.PLAYER_SUBJUGATION);
            double attackerX = attacker.getX();
            double attackerY = attacker.getY(0.5);
            double attackerZ = attacker.getZ();
            int level = item.getEffectLevel(itemStack, EffectGuiStats.subjugationEffect);
            int eff = (int) item.getEffectEfficiency(itemStack, EffectGuiStats.subjugationEffect);
            if (subjugation.getSubjugation() == 10 && target.hasEffect(PotionEffects.TARGETED)) {
                subjugation.resetSubjugation();
                this.performAttack(attacker, item, itemStack, target, eff, level);
                this.playSounds(attackerX, attackerY, attackerZ, (ServerPlayer) attacker);
            }
        }
        attacker.swing(hand, false);
        attacker.getCooldowns().addCooldown(item, this.getCooldown(item, itemStack));
        item.tickProgression(attacker, itemStack, 2);
        item.applyDamage(2, itemStack, attacker);
    }

    private AbilityUseResult performAttack(Player attacker, ItemModularHandheld item, ItemStack itemStack, LivingEntity target, int duration, float damage) {
        AbilityUseResult result = item.hitEntity(itemStack, attacker, target, 0.0, 0.0f, 0.0f);
        if (result != AbilityUseResult.fail) {
            target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, duration * 20, 1, true, true, true));
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, duration * 20, 1, true, true, true));
            target.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, duration * 20, 1, true, true, true));
            target.hurt(target.damageSources().magic(), damage);
            attacker.heal(5.0f);
        }
        return result;
    }

    @SubscribeEvent
    public void onPlayerTickEvent(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        ItemStack heldStack = player.getMainHandItem();
        Item item = heldStack.getItem();
        if (item instanceof ModularItem item2) {
            int level = item2.getEffectLevel(heldStack, EffectGuiStats.subjugationEffect);
            double posX = player.getX();
            double posY = player.getY(0.5);
            double posZ = player.getZ();
            if (level >= 2) {
                PlayerSubjugation subjugation = player.getData(PlayerSubjugationProvider.PLAYER_SUBJUGATION);
                if (subjugation.getSubjugation() >= 10) {
                    ServerLevel world = (ServerLevel) player.level();
                    world.sendParticles(ParticleTypes.CRIMSON_SPORE, posX, posY, posZ, 1, 0.1, 0.1, 0.1, 0.0);
                }
            }
        }
    }

    private void playSounds(double x, double y, double z, ServerPlayer player) {
        player.level().playSound(null, x, y, z, SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.PLAYERS, 1.0f, 0.5f);
    }
}
