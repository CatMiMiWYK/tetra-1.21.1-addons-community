package se.mickelus.tetracelium.compat.twilightforest.effects;

import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import se.mickelus.tetra.blocks.workbench.gui.WorkbenchStatsGui;
import se.mickelus.tetra.effect.EffectHelper;
import se.mickelus.tetra.effect.ItemEffect;
import se.mickelus.tetra.gui.stats.StatsHelper;
import se.mickelus.tetra.gui.stats.bar.GuiStatBar;
import se.mickelus.tetra.gui.stats.getter.IStatFormat;
import se.mickelus.tetra.gui.stats.getter.IStatGetter;
import se.mickelus.tetra.gui.stats.getter.LabelGetterBasic;
import se.mickelus.tetra.gui.stats.getter.StatFormat;
import se.mickelus.tetra.gui.stats.getter.StatGetterCooldown;
import se.mickelus.tetra.gui.stats.getter.StatGetterEffectEfficiency;
import se.mickelus.tetra.gui.stats.getter.StatGetterEffectLevel;
import se.mickelus.tetra.gui.stats.getter.TooltipGetterMultiValue;
import se.mickelus.tetra.items.modular.ItemModularHandheld;
import se.mickelus.tetra.items.modular.impl.holo.gui.craft.HoloStatsGui;
import se.mickelus.tetracelium.TetraceliumMod;
import twilightforest.entity.projectile.TwilightWandBolt;
import twilightforest.init.TFSounds;

public class TwilightBoltEffect {
    public static final ItemEffect twilightBoltEffect = ItemEffect.get("twilightBolt");

    @OnlyIn(value = Dist.CLIENT)
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onClickInput(InputEvent.InteractionKeyMappingTriggered event) {
        Minecraft mc = Minecraft.getInstance();
        ItemStack itemStack = mc.player.getMainHandItem();
        if (event.isAttack() && !event.isCanceled() && itemStack.getItem() instanceof ItemModularHandheld && mc.hitResult != null && HitResult.Type.MISS.equals(mc.hitResult.getType()) && canTrigger(mc.player, itemStack)) {
            sendPacket();
        }
    }

    static boolean canTrigger(Player player, ItemStack itemStack) {
        return player.getAttackStrengthScale(0.5f) > 0.9f && EffectHelper.getEffectLevel(itemStack, twilightBoltEffect) > 0 && !player.getCooldowns().isOnCooldown(itemStack.getItem());
    }

    public static void sendPacket() {
        TetraceliumMod.packetHandler.sendToServer(new TwilightBoltPacket());
    }

    public static void handleServer(Player player) {
        ItemStack itemStack = player.getMainHandItem();
        if (canTrigger(player, itemStack)) {
            fireProjectile(player, player.level(), itemStack);
        }
    }

    static void fireProjectile(Player player, Level level, ItemStack itemStack) {
        player.playSound(TFSounds.TWILIGHT_SCEPTER_USE.get(), 1.0f, (level.random.nextFloat() - level.random.nextFloat()) * 0.2f + 1.0f);
        level.addFreshEntity(new TwilightWandBolt(level, player));
        itemStack.hurtAndBreak((int) Math.ceil((float) itemStack.getMaxDamage() * 0.01f), (ServerLevel) level, null, item -> {
        });
        player.getCooldowns().addCooldown(itemStack.getItem(), (int) Math.ceil(((ItemModularHandheld) itemStack.getItem()).getCooldownBase(itemStack) * 4.0 * 20.0));
    }

    @OnlyIn(value = Dist.CLIENT)
    public static void clientInit() {
        StatGetterEffectLevel statGetter = new StatGetterEffectLevel(twilightBoltEffect, 1.0);
        StatGetterEffectEfficiency efficiencyGetter = new StatGetterEffectEfficiency(twilightBoltEffect, 1.0);
        StatGetterCooldown cooldownGetter = new StatGetterCooldown(0.0, 4.0);
        GuiStatBar statBar = new GuiStatBar(0, 0, 59, "tetra.stats.twilightBolt", 0.0, 1.0, false, false, false, statGetter, LabelGetterBasic.noLabel, new TooltipGetterMultiValue("tetra.stats.twilightBolt.tooltip", StatsHelper.withStats(new IStatGetter[]{efficiencyGetter, cooldownGetter}), StatsHelper.withFormat(new IStatFormat[]{StatFormat.noDecimal, StatFormat.oneDecimal})));
        WorkbenchStatsGui.addBar(statBar);
        HoloStatsGui.addBar(statBar);
    }
}
