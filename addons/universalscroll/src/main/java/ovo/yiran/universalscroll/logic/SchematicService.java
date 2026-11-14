package ovo.yiran.universalscroll.logic;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.BlockPos;
import ovo.yiran.universalscroll.UniversalScroll;
import ovo.yiran.universalscroll.UniversalScrollItem;
import se.mickelus.tetra.blocks.workbench.WorkbenchTile;
import se.mickelus.tetra.items.modular.IModularItem;
import se.mickelus.tetra.module.SchematicRegistry;
import se.mickelus.tetra.module.schematic.CraftingContext;
import se.mickelus.tetra.module.schematic.UpgradeSchematic;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * 服务端校验与“进入图纸”的实现。
 * <p>
 * 校验项与原版工作台列出图纸时的条件一致：
 * {@code isRelevant}、{@code isApplicableForSlot}、{@code matchesRequirements(CraftingContext)}。
 * 选中后调用 {@code WorkbenchTile#setCurrentSchematic} 并打开原版工作台界面，后续合成仍走
 * {@code WorkbenchTile#craft}，不做任何替代实现。
 */
public final class SchematicService {
    private static final double MAX_DISTANCE_SQR = 64.0;

    private SchematicService() {
    }

    public static void open(ServerPlayer player, BlockPos pos, ResourceLocation schematicId) {
        Level level = player.level();

        if (player.distanceToSqr(Vec3.atCenterOf(pos)) > MAX_DISTANCE_SQR) {
            fail(player, "message.universalscroll.too_far");
            return;
        }

        if (!(level.getBlockEntity(pos) instanceof WorkbenchTile workbench)) {
            fail(player, "message.universalscroll.no_workbench");
            return;
        }

        ItemStack held = findHeldScroll(player);
        if (held.isEmpty()) {
            fail(player, "message.universalscroll.not_holding");
            return;
        }

        UpgradeSchematic schematic = SchematicRegistry.getSchematic(schematicId);
        if (schematic == null) {
            fail(player, "message.universalscroll.unknown_schematic");
            return;
        }

        ItemStack target = workbench.getTargetItemStack();
        if (target.isEmpty() || !(target.getItem() instanceof IModularItem item)) {
            fail(player, "message.universalscroll.no_target");
            return;
        }

        String slot = findSlot(schematic, target, item);
        if (slot == null) {
            fail(player, "message.universalscroll.no_slot");
            return;
        }

        CraftingContext context = new CraftingContext(level, pos, level.getBlockState(pos), player, target, slot,
                SchematicUnlocks.withAllUnlocks(workbench.getUnlockedSchematics()));
        if (!schematic.matchesRequirements(context)) {
            fail(player, "message.universalscroll.requirements");
            return;
        }

        workbench.setCurrentSchematic(schematic, slot);
        held.set(UniversalScroll.SELECTED_SCHEMATIC.get(), schematicId);

        // 必须带 pos：WorkbenchContainer 的客户端工厂会从额外数据里读取 BlockPos，
        // 原版 AbstractWorkbenchBlock 也是 player.openMenu(tile, pos)。
        if (player.openMenu(workbench, pos).isEmpty()) {
            fail(player, "message.universalscroll.open_failed");
            return;
        }

        player.displayClientMessage(Component.translatable("message.universalscroll.opened", schematicId.toString()), true);
    }

    private static String findSlot(UpgradeSchematic schematic, ItemStack target, IModularItem item) {
        if (!schematic.isRelevant(target)) {
            return null;
        }

        return Stream.concat(Arrays.stream(item.getMajorModuleKeys(target)), Arrays.stream(item.getMinorModuleKeys(target)))
                .filter(slot -> schematic.isApplicableForSlot(slot, target))
                .findFirst()
                .orElse(null);
    }

    private static ItemStack findHeldScroll(ServerPlayer player) {
        ItemStack main = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (main.getItem() instanceof UniversalScrollItem) {
            return main;
        }

        ItemStack off = player.getItemInHand(InteractionHand.OFF_HAND);
        if (off.getItem() instanceof UniversalScrollItem) {
            return off;
        }

        return ItemStack.EMPTY;
    }

    private static void fail(ServerPlayer player, String translationKey) {
        player.displayClientMessage(Component.translatable(translationKey), true);
    }
}
