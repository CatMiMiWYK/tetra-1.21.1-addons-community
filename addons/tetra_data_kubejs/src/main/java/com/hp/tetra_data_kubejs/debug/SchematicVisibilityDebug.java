/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.inventory.AbstractContainerMenu
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.neoforged.neoforge.event.entity.player.PlayerContainerEvent$Open
 *  net.neoforged.bus.api.SubscribeEvent
 *  net.neoforged.fml.common.EventBusSubscriber
 *  net.neoforged.fml.common.EventBusSubscriber$Bus
 *  se.mickelus.tetra.ConfigHandler
 *  se.mickelus.tetra.FeatureFlag
 *  se.mickelus.tetra.aspect.ItemAspect
 *  se.mickelus.tetra.blocks.workbench.WorkbenchContainer
 *  se.mickelus.tetra.blocks.workbench.WorkbenchTile
 *  se.mickelus.tetra.items.modular.IModularItem
 *  se.mickelus.tetra.module.SchematicRegistry
 *  se.mickelus.tetra.module.schematic.ConfigSchematic
 *  se.mickelus.tetra.module.schematic.CraftingContext
 *  se.mickelus.tetra.module.schematic.PreviewVisibility
 *  se.mickelus.tetra.module.schematic.SchematicDefinition
 *  se.mickelus.tetra.module.schematic.UpgradeSchematic
 *  se.mickelus.tetra.module.schematic.requirement.AcceptsImprovementRequirement
 *  se.mickelus.tetra.module.schematic.requirement.AndRequirement
 *  se.mickelus.tetra.module.schematic.requirement.AspectRequirement
 *  se.mickelus.tetra.module.schematic.requirement.CraftingRequirement
 *  se.mickelus.tetra.module.schematic.requirement.FeatureFlagRequirement
 *  se.mickelus.tetra.module.schematic.requirement.HasImprovementRequirement
 *  se.mickelus.tetra.module.schematic.requirement.IntegerPredicate
 *  se.mickelus.tetra.module.schematic.requirement.LockedRequirement
 *  se.mickelus.tetra.module.schematic.requirement.ModuleRequirement
 *  se.mickelus.tetra.module.schematic.requirement.NeverRequirement
 *  se.mickelus.tetra.module.schematic.requirement.NotRequirement
 *  se.mickelus.tetra.module.schematic.requirement.OrRequirement
 *  se.mickelus.tetra.module.schematic.requirement.PerkRequrement
 *  se.mickelus.tetra.module.schematic.requirement.SlotRequirement
 */
package com.hp.tetra_data_kubejs.debug;

import com.hp.tetra_data_kubejs.TetraDataKubejs;
import java.lang.invoke.CallSite;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.player.PlayerContainerEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import se.mickelus.tetra.ConfigHandler;
import se.mickelus.tetra.FeatureFlag;
import se.mickelus.tetra.aspect.ItemAspect;
import se.mickelus.tetra.blocks.workbench.WorkbenchContainer;
import se.mickelus.tetra.blocks.workbench.WorkbenchTile;
import se.mickelus.tetra.items.modular.IModularItem;
import se.mickelus.tetra.module.SchematicRegistry;
import se.mickelus.tetra.module.schematic.ConfigSchematic;
import se.mickelus.tetra.module.schematic.CraftingContext;
import se.mickelus.tetra.module.schematic.PreviewVisibility;
import se.mickelus.tetra.module.schematic.SchematicDefinition;
import se.mickelus.tetra.module.schematic.UpgradeSchematic;
import se.mickelus.tetra.module.schematic.requirement.AcceptsImprovementRequirement;
import se.mickelus.tetra.module.schematic.requirement.AndRequirement;
import se.mickelus.tetra.module.schematic.requirement.AspectRequirement;
import se.mickelus.tetra.module.schematic.requirement.CraftingRequirement;
import se.mickelus.tetra.module.schematic.requirement.FeatureFlagRequirement;
import se.mickelus.tetra.module.schematic.requirement.HasImprovementRequirement;
import se.mickelus.tetra.module.schematic.requirement.IntegerPredicate;
import se.mickelus.tetra.module.schematic.requirement.LockedRequirement;
import se.mickelus.tetra.module.schematic.requirement.ModuleRequirement;
import se.mickelus.tetra.module.schematic.requirement.NeverRequirement;
import se.mickelus.tetra.module.schematic.requirement.NotRequirement;
import se.mickelus.tetra.module.schematic.requirement.OrRequirement;
import se.mickelus.tetra.module.schematic.requirement.PerkRequrement;
import se.mickelus.tetra.module.schematic.requirement.SlotRequirement;

