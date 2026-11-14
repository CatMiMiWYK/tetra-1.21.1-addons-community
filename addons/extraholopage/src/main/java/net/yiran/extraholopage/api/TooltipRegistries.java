package net.yiran.extraholopage.api;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.network.chat.MutableComponent;
import net.yiran.extraholopage.gui.ComponentHelper;
import net.yiran.extraholopage.gui.MaterialTooltipHelper;
import se.mickelus.tetra.effect.ItemEffect;

public class TooltipRegistries {
    public static Map<ItemEffect, TooltipGetter> map = new HashMap<ItemEffect, TooltipGetter>();

    public static void register(ItemEffect effect, TooltipGetter getter) {
        map.put(effect, getter);
    }

    public static TooltipGetter getTooltip(ItemEffect effect) {
        return map.getOrDefault(effect, TooltipRegistries::defaultGetter);
    }

    public static MutableComponent defaultGetter(ItemEffect itemEffect, float level, float efficiency) {
        return MaterialTooltipHelper.getValue(ComponentHelper.translatable("tetra.stats." + itemEffect.getKey(), MaterialTooltipHelper.color1), ComponentHelper.literal("[ " + level + " , " + efficiency + " ]", MaterialTooltipHelper.color1));
    }

    @FunctionalInterface
    public static interface TooltipGetter {
        public MutableComponent get(ItemEffect var1, float var2, float var3);
    }
}
