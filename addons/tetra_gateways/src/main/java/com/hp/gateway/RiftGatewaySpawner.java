package com.hp.gateway;

import com.hp.effect.RiftEffects;
import com.hp.rift.RiftPearlData;
import dev.shadowsoffire.gateways.entity.GatewayEntity;
import dev.shadowsoffire.gateways.gate.Gateway;
import dev.shadowsoffire.gateways.item.GatePearlItem;
import dev.shadowsoffire.placebo.reload.DynamicHolder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import se.mickelus.tetra.effect.EffectHelper;

public class RiftGatewaySpawner {
    public static InteractionResult open(UseOnContext context) {
        Level level = context.getLevel();
        ItemStack stack = context.getItemInHand();
        RiftPearlData.ensureGateway(stack);
        DynamicHolder<Gateway> holder = GatePearlItem.getGate(stack);
        if (!holder.isBound()) {
            return InteractionResult.FAIL;
        }
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        if (!RiftPearlData.canUse(stack)) {
            Player player = context.getPlayer();
            if (player != null) {
                player.displayClientMessage(Component.translatable("message.tetra_gateways.no_uses").withStyle(ChatFormatting.RED), false);
            }
            return InteractionResult.FAIL;
        }
        Player player = context.getPlayer();
        Gateway gateway = holder.get();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        VoxelShape shape = state.getShape(level, pos);
        double y = (double) pos.getY() + (shape.isEmpty() ? 0.0 : shape.max(Direction.Axis.Y));
        boolean dual = level.random.nextFloat() * 100.0f < (float) EffectHelper.getEffectLevel(stack, RiftEffects.RIFT_DUAL);
        float scale = dual ? Math.max(0.0f, EffectHelper.getEffectEfficiency(stack, RiftEffects.RIFT_DUAL) / 100.0f) : 1.0f;
        int count = dual ? 2 : 1;
        for (int i = 0; i < count; ++i) {
            GatewayEntity entity = gateway.createEntity(level, player);
            double offset = dual ? (i == 0 ? -0.75 : 0.75) : 0.0;
            entity.moveTo((double) pos.getX() + 0.5 + offset, y, (double) pos.getZ() + 0.5);
            if (!prepareEntity(level, gateway, entity, player, i > 0 && dual)) {
                return InteractionResult.FAIL;
            }
            ItemStack source = stack.copy();
            RiftPearlData.setAttributeScale(source, scale);
            RiftPearlData.writeSourcePearl(entity.getPersistentData(), source, level.registryAccess());
            level.addFreshEntity(entity);
            entity.onGateCreated();
        }
        if (player == null || !player.isCreative()) {
            RiftPearlData.consumeUse(stack);
        }
        return InteractionResult.CONSUME;
    }

    private static boolean prepareEntity(Level level, Gateway gateway, GatewayEntity entity, Player player, boolean ignoreSpacing) {
        double spacing = Math.max(0.0, gateway.rules().spacing());
        if (!ignoreSpacing && !level.getEntitiesOfClass(GatewayEntity.class, entity.getBoundingBox().inflate(spacing)).isEmpty()) {
            return false;
        }
        for (int i = 0; i < 4 && !level.noCollision(entity); ++i) {
            entity.moveTo(entity.getX(), entity.getY() + 1.0, entity.getZ());
        }
        if (level.noCollision(entity)) {
            return true;
        }
        if (player != null) {
            player.displayClientMessage(Component.translatable("error.gateways.no_space").withStyle(ChatFormatting.RED), false);
        }
        return false;
    }

    private RiftGatewaySpawner() {
    }
}
