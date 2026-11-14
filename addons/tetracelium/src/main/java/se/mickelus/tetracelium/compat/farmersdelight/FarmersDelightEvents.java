package se.mickelus.tetracelium.compat.farmersdelight;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import se.mickelus.tetra.items.modular.ItemModularHandheld;
import vectorwing.farmersdelight.common.block.entity.CuttingBoardBlockEntity;

public class FarmersDelightEvents {
    @SubscribeEvent
    public static void onSneakPlaceTool(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        Player player = event.getEntity();
        ItemStack heldStack = player.getMainHandItem();
        BlockEntity tileEntity = level.getBlockEntity(event.getPos());
        if (player.isShiftKeyDown() && !heldStack.isEmpty() && tileEntity instanceof CuttingBoardBlockEntity cuttingBoard
                && heldStack.getItem() instanceof ItemModularHandheld
                && cuttingBoard.carveToolOnBoard(player.getAbilities().instabuild ? heldStack.copy() : heldStack)) {
            level.playSound(null, (double) pos.getX(), (double) pos.getY(), (double) pos.getZ(), SoundEvents.WOOD_BREAK, SoundSource.BLOCKS, 1.0f, 0.8f);
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.SUCCESS);
        }
    }
}
