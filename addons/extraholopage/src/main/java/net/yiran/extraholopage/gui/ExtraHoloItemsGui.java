package net.yiran.extraholopage.gui;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.yiran.extraholopage.api.ExtraHoloBuilder;
import net.yiran.extraholopage.api.ExtraHoloRegister;
import net.yiran.extraholopage.core.IHoloItemGui;
import se.mickelus.mutil.gui.GuiAttachment;
import se.mickelus.mutil.gui.GuiElement;
import se.mickelus.mutil.gui.GuiTexture;
import se.mickelus.mutil.gui.animation.Applier;
import se.mickelus.mutil.gui.animation.KeyframeAnimation;
import se.mickelus.tetra.items.modular.IModularItem;
import se.mickelus.tetra.items.modular.impl.holo.gui.craft.HolosphereEntryData;
import se.mickelus.tetra.items.modular.impl.holo.gui.craft.item.HoloItemGui;
import se.mickelus.tetra.items.modular.impl.holo.gui.craft.item.HoloSeparatorsGui;
import se.mickelus.tetra.items.modular.impl.holo.gui.craft.material.HoloMaterialsButtonGui;
import se.mickelus.tetra.module.data.GlyphData;

public class ExtraHoloItemsGui
extends GuiElement {
    private final HoloSeparatorsGui separators;
    private final HoloMaterialsButtonGui materialsButton;
    private final KeyframeAnimation openAnimation;
    private final KeyframeAnimation backAnimation;

    public ExtraHoloItemsGui(int x, int y, int width, int height, BiConsumer<IModularItem, ItemStack> onItemSelect, Consumer<String> onSlotSelect, Runnable onMaterialsClick) {
        super(x, y, width, height);
        this.separators = new HoloSeparatorsGui(1, -71, width, height);
        this.addChild(this.separators);
        int index = 0;
        for (ExtraHoloBuilder value : ExtraHoloRegister.MAP.values()) {
            String key = keyOf(value.item);
            HolosphereEntryData data = new HolosphereEntryData();
            data.key = key;
            data.item = value.item;
            data.icon = new GlyphData(value.texture, value.textureX, value.textureY);
            HoloItemGui holoItemGui = (HoloItemGui)new HoloItemGui(ExtraHoloBuilder.ListX[index] * 40 + 1, ExtraHoloBuilder.ListY[index] * 40, data, () -> onItemSelect.accept((IModularItem)value.item, value.itemSupplier), onSlotSelect).setAttachment(GuiAttachment.topCenter);
            IHoloItemGui iHoloItemGui = IHoloItemGui.cast(holoItemGui);
            iHoloItemGui.setIcon(new GuiTexture(0, 0, value.width, value.height, value.textureX, value.textureY, value.texture));
            this.addChild(holoItemGui);
            ++index;
        }
        this.materialsButton = new HoloMaterialsButtonGui(0, 60, onMaterialsClick);
        this.materialsButton.setAttachment(GuiAttachment.topCenter);
        this.addChild(this.materialsButton);
        this.openAnimation = new KeyframeAnimation(200, this).applyTo(new Applier[]{new Applier.TranslateY((float)(y - 4), (float)y), new Applier.Opacity(0.0f, 1.0f)}).withDelay(800);
        this.backAnimation = new KeyframeAnimation(100, this).applyTo(new Applier[]{new Applier.TranslateY((float)(y - 4), (float)y), new Applier.Opacity(0.0f, 1.0f)});
    }

    public static String keyOf(Item item) {
        return BuiltInRegistries.ITEM.getKey(item).toString();
    }

    public void animateOpen() {
        this.openAnimation.start();
    }

    public void animateOpenAll() {
        this.openAnimation.start();
        this.separators.animateOpen();
    }

    public void animateBack() {
        this.backAnimation.start();
    }

    public void changeItem(IModularItem item) {
        String key = null;
        if (item != null) {
            key = ExtraHoloRegister.MAP.containsKey(item.getItem()) ? keyOf(item.getItem()) : "\u0000";
        }
        String selectedKey = key;
        this.getChildren(HoloItemGui.class).forEach(child -> child.onItemSelected(selectedKey));
        this.materialsButton.setVisible(item == null);
        if (item == null) {
            this.separators.animateReopen();
        } else {
            this.separators.setVisible(false);
        }
    }
}
