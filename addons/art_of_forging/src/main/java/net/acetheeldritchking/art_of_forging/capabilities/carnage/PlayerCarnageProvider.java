package net.acetheeldritchking.art_of_forging.capabilities.carnage;

import net.acetheeldritchking.art_of_forging.events.AoFEvents;
import net.neoforged.neoforge.attachment.AttachmentType;

import java.util.function.Supplier;

public class PlayerCarnageProvider {
    public static final Supplier<AttachmentType<PlayerCarnage>> PLAYER_CARNAGE = AoFEvents.CARNAGE;
}