@EventBusSubscriber(modid="tetra_data_kubejs", bus=EventBusSubscriber.Bus.GAME)
public final class SchematicVisibilityDebug {
    private static final String LOG_PREFIX = "[\u52a0\u5de5\u53f0\u56fe\u7eb8\u8c03\u8bd5]";
    private static volatile boolean enabled;

    private SchematicVisibilityDebug() {
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static void setEnabled(boolean value) {
        enabled = value;
    }

    public static boolean toggle() {
        enabled = !enabled;
        return enabled;
    }

    @SubscribeEvent
    public static void onContainerOpen(PlayerContainerEvent.Open event) {
        if (!enabled) {
            return;
        }
        AbstractContainerMenu abstractContainerMenu = event.getContainer();
        if (!(abstractContainerMenu instanceof WorkbenchContainer)) {
            return;
        }
        WorkbenchContainer container = (WorkbenchContainer)abstractContainerMenu;
        Player player = event.getEntity();
        if (player == null) {
            return;
        }
        WorkbenchTile workbench = container.getTileEntity();
        if (workbench == null || workbench.getLevel() == null) {
            return;
        }
        try {
            SchematicVisibilityDebug.logWorkbenchState(player, workbench);
        }
        catch (Throwable throwable) {
            TetraDataKubejs.LOGGER.error("{} \u8f93\u51fa schematic \u8c03\u8bd5\u65e5\u5fd7\u65f6\u53d1\u751f\u5f02\u5e38", (Object)LOG_PREFIX, (Object)throwable);
        }
    }

    private static void logWorkbenchState(Player player, WorkbenchTile workbench) {
        ItemStack targetStack = workbench.getTargetItemStack();
        TetraDataKubejs.LOGGER.info("{} \u73a9\u5bb6={} \u4f4d\u7f6e={} \u7269\u54c1={}", new Object[]{LOG_PREFIX, player.getGameProfile(), workbench.getBlockPos(), SchematicVisibilityDebug.describeStack(targetStack)});
        if (targetStack.isEmpty()) {
            TetraDataKubejs.LOGGER.info("{} \u672a\u653e\u5165\u76ee\u6807\u7269\u54c1\uff0c\u65e0\u6cd5\u5206\u6790 schematic \u663e\u793a\u539f\u56e0", (Object)LOG_PREFIX);
            return;
        }
        Item item = targetStack.getItem();
        if (!(item instanceof IModularItem)) {
            TetraDataKubejs.LOGGER.info("{} \u76ee\u6807\u7269\u54c1\u4e0d\u662f Tetra \u6a21\u5757\u5316\u7269\u54c1\uff0c\u65e0\u6cd5\u5206\u6790 schematic \u663e\u793a\u539f\u56e0", (Object)LOG_PREFIX);
            return;
        }
        IModularItem modularItem = (IModularItem)item;
        Object[] slots = (String[])Stream.concat(Arrays.stream(modularItem.getMajorModuleKeys(targetStack)), Arrays.stream(modularItem.getMinorModuleKeys(targetStack))).filter(Objects::nonNull).filter(value -> !value.isBlank()).distinct().toArray(String[]::new);
        Object[] unlocks = workbench.getUnlockedSchematics();
        TetraDataKubejs.LOGGER.info("{} \u7269\u54c1\u69fd\u4f4d={} \u5df2\u89e3\u9501={}", new Object[]{LOG_PREFIX, Arrays.toString(slots), Arrays.toString(unlocks)});
        if (slots.length == 0) {
            TetraDataKubejs.LOGGER.info("{} \u5f53\u524d\u7269\u54c1\u6ca1\u6709\u53ef\u5206\u6790\u7684\u6a21\u5757\u69fd\u4f4d", (Object)LOG_PREFIX);
            return;
        }
        Collection<UpgradeSchematic> schematics = SchematicRegistry.getAllSchematics();
        int visibleCount = 0;
        int hiddenCount = 0;
        for (UpgradeSchematic schematic : schematics) {
            VisibilityResult result = SchematicVisibilityDebug.diagnoseSchematic(player, workbench, targetStack, (String[])slots, (ResourceLocation[])unlocks, schematic);
            if (result.visible) {
                ++visibleCount;
                continue;
            }
            ++hiddenCount;
            TetraDataKubejs.LOGGER.info("{} \u9690\u85cf schematic={} \u6765\u6e90={} \u539f\u56e0={}", new Object[]{LOG_PREFIX, schematic.getKey(), Arrays.toString(schematic.getSources()), String.join((CharSequence)" | ", result.reasons)});
        }
        TetraDataKubejs.LOGGER.info("{} \u5206\u6790\u5b8c\u6210\uff0c\u53ef\u89c1={} \u4e0d\u53ef\u89c1={}", new Object[]{LOG_PREFIX, visibleCount, hiddenCount});
    }

    private static VisibilityResult diagnoseSchematic(Player player, WorkbenchTile workbench, ItemStack targetStack, String[] slots, ResourceLocation[] unlocks, UpgradeSchematic schematic) {
        if (!schematic.isRelevant(targetStack)) {
            return VisibilityResult.hidden("\u4e0e\u5f53\u524d\u76ee\u6807\u7269\u54c1\u65e0\u5173");
        }
        List<String> applicableSlots = Arrays.stream(slots).filter(slot -> schematic.isApplicableForSlot(slot, targetStack)).toList();
        if (applicableSlots.isEmpty()) {
            return VisibilityResult.hidden("\u4e0d\u9002\u7528\u4e8e\u5f53\u524d\u7269\u54c1\u7684\u4efb\u4f55\u69fd\u4f4d");
        }
        ArrayList<String> reasons = new ArrayList<String>();
        for (String slot2 : applicableSlots) {
            CraftingContext context = new CraftingContext(workbench.getLevel(), workbench.getBlockPos(), workbench.getBlockState(), player, targetStack, slot2, unlocks);
            if (schematic.matchesRequirements(context)) {
                return VisibilityResult.visible();
            }
            reasons.add("\u69fd\u4f4d " + slot2 + ": " + String.join((CharSequence)"\u3001", SchematicVisibilityDebug.diagnoseRequirementFailure(schematic, context)));
        }
        return VisibilityResult.hidden(reasons);
    }

    private static List<String> diagnoseRequirementFailure(UpgradeSchematic schematic, CraftingContext context) {
        ConfigSchematic configSchematic;
        SchematicDefinition definition;
        ArrayList<String> reasons = new ArrayList<String>();
        if (!(!schematic.isHoning() || ((Boolean)ConfigHandler.moduleProgression.get()).booleanValue() && IModularItem.isHoneable((ItemStack)context.targetStack))) {
            reasons.add("\u8fd9\u662f hone \u56fe\u7eb8\uff0c\u4f46\u5f53\u524d\u7269\u54c1\u4e0d\u80fd\u8fdb\u884c hone \u6216\u6a21\u7ec4\u8fdb\u5ea6\u914d\u7f6e\u672a\u5f00\u542f");
        }
        if (schematic instanceof ConfigSchematic && (definition = SchematicVisibilityDebug.getFieldValue(ConfigSchematic.class, configSchematic = (ConfigSchematic)schematic, "definition", SchematicDefinition.class)) != null) {
            if (definition.materialRevealSlot > -1 && !SchematicVisibilityDebug.hasRevealMaterial(configSchematic, context, definition.materialRevealSlot)) {
                reasons.add("materialRevealSlot=" + definition.materialRevealSlot + "\uff0c\u73a9\u5bb6\u80cc\u5305\u91cc\u6ca1\u6709\u80fd\u63ed\u793a\u8be5\u56fe\u7eb8\u7684\u6750\u6599");
            }
            if (definition.requirement != null && !definition.requirement.test(context)) {
                reasons.addAll(SchematicVisibilityDebug.diagnoseRequirement(definition.requirement, context));
            }
            if (definition.preview == PreviewVisibility.never) {
                reasons.add("preview=never\uff0c\u5728 holosphere \u4e2d\u4e5f\u4e0d\u4f1a\u663e\u793a");
            }
        }
        if (reasons.isEmpty()) {
            reasons.add("matchesRequirements=false\uff0c\u4f46\u672a\u89e3\u6790\u51fa\u66f4\u5177\u4f53\u539f\u56e0");
        }
        return reasons;
    }

    private static boolean hasRevealMaterial(ConfigSchematic schematic, CraftingContext context, int revealSlot) {
        if (context.player == null) {
            return false;
        }
        for (int index = 0; index < context.player.getInventory().getContainerSize(); ++index) {
            ItemStack stack = context.player.getInventory().getItem(index);
            if (!schematic.acceptsMaterial(context.targetStack, context.slot, revealSlot, stack)) continue;
            return true;
        }
        return false;
    }

    private static List<String> diagnoseRequirement(CraftingRequirement requirement, CraftingContext context) {
        if (requirement instanceof AndRequirement) {
            AndRequirement andRequirement = (AndRequirement)requirement;
            CraftingRequirement[] children = SchematicVisibilityDebug.getFieldValue(AndRequirement.class, andRequirement, "requirements", CraftingRequirement[].class);
            if (children == null || children.length == 0) {
                return List.of("and requirement \u4e3a\u7a7a");
            }
            return Arrays.stream(children).filter(child -> !child.test(context)).flatMap(child -> SchematicVisibilityDebug.diagnoseRequirement(child, context).stream()).distinct().toList();
        }
        if (requirement instanceof OrRequirement) {
            OrRequirement orRequirement = (OrRequirement)requirement;
            CraftingRequirement[] children = SchematicVisibilityDebug.getFieldValue(OrRequirement.class, orRequirement, "requirements", CraftingRequirement[].class);
            if (children == null || children.length == 0) {
                return List.of("or requirement \u4e3a\u7a7a");
            }
            List childReasons = Arrays.stream(children).flatMap(child -> SchematicVisibilityDebug.diagnoseRequirement(child, context).stream()).distinct().toList();
            return List.of("or \u6761\u4ef6\u5168\u90e8\u4e0d\u6ee1\u8db3: " + String.join((CharSequence)"\uff1b", childReasons));
        }
        if (requirement instanceof NotRequirement) {
            NotRequirement notRequirement = (NotRequirement)requirement;
            CraftingRequirement child2 = SchematicVisibilityDebug.getFieldValue(NotRequirement.class, notRequirement, "requirement", CraftingRequirement.class);
            if (child2 != null && child2.test(context)) {
                return List.of("not \u6761\u4ef6\u5931\u8d25\uff0c\u56e0\u4e3a\u5176\u5185\u90e8\u6761\u4ef6\u5b9e\u9645\u6210\u7acb: " + String.join((CharSequence)"\uff1b", SchematicVisibilityDebug.diagnoseRequirement(child2, context)));
            }
            return List.of("not \u6761\u4ef6\u5931\u8d25");
        }
        if (requirement instanceof LockedRequirement) {
            LockedRequirement lockedRequirement = (LockedRequirement)requirement;
            ResourceLocation key = SchematicVisibilityDebug.getFieldValue(LockedRequirement.class, lockedRequirement, "key", ResourceLocation.class);
            return List.of("\u7f3a\u5c11\u89e3\u9501\u6761\u4ef6 " + String.valueOf(key));
        }
        if (requirement instanceof SlotRequirement) {
            SlotRequirement slotRequirement = (SlotRequirement)requirement;
            String slot = SchematicVisibilityDebug.getFieldValue(SlotRequirement.class, slotRequirement, "slot", String.class);
            return List.of("\u8981\u6c42\u69fd\u4f4d\u4e3a " + slot + "\uff0c\u5f53\u524d\u4e3a " + context.slot);
        }
        if (requirement instanceof NeverRequirement) {
            return List.of("requirement.type=tetra:never\uff0c\u6c38\u8fdc\u4e0d\u4f1a\u663e\u793a");
        }
        if (requirement instanceof FeatureFlagRequirement) {
            FeatureFlagRequirement featureFlagRequirement = (FeatureFlagRequirement)requirement;
            FeatureFlag feature = SchematicVisibilityDebug.getFieldValue(FeatureFlagRequirement.class, featureFlagRequirement, "feature", FeatureFlag.class);
            return List.of("\u529f\u80fd\u5f00\u5173\u672a\u542f\u7528: " + String.valueOf(feature));
        }
        if (requirement instanceof AcceptsImprovementRequirement) {
            AcceptsImprovementRequirement acceptsImprovementRequirement = (AcceptsImprovementRequirement)requirement;
            String improvement = SchematicVisibilityDebug.getFieldValue(AcceptsImprovementRequirement.class, acceptsImprovementRequirement, "improvement", String.class);
            Integer level = SchematicVisibilityDebug.getFieldValue(AcceptsImprovementRequirement.class, acceptsImprovementRequirement, "level", Integer.class);
            if (context.targetMajorModule == null) {
                return List.of("\u76ee\u6807\u69fd\u4f4d\u6ca1\u6709 major module\uff0c\u65e0\u6cd5\u63a5\u53d7\u5f3a\u5316 " + improvement);
            }
            if (level != null && !context.targetMajorModule.acceptsImprovementLevel(improvement, level.intValue())) {
                return List.of("\u76ee\u6807\u6a21\u5757\u4e0d\u63a5\u53d7\u5f3a\u5316 " + improvement + " \u7684\u7b49\u7ea7 " + level);
            }
            return List.of("\u76ee\u6807\u6a21\u5757\u4e0d\u63a5\u53d7\u5f3a\u5316 " + improvement);
        }
        if (requirement instanceof HasImprovementRequirement) {
            HasImprovementRequirement hasImprovementRequirement = (HasImprovementRequirement)requirement;
            String improvement = SchematicVisibilityDebug.getFieldValue(HasImprovementRequirement.class, hasImprovementRequirement, "improvement", String.class);
            IntegerPredicate level = SchematicVisibilityDebug.getFieldValue(HasImprovementRequirement.class, hasImprovementRequirement, "level", IntegerPredicate.class);
            if (context.targetMajorModule == null) {
                return List.of("\u76ee\u6807\u69fd\u4f4d\u6ca1\u6709 major module\uff0c\u65e0\u6cd5\u68c0\u67e5\u5df2\u6709\u5f3a\u5316 " + improvement);
            }
            if (context.targetMajorModule.getImprovement(context.targetStack, improvement) == null) {
                return List.of("\u76ee\u6807\u6a21\u5757\u6ca1\u6709\u5f3a\u5316 " + improvement);
            }
            if (level != null && !level.test(Integer.valueOf(context.targetMajorModule.getImprovementLevel(context.targetStack, improvement)))) {
                return List.of("\u76ee\u6807\u6a21\u5757\u5df2\u6709\u5f3a\u5316 " + improvement + "\uff0c\u4f46\u7b49\u7ea7\u4e0d\u6ee1\u8db3 " + SchematicVisibilityDebug.describePredicate(level));
            }
            return List.of("\u5df2\u6709\u5f3a\u5316\u6761\u4ef6\u4e0d\u6ee1\u8db3");
        }
        if (requirement instanceof AspectRequirement) {
            int currentLevel;
            AspectRequirement aspectRequirement = (AspectRequirement)requirement;
            ItemAspect aspect = SchematicVisibilityDebug.getFieldValue(AspectRequirement.class, aspectRequirement, "aspect", ItemAspect.class);
            IntegerPredicate level = SchematicVisibilityDebug.getFieldValue(AspectRequirement.class, aspectRequirement, "level", IntegerPredicate.class);
            if (context.targetModule == null) {
                return List.of("\u76ee\u6807\u69fd\u4f4d\u6ca1\u6709 module\uff0c\u65e0\u6cd5\u68c0\u67e5 aspect");
            }
            boolean hasAspect = context.targetModule.getAspects(context.targetStack).contains(aspect);
            if (!hasAspect) {
                return List.of("\u76ee\u6807\u6a21\u5757\u7f3a\u5c11 aspect " + String.valueOf(aspect));
            }
            if (level != null && !level.test(Integer.valueOf(currentLevel = context.targetModule.getAspects(context.targetStack).getLevel(aspect)))) {
                return List.of("aspect " + String.valueOf(aspect) + " \u7684\u7b49\u7ea7\u4e3a " + currentLevel + "\uff0c\u4e0d\u6ee1\u8db3 " + SchematicVisibilityDebug.describePredicate(level));
            }
            return List.of("aspect \u6761\u4ef6\u4e0d\u6ee1\u8db3");
        }
        if (requirement instanceof ModuleRequirement) {
            ModuleRequirement moduleRequirement = (ModuleRequirement)requirement;
            String moduleKey = SchematicVisibilityDebug.getFieldValue(ModuleRequirement.class, moduleRequirement, "moduleKey", String.class);
            String moduleVariant = SchematicVisibilityDebug.getFieldValue(ModuleRequirement.class, moduleRequirement, "moduleVariant", String.class);
            String materialPattern = SchematicVisibilityDebug.getFieldValue(ModuleRequirement.class, moduleRequirement, "materialPattern", String.class);
            if (context.targetModule == null) {
                return List.of("\u76ee\u6807\u69fd\u4f4d\u6ca1\u6709 module");
            }
            ArrayList<String> reasons = new ArrayList<String>();
            if (moduleKey != null && !moduleKey.equals(context.targetModule.getKey())) {
                reasons.add("\u8981\u6c42 module=" + moduleKey + "\uff0c\u5f53\u524d\u4e3a " + context.targetModule.getKey());
            }
            String currentVariant = context.targetModule.getVariantData((ItemStack)context.targetStack).key;
            if (moduleVariant != null && !moduleVariant.equals(currentVariant)) {
                reasons.add("\u8981\u6c42 variant=" + moduleVariant + "\uff0c\u5f53\u524d\u4e3a " + currentVariant);
            }
            if (materialPattern != null && !currentVariant.matches(materialPattern)) {
                reasons.add("\u5f53\u524d variant=" + currentVariant + " \u4e0d\u5339\u914d\u6750\u6599\u89c4\u5219 " + materialPattern);
            }
            return reasons.isEmpty() ? List.of("module \u6761\u4ef6\u4e0d\u6ee1\u8db3") : reasons;
        }
        if (requirement instanceof PerkRequrement) {
            PerkRequrement perkRequrement = (PerkRequrement)requirement;
            String perk = SchematicVisibilityDebug.getFieldValue(PerkRequrement.class, perkRequrement, "perk", String.class);
            IntegerPredicate level = SchematicVisibilityDebug.getFieldValue(PerkRequrement.class, perkRequrement, "level", IntegerPredicate.class);
            return List.of("perk \u6761\u4ef6\u53ef\u80fd\u5728\u5ba2\u6237\u7aef\u4fa7\u4e0d\u6ee1\u8db3: " + perk + (String)(level != null ? " " + SchematicVisibilityDebug.describePredicate(level) : ""));
        }
        return List.of("\u672a\u8bc6\u522b\u7684 requirement \u7c7b\u578b: " + requirement.getClass().getSimpleName());
    }

    private static String describePredicate(IntegerPredicate predicate) {
        Integer min = SchematicVisibilityDebug.getFieldValue(IntegerPredicate.class, predicate, "min", Integer.class);
        Integer max = SchematicVisibilityDebug.getFieldValue(IntegerPredicate.class, predicate, "max", Integer.class);
        if (min != null && max != null) {
            return "[" + min + ", " + max + "]";
        }
        if (min != null) {
            return ">= " + min;
        }
        if (max != null) {
            return "<= " + max;
        }
        return "\u672a\u77e5\u8303\u56f4";
    }

    private static String describeStack(ItemStack stack) {
        if (stack.isEmpty()) {
            return "<empty>";
        }
        return stack.getItem().toString() + " x" + stack.getCount();
    }

    private static <T> T getFieldValue(Class<?> owner, Object instance, String name, Class<T> type) {
        try {
            Field field = owner.getDeclaredField(name);
            field.setAccessible(true);
            Object value = field.get(instance);
            if (value == null) {
                return null;
            }
            if (type.isInstance(value)) {
                return (T)value;
            }
        }
        catch (ReflectiveOperationException exception) {
            TetraDataKubejs.LOGGER.warn("{} \u8bfb\u53d6\u5b57\u6bb5 {}.{} \u5931\u8d25", new Object[]{LOG_PREFIX, owner.getSimpleName(), name, exception});
        }
        return null;
    }

    private static final class VisibilityResult {
        private final boolean visible;
        private final List<String> reasons;

        private VisibilityResult(boolean visible, List<String> reasons) {
            this.visible = visible;
            this.reasons = reasons;
        }

        private static VisibilityResult visible() {
            return new VisibilityResult(true, List.of());
        }

        private static VisibilityResult hidden(String reason) {
            return new VisibilityResult(false, List.of(reason));
        }

        private static VisibilityResult hidden(List<String> reasons) {
            LinkedHashSet<String> deduped = new LinkedHashSet<String>(reasons);
            return new VisibilityResult(false, new ArrayList<String>(deduped));
        }
    }
}

