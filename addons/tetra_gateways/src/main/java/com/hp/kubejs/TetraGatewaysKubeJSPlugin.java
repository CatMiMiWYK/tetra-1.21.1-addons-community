package com.hp.kubejs;

import com.hp.rift.RiftConfigBridge;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.BindingRegistry;

public class TetraGatewaysKubeJSPlugin implements KubeJSPlugin {
    @Override
    public void registerBindings(BindingRegistry event) {
        event.add("TetraGatewaysRift", RiftConfigBridge.class);
    }
}
