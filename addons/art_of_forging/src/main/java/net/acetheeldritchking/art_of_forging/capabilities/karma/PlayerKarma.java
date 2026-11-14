package net.acetheeldritchking.art_of_forging.capabilities.karma;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;

public class PlayerKarma implements INBTSerializable<CompoundTag> {
    private int karma;
    private final int MAX_KARMA = 5;
    private final int MIN_KARMA = -5;

    public int getKarma() {
        return this.karma;
    }

    public int setKarma(int set) {
        this.karma = set;
        return this.karma;
    }

    public void resetKarma() {
        this.karma = 0;
    }

    public void addKarma(int add) {
        this.karma = Math.min(this.karma + add, 5);
    }

    public void subKarma(int sub) {
        this.karma = Math.max(this.karma - sub, -5);
    }

    public void copyFrom(PlayerKarma source) {
        this.karma = source.karma;
    }

    public void saveNBTdata(CompoundTag nbt) {
        nbt.putInt("karma", this.karma);
    }

    public void loadNBTdata(CompoundTag nbt) {
        this.karma = nbt.getInt("karma");
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
