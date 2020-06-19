package me.fullpotato.badlandscaves.WorldGeneration;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.Loot.DestroySpawner;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class StartingDungeons {
    private final BadlandsCaves plugin;


    public StartingDungeons(BadlandsCaves plugin) {
        this.plugin = plugin;
    }

    public void genSpawnDungeons () {
        if (plugin.getConfig().getBoolean("system.starting_dungeons_finished_spawning")) return;
        final int starting_dungeons = plugin.getConfig().getInt("options.starting_dungeons");

        if (starting_dungeons > 0) {
            Random random = new Random();
            DestroySpawner dungeonGenerator = new DestroySpawner(plugin);
            World world = plugin.getServer().getWorld(plugin.mainWorldName);

            int[] ran = {0};
            new BukkitRunnable() {
                @Override
                public void run() {
                    final int dungeons_spawned = plugin.getConfig().getInt("system.starting_dungeons_spawned");
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

        plugin.getConfig().set("system.starting_dungeons_finished_spawning", true);
        plugin.saveConfig();
    }
}
