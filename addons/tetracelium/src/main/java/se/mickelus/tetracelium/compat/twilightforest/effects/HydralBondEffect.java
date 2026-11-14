package se.mickelus.tetracelium.compat.twilightforest.effects;

import java.util.stream.Stream;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import se.mickelus.tetra.blocks.workbench.gui.WorkbenchStatsGui;
import se.mickelus.tetra.effect.EffectHelper;
import se.mickelus.tetra.effect.ItemEffect;
import se.mickelus.tetra.gui.stats.bar.GuiStatBar;
import se.mickelus.tetra.gui.stats.getter.IStatGetter;
import se.mickelus.tetra.gui.stats.getter.IStatFormat;
import se.mickelus.tetra.gui.stats.getter.LabelGetterBasic;
import se.mickelus.tetra.gui.stats.getter.StatFormat;
import se.mickelus.tetra.gui.stats.getter.StatGetterEffectEfficiency;
import se.mickelus.tetra.gui.stats.getter.StatGetterEffectLevel;
import se.mickelus.tetra.gui.stats.getter.StatGetterMultiply;
import se.mickelus.tetra.gui.stats.getter.TooltipGetterMultiValue;
import se.mickelus.tetra.items.modular.IModularItem;
import se.mickelus.tetra.items.modular.impl.holo.gui.craft.HoloStatsGui;

public class HydralBondEffect {
    public static final ItemEffect hydralBondEffect = ItemEffect.get("hydralBond");

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent.Pre event) {
        if (event.getSource().is(DamageTypeTags.IS_FIRE)) {
            LivingEntity entity = event.getEntity();
            Stream.of(entity.getMainHandItem(), entity.getOffhandItem())
                    .filter(itemStack -> itemStack.getItem() instanceof IModularItem)
                    .filter(itemStack -> EffectHelper.getEffectLevel(itemStack, hydralBondEffect) > 0)
                    .forEach(itemStack -> {
                        int amount = EffectHelper.getEffectLevel(itemStack, hydralBondEffect);
                        float probability = EffectHelper.getEffectEfficiency(itemStack, hydralBondEffect);
                        if (probability >= 1.0f || entity.getRandom().nextFloat() < probability) {
                            itemStack.setDamageValue(itemStack.getDamageValue() - amount);
                        }
                    });
        }
    }

    @OnlyIn(value = Dist.CLIENT)
    public static void clientInit() {
        StatGetterEffectLevel amount = new StatGetterEffectLevel(hydralBondEffect, 1.0);
        StatGetterEffectEfficiency chance = new StatGetterEffectEfficiency(hydralBondEffect, 1.0);
        StatGetterMultiply barValue = new StatGetterMultiply(new IStatGetter[]{amount, chance});
        GuiStatBar statBar = new GuiStatBar(0, 0, 59, "tetra.stats.hydralBond", 0.0, 5.0, false, false, false, barValue, LabelGetterBasic.decimalLabel, new TooltipGetterMultiValue("tetra.stats.hydralBond.tooltip", new IStatGetter[]{chance, amount}, new IStatFormat[]{StatFormat.twoDecimal, StatFormat.noDecimal}));
        WorkbenchStatsGui.addBar(statBar);
        HoloStatsGui.addBar(statBar);
    }
}
