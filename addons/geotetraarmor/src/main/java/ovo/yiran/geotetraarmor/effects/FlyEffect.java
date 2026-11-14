package ovo.yiran.geotetraarmor.effects;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;
import ovo.yiran.geotetraarmor.ArmorEffectUtil;
import se.mickelus.tetra.effect.ItemEffect;
import se.mickelus.tetra.items.modular.IModularItem;

@EventBusSubscriber(modid = "geotetraarmor")
public class FlyEffect {
    public static final ItemEffect FLY = ItemEffect.get("gta:fly");

    @SubscribeEvent
    public static void onLivingEquipmentChange(LivingEquipmentChangeEvent event) {
        ItemStack from = event.getFrom();
        ItemStack to = event.getTo();
        boolean fromModular = from.getItem() instanceof IModularItem;
        boolean toModular = to.getItem() instanceof IModularItem;
        if (!fromModular && !toModular) {
            return;
        }
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        CompoundTag data = player.getPersistentData();
        if (ArmorEffectUtil.getArmorTotalEffectLevel(player, FLY) > 0) {
            if (!data.contains("gta:fly")) {
                data.putBoolean("gta:fly", true);
                player.getAbilities().mayfly = true;
                player.onUpdateAbilities();
            }
        } else if (data.contains("gta:fly")) {
            data.remove("gta:fly");
            if (!player.isCreative() && !player.isSpectator()) {
                player.getAbilities().mayfly = false;
                player.getAbilities().flying = false;
                player.onUpdateAbilities();
            }
        }
    }
}
