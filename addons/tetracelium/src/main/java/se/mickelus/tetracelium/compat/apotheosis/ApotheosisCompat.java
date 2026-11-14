package se.mickelus.tetracelium.compat.apotheosis;

import java.util.function.BiFunction;
import javax.annotation.ParametersAreNonnullByDefault;
import net.neoforged.fml.ModList;
import se.mickelus.tetra.module.ItemUpgradeRegistry;

@ParametersAreNonnullByDefault
public class ApotheosisCompat {
    public static final String modId = "apotheosis";
    public static final Boolean isLoaded = ModList.get().isLoaded("apotheosis");

    public static void setup() {
        ItemUpgradeRegistry.instance.registerReplacementHook(new AffixReplacementHook());
    }
}
