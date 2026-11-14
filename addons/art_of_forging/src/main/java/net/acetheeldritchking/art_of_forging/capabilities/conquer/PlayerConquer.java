package net.acetheeldritchking.art_of_forging.capabilities.conquer;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;

public class PlayerConquer implements INBTSerializable<CompoundTag> {
    private int conquer;
    private final int MAX_CONQUER = 5;
    private final int MIN_CONQUER = 0;

    public int getConquer() {
        return this.conquer;
    }

    public int setConquer(int set) {
        this.conquer = set;
        return this.conquer;
    }

    public void resetConquer() {
        this.conquer = 0;
    }

    public void addConquer(int add) {
        this.conquer = Math.min(this.conquer + add, 5);
    }

    public void subConquer(int sub) {
        this.conquer = Math.max(this.conquer - sub, 0);
    }

    public void copyFrom(PlayerConquer source) {
        this.conquer = source.conquer;
    }

    public void saveNBTdata(CompoundTag nbt) {
        nbt.putInt("conquer", this.conquer);
    }

    public void loadNBTdata(CompoundTag nbt) {
        this.conquer = nbt.getInt("conquer");
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
