package net.yiran.morerequirement.requirements;

import com.google.common.collect.ImmutableList;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import se.mickelus.tetra.module.schematic.CraftingContext;
import se.mickelus.tetra.module.schematic.requirement.CraftingRequirement;

import javax.annotation.Nullable;
import java.util.List;

public class OtherModuleRequirement implements CraftingRequirement {
    public CraftingRequirement requirement;
    public String slot;

    @Override
    public boolean test(CraftingContext cxt) {
        return this.requirement.test(changeSlot(cxt, this.slot));
    }

    @Nullable
    @Override
    @OnlyIn(Dist.CLIENT)
    public List<Component> getDescription() {
        ImmutableList.Builder<Component> builder = ImmutableList.builder();
        builder.add(Component.translatable("more_requirement.holo.other_module_requirement", Component.translatable("tetra.slot." + this.slot)));
        List<Component> description = this.requirement.getDescription();
        if (description != null) {
            for (int j = 0; j < description.size(); ++j) {
                if (j == 0) {
                    builder.add(Component.literal(" \u00a78\u2514\u00a7r ").append(description.get(j)));
                } else {
                    builder.add(Component.literal("      ").append(description.get(j)));
                }
            }
        }
        return builder.build();
    }

    public static CraftingContext changeSlot(CraftingContext cxt, String slot) {
        return new CraftingContext(cxt.world, cxt.pos, cxt.blockState, cxt.player, cxt.targetStack, slot, cxt.unlocks);
    }
}
