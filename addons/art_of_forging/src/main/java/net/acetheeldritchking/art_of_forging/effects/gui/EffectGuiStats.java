package net.acetheeldritchking.art_of_forging.effects.gui;

import net.acetheeldritchking.art_of_forging.effects.SonicShockEffect;
import net.acetheeldritchking.art_of_forging.effects.SoulChargedEffect;
import net.acetheeldritchking.art_of_forging.effects.SubjugationEffect;
import net.acetheeldritchking.art_of_forging.mixins.tetra.ItemModularHandheldAccessor;
import net.acetheeldritchking.art_of_forging.util.AoFTags;
import net.minecraft.world.entity.EntityType;
import se.mickelus.tetra.effect.ChargedAbilityEffect;
import se.mickelus.tetra.effect.ItemEffect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class EffectGuiStats {
    public static final ItemEffect cavalryEffect = ItemEffect.get("art_of_forging:cavalry");
    public static final String cavalryEffectName = "art_of_forging.effect.cavalry.name";
    public static final String cavalryEffectTooltip = "art_of_forging.effect.cavalry.tooltip";
    public static final ItemEffect evokingMaw = ItemEffect.get("art_of_forging:evoking_maw");
    public static final String evokingMawName = "art_of_forging.effect.evokingmaw.name";
    public static final String evokingMawTooltip = "art_of_forging.effect.evokingmaw.tooltip";
    public static final ItemEffect dismountingEffect = ItemEffect.get("art_of_forging:dismounting");
    public static final String dismountingEffectName = "art_of_forging.effect.dismounting.name";
    public static final String dismountingEffectTooltip = "art_of_forging.effect.dismounting.tooltip";
    public static final ItemEffect knockbackEffect = ItemEffect.get("art_of_forging:knockback");
    public static final String knockbackEffectName = "art_of_forging.effect.knockback.name";
    public static String knockbackEffectTooltip = "art_of_forging.effect.knockback.tooltip";
    public static final ItemEffect lifeFiberLoss = ItemEffect.get("art_of_forging:life_fiber_loss");
    public static final String lifeFiberLossName = "art_of_forging.effect.life_fiber_loss.name";
    public static final String lifeFiberLossTooltip = "art_of_forging.effect.life_fiber_loss.tooltip";
    public static final ItemEffect lifeSteal = ItemEffect.get("art_of_forging:life_steal");
    public static final String lifeStealName = "art_of_forging.effect.life_steal.name";
    public static final String lifeStealTooltip = "art_of_forging.effect.life_steal.tooltip";
    public static final ItemEffect stormCaller = ItemEffect.get("art_of_forging:stormcaller");
    public static final String stormCallerName = "art_of_forging.effect.stormcaller.name";
    public static final String stormCallerTooltip = "art_of_forging.effect.stormcaller.tooltip";
    public static final ItemEffect decaying = ItemEffect.get("art_of_forging:decaying");
    public static final String decayingName = "art_of_forging.effect.decaying.name";
    public static final String decayingTooltip = "art_of_forging.effect.decaying.tooltip";
    public static final ItemEffect dragonMist = ItemEffect.get("art_of_forging:dragon_mist");
    public static final String dragonMistName = "art_of_forging.effect.dragon_mist.name";
    public static final String dragonMistTooltip = "art_of_forging.effect.dragon_mist.tooltip";
    public static final ItemEffect disorientingEffect = ItemEffect.get("art_of_forging:disorienting");
    public static final String disorientingName = "art_of_forging.effect.disorienting.name";
    public static final String disorientingTooltip = "art_of_forging.effect.disorienting.tooltip";
    public static final ItemEffect vengeanceEffect = ItemEffect.get("art_of_forging:vengeance");
    public static final String vengeanceName = "art_of_forging.effect.vengeance.name";
    public static final String vengeanceTooltip = "art_of_forging.effect.vengeance.tooltip";
    public static final ItemEffect resolveEffect = ItemEffect.get("art_of_forging:resolve");
    public static final String resolveName = "art_of_forging.effect.resolve.name";
    public static final String resolveTooltip = "art_of_forging.effect.resolve.tooltip";
    public static final ItemEffect devouringEffect = ItemEffect.get("art_of_forging:devouring");
    public static final String devouringEffectName = "art_of_forging.effect.devouring.name";
    public static final String devouringEffectTooltip = "art_of_forging.effect.devouring.tooltip";
    public static final ItemEffect infernalRebukeEffect = ItemEffect.get("art_of_forging:infernal_rebuke");
    public static final String infernalRebukeName = "art_of_forging.effect.infernal_rebuke.name";
    public static final String infernalRebukeTooltip = "art_of_forging.effect.infernal_rebuke.tooltip";
    public static final ItemEffect hubrisEffect = ItemEffect.get("art_of_forging:hubris");
    public static final String hubrisName = "art_of_forging.effect.hubris.name";
    public static final String hubrisTooltip = "art_of_forging.effect.hubris.tooltip";
    public static final ItemEffect slaughteringEffect = ItemEffect.get("art_of_forging:slaughtering");
    public static final String slaughteringName = "art_of_forging.effect.slaughtering.name";
    public static final String slaughteringTooltip = "art_of_forging.effect.slaughtering.tooltip";
    public static final ItemEffect carnageEffect = ItemEffect.get("art_of_forging:carnage");
    public static final String carnageName = "art_of_forging.effect.carnage.name";
    public static final String carnageTooltip = "art_of_forging.effect.carnage.tooltip";
    public static final ItemEffect judgementEffect = ItemEffect.get("art_of_forging:judgement");
    public static final String judgementName = "art_of_forging.effect.judgement.name";
    public static final String judgementTooltip = "art_of_forging.effect.judgement.tooltip";
    public static final ItemEffect retributionEffect = ItemEffect.get("art_of_forging:retribution");
    public static final String retributionName = "art_of_forging.effect.retribution.name";
    public static final String retributionTooltip = "art_of_forging.effect.retribution.tooltip";
    public static final ItemEffect esotericEdgeEffect = ItemEffect.get("art_of_forging:esoteric_edge");
    public static final String esotericEdgeName = "art_of_forging.effect.esoteric_edge.name";
    public static final String esotericEdgeTooltip = "art_of_forging.effect.esoteric_edge.tooltip";
    public static final ItemEffect feastingEffect = ItemEffect.get("art_of_forging:feasting");
    public static final String feastingName = "art_of_forging.effect.feasting.name";
    public static final String feastingTooltip = "art_of_forging.effect.feasting.tooltip";
    public static final ItemEffect nanoFusedEffect = ItemEffect.get("art_of_forging:nano_fused");
    public static final String nanoFusedName = "art_of_forging.effect.nano_fused.name";
    public static final String nanoFusedTooltip = "art_of_forging.effect.nano_fused.tooltip";
    public static final ItemEffect beheadingEffect = ItemEffect.get("art_of_forging:beheading");
    public static final String beheadingName = "art_of_forging.effect.beheading.name";
    public static final String beheadingTooltip = "art_of_forging.effect.beheading.tooltip";
    public static final ItemEffect soulChargedEffect = ItemEffect.get("art_of_forging:soul_charged");
    public static final String soulChargedName = "art_of_forging.effect.soul_charged.name";
    public static final String soulChargedTooltip = "art_of_forging.effect.soul_charged.tooltip";
    public static final ItemEffect sonicShockEffect = ItemEffect.get("art_of_forging:sonic_shock");
    public static final String sonicShockName = "art_of_forging.effect.sonic_shock.name";
    public static final String sonicShockTooltip = "art_of_forging.effect.sonic_shock.tooltip";
    public static final ItemEffect conqueringEffect = ItemEffect.get("art_of_forging:conquering");
    public static final String conqueringName = "art_of_forging.effect.conquering.name";
    public static final String conqueringTooltip = "art_of_forging.effect.conquering.tooltip";
    public static final ItemEffect subjugationEffect = ItemEffect.get("art_of_forging:subjugation");
    public static final String subjugationName = "art_of_forging.effect.subjugation.name";
    public static final String subjugationTooltip = "art_of_forging.effect.subjugation.tooltip";
    public static final ItemEffect goliathSlayerEffect = ItemEffect.get("art_of_forging:goliath_slayer");
    public static final String goliathSlayerName = "art_of_forging.effect.goliath_slayer.name";
    public static final String goliathSlayerTooltip = "art_of_forging.effect.goliath_slayer.tooltip";
    public static final ItemEffect extraBeltSlotEffect = ItemEffect.get("art_of_forging:extra_belt_slot");
    public static final String extraBeltSlotName = "art_of_forging.effect.extra_belt_slot.name";
    public static final String extraBeltSlotTooltip = "art_of_forging.effect.extra_belt_slot.tooltip";
    public static final ItemEffect extraCharmSlotEffect = ItemEffect.get("art_of_forging:extra_charm_slot");
    public static final String extraCharmSlotName = "art_of_forging.effect.extra_charm_slot.name";
    public static final String extraCharmSlotTooltip = "art_of_forging.effect.extra_charm_slot.tooltip";
    public static final ItemEffect extraCurioSlotEffect = ItemEffect.get("art_of_forging:extra_curio_slot");
    public static final String extraCurioSlotName = "art_of_forging.effect.extra_curio_slot.name";
    public static final String extraCurioSlotTooltip = "art_of_forging.effect.extra_curio_slot.tooltip";
    public static final ItemEffect flameProtectionEffect = ItemEffect.get("art_of_forging:flame_protection");
    public static final String flameProtectionName = "art_of_forging.effect.flame_protection.name";
    public static final String flameProtectionTooltip = "art_of_forging.effect.flame_protection.tooltip";
    public static final ItemEffect arcaneProtectionEffect = ItemEffect.get("art_of_forging:arcane_protection");
    public static final String arcaneProtectionName = "art_of_forging.effect.arcane_protection.name";
    public static final String arcaneProtectionTooltip = "art_of_forging.effect.arcane_protection.tooltip";
    public static final ItemEffect strengthInfusedEffect = ItemEffect.get("art_of_forging:strength_infused");
    public static final String strengthInfusedName = "art_of_forging.effect.strength_infused.name";
    public static final String strengthInfusedTooltip = "art_of_forging.effect.strength_infused.tooltip";
    public static final ItemEffect healingInfusedEffect = ItemEffect.get("art_of_forging:healing_infused");
    public static final String healingInfusedName = "art_of_forging.effect.healing_infused.name";
    public static final String healingInfusedTooltip = "art_of_forging.effect.healing_infused.tooltip";
    public static final ItemEffect karmaInfusedEffect = ItemEffect.get("art_of_forging:karma_infused");
    public static final String karmaInfusedName = "art_of_forging.effect.karma_infused.name";
    public static final String karmaInfusedTooltip = "art_of_forging.effect.karma_infused.tooltip";
    public static final ItemEffect hasteInfusedEffect = ItemEffect.get("art_of_forging:haste_infused");
    public static final String hasteInfusedName = "art_of_forging.effect.haste_infused.name";
    public static final String hasteInfusedTooltip = "art_of_forging.effect.haste_infused.tooltip";
    public static final ItemEffect thirdSightEffect = ItemEffect.get("art_of_forging:third_sight");
    public static final String thirdSightName = "art_of_forging.effect.third_sight.name";
    public static final String thirdSightTooltip = "art_of_forging.effect.third_sight.tooltip";

    public static boolean isBossEntity(EntityType<?> entity) {
        return entity.is(AoFTags.Entities.BOSS_ENTITIES);
    }

    public static float getDecimalPercentage(float percentage, float base) {
        float totalPercentage = base * (percentage / 100.0f);
        return totalPercentage;
    }

    public static float getExactPercentage(float base, float percentage) {
        float totalPercentage = base + percentage;
        return totalPercentage;
    }

    public static List<ChargedAbilityEffect> setupAbilites() {
        List<ChargedAbilityEffect> list = new ArrayList<>(Arrays.asList(ItemModularHandheldAccessor.getAbilities()));
        list.add(SoulChargedEffect.instance);
        list.add(SonicShockEffect.instance);
        list.add(SubjugationEffect.instance);
        list.removeIf(Objects::isNull);
        ItemModularHandheldAccessor.setAbilities(list.toArray(new ChargedAbilityEffect[0]));
        return list;
    }
}
