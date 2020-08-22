package me.fullpotato.badlandscaves.Other;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.SupernaturalPowers.ReflectionStage.ZombieBossBehavior;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.Random;

public class Surface extends BukkitRunnable {
    private final BadlandsCaves plugin;
    private final World world;
    private final Random random;
    private final int surfaceLayer;
    private final ZombieBossBehavior finder;
    private final EntityType[] banditTypes = {
            EntityType.VINDICATOR,
            EntityType.WITCH,
            EntityType.PILLAGER,
    };
    private final EntityType[] banditTypesRare = {
            EntityType.ILLUSIONER,
            EntityType.EVOKER,
    };
    private static final int CHECK_RANGE = 20;

    public Surface(BadlandsCaves plugin, Random random) {
        this.plugin = plugin;
        world = plugin.getServer().getWorld(plugin.getMainWorldName());
        surfaceLayer = plugin.getOptionsConfig().getInt("surface_layer");
        finder = new ZombieBossBehavior(plugin, random);
        this.random = random;
    }

    @Override
    public void run() {
        final boolean hardmode = plugin.getSystemConfig().getBoolean("hardmode");
        final int chaos = plugin.getSystemConfig().getInt("chaos_level");
        long time = world.getTime();
        for (Player player : world.getEntitiesByClass(Player.class)) {
            if (player.getGameMode().equals(GameMode.SURVIVAL) || player.getGameMode().equals(GameMode.ADVENTURE)) {
                Location location = player.getLocation();
                if (location.getY() > surfaceLayer) {
                    if (random.nextInt(100) < (25 + (chaos / (hardmode ? 4.0 : 8.0)))) {
                        final Collection<Entity> nearbyMonsters = world.getNearbyEntities(player.getLocation(), CHECK_RANGE, CHECK_RANGE, CHECK_RANGE, entity -> (entity instanceof Monster || entity instanceof Phantom));
                        final int maxEntities = (chaos / 4) + 15;
                        if (nearbyMonsters.size() < maxEntities) {
                            Location spawn = finder.getNearbyLocation(location, random, 20);
                            if (spawn != null && spawn.distanceSquared(player.getLocation()) > 9) {
                                world.strikeLightningEffect(spawn);

                                if (time < 12300 || time > 23850) {
                                    int mobs = hardmode ? (random.nextInt((chaos / 20) + 1) + 1) : (random.nextInt((chaos / 40) + 1) + 1);
                                    for (int i = 0; i < mobs; i++) {
                                        if (random.nextInt(100) < 75) {
                                            world.spawnEntity(spawn, banditTypes[random.nextInt(banditTypes.length)]);
                                        }
                                        else if (random.nextInt(100) < 75) {
                                            world.spawnEntity(spawn, banditTypesRare[random.nextInt(banditTypesRare.length)]);
                                        }
                                        else {
                                            Ravager ravager = (Ravager) world.spawnEntity(spawn, EntityType.RAVAGER);
                                            Entity entity = world.spawnEntity(spawn, banditTypes[random.nextInt(banditTypes.length)]);
                                            ravager.addPassenger(entity);
                                        }
                                    }
                                }
                                else {
                                    int mobs = hardmode ? (random.nextInt((chaos / 15) + 1) + 1) : (random.nextInt((chaos / 30) + 1) + 1);
                                    for (int i = 0; i < mobs; i++) {
                                        if (random.nextInt(100) < 75) {
                                            world.spawnEntity(spawn.clone().add(0, 10, 0), EntityType.PHANTOM);
                                        }
                                        else {
                                            world.spawnEntity(spawn, EntityType.ZOMBIE);
                                        }

                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
