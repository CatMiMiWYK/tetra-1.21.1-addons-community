package se.mickelus.tetracelium.compat.twilightforest;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.NeoForge;
import se.mickelus.tetra.blocks.workbench.gui.WorkbenchStatsGui;
import se.mickelus.tetra.effect.ItemEffect;
import se.mickelus.tetra.gui.stats.bar.GuiStatBar;
import se.mickelus.tetra.gui.stats.bar.GuiStatBase;
import se.mickelus.tetra.gui.stats.getter.IStatGetter;
import se.mickelus.tetra.gui.stats.getter.ITooltipGetter;
import se.mickelus.tetra.gui.stats.getter.LabelGetterBasic;
import se.mickelus.tetra.gui.stats.getter.StatGetterEffectLevel;
import se.mickelus.tetra.gui.stats.getter.TooltipGetterInteger;
import se.mickelus.tetra.items.modular.impl.holo.gui.craft.HoloStatsGui;
import se.mickelus.tetracelium.compat.twilightforest.effects.ChargingEffect;
import se.mickelus.tetracelium.compat.twilightforest.effects.EnderProjectilesEffect;
import se.mickelus.tetracelium.compat.twilightforest.effects.FortifyingEffect;
import se.mickelus.tetracelium.compat.twilightforest.effects.HydralBondEffect;
import se.mickelus.tetracelium.compat.twilightforest.effects.IceProjectilesEffect;
import se.mickelus.tetracelium.compat.twilightforest.effects.IgnitingEffect;
import se.mickelus.tetracelium.compat.twilightforest.effects.PunishingEffect;
import se.mickelus.tetracelium.compat.twilightforest.effects.ReanimatingEffect;
import se.mickelus.tetracelium.compat.twilightforest.effects.SappingEffect;
import se.mickelus.tetracelium.compat.twilightforest.effects.SeekerProjectilesEffect;
import se.mickelus.tetracelium.compat.twilightforest.effects.TwilightBoltEffect;
import se.mickelus.tetracelium.compat.twilightforest.effects.VerticalMultishotEffect;

public class TwilightForestCompat {
    public static final String modId = "twilightforest";
    public static final Boolean isLoaded = ModList.get().isLoaded("twilightforest");

    public static void init() {
        NeoForge.EVENT_BUS.register(IgnitingEffect.class);
        NeoForge.EVENT_BUS.register(HydralBondEffect.class);
        NeoForge.EVENT_BUS.register(PunishingEffect.class);
        NeoForge.EVENT_BUS.register(ChargingEffect.class);
        NeoForge.EVENT_BUS.register(ReanimatingEffect.class);
        NeoForge.EVENT_BUS.register(SappingEffect.class);
        NeoForge.EVENT_BUS.register(VerticalMultishotEffect.class);
        NeoForge.EVENT_BUS.register(SeekerProjectilesEffect.class);
        NeoForge.EVENT_BUS.register(IceProjectilesEffect.class);
        NeoForge.EVENT_BUS.register(EnderProjectilesEffect.class);
    }

    @OnlyIn(value = Dist.CLIENT)
    public static void clientInit() {
        NeoForge.EVENT_BUS.register(TwilightBoltEffect.class);
        IgnitingEffect.clientInit();
        HydralBondEffect.clientInit();
        PunishingEffect.clientInit();
        ChargingEffect.clientInit();
        FortifyingEffect.clientInit();
        ReanimatingEffect.clientInit();
        SappingEffect.clientInit();
        TwilightBoltEffect.clientInit();
        VerticalMultishotEffect.clientInit();
        SeekerProjectilesEffect.clientInit();
        IceProjectilesEffect.clientInit();
        EnderProjectilesEffect.clientInit();
        StatGetterEffectLevel statGetter = new StatGetterEffectLevel(ItemEffect.get("autoSmelt"), 1.0);
        GuiStatBar statBar = new GuiStatBar(0, 0, 59, "tetra.stats.autoSmelt", 0.0, 1.0, false, false, false, statGetter, LabelGetterBasic.noLabel, new TooltipGetterInteger("tetra.stats.autoSmelt.tooltip", statGetter));
        WorkbenchStatsGui.addBar(statBar);
        HoloStatsGui.addBar(statBar);
    }
}
