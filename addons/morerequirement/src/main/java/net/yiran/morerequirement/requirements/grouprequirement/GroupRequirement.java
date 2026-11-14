package net.yiran.morerequirement.requirements.grouprequirement;

import com.google.common.collect.ImmutableList;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import se.mickelus.tetra.module.schematic.CraftingContext;
import se.mickelus.tetra.module.schematic.requirement.CraftingRequirement;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class GroupRequirement implements CraftingRequirement {
    public String key;
    public boolean hideExtend = false;

    @Override
    public boolean test(CraftingContext cxt) {
        GroupRequirementStore groupRequirementStore = GroupRequirementStore.groups.get(this.key);
        if (groupRequirementStore == null) {
            return true;
        }
        return Arrays.stream(groupRequirementStore.requirements).allMatch(requirement -> requirement.test(cxt));
    }

    @Nullable
    @Override
    @OnlyIn(Dist.CLIENT)
    public List<Component> getDescription() {
        GroupRequirementStore groupRequirementStore = GroupRequirementStore.groups.get(this.key);
        if (groupRequirementStore == null) {
            return List.of(Component.translatable("more_requirement.group.empty"));
        }
        ImmutableList.Builder<Component> builder = ImmutableList.builder();
        builder.add(Component.translatable(groupRequirementStore.translation));
        if (!this.hideExtend && !groupRequirementStore.hideExtend) {
            CraftingRequirement[] requirements = groupRequirementStore.requirements;
            for (int i = 0; i < requirements.length; ++i) {
                List<Component> description = requirements[i].getDescription();
                if (description == null) {
                    continue;
                }
                for (int j = 0; j < description.size(); ++j) {
                    if (j == 0) {
                        builder.add(Component.literal(i == requirements.length - 1 ? " \u00a78\u2514\u00a7r " : " \u00a78\u251c\u00a7r ")
                                .append(description.get(j)));
                    } else {
                        builder.add(Component.literal(i == requirements.length - 1 ? "      " : " \u00a78\u2502\u00a7r ")
                                .append(description.get(j)));
                    }
                }
            }
        }
        return builder.build();
    }
}
