package net.yiran.extraholopage.gui;

import java.awt.Color;
import net.minecraft.network.chat.MutableComponent;

public class MaterialTooltipHelper {
    public static int color0 = Color.WHITE.getRGB();
    public static int color1 = Color.LIGHT_GRAY.getRGB();
    public static int color2 = Color.GRAY.getRGB();
    public static int color3 = Color.DARK_GRAY.getRGB();
    public static int color_good = new Color(80, 169, 80).getRGB();
    public static int color_bad = new Color(197, 96, 96).getRGB();
    public static MutableComponent PREFIX = ComponentHelper.literal("\u00bb ", color3);
    public static MutableComponent MIDDLE = ComponentHelper.literal("  \u251c ", color2);
    public static MutableComponent END = ComponentHelper.literal("  \u2514 ", color2);

    public static MutableComponent getValue(MutableComponent component1, int color, MutableComponent component2) {
        return ComponentHelper.append(component1, ComponentHelper.literal(": ", color), component2);
    }

    public static MutableComponent getValue(MutableComponent component1, MutableComponent component2) {
        return MaterialTooltipHelper.getValue(component1, color2, component2);
    }

    public static MutableComponent getHeadTooltip(int index) {
        return index == 0 ? MaterialTooltipHelper.getEnd() : MaterialTooltipHelper.getMiddle();
    }

    public static MutableComponent addHeadTooltip(int index, MutableComponent ... components) {
        return ComponentHelper.append(MaterialTooltipHelper.getHeadTooltip(index), components);
    }

    public static MutableComponent addHeadTooltip(int index, MutableComponent component1, MutableComponent component2) {
        return ComponentHelper.append(MaterialTooltipHelper.getHeadTooltip(index), MaterialTooltipHelper.getValue(component1, component2));
    }

    public static MutableComponent getMiddle() {
        return MIDDLE.copy();
    }

    public static MutableComponent getEnd() {
        return END.copy();
    }

    public static MutableComponent getPrefix() {
        return PREFIX.copy();
    }

    public static MutableComponent addPrefix(MutableComponent component) {
        return ComponentHelper.append(MaterialTooltipHelper.getPrefix(), component);
    }

    public static MutableComponent addPrefix(MutableComponent component1, MutableComponent component2) {
        return ComponentHelper.append(MaterialTooltipHelper.getPrefix(), MaterialTooltipHelper.getValue(component1, component2));
    }
}
