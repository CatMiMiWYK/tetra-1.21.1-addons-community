package net.acetheeldritchking.art_of_forging.item.modular;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import se.mickelus.tetra.gui.GuiModuleOffsets;
import se.mickelus.tetra.items.modular.ModularItem;
import se.mickelus.tetra.module.ItemModule;
import se.mickelus.tetra.module.ItemUpgradeRegistry;
import se.mickelus.tetra.util.ItemStackTagHelper;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ModularArtifact
        extends ModularItem
        implements ICurioItem {
    public static final String artifactCasing = "artifact/casing";
    public static final String artifactInternal = "artifact/internal";
    public static final String artifactAttachment = "artifact/attachment";
    public static final String identifier = "modular_artifact";
    private static final GuiModuleOffsets majorOffsets = new GuiModuleOffsets(-13, -1, 3, 19);
    private static final GuiModuleOffsets minorOffsets = new GuiModuleOffsets(6, 1);
    public static ModularArtifact instance;

    public ModularArtifact() {
        super(new Item.Properties().stacksTo(1).fireResistant());
        instance = this;
        this.canHone = false;
        this.majorModuleKeys = new String[]{artifactCasing, artifactInternal};
        this.minorModuleKeys = new String[]{artifactAttachment};
        this.requiredModules = new String[]{artifactCasing, artifactInternal};
    }

    public Collection<ItemModule> getAllModules(ItemStack stack) {
        CompoundTag stackTag = ItemStackTagHelper.getTag(stack);
        if (stackTag != null) {
            return Stream.concat(Arrays.stream(this.getMajorModuleKeys(stack)), Arrays.stream(this.getMinorModuleKeys(stack))).map(stackTag::getString).map(ItemUpgradeRegistry.instance::getModule).filter(Objects::nonNull).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @OnlyIn(value = Dist.CLIENT)
    public GuiModuleOffsets getMajorGuiOffsets(ItemStack itemStack) {
        return majorOffsets;
    }

    @OnlyIn(value = Dist.CLIENT)
    public GuiModuleOffsets getMinorGuiOffsets(ItemStack itemStack) {
        return minorOffsets;
    }

    public ItemStack getStack() {
        return null;
    }
}
