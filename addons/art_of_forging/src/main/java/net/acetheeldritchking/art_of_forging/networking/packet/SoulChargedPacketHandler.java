package net.acetheeldritchking.art_of_forging.networking.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SoulChargedPacketHandler {
    public static void doSoulParticles(double xPos, double yPos, double zPos) {
        ClientLevel world = Minecraft.getInstance().level;
        if (world != null) {
            double radius = 2.0;
            double angleIncrement = 2.0 * Math.toRadians(0.5 / radius);
            int speedFactor = -10;
            for (double angle = 0.0; angle < Math.PI * 2; angle += angleIncrement) {
                double offsetX = radius * Math.sin(angle);
                double offsetZ = radius * Math.cos(angle);
                double targetX = xPos + offsetX;
                double targetY = yPos;
                double targetZ = zPos + offsetZ;
                world.addParticle(ParticleTypes.SOUL_FIRE_FLAME, targetX, targetY, targetZ, offsetX * (double) speedFactor, 0.15, offsetZ * (double) speedFactor);
            }
        }
    }
}
