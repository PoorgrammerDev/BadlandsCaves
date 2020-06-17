package me.fullpotato.badlandscaves.Extraterrestrial.Hazards;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.SupernaturalPowers.ReflectionStage.ZombieBossBehavior;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Random;

public class MeteorShowerRunnable extends BukkitRunnable{
    private final BadlandsCaves plugin;
    private final EnvironmentalHazards planets;
    private final ZombieBossBehavior locationFinder;
    private final Random random = new Random();

    public MeteorShowerRunnable(BadlandsCaves plugin) {
        this.plugin = plugin;
        this.planets = new EnvironmentalHazards(plugin);
        this.locationFinder = new ZombieBossBehavior(plugin);
    }


    @Override
    public void run() {
        final int chaos = plugin.getConfig().getInt("system.chaos_level");
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            World world = player.getWorld();
            if (planets.isPlanet(world) && planets.hasHazard(world, EnvironmentalHazards.Hazard.METEOR_SHOWERS)) {
                if (player.getGameMode().equals(GameMode.SURVIVAL) || player.getGameMode().equals(GameMode.ADVENTURE)) {
                    for (int i = 0; i < (chaos / 10) + 1; i++) {
                        if (random.nextInt(100) < Math.max(chaos / 1.25, 25)) {
                            Location nearby = locationFinder.getNearbyLocation(player.getLocation(), random, 5);
                            if (nearby != null) {
                                nearby.setY(255);

                                if (random.nextInt(100) < Math.max(chaos / 2, 10) && random.nextInt(255) < player.getLocation().getY()) {
                                    nearby.setDirection(player.getLocation().subtract(nearby.clone()).toVector().multiply(5));
                                }
                                else {
                                    nearby.setDirection(new Vector(random.nextDouble() / 2.0, -1, random.nextDouble() / 2.0).normalize().multiply(2));
                                }

                                spawnMeteor(nearby);
                            }
                        }
                    }
                }
            }
        }
    }

    public void spawnMeteor(Location location) {
        Location clone = location.clone();
        Vector dir = clone.getDirection();
        World world = clone.getWorld();
        if (world != null) {
            final int limit = 1000;
            int[] times = {0};

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!clone.getBlock().isPassable() || times[0] > limit) {
                        world.createExplosion(clone, 5, false, false);
                        this.cancel();
                    }
                    else {
                        world.spawnParticle(Particle.LAVA, clone, 2, 0, 0, 0, 0);
                        clone.add(dir);
                        times[0]++;
                    }
                }
            }.runTaskTimer(plugin, 0, 0);
        }
    }
}
