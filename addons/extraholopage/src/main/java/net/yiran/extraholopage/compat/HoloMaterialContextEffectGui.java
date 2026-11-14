package net.yiran.extraholopage.compat;

import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import se.mickelus.mutil.gui.GuiAttachment;
import se.mickelus.mutil.gui.GuiElement;
import se.mickelus.mutil.gui.GuiString;
import se.mickelus.mutil.gui.GuiStringOutline;
import se.mickelus.mutil.gui.GuiTexture;
import se.mickelus.tetra.gui.GuiTextures;

public class HoloMaterialContextEffectGui
extends GuiElement {
    protected GuiTexture backdrop;
    protected GuiString label;
    protected GuiString value;
    List<Component> tooltip;

    public HoloMaterialContextEffectGui(int x, int y, String key, boolean current, boolean preview, boolean contextsChange, Collection<String> contexts) {
        super(x, y, 29, 29);
        String stringContext = null;
        for (String context : contexts) {
            stringContext = OverhaulCompat.mergeGroup(stringContext, context);
        }
        this.tooltip = ImmutableList.of(Component.translatable("tetra.holo.craft.materials.stat_effect.tooltip", I18n.get("tetra.stats." + key)), Component.translatable("tetra.stats." + key + ".tooltip_short").withStyle(ChatFormatting.GRAY), Component.literal(I18n.get("ehp.holo.material.context.effect.tooltip", stringContext)));
        this.backdrop = new GuiTexture(0, 0, 29, 29, 97, 0, GuiTextures.workbench);
        this.backdrop.setColor(0x222222);
        this.addChild(this.backdrop);
        this.value = new GuiStringOutline(0, 8, I18n.get("tetra.stats." + key));
        this.value.setAttachment(GuiAttachment.topCenter);
        this.addChild(this.value);
        if (current != preview) {
            this.value.setColor(preview ? 0xAAFFAA : 0xFFAAAA);
        }
        this.label = new GuiStringOutline(0, -3, I18n.get("tetra.holo.craft.materials.stat_effect"));
        this.label.setColor(contextsChange ? 0x7F7F7F : 26214);
        this.label.setAttachment(GuiAttachment.bottomCenter);
        this.addChild(this.label);
    }

    public List<Component> getTooltipLines() {
        return this.hasFocus() ? this.tooltip : null;
    }
}
