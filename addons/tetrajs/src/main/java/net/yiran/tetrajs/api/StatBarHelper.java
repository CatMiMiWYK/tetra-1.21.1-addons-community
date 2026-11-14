package net.yiran.tetrajs.api;

import dev.latvian.mods.kubejs.typings.Info;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.yiran.tetrajs.util.StatGetterWrapper;
import net.yiran.tetrajs.util.TooltipGetterMultiValueWrapper;
import se.mickelus.tetra.effect.ItemEffect;
import se.mickelus.tetra.gui.stats.getter.IStatFormat;
import se.mickelus.tetra.gui.stats.getter.IStatGetter;
import se.mickelus.tetra.gui.stats.getter.ITooltipGetter;
import se.mickelus.tetra.gui.stats.getter.StatFormat;
import se.mickelus.tetra.gui.stats.getter.StatGetterAdd;
import se.mickelus.tetra.gui.stats.getter.StatGetterAttribute;
import se.mickelus.tetra.gui.stats.getter.StatGetterAttributeAddition;
import se.mickelus.tetra.gui.stats.getter.StatGetterAttributeMultiply;
import se.mickelus.tetra.gui.stats.getter.StatGetterEffectEfficiency;
import se.mickelus.tetra.gui.stats.getter.StatGetterEffectLevel;
import se.mickelus.tetra.gui.stats.getter.StatGetterIntegrity;
import se.mickelus.tetra.gui.stats.getter.StatGetterMagicCapacity;
import se.mickelus.tetra.gui.stats.getter.StatGetterMultiply;

public class StatBarHelper {
    public static StatBarHelper INSTANCE = new StatBarHelper();

    public IStatGetter effectLevel(ItemEffect effect, double multiplier, double base) {
        return new StatGetterEffectLevel(effect, multiplier, base);
    }

    public IStatGetter effectLevel(ItemEffect effect, double multiplier) {
        return this.effectLevel(effect, multiplier, 0.0);
    }

    public IStatGetter effectLevel(ItemEffect effect) {
        return this.effectLevel(effect, 1.0);
    }

    public IStatGetter effectEfficiency(ItemEffect effect, double multiplier, double base) {
        return new StatGetterEffectEfficiency(effect, multiplier, base);
    }

    public IStatGetter effectEfficiency(ItemEffect effect, double multiplier) {
        return this.effectEfficiency(effect, multiplier, 0.0);
    }

    public IStatGetter effectEfficiency(ItemEffect effect) {
        return this.effectEfficiency(effect, 1.0);
    }

    public IStatGetter sum(double amount, IStatGetter... statGetter) {
        return new StatGetterAdd(amount, statGetter);
    }

    public IStatGetter sum(IStatGetter... statGetter) {
        return this.sum(0.0, statGetter);
    }

    public IStatGetter multiply(double multiplier, IStatGetter... statGetter) {
        return new StatGetterMultiply(multiplier, statGetter);
    }

    public IStatGetter multiply(IStatGetter... statGetter) {
        return this.multiply(1.0, statGetter);
    }

    public IStatGetter attribute(Attribute attribute, boolean ignoreBase, boolean ignoreBonuses, double offset) {
        return new StatGetterAttribute(attribute, ignoreBase, ignoreBonuses, offset);
    }

    public IStatGetter attribute(Attribute attribute, boolean ignoreBase, boolean ignoreBonuses) {
        return this.attribute(attribute, ignoreBase, ignoreBonuses, 0.0);
    }

    public IStatGetter attribute(Attribute attribute, boolean ignoreBase) {
        return this.attribute(attribute, ignoreBase, false);
    }

    public IStatGetter attribute(Attribute attribute) {
        return this.attribute(attribute, false);
    }

    public IStatGetter attributeAddition(Attribute attribute) {
        return new StatGetterAttributeAddition(attribute);
    }

    public IStatGetter attributeMultiply(Attribute attribute) {
        return this.sum(-1.0, new StatGetterAttributeMultiply(attribute));
    }

    public IStatGetter integrity() {
        return new StatGetterIntegrity();
    }

    public IStatGetter magicCapacity(IStatGetter... statGetter) {
        return new StatGetterMagicCapacity();
    }

    public IStatGetter showWrapper(IStatGetter statGetter, StatGetterWrapper.ShowCheck showCheck) {
        return new StatGetterWrapper(statGetter, showCheck);
    }

    @Info("获取一个TooltipGetter的Builder，用法与注册StatBar一样")
    public TooltipGetterBuilder newTooltipGetter(String langKey) {
        return new TooltipGetterBuilder(langKey);
    }

    public static class TooltipGetterBuilder {
        public String langKey;
        public List<IStatGetter> statGetters = new ArrayList<>();
        public List<IStatGetter> extendStatGetters = new ArrayList<>();
        public List<IStatFormat> statFormats = new ArrayList<>();

        public TooltipGetterBuilder(String langKey) {
            this.langKey = langKey;
        }

        public TooltipGetterBuilder addTooltipStatGetter(IStatGetter statGetter, IStatFormat statFormat) {
            this.statGetters.add(statGetter);
            this.statFormats.add(statFormat);
            return this;
        }

        public TooltipGetterBuilder addIntegerTooltip(IStatGetter statGetter) {
            return this.addTooltipStatGetter(statGetter, StatFormat.noDecimal);
        }

        public TooltipGetterBuilder addOneDecimalTooltip(IStatGetter statGetter) {
            return this.addTooltipStatGetter(statGetter, StatFormat.oneDecimal);
        }

        public TooltipGetterBuilder addTowDecimalTooltip(IStatGetter statGetter) {
            return this.addTooltipStatGetter(statGetter, StatFormat.twoDecimal);
        }

        public TooltipGetterBuilder addAbbreviateTooltip(IStatGetter statGetter) {
            return this.addTooltipStatGetter(statGetter, StatFormat.abbreviate);
        }

        public TooltipGetterBuilder addExtendGetter(IStatGetter statGetter) {
            this.extendStatGetters.add(statGetter);
            return this;
        }

        public ITooltipGetter build() {
            return new TooltipGetterMultiValueWrapper(this.langKey, this.statGetters.toArray(new IStatGetter[0]), this.statFormats.toArray(new IStatFormat[0]), this.extendStatGetters.toArray(new IStatGetter[0]));
        }
    }
}
