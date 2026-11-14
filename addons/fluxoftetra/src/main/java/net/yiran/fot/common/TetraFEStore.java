package net.yiran.fot.common;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.energy.IEnergyStorage;
import se.mickelus.tetra.items.modular.IModularItem;
import se.mickelus.tetra.util.ItemStackTagHelper;

public class TetraFEStore
        implements IEnergyStorage {
    public static String KEY = "FOT$energy";
    protected ItemStack stack;
    protected int energy;
    protected int capacity = -999;
    protected int maxReceive = 20000;

    public TetraFEStore(ItemStack stack) {
        this.stack = stack;
        CompoundTag tag = ItemStackTagHelper.getOrCreateTag(stack);
        if (!stack.isEmpty() && tag.contains(KEY)) {
            this.deserializeNBT(tag.getCompound(KEY));
        }
    }

    @Override
    public boolean canReceive() {
        return true;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        int energyReceived = Math.min(this.getMaxEnergyStored() - this.energy, Math.min(this.maxReceive, maxReceive));
        if (!simulate) {
            this.setEnergyStored(this.energy + energyReceived);
        }
        return energyReceived;
    }

    @Override
    public int getEnergyStored() {
        return this.energy;
    }

    public void setEnergyStored(int energy) {
        this.energy = energy;
        this.serializeNBT();
    }

    @Override
    public int getMaxEnergyStored() {
        if (this.capacity == -999) {
            this.capacity = ((IModularItem) this.stack.getItem()).getEffectLevel(this.stack, FEItemEffects.FE_STORE);
        }
        return this.capacity;
    }

    @Override
    public boolean canExtract() {
        return true;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        int energyReceived = Math.min(this.getEnergyStored(), Math.min(this.maxReceive, maxExtract));
        if (!simulate) {
            this.setEnergyStored(this.energy - energyReceived);
        }
        return energyReceived;
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("energy", this.getEnergyStored());
        ItemStackTagHelper.mutate(this.stack, root -> root.put(KEY, tag));
        return tag;
    }

    public void deserializeNBT(CompoundTag tag) {
        this.energy = tag.getInt("energy");
    }
}
