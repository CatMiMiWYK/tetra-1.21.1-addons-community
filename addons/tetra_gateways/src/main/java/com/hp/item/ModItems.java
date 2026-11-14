package com.hp.item;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.yiran.tetrajs.util.SlotData;

import java.util.List;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("tetra_gateways");
    public static final DeferredItem<RiftGatePearlItem> RIFT_GATE_PEARL = ITEMS.register("rift_gate_pearl",
            () -> new RiftGatePearlItem(ResourceLocation.fromNamespaceAndPath("tetra_gateways", "rift_gate_pearl"),
                    List.of(SlotData.create("rift_core", 1, -3, true),
                            SlotData.create("rift_stability", -11, 21, true),
                            SlotData.create("rift_depth", -14, 0, true)),
                    List.of()));

    private ModItems() {
    }
}
