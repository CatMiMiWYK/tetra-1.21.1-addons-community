package net.tetra.modify.api.event;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.common.NeoForge;
import se.mickelus.tetra.items.modular.IModularItem;

import java.util.function.Predicate;

public class ModularGetterProjectileEvent extends Event implements ICancellableEvent {
    public IModularItem modularItem;
    public ItemStack modularStack;
    public Player shooter;
    private int heldPriority = 0;
    private int allPriority = 0;
    private boolean isHandle = false;
    private Predicate<ItemStack> heldPredicate = null;
    private Predicate<ItemStack> allPredicate = null;

    public ModularGetterProjectileEvent(IModularItem modularItem, ItemStack stack, Player player) {
        this.modularItem = modularItem;
        this.modularStack = stack;
        this.shooter = player;
    }

    public static ItemStack onGetter(IModularItem modularItem, Player instance, ItemStack shootable, ItemStack bowStack) {
        ModularGetterProjectileEvent event = new ModularGetterProjectileEvent(modularItem, bowStack, instance);
        NeoForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            return ItemStack.EMPTY;
        }
        if (!event.isHandle()) {
            return instance.getProjectile(shootable);
        }
        Predicate<ItemStack> predicate = event.getSupportedHeldProjectiles();
        ItemStack itemstack = ProjectileWeaponItem.getHeldProjectile(instance, predicate);
        if (!itemstack.isEmpty()) {
            return CommonHooks.getProjectile(instance, bowStack, itemstack);
        }
        predicate = event.getAllSupportedProjectiles();
        for (int i = 0; i < instance.getInventory().getContainerSize(); ++i) {
            ItemStack itemstack1 = instance.getInventory().getItem(i);
            if (!predicate.test(itemstack1)) {
                continue;
            }
            return CommonHooks.getProjectile(instance, bowStack, itemstack1);
        }
        return CommonHooks.getProjectile(instance, bowStack, instance.getAbilities().instabuild ? new ItemStack(Items.ARROW) : ItemStack.EMPTY);
    }

    public boolean setHeldPredicate(int heldPriority, Predicate<ItemStack> heldPredicate) {
        if (heldPriority > this.heldPriority) {
            this.heldPredicate = heldPredicate;
            this.heldPriority = heldPriority;
            this.isHandle = true;
            return true;
        }
        return false;
    }

    public boolean setAllPredicate(int allPriority, Predicate<ItemStack> allPredicate) {
        if (allPriority > this.allPriority) {
            this.allPredicate = allPredicate;
            this.allPriority = allPriority;
            this.isHandle = true;
            return true;
        }
        return false;
    }

    public Predicate<ItemStack> getSupportedHeldProjectiles() {
        return this.heldPredicate != null ? this.heldPredicate : this.allPredicate;
    }

    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return this.allPredicate != null ? this.allPredicate : this.heldPredicate;
    }

    public boolean isHandle() {
        return this.isHandle;
    }

    public IModularItem getModularItem() {
        return this.modularItem;
    }

    public ItemStack getModularStack() {
        return this.modularStack;
    }

    public Player getShooter() {
        return this.shooter;
    }
}
