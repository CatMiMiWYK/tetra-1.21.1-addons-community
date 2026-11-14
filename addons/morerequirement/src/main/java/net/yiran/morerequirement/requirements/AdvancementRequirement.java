package net.yiran.morerequirement.requirements;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.ItemAbility;
import se.mickelus.tetra.craftingeffect.condition.CraftingEffectCondition;
import se.mickelus.tetra.module.schematic.CraftingContext;
import se.mickelus.tetra.module.schematic.UpgradeSchematic;
import se.mickelus.tetra.module.schematic.requirement.CraftingRequirement;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class AdvancementRequirement implements CraftingRequirement, CraftingEffectCondition {
    public ResourceLocation advancement;

    @Override
    public boolean test(ResourceLocation[] unlocks, ItemStack upgradedStack, String slot, boolean isReplacing, Player player,
            ItemStack[] materials, Map<ItemAbility, Integer> tools, UpgradeSchematic schematic, Level world, BlockPos pos, BlockState blockState) {
        return hasAdvancement(player, this.advancement);
    }

    @Override
    public boolean test(CraftingContext cxt) {
        return hasAdvancement(cxt.player, this.advancement);
    }

    private static boolean hasAdvancement(@Nullable Player player, ResourceLocation advancement) {
        if (player instanceof ServerPlayer serverPlayer) {
            AdvancementHolder holder = serverPlayer.server.getAdvancements().get(advancement);
            return holder != null && serverPlayer.getAdvancements().getOrStartProgress(holder).isDone();
        }
        if (player == null) {
            return false;
        }
        return hasClientAdvancement(advancement);
    }

    @OnlyIn(Dist.CLIENT)
    private static boolean hasClientAdvancement(ResourceLocation advancement) {
        ClientPacketListener connection = Minecraft.getInstance().getConnection();
        return connection != null && connection.getAdvancements().get(advancement) != null;
    }

    @Nullable
    @Override
    @OnlyIn(Dist.CLIENT)
    public List<Component> getDescription() {
        if (!this.advancement.getNamespace().equals("minecraft")) {
            return List.of(Component.translatable("more_requirement.holo.advancement_requirement",
                    Component.translatable(this.advancement.getNamespace() + ".advancements." + this.advancement.getPath().replace("/", ".") + ".title")));
        }
        return List.of(Component.translatable("more_requirement.holo.advancement_requirement",
                Component.translatable("advancements." + this.advancement.getPath().replace("/", ".") + ".title")));
    }
}
