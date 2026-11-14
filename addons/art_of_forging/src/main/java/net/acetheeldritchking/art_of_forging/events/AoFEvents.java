package net.acetheeldritchking.art_of_forging.events;

import net.acetheeldritchking.art_of_forging.ArtOfForging;
import net.acetheeldritchking.art_of_forging.capabilities.carnage.PlayerCarnage;
import net.acetheeldritchking.art_of_forging.capabilities.conquer.PlayerConquer;
import net.acetheeldritchking.art_of_forging.capabilities.devouring.PlayerDevouring;
import net.acetheeldritchking.art_of_forging.capabilities.karma.PlayerKarma;
import net.acetheeldritchking.art_of_forging.capabilities.soulCharge.PlayerSoulCharge;
import net.acetheeldritchking.art_of_forging.capabilities.subjugation.PlayerSubjugation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

/**
 * Registers the per-player state that used to be exposed through the Forge capability system.
 * NeoForge 1.21 uses data attachments instead; they are serialized to NBT and copied on death.
 */
public class AoFEvents {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, ArtOfForging.MOD_ID);

    private static <T extends INBTSerializable<net.minecraft.nbt.CompoundTag>> Supplier<AttachmentType<T>> register(String name, Supplier<T> factory) {
        return ATTACHMENT_TYPES.register(name, () -> AttachmentType.serializable(factory).copyOnDeath().build());
    }

    public static final Supplier<AttachmentType<PlayerDevouring>> DEVOURING = register("devouring", PlayerDevouring::new);
    public static final Supplier<AttachmentType<PlayerCarnage>> CARNAGE = register("carnage", PlayerCarnage::new);
    public static final Supplier<AttachmentType<PlayerSoulCharge>> SOUL_CHARGE = register("soul_charge", PlayerSoulCharge::new);
    public static final Supplier<AttachmentType<PlayerKarma>> KARMA = register("karma", PlayerKarma::new);
    public static final Supplier<AttachmentType<PlayerConquer>> CONQUER = register("conquer", PlayerConquer::new);
    public static final Supplier<AttachmentType<PlayerSubjugation>> SUBJUGATION = register("subjugation", PlayerSubjugation::new);

    public static void register(IEventBus eventBus) {
        ATTACHMENT_TYPES.register(eventBus);
    }
}
