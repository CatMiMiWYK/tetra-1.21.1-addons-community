/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.FriendlyByteBuf
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 *  se.mickelus.mutil.data.AbstractUpdateDataPacket
 */
package com.hp.tetra_data_kubejs.mixin;

import net.minecraft.network.FriendlyByteBuf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import se.mickelus.mutil.data.AbstractUpdateDataPacket;

@Mixin(value={AbstractUpdateDataPacket.class}, remap=false)
public abstract class AbstractUpdateDataPacketMixin {
    private static final int TETRA_DATA_KUBEJS_MAX_UTF_LENGTH = 0x100000;

    @Redirect(method={"toBytes"}, at=@At(value="INVOKE", target="Lnet/minecraft/network/FriendlyByteBuf;writeUtf(Ljava/lang/String;)Lnet/minecraft/network/FriendlyByteBuf;", remap=true), remap=false)
    private FriendlyByteBuf tetraDataKubejs$writeLargeDirectory(FriendlyByteBuf buffer, String value) {
        return buffer.writeUtf(value, 0x100000);
    }

    @Redirect(method={"lambda$toBytes$1"}, at=@At(value="INVOKE", target="Lnet/minecraft/network/FriendlyByteBuf;writeUtf(Ljava/lang/String;)Lnet/minecraft/network/FriendlyByteBuf;", remap=true), remap=false)
    private static FriendlyByteBuf tetraDataKubejs$writeLargeData(FriendlyByteBuf buffer, String value) {
        return buffer.writeUtf(value, 0x100000);
    }

    @Redirect(method={"fromBytes"}, at=@At(value="INVOKE", target="Lnet/minecraft/network/FriendlyByteBuf;readUtf()Ljava/lang/String;", ordinal=0, remap=true), remap=false)
    private String tetraDataKubejs$readLargeDirectory(FriendlyByteBuf buffer) {
        return buffer.readUtf(0x100000);
    }

    @Redirect(method={"fromBytes"}, at=@At(value="INVOKE", target="Lnet/minecraft/network/FriendlyByteBuf;readUtf()Ljava/lang/String;", ordinal=1, remap=true), remap=false)
    private String tetraDataKubejs$readLargeData(FriendlyByteBuf buffer) {
        return buffer.readUtf(0x100000);
    }
}

