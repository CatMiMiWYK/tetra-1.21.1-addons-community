package net.yiran.extraholopage.util;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.entity.player.Player;
import net.yiran.extraholopage.Config;
import se.mickelus.mutil.gui.GuiElement;
import se.mickelus.mutil.gui.GuiStringSmall;
import se.mickelus.mutil.gui.animation.Applier;
import se.mickelus.mutil.gui.animation.KeyframeAnimation;
import se.mickelus.tetra.gui.stats.sorting.IStatSorter;
import se.mickelus.tetra.items.modular.impl.holo.gui.craft.schematic.HoloVariantItemGui;
import se.mickelus.tetra.items.modular.impl.holo.gui.craft.schematic.HoloVariantMajorItemGui;
import se.mickelus.tetra.module.schematic.OutcomePreview;
import se.mickelus.tetra.module.schematic.SchematicType;

public class MyHoloVariantGroupGui
extends GuiElement {
    private final GuiElement variantsContainer;
    private final KeyframeAnimation labelAnimation;
    private final KeyframeAnimation[] itemAnimations;

    public MyHoloVariantGroupGui(int x, int y, String category, List<OutcomePreview> outcomes, int offset, IStatSorter sorter, Player player, Consumer<OutcomePreview> onVariantHover, Consumer<OutcomePreview> onVariantBlur, Consumer<OutcomePreview> onVariantSelect) {
        super(x, y, 0, 50);
        GuiStringSmall label = new GuiStringSmall(0, 0, I18n.get("tetra.variant_category." + category + ".label"));
        label.setColor(0x7F7F7F);
        this.addChild(label);
        this.variantsContainer = new GuiElement(0, 8, this.width, this.height);
        this.addChild(this.variantsContainer);
        this.labelAnimation = new KeyframeAnimation(100, label).applyTo(new Applier[]{new Applier.Opacity(0.0f, 1.0f), new Applier.TranslateX((float)(x - 5), (float)x)}).withDelay(40 * offset);
        int width = 0;
        this.itemAnimations = new KeyframeAnimation[outcomes.size()];
        int line = Config.HOLO_MODULE_LINE.get();
        for (int i = 0; i < outcomes.size(); ++i) {
            HoloVariantItemGui variant;
            OutcomePreview outcome = outcomes.get(i);
            if (SchematicType.minor.equals(outcome.type)) {
                variant = new HoloVariantItemGui(i / line * 15, i % line * 15, outcome, sorter.getValue(player, outcome.itemStack), onVariantHover, onVariantBlur, onVariantSelect);
                this.variantsContainer.addChild(variant);
                this.itemAnimations[i] = new KeyframeAnimation(80, variant).applyTo(new Applier[]{new Applier.Opacity(0.0f, 1.0f), new Applier.TranslateY(-5.0f, 0.0f, true)}).withDelay(40 + 40 * (offset + i) / line);
                width = variant.getX() + variant.getWidth();
                continue;
            }
            variant = new HoloVariantMajorItemGui(i / line * 20 + i % line % 2 * 10, i % line * 15, outcome, sorter.getValue(player, outcome.itemStack), onVariantHover, onVariantBlur, onVariantSelect);
            this.variantsContainer.addChild(variant);
            this.itemAnimations[i] = new KeyframeAnimation(80, variant).applyTo(new Applier[]{new Applier.Opacity(0.0f, 1.0f), new Applier.TranslateX(-5.0f, 0.0f, true), new Applier.TranslateY(-5.0f, 0.0f, true)}).withDelay(40 + 40 * (offset + i) / line);
            width = variant.getX() + variant.getWidth() + 20 - i % line % 2 * 10;
        }
        this.setWidth(Math.max(width, label.getWidth()));
    }

    public void updateSelection(OutcomePreview outcome) {
        this.variantsContainer.getChildren(HoloVariantItemGui.class).forEach(variant -> variant.updateSelection(outcome));
    }

    public void animateIn() {
        this.labelAnimation.start();
        Arrays.stream(this.itemAnimations).forEach(KeyframeAnimation::start);
    }
}
