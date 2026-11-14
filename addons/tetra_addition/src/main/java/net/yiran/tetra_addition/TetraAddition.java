package net.yiran.tetra_addition;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(TetraAddition.MODID)
public class TetraAddition {
    public static final String MODID = "tetra_addition";

    public IEventBus modEventBus;

    public TetraAddition(IEventBus modEventBus, ModContainer modContainer) {
        this.modEventBus = modEventBus;
    }
}
