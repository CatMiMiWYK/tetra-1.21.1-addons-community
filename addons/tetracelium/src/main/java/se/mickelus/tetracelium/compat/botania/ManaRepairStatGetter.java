package se.mickelus.tetracelium.compat.botania;

import java.util.Optional;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import se.mickelus.tetra.gui.stats.getter.StatGetterEffectEfficiency;

@ParametersAreNonnullByDefault
public class ManaRepairStatGetter extends StatGetterEffectEfficiency {
    public ManaRepairStatGetter() {
        super(ManaRepair.effect, 1.0);
    }

    @Override
    public double getValue(Player player, ItemStack itemStack) {
        return Optional.of(super.getValue(player, itemStack)).filter(val -> val != 0.0).map(val -> 1.0 / val).orElse(0.0);
    }

    @Override
    public double getValue(Player player, ItemStack itemStack, String slot) {
        return Optional.of(super.getValue(player, itemStack, slot)).filter(val -> val != 0.0).map(val -> 1.0 / val).orElse(0.0);
    }

    @Override
    public double getValue(Player player, ItemStack itemStack, String slot, String improvement) {
        return Optional.of(super.getValue(player, itemStack, slot, improvement)).filter(val -> val != 0.0).map(val -> 1.0 / val).orElse(0.0);
    }
}
