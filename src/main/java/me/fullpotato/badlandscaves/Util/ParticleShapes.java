package me.fullpotato.badlandscaves.Util;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class ParticleShapes {
    public static void particleLine(Player player, Particle particle, Location origin, Location target, double extra, Object options, double line_scale) {
        if (origin != null && target != null) {
            if (origin.getWorld() != null && target.getWorld() != null && origin.getWorld().equals(target.getWorld())) {
                final World world = origin.getWorld();
                final Vector target_vector = target.toVector();
                origin.setDirection(target_vector.subtract(origin.toVector()));
                Vector incr = origin.getDirection();
                if (line_scale != 1) incr.multiply(line_scale);
                for (int i = 0; i < 100; i++) {
                    final Location scout = origin.add(incr);
                    if (scout.distanceSquared(target) < 2) {
                        break;
                    }
                    else {
                        findCorrectParticleMethod(player, world, particle, scout, extra, options);
                    }
                }
            }
        }
    }

    public static void particleSphere (Player player, Particle particle, Location location, double radius, double extra, Object options) {
        if (location.getWorld() != null) {
            final World world = location.getWorld();
            double phi = 0;
            while (phi <= Math.PI) {
                phi += Math.PI / (radius * 2);

                for (double theta = 0; theta <= 2 * Math.PI; theta += Math.PI / (radius * 4)) {
                    double x = radius * Math.cos(theta) * Math.sin(phi);
                    double y = radius * Math.cos(phi) + 1.5;
                    double z = radius * Math.sin(theta) * Math.sin(phi);

                    location.add(x, y, z);
                    findCorrectParticleMethod(player, world, particle, location, extra, options);
                    location.subtract(x, y, z);

                }
            }
        }
    }

    public static void particleCircle(Player player, Particle particle, Location location, double radius, double extra, Object options) {
        for (double theta = 0; theta <= 2*Math.PI; theta += Math.PI / 5) {
            double x = (radius / 2) * Math.cos(theta);
            double z = (radius / 2) * Math.sin(theta);

            location.add(x, 0, z);
            findCorrectParticleMethod(player, location.getWorld(), particle, location, extra, options);
            location.subtract(x, 0, z);
        }
    }

    private static void findCorrectParticleMethod (Player player, World world, Particle particle, Location scout, double extra, Object options) {
        if (options != null) {
            if (extra != -1) {
                if (player == null) {
                    world.spawnParticle(particle, scout, 1, 0, 0, 0, extra, options);
                }
                else {
                    player.spawnParticle(particle, scout, 1, 0, 0, 0, extra, options);
                }
            }
            else {
                if (player == null) {
                    world.spawnParticle(particle, scout, 1, 0, 0, 0, options);
                }
                else {
                    player.spawnParticle(particle, scout, 1, 0, 0, 0, options);
                }
            }
        }
        else {
            if (extra != -1) {
                if (player == null) {
                    world.spawnParticle(particle, scout, 1, 0, 0, 0, extra);
                }
                else {
                    player.spawnParticle(particle, scout, 1, 0, 0, 0, extra);
                }
            }
            else {
                if (player == null) {
                    world.spawnParticle(particle, scout, 1, 0, 0, 0);
                }
                else {
                    player.spawnParticle(particle, scout, 1, 0, 0, 0);
                }
            }
        }
    }
}
