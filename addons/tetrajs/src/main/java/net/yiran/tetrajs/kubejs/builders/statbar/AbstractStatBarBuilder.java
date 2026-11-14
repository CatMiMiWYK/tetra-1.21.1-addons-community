package net.yiran.tetrajs.kubejs.builders.statbar;

import java.util.ArrayList;
import java.util.List;
import net.yiran.tetrajs.util.StatGetterWrapper;
import net.yiran.tetrajs.util.TooltipGetterMultiValueWrapper;
import se.mickelus.tetra.gui.stats.bar.GuiStatBar;
import se.mickelus.tetra.gui.stats.bar.GuiStatIndicator;
import se.mickelus.tetra.gui.stats.getter.ILabelGetter;
import se.mickelus.tetra.gui.stats.getter.IStatFormat;
import se.mickelus.tetra.gui.stats.getter.IStatGetter;
import se.mickelus.tetra.gui.stats.getter.ITooltipGetter;
import se.mickelus.tetra.gui.stats.getter.LabelGetterBasic;
import se.mickelus.tetra.gui.stats.getter.StatFormat;

public abstract class AbstractStatBarBuilder<THIS extends IStatBarBuilder<?>> implements IStatBarBuilder<THIS> {
    public int max = 100;
    public int min = 0;
    public boolean segmented = false;
    public boolean split = false;
    public boolean inverted = false;
    public ILabelGetter labelGetter = LabelGetterBasic.integerLabel;
    public IStatGetter statGetter;
    public List<IStatGetter> statGetters = new ArrayList<>();
    public List<IStatGetter> extendStatGetters = new ArrayList<>();
    public List<IStatFormat> statFormats = new ArrayList<>();
    public List<GuiStatIndicator> statIndicators = new ArrayList<>();
    public StatGetterWrapper.ShowCheck showCheck;

    @SuppressWarnings("unchecked")
    public THIS setShowCheck(StatGetterWrapper.ShowCheck showCheck) {
        this.showCheck = showCheck;
        return (THIS) this;
    }

    @SuppressWarnings("unchecked")
    public THIS addTooltipStatGetter(IStatGetter statGetter, IStatFormat statFormat) {
        this.statGetters.add(statGetter);
        this.statFormats.add(statFormat);
        return (THIS) this;
    }

    public THIS addIntegerTooltip(IStatGetter statGetter) {
        return this.addTooltipStatGetter(statGetter, StatFormat.noDecimal);
    }

    public THIS addOneDecimalTooltip(IStatGetter statGetter) {
        return this.addTooltipStatGetter(statGetter, StatFormat.oneDecimal);
    }

    public THIS addTowDecimalTooltip(IStatGetter statGetter) {
        return this.addTooltipStatGetter(statGetter, StatFormat.twoDecimal);
    }

    public THIS addAbbreviateTooltip(IStatGetter statGetter) {
        return this.addTooltipStatGetter(statGetter, StatFormat.abbreviate);
    }

    @SuppressWarnings("unchecked")
    public THIS addExtendGetter(IStatGetter statGetter) {
        this.extendStatGetters.add(statGetter);
        return (THIS) this;
    }

    @SuppressWarnings("unchecked")
    public THIS addStatIndicator(GuiStatIndicator statIndicator) {
        this.statIndicators.add(statIndicator);
        return (THIS) this;
    }

    @Override
    public IStatGetter getStatGetter() {
        return this.statGetter;
    }

    @Override
    @SuppressWarnings("unchecked")
    public THIS setStatGetter(IStatGetter statGetter) {
        this.statGetter = statGetter;
        return (THIS) this;
    }

    @Override
    public int getMax() {
        return this.max;
    }

    @Override
    @SuppressWarnings("unchecked")
    public THIS setMax(int max) {
        this.max = max;
        return (THIS) this;
    }

    @Override
    public int getMin() {
        return this.min;
    }

    @Override
    @SuppressWarnings("unchecked")
    public THIS setMin(int min) {
        this.min = min;
        return (THIS) this;
    }

    @Override
    public boolean getSegmented() {
        return this.segmented;
    }

    @Override
    @SuppressWarnings("unchecked")
    public THIS setSegmented(boolean segmented) {
        this.segmented = segmented;
        return (THIS) this;
    }

    @Override
    public boolean getSplit() {
        return this.split;
    }

    @Override
    @SuppressWarnings("unchecked")
    public THIS setSplit(boolean split) {
        this.split = split;
        return (THIS) this;
    }

    @Override
    public boolean getInverted() {
        return this.inverted;
    }

    @Override
    @SuppressWarnings("unchecked")
    public THIS setInverted(boolean inverted) {
        this.inverted = inverted;
        return (THIS) this;
    }

    @Override
    public ILabelGetter getLabelGetter() {
        return this.labelGetter;
    }

    @Override
    @SuppressWarnings("unchecked")
    public THIS setLabelGetter(ILabelGetter labelGetter) {
        this.labelGetter = labelGetter;
        return (THIS) this;
    }

    @SuppressWarnings("unchecked")
    public THIS setLabelGetter(LABELS label) {
        this.labelGetter = label.getLabelGetter();
        return (THIS) this;
    }

    public abstract void checkBuildPre();

    @Override
    public GuiStatBar build() {
        this.checkBuildPre();
        GuiStatBar result = new GuiStatBar(0, 0, 59, this.getKey(), this.min, this.max, this.segmented, this.split, this.inverted,
                this.showCheck == null ? this.statGetter : new StatGetterWrapper(this.statGetter, this.showCheck),
                this.labelGetter,
                new TooltipGetterMultiValueWrapper(this.getLangKey(), this.statGetters.toArray(new IStatGetter[0]), this.statFormats.toArray(new IStatFormat[0]), this.extendStatGetters.toArray(new IStatGetter[0])));
        result.setIndicators(this.statIndicators.toArray(new GuiStatIndicator[0]));
        return result;
    }

    public enum LABELS {
        integerLabel(LabelGetterBasic.integerLabel),
        integerLabelInverted(LabelGetterBasic.integerLabelInverted),
        decimalLabel(LabelGetterBasic.decimalLabel),
        singleDecimalLabel(LabelGetterBasic.singleDecimalLabel),
        decimalLabelInverted(LabelGetterBasic.decimalLabelInverted),
        percentageLabel(LabelGetterBasic.percentageLabel),
        percentageLabelInverted(LabelGetterBasic.percentageLabelInverted),
        percentageLabelDecimal(LabelGetterBasic.percentageLabelDecimal),
        percentageLabelDecimalInverted(LabelGetterBasic.percentageLabelDecimalInverted);

        final ILabelGetter labelGetter;

        LABELS(ILabelGetter labelGetter) {
            this.labelGetter = labelGetter;
        }

        public ILabelGetter getLabelGetter() {
            return this.labelGetter;
        }
    }
}
