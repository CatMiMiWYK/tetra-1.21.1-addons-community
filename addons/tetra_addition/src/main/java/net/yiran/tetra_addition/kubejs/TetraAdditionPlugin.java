package net.yiran.tetra_addition.kubejs;

import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.BindingRegistry;
import net.yiran.tetra_addition.MyTetraUtil;
import net.yiran.tetra_addition.WorkbenchTileCraftEvent;

public class TetraAdditionPlugin
        implements KubeJSPlugin {
    @Override
    public void registerBindings(BindingRegistry event) {
        event.add("MyTetraUtil", MyTetraUtil.instance);
        event.add("$WorkbenchTileCraftEvent", WorkbenchTileCraftEvent.class);
    }
}
