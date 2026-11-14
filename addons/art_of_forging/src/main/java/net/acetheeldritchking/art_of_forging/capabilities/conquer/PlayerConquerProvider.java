package net.acetheeldritchking.art_of_forging.capabilities.conquer;

import net.acetheeldritchking.art_of_forging.events.AoFEvents;
import net.neoforged.neoforge.attachment.AttachmentType;

import java.util.function.Supplier;

public class PlayerConquerProvider {
    public static final Supplier<AttachmentType<PlayerConquer>> PLAYER_CONQUER = AoFEvents.CONQUER;
}
