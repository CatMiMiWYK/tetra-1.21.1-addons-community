package net.acetheeldritchking.art_of_forging.capabilities.subjugation;

import net.acetheeldritchking.art_of_forging.events.AoFEvents;
import net.neoforged.neoforge.attachment.AttachmentType;

import java.util.function.Supplier;

public class PlayerSubjugationProvider {
    public static final Supplier<AttachmentType<PlayerSubjugation>> PLAYER_SUBJUGATION = AoFEvents.SUBJUGATION;
}
