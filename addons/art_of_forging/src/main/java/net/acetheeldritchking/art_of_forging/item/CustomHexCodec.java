package net.acetheeldritchking.art_of_forging.item;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.PrimitiveCodec;
import se.mickelus.mutil.util.HexCodec;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class CustomHexCodec
        implements PrimitiveCodec<Integer> {
    public static final HexCodec instance = new HexCodec();

    public <T> DataResult<Integer> read(DynamicOps<T> ops, T input) {
        return ops.getStringValue(input).map(val -> (int) Long.parseLong(val, 16));
    }

    public <T> T write(DynamicOps<T> ops, Integer value) {
        return ops.createString(Integer.toHexString(value));
    }

    public String toString() {
        return "mutil-hex";
    }
}
