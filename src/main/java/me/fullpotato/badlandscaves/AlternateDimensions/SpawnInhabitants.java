package me.fullpotato.badlandscaves.AlternateDimensions;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.WorldGeneration.DimensionsWorlds;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.loot.LootTables;

import java.util.Random;


public class SpawnInhabitants implements Listener {
    private final BadlandsCaves plugin;
    private final EntityType[] banditTypes = {
            EntityType.VINDICATOR,
            EntityType.PILLAGER,
    };
    private final EntityType[] banditTypesRare = {
            EntityType.WITCH,
            EntityType.ILLUSIONER,
            EntityType.EVOKER,
    };

    public SpawnInhabitants(BadlandsCaves plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void mobSpawn(CreatureSpawnEvent event) {
        LivingEntity entity = event.getEntity();
        World world = entity.getWorld();
        if (world.getName().startsWith(plugin.getDimensionPrefixName())) {

            if (plugin.getConfig().getBoolean("system.hardmode")) {
                if (event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.CUSTOM)) {
                    if (entity instanceof CaveSpider || entity instanceof WitherSkeleton) {
                        event.setCancelled(true);
                        entity.remove();
                    }
                    return;
                }

                if (entity instanceof Monster) {
                    DimensionsWorlds.Habitation habitationType = null;

                    String savedHabitatType = plugin.getConfig().getString("system.dim_stats." + world.getName() + ".habitation");
                    if (savedHabitatType != null) {
                        try {
                            habitationType = DimensionsWorlds.Habitation.valueOf(savedHabitatType.toUpperCase());
                        } catch (IllegalArgumentException e) {
                            return;
                        }
                    }

                    if (habitationType != null) {
                        final Random random = new Random();
                        final Location location = event.getLocation();
                        if (habitationType.equals(DimensionsWorlds.Habitation.INHABITED)) {
                            event.setCancelled(true);
                            entity.remove();

                            final int cap = 5;
                            int num = 0;
                            for (Entity nearby : world.getNearbyEntities(location, 20, 20, 20)) {
                                if (nearby instanceof Monster) {
                                    num++;
                                }
                            }

                            if (num < cap) {
                                Monster spawned;
                                if (random.nextInt(100) < 80) {
                                    spawned = (Monster) world.spawnEntity(location, banditTypes[random.nextInt(banditTypes.length)]);
                                } else {
                                   spawned = (Monster) world.spawnEntity(location, banditTypesRare[random.nextInt(banditTypesRare.length)]);
                                }
                                spawned.setLootTable(LootTables.EMPTY.getLootTable());

                            }

                        }
                    }

                }
            }
        }
    }
}
