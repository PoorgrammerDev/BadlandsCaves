package me.fullpotato.badlandscaves.WorldGeneration;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.Loot.DestroySpawner;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class StartingDungeons {
    private final BadlandsCaves plugin;
    private final Random random;

    public StartingDungeons(BadlandsCaves plugin, Random random) {
        this.plugin = plugin;
        this.random = random;
    }

    public void genSpawnDungeons () {
        if (plugin.getSystemConfig().getBoolean("starting_dungeons_finished_spawning")) return;
        final int starting_dungeons = plugin.getOptionsConfig().getInt("starting_dungeons");

        if (starting_dungeons > 0) {
            DestroySpawner dungeonGenerator = new DestroySpawner(plugin, random);
            World world = plugin.getServer().getWorld(plugin.getMainWorldName());

            int[] ran = {0};
            new BukkitRunnable() {
                @Override
                public void run() {
                    final int dungeons_spawned = plugin.getSystemConfig().getInt("starting_dungeons_spawned");
                    if (dungeons_spawned >= starting_dungeons) {
                        plugin.getServer().broadcastMessage(ChatColor.DARK_AQUA + "Starting Dungeons have been spawned.");
                        this.cancel();
                    }
                    else {
                        if (ran[0] <= dungeons_spawned) {
                            dungeonGenerator.getNewLocation(world.getSpawnLocation(), random, 1000);
                            dungeonGenerator.makeDungeon(null, random, true, true);
                            ran[0]++;
                        }
                    }
                }
            }.runTaskTimer(plugin, 0, 200);
        }

        plugin.getSystemConfig().set("starting_dungeons_finished_spawning", true);
        plugin.saveSystemConfig();
    }
}
