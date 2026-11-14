package net.acetheeldritchking.art_of_forging.capabilities.soulCharge;

import net.acetheeldritchking.art_of_forging.events.AoFEvents;
import net.neoforged.neoforge.attachment.AttachmentType;

import java.util.function.Supplier;

public class PlayerSoulChargeProvider {
    public static final Supplier<AttachmentType<PlayerSoulCharge>> PLAYER_SOUL_CHARGE = AoFEvents.SOUL_CHARGE;
}
