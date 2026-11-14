package net.yiran.extraholopage.gui;

import com.google.common.base.Suppliers;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import se.mickelus.tetra.data.DataManager;
import se.mickelus.tetra.data.predicate.TetraItemPredicate;
import se.mickelus.tetra.module.schematic.OutcomeDefinition;
import se.mickelus.tetra.module.schematic.SchematicDefinition;

public class SpecialMaterialTooltipHandler {
    public static Supplier<SpecialMaterialTooltipHandler> INSTANCE = Suppliers.memoize(SpecialMaterialTooltipHandler::new);
    public boolean needLoad = true;
    public Map<TetraItemPredicate, SchematicDefinition> SpecialMaterialPredicatesMap = new HashMap<TetraItemPredicate, SchematicDefinition>();

    public void init() {
        DataManager.instance.schematicData.onReload(() -> {
            this.needLoad = true;
        });
    }

    @SubscribeEvent(priority=EventPriority.HIGHEST)
    public void onScrollTooltip(ItemTooltipEvent event) {
        if (this.needLoad) {
            this.SpecialMaterialPredicatesMap.clear();
            Set<TetraItemPredicate> materialPredicates = DataManager.instance.materialData.getData().values().stream().map(materialData -> materialData.material.getPredicate()).filter(Objects::nonNull).collect(Collectors.toSet());
            for (SchematicDefinition definition : DataManager.instance.schematicData.getData().values()) {
                for (OutcomeDefinition outcome : definition.outcomes) {
                    TetraItemPredicate outcomePredicate = outcome.material.getPredicate();
                    if (outcomePredicate == null || materialPredicates.contains(outcomePredicate)) continue;
                    this.SpecialMaterialPredicatesMap.put(outcomePredicate, definition);
                }
            }
            this.needLoad = false;
        }
        HashSet<SchematicDefinition> schematics = new HashSet<SchematicDefinition>();
        this.SpecialMaterialPredicatesMap.forEach((itemPredicate, schematicDefinition) -> {
            if (itemPredicate.test(event.getItemStack())) {
                schematics.add(schematicDefinition);
            }
        });
        if (schematics.isEmpty()) {
            return;
        }
        ArrayList<Component> tooltip = new ArrayList<Component>();
        if (Screen.hasShiftDown()) {
            for (SchematicDefinition schematic : schematics) {
                String schematicName = I18n.get("tetra/schematic/" + (schematic.localizationKey == null ? schematic.key : schematic.localizationKey) + ".name");
                String slots = String.join(I18n.get("ehp.special.material.tooltip.slots.merge.format"), Arrays.stream(schematic.slots).map(string -> I18n.get("tetra.slot." + string)).collect(Collectors.toSet()));
                tooltip.add(ComponentHelper.literal(I18n.get("ehp.special.material.tooltip.total.format", schematicName, slots)));
            }
            tooltip.sort(Comparator.comparing(Component::getString));
            tooltip.add(0, ComponentHelper.translatable("ehp.special.material.shift.press.tooltip"));
        } else {
            tooltip.add(ComponentHelper.translatable("ehp.special.material.shift.release.tooltip"));
        }
        event.getToolTip().addAll(1, tooltip);
    }
}
