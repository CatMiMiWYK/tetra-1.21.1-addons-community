package net.yiran.morerequirement.requirements;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
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

public class PotionEffectRequirement implements CraftingRequirement, CraftingEffectCondition {
    public MobEffect effect;
    public int duration = 0;
    public int amplifier = 0;

    @Override
    public boolean test(ResourceLocation[] unlocks, ItemStack upgradedStack, String slot, boolean isReplacing, Player player,
            ItemStack[] materials, Map<ItemAbility, Integer> tools, UpgradeSchematic schematic, Level world, BlockPos pos, BlockState blockState) {
        return matches(player);
    }

    @Override
    public boolean test(CraftingContext cxt) {
        Player player = cxt.player;
        if (player == null) {
            return false;
        }
        return matches(player);
    }

    private boolean matches(Player player) {
        Holder<MobEffect> holder = BuiltInRegistries.MOB_EFFECT.wrapAsHolder(this.effect);
        MobEffectInstance instance = player.getEffect(holder);
        return instance != null && instance.getAmplifier() >= this.amplifier && instance.getDuration() >= this.duration;
    }

    @Nullable
    @Override
    @OnlyIn(Dist.CLIENT)
    public List<Component> getDescription() {
        return List.of(Component.translatable("more_requirement.holo.potion_effect_requirement",
                this.effect.getDisplayName(), this.amplifier + 1, this.duration / 20));
    }
}
