package net.acetheeldritchking.art_of_forging.capabilities.devouring;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;

public class PlayerDevouring implements INBTSerializable<CompoundTag> {
    private int devour;
    private final int MAX_DEVOUR = 30;
    private final int MIN_DEVOUR = 0;

    public int getDevour() {
        return this.devour;
    }

    public void addDevour(int add) {
        this.devour = Math.min(this.devour + add, 30);
    }

    public void subDevour(int sub) {
        this.devour = Math.max(this.devour - sub, 0);
    }

    public int setDevour(int set) {
        this.devour = set;
        return this.devour;
    }

    public void resetDevour() {
        this.devour = 0;
    }

    public void copyFrom(PlayerDevouring source) {
        this.devour = source.devour;
    }

    public void saveNBTdata(CompoundTag nbt) {
        nbt.putInt("devouring", this.devour);
    }

    public void loadNBTdata(CompoundTag nbt) {
        this.devour = nbt.getInt("devouring");
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
