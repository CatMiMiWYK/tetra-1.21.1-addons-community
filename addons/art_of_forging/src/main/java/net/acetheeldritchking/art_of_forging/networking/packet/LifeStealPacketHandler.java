package net.acetheeldritchking.art_of_forging.networking.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.DustColorTransitionOptions;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public class LifeStealPacketHandler {
    public static void doLifestealParticles(double xPos, double yPos, double zPos) {
        ClientLevel world = Minecraft.getInstance().level;
        if (world != null) {
            double radius = 0.7;
            double angleIncrement = 2.0 * Math.toRadians(0.5 / radius);
            int speedFactor = 10;
            for (double angle = 0.0; angle < Math.PI * 2; angle += angleIncrement) {
                double offsetX = radius * Math.sin(angle);
                double offsetZ = radius * Math.cos(angle);
                double targetX = xPos + offsetX;
                double targetY = yPos;
                double targetZ = zPos + offsetZ;
                world.addParticle(new DustColorTransitionOptions(new Vector3f(0.78f, 0.18f, 0.18f), new Vector3f(0.0f, 0.0f, 0.0f), 1.0f), targetX, targetY, targetZ, offsetX * (double) speedFactor, 0.15, offsetZ * (double) speedFactor);
            }
        }
    }
}
