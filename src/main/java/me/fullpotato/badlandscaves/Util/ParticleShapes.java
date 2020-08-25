package me.fullpotato.badlandscaves.Util;

import me.fullpotato.badlandscaves.BadlandsCaves;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class ParticleShapes {
    private final BadlandsCaves plugin;

    public ParticleShapes(BadlandsCaves plugin) {
        this.plugin = plugin;
    }

    public void line(Player player, Particle particle, Location origin, Location target, double extra, Object options, double line_scale) {
        if (origin != null && target != null) {
            final Location originClone = origin.clone();
            final Location targetClone = target.clone();

            if (originClone.getWorld() != null && targetClone.getWorld() != null && originClone.getWorld().equals(targetClone.getWorld())) {
                final World world = originClone.getWorld();
                final Vector target_vector = targetClone.toVector();
                originClone.setDirection(target_vector.subtract(originClone.toVector()));
                Vector incr = originClone.getDirection();
                if (line_scale != 1) incr.multiply(line_scale);
                for (int i = 0; i < 100; i++) {
                    final Location scout = originClone.add(incr);
                    if (scout.distanceSquared(targetClone) < 2) {
                        break;
                    }
                    else {
                        findCorrectParticleMethod(player, world, particle, scout, extra, options);
                    }
                }
            }
        }
    }

    public void lineDelayed(Player player, Particle particle, Location origin, Location target, double extra, Object options, double line_scale, long delay) {
        if (origin != null && target != null) {
            final Location originClone = origin.clone();
            final Location targetClone = target.clone();

            if (originClone.getWorld() != null && targetClone.getWorld() != null && originClone.getWorld().equals(targetClone.getWorld())) {
                final World world = originClone.getWorld();
                final Vector target_vector = targetClone.toVector();
                originClone.setDirection(target_vector.subtract(originClone.toVector()));
                Vector incr = originClone.getDirection();
                if (line_scale != 1) incr.multiply(line_scale);

                int[] i = {0};
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (i[0] > 100) this.cancel();
                        else {
                            i[0]++;
                            final Location scout = originClone.add(incr);
                            if (scout.distanceSquared(targetClone) < 2) {
                                this.cancel();
                            }
                            else {
                                findCorrectParticleMethod(player, world, particle, scout, extra, options);
                            }
                        }
                    }
                }.runTaskTimerAsynchronously(plugin, 0, delay);

            }
        }
    }

    public void sphere(Player player, Particle particle, Location location, double radius, double extra, Object options) {
        final Location clone = location.clone();
        if (clone.getWorld() != null) {
            final World world = clone.getWorld();
            double phi = 0;
            while (phi <= Math.PI) {
                phi += Math.PI / (radius * 2);

                for (double theta = 0; theta <= 2 * Math.PI; theta += Math.PI / (radius * 4)) {
                    double x = radius * Math.cos(theta) * Math.sin(phi);
                    double y = radius * Math.cos(phi) + 1.5;
                    double z = radius * Math.sin(theta) * Math.sin(phi);

                    clone.add(x, y, z);
                    findCorrectParticleMethod(player, world, particle, clone, extra, options);
                    clone.subtract(x, y, z);

                }
            }
        }
    }

    public void sphereDelayed(Player player, Particle particle, Location location, double radius, double extra, Object options, long delay, boolean reverse) {
        final Location clone = location.clone();
        if (clone.getWorld() != null) {
            final World world = clone.getWorld();
            double[] phi = {reverse ? Math.PI : 0};

            new BukkitRunnable() {
                @Override
                public void run() {
                    if ((!reverse && phi[0] > Math.PI) || (reverse && phi[0] < 0)) {
                        this.cancel();
                    }
                    else {
                        if (reverse) {
                            phi[0] -= Math.PI / (radius * 2);
                        }
                        else {
                            phi[0] += Math.PI / (radius * 2);
                        }

                        for (double theta = 0; theta <= 2 * Math.PI; theta += Math.PI / (radius * 4)) {
                            double x = radius * Math.cos(theta) * Math.sin(phi[0]);
                            double y = radius * Math.cos(phi[0]) + 1.5;
                            double z = radius * Math.sin(theta) * Math.sin(phi[0]);

                            clone.add(x, y, z);
                            findCorrectParticleMethod(player, world, particle, clone, extra, options);
                            clone.subtract(x, y, z);

                        }
                    }
                }
            }.runTaskTimerAsynchronously(plugin, 0, delay);
        }
    }

    public void circle(Player player, Particle particle, Location location, double radius, double extra, Object options) {
        final Location clone = location.clone();
        for (double theta = 0; theta <= 2*Math.PI; theta += Math.PI / 5) {
            double x = (radius / 2) * Math.cos(theta);
            double z = (radius / 2) * Math.sin(theta);

            clone.add(x, 0, z);
            findCorrectParticleMethod(player, clone.getWorld(), particle, clone, extra, options);
            clone.subtract(x, 0, z);
        }
    }

    private void findCorrectParticleMethod (Player player, World world, Particle particle, Location scout, double extra, Object options) {
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
