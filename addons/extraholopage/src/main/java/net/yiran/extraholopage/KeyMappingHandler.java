package net.yiran.extraholopage;

import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import se.mickelus.tetra.items.modular.impl.holo.ModularHolosphereItem;

public class KeyMappingHandler {
    public static KeyMapping openHolo = new KeyMapping("ehp.open_holo_gui", 72, "ehp.binding.group");
    public static KeyMapping showTooltip = new KeyMapping("ehp.show_material_tooltip", 341, "ehp.binding.group");
    public static KeyMapping nextElement = new KeyMapping("ehp.next_elements_page", 262, "ehp.binding.group");
    public static KeyMapping previousElement = new KeyMapping("ehp.previous_elements_page", 263, "ehp.binding.group");

    public static void onRegisterKeyMapping(RegisterKeyMappingsEvent event) {
        event.register(openHolo);
        event.register(showTooltip);
        event.register(nextElement);
        event.register(previousElement);
    }

    public static void onClientTick(ClientTickEvent.Pre event) {
        if (openHolo.consumeClick()) {
            ModularHolosphereItem.showGui();
        }
    }
}
