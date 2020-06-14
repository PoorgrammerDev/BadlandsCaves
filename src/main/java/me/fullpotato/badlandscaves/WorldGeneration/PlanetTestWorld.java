package me.fullpotato.badlandscaves.WorldGeneration;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.Util.StructureCopier;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.WorldCreator;
import org.bukkit.block.Biome;

import java.util.Random;
import java.util.UUID;

public class PlanetTestWorld {
    private final BadlandsCaves plugin;
    private final Random random = new Random();

    public PlanetTestWorld(BadlandsCaves plugin) {
        this.plugin = plugin;
    }

    private static final Biome[] biomes = {
            Biome.PLAINS,
            Biome.DESERT,
            Biome.FOREST,
            Biome.TAIGA,
            Biome.SWAMP,
            Biome.WOODED_HILLS,
            Biome.MOUNTAIN_EDGE,
            Biome.JUNGLE,
            Biome.JUNGLE_EDGE,
            Biome.SNOWY_BEACH,
            Biome.BIRCH_FOREST,
            Biome.DARK_FOREST,
            Biome.SNOWY_TAIGA,
            Biome.GIANT_TREE_TAIGA,
            Biome.SAVANNA,
            Biome.SUNFLOWER_PLAINS,
            Biome.FLOWER_FOREST,
            Biome.GIANT_SPRUCE_TAIGA,
            Biome.BAMBOO_JUNGLE,
            Biome.MUSHROOM_FIELD_SHORE,
            Biome.ICE_SPIKES,
    };

    public World generate() {
        return generate(biomes[random.nextInt(biomes.length)]);
    }

    public World generate(Biome biome) {
        WorldCreator creator = new WorldCreator(plugin.planetPrefixName + UUID.randomUUID());

        creator.generator(new PlanetsGeneration(biome));
        World world = creator.createWorld();
        world.setSpawnLocation(0, 127, 0);
        world.setGameRule(GameRule.DO_INSOMNIA, false);
        world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        world.setGameRule(GameRule.FALL_DAMAGE, true);
        world.setGameRule(GameRule.DISABLE_RAIDS, true);
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        world.setGameRule(GameRule.MOB_GRIEFING, false);
        //world.setGameRule(GameRule.REDUCED_DEBUG_INFO, true);
        world.setGameRule(GameRule.DO_FIRE_TICK, false);
        world.setGameRule(GameRule.DO_PATROL_SPAWNING, false);
        world.setGameRule(GameRule.DO_TRADER_SPAWNING, false);
        world.setGameRule(GameRule.SPAWN_RADIUS, 0);
        world.setGameRule(GameRule.RANDOM_TICK_SPEED, 0);

        WorldBorder border = world.getWorldBorder();
        border.setCenter(0, 0);
        border.setWarningDistance(0);
        border.setSize(1000);

        gravity(random, world);

        StructureCopier.copyStructures(plugin.getServer().getWorld(plugin.mainWorldName), world, "planet_structures");
        return world;
    }
    public void gravity (Random random, World world) {
        int rand = random.nextInt(3);

        double gravityModifier;
        if (rand == 0) {
            //high gravity (2 - 5)
            gravityModifier = random.doubles(1, 2, 5).toArray()[0];
        }
        else if (rand == 1) {
            //normal gravity (1)
            gravityModifier = 1;
        }
        else {
            //low gravity (0.1 - 0.9)
            gravityModifier = random.doubles(1, 0.1, 0.9).toArray()[0];
        }

        plugin.getConfig().set("system.planet_stats." + world.getName() + ".gravity", gravityModifier);
        plugin.saveConfig();
    }
}
