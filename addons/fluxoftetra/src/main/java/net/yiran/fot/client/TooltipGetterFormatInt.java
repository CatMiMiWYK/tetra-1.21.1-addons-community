package net.yiran.fot.client;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import se.mickelus.tetra.gui.stats.getter.IStatGetter;
import se.mickelus.tetra.gui.stats.getter.ITooltipGetter;

public class TooltipGetterFormatInt
        implements ITooltipGetter {
    protected IStatGetter statGetter;
    protected String localizationKey;
    protected boolean absolute = false;
    protected Object[] extraArgs;

    public TooltipGetterFormatInt(String localizationKey, IStatGetter statGetter, boolean absolute, Object... extraArgs) {
        this(localizationKey, statGetter, extraArgs);
        this.absolute = absolute;
    }

    public TooltipGetterFormatInt(String localizationKey, IStatGetter statGetter, Object... extraArgs) {
        this.localizationKey = localizationKey;
        this.statGetter = statGetter;
        this.extraArgs = extraArgs;
    }

    @Override
    public String getTooltipBase(Player player, ItemStack itemStack) {
        return this.absolute
                ? I18n.get(this.localizationKey, ItemRenderHandler.formatInt((int) Math.abs(this.statGetter.getValue(player, itemStack))), this.extraArgs)
                : I18n.get(this.localizationKey, ItemRenderHandler.formatInt((int) this.statGetter.getValue(player, itemStack)), this.extraArgs);
    }

    @Override
    public boolean hasExtendedTooltip(Player player, ItemStack itemStack) {
        return I18n.exists(this.localizationKey + "_extended");
    }

    @Override
    public String getTooltipExtension(Player player, ItemStack itemStack) {
        return I18n.get(this.localizationKey + "_extended", new Object[0]);
    }
}
