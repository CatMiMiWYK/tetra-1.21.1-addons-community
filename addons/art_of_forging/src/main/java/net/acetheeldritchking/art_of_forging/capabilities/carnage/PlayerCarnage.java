package net.acetheeldritchking.art_of_forging.capabilities.carnage;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;

public class PlayerCarnage implements INBTSerializable<CompoundTag> {
    private int carnage;
    private final int MAX_CARNAGE = 5;
    private final int MIN_CARNAGE = 0;

    public int getCarnage() {
        return this.carnage;
    }

    public int setCarnage(int set) {
        this.carnage = set;
        return this.carnage;
    }

    public void resetCarnage() {
        this.carnage = 0;
    }

    public void addCarnage(int add) {
        this.carnage = Math.min(this.carnage + add, 5);
    }

    public void subCarnage(int sub) {
        this.carnage = Math.max(this.carnage - sub, 0);
    }

    public void copyFrom(PlayerCarnage source) {
        this.carnage = source.carnage;
    }

    public void saveNBTdata(CompoundTag nbt) {
        nbt.putInt("carnage", this.carnage);
    }

    public void loadNBTdata(CompoundTag nbt) {
        this.carnage = nbt.getInt("carnage");
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag nbt = new CompoundTag();
        this.saveNBTdata(nbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        this.loadNBTdata(nbt);
    }
}
