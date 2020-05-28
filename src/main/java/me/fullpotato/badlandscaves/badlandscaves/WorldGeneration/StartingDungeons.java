package me.fullpotato.badlandscaves.badlandscaves.WorldGeneration;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.badlandscaves.Events.Loot.DestroySpawner;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.loot.LootTables;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class StartingDungeons {
    private BadlandsCaves plugin;
    private EntityType[] mobTypes = {
        EntityType.ZOMBIE,
        EntityType.SKELETON,
        EntityType.SPIDER,
        EntityType.WITCH,
        EntityType.CREEPER,
    };


    public StartingDungeons(BadlandsCaves plugin) {
        this.plugin = plugin;
    }

    public void genSpawnDungeons () {
        if (plugin.getConfig().getBoolean("game_values.starting_dungeons_spawned")) return;
        final int starting_dungeons = plugin.getConfig().getInt("game_values.starting_dungeons");

        if (starting_dungeons > 0) {
            Random random = new Random();
            DestroySpawner dungeonGenerator = new DestroySpawner(plugin);
            World world = plugin.getServer().getWorld("world");

            int[] ran = {0};
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (ran[0] > starting_dungeons) {
                        plugin.getServer().broadcastMessage(ChatColor.DARK_AQUA.toString() + starting_dungeons + " Starting Dungeons have been spawned.");
                        this.cancel();
                    }
                    else {
                        dungeonGenerator.getNewLocation(world.getSpawnLocation(), random, 1000);
                        dungeonGenerator.makeDungeon(mobTypes[random.nextInt(mobTypes.length)], random, true);
                        ran[0]++;
                    }
                }
            }.runTaskTimer(plugin, 0, 200);
        }

        plugin.getConfig().set("game_values.starting_dungeons_spawned", true);
        plugin.saveConfig();
    }
}
