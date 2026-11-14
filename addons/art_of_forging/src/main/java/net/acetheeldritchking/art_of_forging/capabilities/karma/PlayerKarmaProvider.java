package net.acetheeldritchking.art_of_forging.capabilities.karma;

import net.acetheeldritchking.art_of_forging.events.AoFEvents;
import net.neoforged.neoforge.attachment.AttachmentType;

import java.util.function.Supplier;

public class PlayerKarmaProvider {
    public static final Supplier<AttachmentType<PlayerKarma>> PLAYER_KARMA = AoFEvents.KARMA;
}
