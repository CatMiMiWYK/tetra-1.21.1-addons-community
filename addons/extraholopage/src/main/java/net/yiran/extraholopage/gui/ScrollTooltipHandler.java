package net.yiran.extraholopage.gui;

import com.google.common.base.Suppliers;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.logging.LogUtils;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import se.mickelus.tetra.blocks.scroll.ScrollData;
import se.mickelus.tetra.blocks.scroll.ScrollItem;
import se.mickelus.tetra.data.DataManager;
import se.mickelus.tetra.module.schematic.SchematicDefinition;
import se.mickelus.tetra.module.schematic.requirement.AndRequirement;
import se.mickelus.tetra.module.schematic.requirement.CraftingRequirement;
import se.mickelus.tetra.module.schematic.requirement.LockedRequirement;
import se.mickelus.tetra.module.schematic.requirement.ModuleRequirement;
import se.mickelus.tetra.module.schematic.requirement.OrRequirement;

public class ScrollTooltipHandler {
    public static Supplier<ScrollTooltipHandler> INSTANCE = Suppliers.memoize(ScrollTooltipHandler::new);
    public Multimap<String, SchematicDefinition> locked2Schematic = HashMultimap.create();
    public Multimap<SchematicDefinition, String> schematic2module = HashMultimap.create();
    public static Field and;
    public static Field or;
    public static Field module;

    public static void init() {
        DataManager.instance.schematicData.onReload(INSTANCE.get()::onSchematicReload);
    }

    public static CraftingRequirement[] getRequirements(CraftingRequirement requirement) {
        try {
            if (requirement instanceof AndRequirement) {
                return (CraftingRequirement[])and.get(requirement);
            }
            return (CraftingRequirement[])or.get(requirement);
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getModuleKey(CraftingRequirement schematic, String[] keySuffixes) {
        try {
            String result = (String)module.get(schematic);
            if (result != null) {
                result = result.replace("_right", "");
                result = result.replace("_left", "");
                for (String keySuffix : keySuffixes) {
                    result = result.replace(keySuffix, "");
                }
            }
            return result;
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<String> getLocked(CraftingRequirement requirement) {
        ArrayList<String> locked = new ArrayList<String>();
        if (requirement instanceof AndRequirement || requirement instanceof OrRequirement) {
            for (CraftingRequirement craftingRequirement : ScrollTooltipHandler.getRequirements(requirement)) {
                locked.addAll(ScrollTooltipHandler.getLocked(craftingRequirement));
            }
        } else if (requirement instanceof LockedRequirement) {
            return List.of(((LockedRequirement)requirement).key.toString());
        }
        return locked;
    }

    public static List<String> getModule(CraftingRequirement requirement, String[] keySuffixes) {
        ArrayList<String> locked = new ArrayList<String>();
        if (requirement instanceof AndRequirement || requirement instanceof OrRequirement) {
            for (CraftingRequirement craftingRequirement : ScrollTooltipHandler.getRequirements(requirement)) {
                locked.addAll(ScrollTooltipHandler.getModule(craftingRequirement, keySuffixes));
            }
        } else if (requirement instanceof ModuleRequirement) {
            String moduleKey = ScrollTooltipHandler.getModuleKey(requirement, keySuffixes);
            if (moduleKey != null) {
                return List.of(ScrollTooltipHandler.getModuleKey(requirement, keySuffixes));
            }
            return List.of();
        }
        return locked;
    }

    public void onSchematicReload() {
        LogUtils.getLogger().debug("onSchematicReload-\u5f00\u59cb\u89e3\u6790\u5168\u90e8\u539f\u7406\u56fe");
        this.locked2Schematic.clear();
        for (SchematicDefinition value : DataManager.instance.schematicData.getData().values()) {
            for (String string : ScrollTooltipHandler.getLocked(value.requirement)) {
                this.locked2Schematic.put(string, value);
            }
            for (String string : ScrollTooltipHandler.getModule(value.requirement, value.keySuffixes)) {
                this.schematic2module.put(value, string);
            }
        }
        LogUtils.getLogger().debug("onSchematicReload-locked\u6570\u91cf\uff1a{}", this.schematic2module.size());
    }

    @SubscribeEvent
    public void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (!stack.is(ScrollItem.instance)) {
            return;
        }
        HashSet<?> strings = new HashSet<Object>();
        ScrollData.read(stack).schematics.stream().map(ResourceLocation::toString).map(arg_0 -> this.locked2Schematic.get(arg_0)).flatMap(Collection::stream).forEach(schematicDefinition -> {
            Set<String> setSlots = Arrays.stream(schematicDefinition.slots).map(string -> I18n.get("tetra.slot." + string)).collect(Collectors.toSet());
            String slots = setSlots.size() > 3 && !Screen.hasShiftDown() ? String.join(I18n.get("ehp.scroll.tooltip.slots.merge.format"), setSlots.stream().limit(3L).toList()) + I18n.get("ehp.scroll.tooltip.slots.extra.format", setSlots.size() - 3) : String.join(I18n.get("ehp.scroll.tooltip.slots.merge.format"), setSlots) + "\u00a7r";
            String sl = schematicDefinition.localizationKey == null ? schematicDefinition.key : schematicDefinition.localizationKey;
            String name = I18n.get("tetra/schematic/" + sl + ".name") + "\u00a7r";
            String module = String.join(I18n.get("ehp.scroll.tooltip.slots.merge.format"), this.schematic2module.get(schematicDefinition).stream().map(string -> I18n.get("tetra.module." + string + ".name")).collect(Collectors.toSet()));
            if (module.isEmpty()) {
                module = I18n.get("ehp.scroll.tooltip.module.empty");
            }
            event.getToolTip().add(1, Component.translatable("ehp.scroll.tooltip.total.format", name, slots, module));
        });
    }

    static {
        try {
            and = AndRequirement.class.getDeclaredField("requirements");
            and.setAccessible(true);
            or = OrRequirement.class.getDeclaredField("requirements");
            or.setAccessible(true);
            module = ModuleRequirement.class.getDeclaredField("moduleKey");
            module.setAccessible(true);
        }
        catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }
}
