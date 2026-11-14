package net.tetra.modify.wrapper;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import se.mickelus.tetra.ConfigHandler;
import se.mickelus.tetra.TetraMod;
import se.mickelus.tetra.Tooltips;
import se.mickelus.tetra.items.modular.IModularItem;
import se.mickelus.tetra.module.improvement.HonePacket;
import se.mickelus.tetra.util.ItemStackTagHelper;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class IModularItemWrapper {
    public static <T> T cast(Item item) {
        return (T) item;
    }

    public static void tickHoningProgression(LivingEntity entity, ItemStack itemStack, int multiplier) {
        IModularItem item = cast(itemStack.getItem());
        if (!ConfigHandler.moduleProgression.get() || !item.canGainHoneProgress(itemStack) || IModularItem.isHoneable(itemStack)) {
            return;
        }

        int[] progress = {0};
        ItemStackTagHelper.mutate(itemStack, tag -> {
            int honingProgress = tag.contains("honing_progress") ? tag.getInt("honing_progress") : item.getHoningLimit(itemStack);
            honingProgress -= multiplier;
            progress[0] = honingProgress;
            tag.putInt("honing_progress", honingProgress);
            if (honingProgress <= 0) {
                tag.putBoolean("honing_available", true);
            }
        });

        if (progress[0] <= 0 && entity instanceof ServerPlayer serverPlayer) {
            TetraMod.packetHandler.sendTo(new HonePacket(itemStack), serverPlayer);
        }
    }

    @OnlyIn(value = Dist.CLIENT)
    public static class Client {
        public static List<Component> getTooltip(ItemStack itemStack, @Nullable Level world, TooltipFlag advanced) {
            List<Component> tooltip = new java.util.ArrayList<>();
            IModularItem item = cast(itemStack.getItem());
            if (item.isBroken(itemStack)) {
                tooltip.add(Component.translatable("item.tetra.modular.broken").withStyle(ChatFormatting.DARK_RED, ChatFormatting.ITALIC));
            }
            if (Screen.hasShiftDown()) {
                tooltip.add(Tooltips.expanded);
                Arrays.stream(item.getMajorModules(itemStack)).filter(Objects::nonNull).forEach(module -> {
                    tooltip.add(Component.literal("\u00bb ").withStyle(ChatFormatting.DARK_GRAY)
                            .append(Component.literal(module.getName(itemStack)).withStyle(ChatFormatting.GRAY)));
                    module.getEnchantmentHolders(itemStack).entrySet().stream()
                            .map(entry -> Enchantment.getFullname(entry.getKey(), entry.getValue()))
                            .map(text -> Component.literal("  - " + text.getString()))
                            .map(text -> text.withStyle(ChatFormatting.DARK_GRAY))
                            .forEach(tooltip::add);
                    Arrays.stream(module.getImprovements(itemStack))
                            .map(improvement -> "  - " + item.getImprovementTooltip(improvement.key, improvement.level, true))
                            .map(Component::literal)
                            .map(text -> text.withStyle(ChatFormatting.DARK_GRAY))
                            .forEach(tooltip::add);
                });
                Arrays.stream(item.getMinorModules(itemStack)).filter(Objects::nonNull)
                        .map(module -> Component.literal(" * ").withStyle(ChatFormatting.DARK_GRAY)
                                .append(Component.literal(module.getName(itemStack)).withStyle(ChatFormatting.GRAY)))
                        .forEach(tooltip::add);

                addHoningTooltip(item, itemStack, tooltip);
            } else {
                itemStack.getTagEnchantments().entrySet().stream()
                        .map(entry -> Enchantment.getFullname(entry.getKey(), entry.getIntValue()))
                        .forEach(tooltip::add);
                tooltip.add(Tooltips.expand);
            }

            return tooltip;
        }

        public static void addHoningTooltip(IModularItem item, ItemStack itemStack, List<Component> tooltip) {
            if (!ConfigHandler.moduleProgression.get() || !item.canGainHoneProgress(itemStack)) {
                return;
            }
            if (IModularItem.isHoneable(itemStack)) {
                tooltip.add(Component.literal(" > ").withStyle(ChatFormatting.AQUA)
                        .append(Component.translatable("tetra.hone.available").withStyle(ChatFormatting.GRAY)));
            } else {
                int honingProgress = item.getHoningProgress(itemStack);
                int honingLimit = item.getHoningLimit(itemStack);
                String percentage = String.format("%.0f", 100.0f * (float) (honingLimit - honingProgress) / (float) honingLimit);
                tooltip.add(Component.literal(" > ").withStyle(ChatFormatting.DARK_AQUA)
                        .append(Component.translatable("tetra.hone.progress", honingLimit - honingProgress, honingLimit, percentage).withStyle(ChatFormatting.GRAY)));
            }
        }
    }
}
