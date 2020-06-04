package me.fullpotato.badlandscaves.badlandscaves.Runnables;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.badlandscaves.Runnables.SupernaturalPowers.ReflectionStage.ZombieBossBehavior;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class Surface extends BukkitRunnable {
    private BadlandsCaves plugin;
    private World world = Bukkit.getWorld("world");
    private Random random = new Random();
    private final EntityType[] banditTypes = {
            EntityType.VINDICATOR,
            EntityType.WITCH,
            EntityType.PILLAGER,
    };
    private final EntityType[] banditTypesRare = {
            EntityType.ILLUSIONER,
            EntityType.EVOKER,
    };

    public Surface(BadlandsCaves plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        final boolean hardmode = plugin.getConfig().getBoolean("game_values.hardmode");
        final int chaos = plugin.getConfig().getInt("game_values.chaos_level");
        long time = world.getTime();
        for (Player player : world.getEntitiesByClass(Player.class)) {
            if (player.getGameMode().equals(GameMode.SURVIVAL) || player.getGameMode().equals(GameMode.ADVENTURE)) {
                Location location = player.getLocation();
                if (location.getY() > 128) {
                    if (random.nextInt(100) < (25 + (chaos / (hardmode ? 4.0 : 8.0)))) {
                        ZombieBossBehavior finder = new ZombieBossBehavior(plugin);
                        Location spawn = finder.getNearbyLocation(location, random, 20);
                        if (spawn != null && spawn.distanceSquared(player.getLocation()) > 9) {
                            world.strikeLightningEffect(spawn);

                            int mobs = hardmode ? (random.nextInt(chaos / 20) + 1) : (random.nextInt(chaos / 40) + 1);
                            if (time < 12300 || time > 23850) {
                                for (int i = 0; i < mobs; i++) {
                                    if (random.nextInt(100) < 75) {
                                        world.spawnEntity(spawn, banditTypes[random.nextInt(banditTypes.length)]);
                                    }
                                    else if (random.nextInt(100) < 75) {
                                        world.spawnEntity(spawn, banditTypes[random.nextInt(banditTypesRare.length)]);
                                    }
                                    else {
                                        Ravager ravager = (Ravager) world.spawnEntity(spawn, EntityType.RAVAGER);
                                        Entity entity = world.spawnEntity(spawn, banditTypes[random.nextInt(banditTypes.length)]);
                                        ravager.addPassenger(entity);
                                    }
                                }
                            }
                            else {
                                spawn.add(0, 30, 0);
                                for (int i = 0; i < mobs; i++) {
                                    world.spawnEntity(spawn, EntityType.PHANTOM);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
