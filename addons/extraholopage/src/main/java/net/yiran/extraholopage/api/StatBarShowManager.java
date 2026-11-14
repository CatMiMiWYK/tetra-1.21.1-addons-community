package net.yiran.extraholopage.api;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import se.mickelus.tetra.items.modular.impl.ModularBladedItem;
import se.mickelus.tetra.items.modular.impl.ModularDoubleHeadedItem;
import se.mickelus.tetra.items.modular.impl.ModularSingleHeadedItem;
import se.mickelus.tetra.items.modular.impl.bow.ModularBowItem;
import se.mickelus.tetra.items.modular.impl.crossbow.ModularCrossbowItemImpl;
import se.mickelus.tetra.items.modular.impl.shield.ModularShieldItem;

public class StatBarShowManager {
    public Map<String, ShowHandler> LabelToHandle = new HashMap<String, ShowHandler>();
    public Multimap<String, Item> ItemGroup = ArrayListMultimap.create();

    public StatBarShowManager() {
        this.addItemInGroup("bow", ModularBowItem.instance);
        this.addItemInGroup("crossbow", ModularCrossbowItemImpl.instance);
        this.addItemInGroup("ranged", new Item[]{ModularBowItem.instance, ModularCrossbowItemImpl.instance});
        this.addItemInGroup("weapon", new Item[]{ModularBladedItem.instance, ModularDoubleHeadedItem.instance, ModularSingleHeadedItem.instance});
        this.addItemInGroup("shield", ModularShieldItem.instance);
        this.registerAllowWithGroup("weapon", "tetra.stats.speed", "tetra.stats.attack_damage");
        this.registerAllowWithGroup("shield", "tetra.stats.blocking_reflect");
        this.registerAllowWithGroup("ranged", "tetra.stats.spread", "tetra.stats.draw_speed", "tetra.stats.draw_strength");
    }

    public static void addGroupItem(String group, Item ... item) {
        StatBarShowManager.getInstance().addItemInGroup(group, item);
    }

    public static Set<String> getAllGroups() {
        return StatBarShowManager.getInstance().ItemGroup.keySet();
    }

    public static Collection<Item> getItemsInGroup(String group) {
        return StatBarShowManager.getInstance().ItemGroup.get(group);
    }

    public static StatBarShowManager getInstance() {
        return Internal.INSTANCE;
    }

    public void addItemInGroup(String group, Item item) {
        this.ItemGroup.put(group, item);
    }

    public void addItemInGroup(String group, Item ... item) {
        this.ItemGroup.putAll(group, List.of(item));
    }

    public void registerAllowWithGroup(String group, String label) {
        this.registerSimplyHandle(label, checkItem -> this.ItemGroup.containsKey(group) ? (this.ItemGroup.get(group).contains(checkItem) ? ShowResult.SKIP : ShowResult.FAIL) : ShowResult.FAIL);
    }

    public void registerAllowWithGroup(String group, String ... labels) {
        for (String label : labels) {
            this.registerAllowWithGroup(group, label);
        }
    }

    public void registerDestroyWithGroup(String group, String label) {
        this.registerSimplyHandle(label, checkItem -> this.ItemGroup.containsKey(group) ? (this.ItemGroup.get(group).contains(checkItem) ? ShowResult.FAIL : ShowResult.SKIP) : ShowResult.SKIP);
    }

    public void registerDestroyWithGroup(String group, String ... labels) {
        for (String label : labels) {
            this.registerDestroyWithGroup(group, label);
        }
    }

    public void registerSimplyHandle(String label, ShowSimplyHandler handler) {
        this.registerHandler(label, handler);
    }

    public void registerHandler(String label, ShowHandler handler) {
        this.LabelToHandle.put(label, handler);
    }

    public ShowResult handle(String label, ItemStack stack) {
        if (!this.LabelToHandle.containsKey(label)) {
            return ShowResult.SKIP;
        }
        return this.LabelToHandle.get(label).check(stack);
    }

    public static class Internal {
        public static StatBarShowManager INSTANCE = new StatBarShowManager();
    }

    @FunctionalInterface
    public static interface ShowSimplyHandler
    extends ShowHandler {
        public ShowResult check(Item var1);

        @Override
        default public ShowResult check(ItemStack checkStack) {
            return this.check(checkStack.getItem());
        }
    }

    @FunctionalInterface
    public static interface ShowHandler {
        public ShowResult check(ItemStack var1);
    }

    public static enum ShowResult {
        SUCCESS,
        FAIL,
        SKIP;

    }
}
