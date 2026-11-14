package net.acetheeldritchking.art_of_forging.capabilities.subjugation;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;

public class PlayerSubjugation implements INBTSerializable<CompoundTag> {
    private int subjugation;
    private final int MAX_SUBJUGATION = 10;
    private final int MIN_SUBJUGATION = 0;

    public int getSubjugation() {
        return this.subjugation;
    }

    public int setSubjugation(int set) {
        this.subjugation = set;
        return this.subjugation;
    }

    public void resetSubjugation() {
        this.subjugation = 0;
    }

    public void addSubjugation(int add) {
        this.subjugation = Math.min(this.subjugation + add, 10);
    }

    public void subSubjugation(int sub) {
        this.subjugation = Math.max(this.subjugation - sub, 0);
    }

    public void copyFrom(PlayerSubjugation source) {
        this.subjugation = source.subjugation;
    }

    public void saveNBTdata(CompoundTag nbt) {
        nbt.putInt("subjugation", this.subjugation);
    }

    public void loadNBTdata(CompoundTag nbt) {
        this.subjugation = nbt.getInt("subjugation");
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
