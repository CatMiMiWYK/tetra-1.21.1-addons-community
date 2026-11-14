package net.acetheeldritchking.art_of_forging.capabilities.devouring;

import net.acetheeldritchking.art_of_forging.events.AoFEvents;
import net.neoforged.neoforge.attachment.AttachmentType;

import java.util.function.Supplier;

public class PlayerDevouringProvider {
    public static final Supplier<AttachmentType<PlayerDevouring>> PLAYER_DEVOURING = AoFEvents.DEVOURING;
}
