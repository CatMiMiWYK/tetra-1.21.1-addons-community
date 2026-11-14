package net.yiran.tetra_addition;

import dev.latvian.mods.kubejs.typings.Info;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.yiran.tetra_addition.util.IConfigSchematic;
import net.yiran.tetra_addition.util.ISchematicRegistry;
import org.jetbrains.annotations.NotNull;
import se.mickelus.tetra.module.SchematicRegistry;
import se.mickelus.tetra.module.schematic.ConfigSchematic;
import se.mickelus.tetra.module.schematic.OutcomeDefinition;
import se.mickelus.tetra.module.schematic.SchematicDefinition;
import se.mickelus.tetra.module.schematic.UpgradeSchematic;
import se.mickelus.tetra.util.ItemStackTagHelper;

public class MyTetraUtil {
    public static MyTetraUtil instance = new MyTetraUtil();
    @Nullable
    private Map<String, ConfigSchematic> moduleKey2Schematic;
    @Nullable
    private Set<ConfigSchematic> schematicsSet;

    public Map<ResourceLocation, UpgradeSchematic> getSchematicMap() {
        return ((ISchematicRegistry) SchematicRegistry.instance).getSchematicMap();
    }

    public String getModuleKey(@NotNull ItemStack itemStack, String slot) {
        return ItemStackTagHelper.getOrCreateTag(itemStack).getString(slot);
    }

    public String getModuleMaterial(@NotNull ItemStack itemStack, String slot) {
        return ItemStackTagHelper.getOrCreateTag(itemStack).getString(this.getModuleKey(itemStack, slot) + "_material").split("/")[1];
    }

    public int getMaterialCount(@NotNull ItemStack itemStack, String slot) {
        ConfigSchematic schematic = this.getModuleKey2Schematic().get(this.getModuleKey(itemStack, slot));
        if (schematic == null) {
            return 1;
        }
        return ((IConfigSchematic) schematic).getDefinition().outcomes[0].material.count;
    }

    @Info(value = "精准确定outcomeItemStack的材料数量")
    public ItemStack getModuleMaterial(@NotNull ItemStack itemStack, String slot, ItemStack outcomeItemStack) {
        ConfigSchematic schematic = this.getModuleKey2Schematic().get(this.getModuleKey(itemStack, slot));
        if (schematic == null) {
            return ItemStack.EMPTY;
        }
        for (OutcomeDefinition outcome : ((IConfigSchematic) schematic).getDefinition().outcomes) {
            for (ItemStack stack : outcome.material.getApplicableItemStacks()) {
                if (stack.getItem() != outcomeItemStack.getItem()) {
                    continue;
                }
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    public Map<String, ConfigSchematic> getModuleKey2Schematic() {
        if (this.moduleKey2Schematic == null) {
            return this.getAndUpdateModuleKey2Schematic();
        }
        return this.moduleKey2Schematic;
    }

    @Info(value = "当你使用了reload指令会导致Schematic重新构造,于是便需要使用这个方法")
    public Map<String, ConfigSchematic> getAndUpdateModuleKey2Schematic() {
        HashMap<String, ConfigSchematic> moduleKey2Schematic = new HashMap<>();
        HashSet<ConfigSchematic> schematicsSet = new HashSet<>();
        this.getSchematicMap().forEach((k, v) -> {
            if (v instanceof ConfigSchematic configSchematic) {
                if (!v.isHoning()) {
                    SchematicDefinition schematicDefinition = ((IConfigSchematic) configSchematic).getDefinition();
                    String moduleKey = schematicDefinition.outcomes[0].moduleKey;
                    if (moduleKey != null) {
                        if (schematicDefinition.keySuffixes.length > 0) {
                            for (String keySuffix : schematicDefinition.keySuffixes) {
                                moduleKey2Schematic.put(moduleKey + keySuffix, configSchematic);
                            }
                        }
                        moduleKey2Schematic.put(moduleKey, configSchematic);
                        schematicsSet.add(configSchematic);
                    }
                }
            }
        });
        this.schematicsSet = schematicsSet;
        this.moduleKey2Schematic = moduleKey2Schematic;
        return moduleKey2Schematic;
    }

    public boolean isSame(ItemStack itemStack, String slot, ConfigSchematic schematic) {
        return this.getModuleKey2Schematic().get(this.getModuleKey(itemStack, slot)) == schematic;
    }

    public boolean isReplace(ConfigSchematic schematic) {
        if (this.schematicsSet == null) {
            this.getAndUpdateModuleKey2Schematic();
        }
        return this.schematicsSet.contains(schematic);
    }
}
