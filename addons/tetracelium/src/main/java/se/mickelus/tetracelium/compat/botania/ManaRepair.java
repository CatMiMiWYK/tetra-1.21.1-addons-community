package se.mickelus.tetracelium.compat.botania;

import com.google.common.collect.Streams;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import se.mickelus.tetra.blocks.workbench.gui.WorkbenchStatsGui;
import se.mickelus.tetra.effect.EffectHelper;
import se.mickelus.tetra.effect.ItemEffect;
import se.mickelus.tetra.event.ModularItemDamageEvent;
import se.mickelus.tetra.gui.stats.bar.GuiStatBar;
import se.mickelus.tetra.gui.stats.getter.IStatGetter;
import se.mickelus.tetra.gui.stats.getter.LabelGetterBasic;
import se.mickelus.tetra.gui.stats.getter.TooltipGetterInteger;
import se.mickelus.tetra.items.modular.IModularItem;
import se.mickelus.tetra.items.modular.impl.holo.gui.craft.HoloStatsGui;
import vazkii.botania.api.mana.ManaItemHandler;

@ParametersAreNonnullByDefault
public class ManaRepair {
    public static ItemEffect effect = ItemEffect.get("manaRepair");

    @OnlyIn(value = Dist.CLIENT)
    public static void clientInit() {
        ManaRepairStatGetter statGetter = new ManaRepairStatGetter();
        GuiStatBar statBar = new GuiStatBar(0, 0, 59, "tetra.stats.manaRepair", 0.0, 400.0, false, false, true, statGetter, LabelGetterBasic.integerLabelInverted, new TooltipGetterInteger("tetra.stats.manaRepair.tooltip", statGetter));
        WorkbenchStatsGui.addBar(statBar);
        HoloStatsGui.addBar(statBar);
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (!player.level().isClientSide && player.level().getGameTime() % 20L == 0L) {
            Inventory inventory = player.getInventory();
            Streams.concat(inventory.items.stream(), inventory.armor.stream(), inventory.offhand.stream())
                    .filter(itemStack -> !itemStack.isEmpty())
                    .filter(itemStack -> itemStack.getItem() instanceof IModularItem)
                    .forEach(itemStack -> itemInventoryTick(itemStack, player));
        }
    }

    public static void itemInventoryTick(ItemStack itemStack, Entity entity) {
        float durabilityPerMana = EffectHelper.getEffectEfficiency(itemStack, effect);
        if (durabilityPerMana > 0.0f && itemStack.getDamageValue() > 0 && entity instanceof Player player) {
            int damage = itemStack.getDamageValue();
            int repairAmount = requestManaForDurability(player, itemStack, damage, durabilityPerMana);
            if (repairAmount > 0) {
                itemStack.setDamageValue(damage - repairAmount);
                player.getInventory().setChanged();
            }
        }
    }

    @SubscribeEvent
    public static void onModularItemDamage(ModularItemDamageEvent event) {
        event.setAmount(reduceDurabilityDamage(event.getUsingEntity(), event.getItemStack(), event.getAmount()));
    }

    public static int reduceDurabilityDamage(@Nullable Entity entity, ItemStack itemStack, int amount) {
        float durabilityPerMana = EffectHelper.getEffectEfficiency(itemStack, effect);
        if (durabilityPerMana > 0.0f && entity instanceof Player player) {
            int result = requestManaForDurability(player, itemStack, amount, durabilityPerMana);
            return amount - result;
        }
        return amount;
    }

    private static int requestManaForDurability(Player player, ItemStack itemStack, int damage, float durabilityPerMana) {
        int grantedMana = ManaItemHandler.instance().requestManaForTool(itemStack, player, Mth.ceil((float) damage / durabilityPerMana), true);
        return Math.min((int) (durabilityPerMana * (float) grantedMana), damage);
    }
}
