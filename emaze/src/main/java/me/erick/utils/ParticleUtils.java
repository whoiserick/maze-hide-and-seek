package me.erick.utils;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;

public class ParticleUtils {
    public static void spawnCircle(Location center, Effect effect, int amount) {
        World world = center.getWorld();
        for (int i = 0; i < amount; i++) {
            double angle = 2 * Math.PI * i / amount;
            double x = Math.cos(angle) * 0.7;
            double z = Math.sin(angle) * 0.7;
            world.playEffect(
                    center.clone().add(x, 0.5, z),
                    effect,
                    0
            );
        }
    }
}