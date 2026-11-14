package se.mickelus.tetracelium.compat.twilightforest.effects;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import se.mickelus.tetra.blocks.workbench.gui.WorkbenchStatsGui;
import se.mickelus.tetra.effect.EffectHelper;
import se.mickelus.tetra.effect.ItemEffect;
import se.mickelus.tetra.event.ModularLooseProjectilesEvent;
import se.mickelus.tetra.gui.stats.StatsHelper;
import se.mickelus.tetra.gui.stats.bar.GuiStatBar;
import se.mickelus.tetra.gui.stats.getter.IStatFormat;
import se.mickelus.tetra.gui.stats.getter.IStatGetter;
import se.mickelus.tetra.gui.stats.getter.LabelGetterBasic;
import se.mickelus.tetra.gui.stats.getter.StatFormat;
import se.mickelus.tetra.gui.stats.getter.StatGetterEffectEfficiency;
import se.mickelus.tetra.gui.stats.getter.StatGetterEffectLevel;
import se.mickelus.tetra.gui.stats.getter.TooltipGetterMultiValue;
import se.mickelus.tetra.items.modular.impl.bow.ModularBowItem;
import se.mickelus.tetra.items.modular.impl.holo.gui.craft.HoloStatsGui;

public class VerticalMultishotEffect {
    public static final ItemEffect verticalMultishotEffect = ItemEffect.get("verticalMultishot");

    @SubscribeEvent
    public static void onModularLooseProjectiles(ModularLooseProjectilesEvent event) {
        int verticalCount = EffectHelper.getEffectLevel(event.getFiringStack(), verticalMultishotEffect);
        if (verticalCount > 0 && event.getProjectileVelocity() > 0.1f && !event.getLevel().isClientSide()) {
            ItemStack itemStack = event.getFiringStack();
            Registry<Enchantment> enchantments = event.getLevel().registryAccess().registryOrThrow(Registries.ENCHANTMENT);
            int piercingLevel = EffectHelper.getEffectLevel(itemStack, ItemEffect.piercing) + EnchantmentHelper.getTagEnchantmentLevel(enchantments.getHolderOrThrow(Enchantments.PIERCING), itemStack);
            float spread = EffectHelper.getEffectEfficiency(event.getFiringStack(), verticalMultishotEffect);
            for (int i = 0; i < verticalCount; ++i) {
                int offset = (i / 2 + 1) * (i % 2 == 0 ? 1 : -1);
                double pitch = event.getBasePitch() + (double) (spread * (float) offset);
                ModularBowItem.fireProjectile(itemStack, event.getLevel(), (ArrowItem) event.getAmmoStack().getItem(), event.getAmmoStack(), event.getProjectileRemappers(), event.getShooter(), (float) pitch, (float) event.getBaseYaw(), event.getProjectileVelocity(), event.getAccuracy(), event.getDrawProgress(), event.getStrength(), piercingLevel, event.isHasSuspend(), true);
            }
        }
    }

    @OnlyIn(value = Dist.CLIENT)
    public static void clientInit() {
        StatGetterEffectLevel countGetter = new StatGetterEffectLevel(verticalMultishotEffect, 1.0);
        StatGetterEffectEfficiency spreadGetter = new StatGetterEffectEfficiency(verticalMultishotEffect, 1.0);
        GuiStatBar statBar = new GuiStatBar(0, 0, 59, "tetra.stats.verticalMultishot", 0.0, 12.0, true, false, false, countGetter, LabelGetterBasic.integerLabel, new TooltipGetterMultiValue("tetra.stats.verticalMultishot.tooltip", StatsHelper.withStats(new IStatGetter[]{countGetter, spreadGetter}), StatsHelper.withFormat(new IStatFormat[]{StatFormat.noDecimal, StatFormat.oneDecimal})));
        WorkbenchStatsGui.addBar(statBar);
        HoloStatsGui.addBar(statBar);
    }
}
