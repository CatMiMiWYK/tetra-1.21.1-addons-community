package net.acetheeldritchking.art_of_forging.capabilities.soulCharge;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;

public class PlayerSoulCharge implements INBTSerializable<CompoundTag> {
    private int soul_charge;
    private final int MAX_CHARGE = 5;
    private final int MIN_CHARGE = 0;

    public int getSoulCharge() {
        return this.soul_charge;
    }

    public int setSoulCharge(int set) {
        this.soul_charge = set;
        return this.soul_charge;
    }

    public void resetSoulCharge() {
        this.soul_charge = 0;
    }

    public void addSoulCharge(int add) {
        this.soul_charge = Math.min(this.soul_charge + add, 5);
    }

    public void subSoulCharge(int sub) {
        this.soul_charge = Math.max(this.soul_charge + sub, 0);
    }

    public void copyFrom(PlayerSoulCharge source) {
        this.soul_charge = source.soul_charge;
    }

    public void saveNBTdata(CompoundTag nbt) {
        nbt.putInt("soul_charge", this.soul_charge);
    }

    public void loadNBTdata(CompoundTag nbt) {
        this.soul_charge = nbt.getInt("soul_charge");
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
