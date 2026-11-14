package net.yiran.extraholopage.core.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Stream;
import net.yiran.extraholopage.Config;
import net.yiran.extraholopage.core.IHoloMaterialListGui;
import net.yiran.extraholopage.util.GuiFilter;
import net.yiran.extraholopage.util.GuiSorter;
import net.yiran.extraholopage.util.MyHoloMaterialGroupGui;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import se.mickelus.mutil.gui.GuiElement;
import se.mickelus.mutil.gui.impl.GuiHorizontalLayoutGroup;
import se.mickelus.tetra.items.modular.impl.holo.gui.craft.material.HoloMaterialDetailGui;
import se.mickelus.tetra.items.modular.impl.holo.gui.craft.material.HoloMaterialListGui;
import se.mickelus.tetra.module.data.MaterialData;

@Mixin(value={HoloMaterialListGui.class}, remap=false)
public abstract class HoloMaterialListGuiMixin
extends GuiElement
implements IHoloMaterialListGui {
    @Unique
    public GuiSorter guiSorter;
    @Unique
    public GuiFilter guiFilter1;
    @Unique
    public GuiFilter guiFilter2;
    @Unique
    public GuiFilter guiFilter3;
    @Shadow
    @Final
    private HoloMaterialDetailGui detail;
    @Shadow
    @Final
    private GuiHorizontalLayoutGroup groups;

    public HoloMaterialListGuiMixin(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    @Shadow
    private void onHover(MaterialData material) {
        throw new AssertionError();
    }

    @Shadow
    private void onBlur(MaterialData material) {
        throw new AssertionError();
    }

    @Shadow
    private void onSelect(MaterialData material) {
        throw new AssertionError();
    }

    @Shadow
    protected abstract void updateGroups();

    @Inject(method={"onMouseClick"}, at={@At(value="RETURN")})
    private void ppp(int x, int y, int button, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) {
            return;
        }
        this.resetAllSF();
    }

    @Inject(method={"<init>"}, at={@At(value="RETURN")})
    private void jjj(int x, int y, int width, int height, CallbackInfo ci) {
        this.guiFilter1 = new GuiFilter(0, y - 36, width, height, this::updateGroups, null, this);
        this.addChild(this.guiFilter1);
        this.guiFilter2 = new GuiFilter(0, y - 36, width, height, this::updateGroups, this.guiFilter1, this);
        this.addChild(this.guiFilter2);
        this.guiFilter3 = new GuiFilter(0, y - 36, width, height, this::updateGroups, this.guiFilter2, this);
        this.addChild(this.guiFilter3);
        this.guiSorter = new GuiSorter(x + 60, y - 36, width, height, this::updateGroups, this.guiFilter3, this);
        this.addChild(this.guiSorter);
    }

    @Override
    public GuiFilter[] getHoloFilters() {
        return new GuiFilter[]{this.guiFilter1, this.guiFilter2, this.guiFilter3};
    }

    @Override
    public GuiSorter getGuiSorter() {
        return this.guiSorter;
    }

    @Inject(method={"updateGroups"}, at={@At(value="HEAD")})
    private void zzz(CallbackInfo ci) {
        this.detail.setY(Config.HOLO_MATERIAL_LINE.get() < 4 ? 75 : Config.HOLO_MATERIAL_LINE.get() * 20);
    }

    @WrapOperation(method={"updateGroups"}, at={@At(value="INVOKE", target="Ljava/util/stream/Stream;collect(Ljava/util/stream/Collector;)Ljava/lang/Object;")})
    private Object ehp$filterAndSort(Stream<MaterialData> instance, Collector<MaterialData, ?, ?> collector, Operation<Object> original) {
        Stream<MaterialData> stream = instance;
        if (this.guiFilter1 != null) {
            stream = stream.filter(material -> this.guiFilter1.focusFiler.shouldShow(material));
        }
        if (this.guiFilter2 != null) {
            stream = stream.filter(material -> this.guiFilter2.focusFiler.shouldShow(material));
        }
        if (this.guiFilter3 != null) {
            stream = stream.filter(material -> this.guiFilter3.focusFiler.shouldShow(material));
        }
        if (this.guiSorter != null) {
            stream = stream.sorted((a, b) -> -Float.compare(this.guiSorter.focusSorter.getPriority(a), this.guiSorter.focusSorter.getPriority(b)));
        }
        return original.call(stream, collector);
    }

    @WrapOperation(method={"updateGroups"}, at={@At(value="INVOKE", target="Lse/mickelus/mutil/gui/impl/GuiHorizontalLayoutGroup;addChild(Lse/mickelus/mutil/gui/GuiElement;)V")})
    private void iii(GuiHorizontalLayoutGroup instance, GuiElement child, Operation<Void> original, @Local Map.Entry<String, List<MaterialData>> entry, @Local int offset) {
        instance.addChild(new MyHoloMaterialGroupGui(0, 0, this.guiSorter, entry.getKey(), entry.getValue(), offset, this::onHover, this::onBlur, this::onSelect));
    }

    @Inject(method={"onSelect"}, at={@At(value="INVOKE", target="Lse/mickelus/mutil/gui/impl/GuiHorizontalLayoutGroup;getChildren(Ljava/lang/Class;)Ljava/util/List;")})
    private void ttt(MaterialData material, CallbackInfo ci) {
        this.groups.getChildren(MyHoloMaterialGroupGui.class).forEach(group -> group.updateSelection(material));
    }

    @Inject(method={"onShow"}, at={@At(value="INVOKE", target="Lse/mickelus/mutil/gui/impl/GuiHorizontalLayoutGroup;getChildren(Ljava/lang/Class;)Ljava/util/List;")})
    private void ooo(CallbackInfo ci) {
        this.groups.getChildren(MyHoloMaterialGroupGui.class).forEach(MyHoloMaterialGroupGui::animateIn);
    }

    @Inject(method={"reload"}, at={@At(value="HEAD")})
    private void hhh(CallbackInfo ci) {
        this.guiSorter.resetSorter();
    }
}
