package net.yiran.fot.util;

import java.util.Optional;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.yiran.fot.common.TetraFEStore;

public class FEUtil {
    public static Optional<TetraFEStore> getTetraFEStore(ItemStack stack) {
        Object capability = stack.getCapability(Capabilities.EnergyStorage.ITEM);
        if (capability instanceof TetraFEStore store) {
            return Optional.of(store);
        }
        return Optional.empty();
    }
}
