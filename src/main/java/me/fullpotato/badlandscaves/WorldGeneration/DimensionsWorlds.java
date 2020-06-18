package me.fullpotato.badlandscaves.WorldGeneration;

import me.fullpotato.badlandscaves.AlternateDimensions.DimensionStructures;
import me.fullpotato.badlandscaves.AlternateDimensions.Hazards.EnvironmentalHazards;
import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.Util.StructureCopier;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.WorldCreator;
import org.bukkit.block.Biome;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Random;

public class DimensionsWorlds {

    private final BadlandsCaves plugin;
    private final Random random = new Random();

    public DimensionsWorlds(BadlandsCaves plugin) {
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

    public enum Habitation {
        INHABITED,
        UNINHABITED,
        REINHABITED
    }

    public World generate(String name) {
        return generate(name, biomes[random.nextInt(biomes.length)]);
    }

    public World generate(String name, Biome biome) {
        WorldCreator creator = new WorldCreator(plugin.dimensionPrefixName + name);

        World.Environment environment = getEnvironment();
        creator.environment(environment);

        boolean shadowrealm = environment.equals(World.Environment.THE_END) && random.nextInt(100) < 25;

        if (shadowrealm) {
            creator.generator(new DimensionsGen(Biome.THE_VOID, DimensionsGen.SpecialGen.SHADOW_REALM));
        }
        else {
            creator.generator(new DimensionsGen(biome, null));
        }

        World world = plugin.getServer().createWorld(creator);
        world.setSpawnLocation(0, 127, 0);
        world.setGameRule(GameRule.DO_INSOMNIA, false);
        world.setGameRule(GameRule.FALL_DAMAGE, true);
        world.setGameRule(GameRule.DISABLE_RAIDS, true);
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
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

        if (environment.equals(World.Environment.THE_END)) PreventDragon.preventDragonSpawn(world);

        genGravity(world);
        genHabitation(world);

        final int chaos = plugin.getConfig().getInt("system.chaos_level");
        final int amount = chaos > 0 ? 1 + random.nextInt((chaos / 15)) : 1;
        addHazards(world, environment, biome, amount);

        StructureCopier.copyStructures(plugin.getServer().getWorld(plugin.mainWorldName), world, "planet_structures");

        DimensionStructures structures = new DimensionStructures(plugin);
        new BukkitRunnable() {
            @Override
            public void run() {
                structures.generateStructure(world, null, null);
            }
        }.runTaskLater(plugin, 5);

        return world;
    }

    public void addHazards (World world, World.Environment environment, Biome biome, int amount) {
        ArrayList<EnvironmentalHazards.Hazard> list = new ArrayList<>();
        ArrayList<EnvironmentalHazards.Hazard> priority = new ArrayList<>();

        //EXCLUSIVE-----------------------------------------------
        if (environment.equals(World.Environment.NORMAL)) list.add(EnvironmentalHazards.Hazard.ACID_RAIN);
        if (biome.equals(Biome.SNOWY_BEACH) || biome.equals(Biome.SNOWY_TAIGA) || biome.equals(Biome.ICE_SPIKES)) list.add(EnvironmentalHazards.Hazard.FREEZING);

        //SWAMP----------------------------------------------------
        if (biome.equals(Biome.SWAMP)) {
            priority.add(EnvironmentalHazards.Hazard.TOXIC_WATER);
        }
        else {
            list.add(EnvironmentalHazards.Hazard.TOXIC_WATER);
        }

        //NETHER---------------------------------------------------
        if (environment.equals(World.Environment.NETHER)) {
            priority.add(EnvironmentalHazards.Hazard.METEOR_SHOWERS);
            priority.add(EnvironmentalHazards.Hazard.LAVA_FLOOR);
        }
        else {
            list.add(EnvironmentalHazards.Hazard.METEOR_SHOWERS);
            list.add(EnvironmentalHazards.Hazard.LAVA_FLOOR);
        }

        //END-------------------------------------------------------
        if (environment.equals(World.Environment.THE_END)) {
            priority.add(EnvironmentalHazards.Hazard.NO_OXYGEN);
        }
        else {
            list.add(EnvironmentalHazards.Hazard.NO_OXYGEN);
        }

        //NONSPECIFIC-----------------------------------------------
        list.add(EnvironmentalHazards.Hazard.SLOW_BREAK);
        list.add(EnvironmentalHazards.Hazard.NO_FLOOR);
        list.add(EnvironmentalHazards.Hazard.NO_FOOD);
        list.add(EnvironmentalHazards.Hazard.PARANOIA);
        list.add(EnvironmentalHazards.Hazard.BEWILDERMENT);


        //RETURNING----------------------------------------------------------------------------
        EnvironmentalHazards hazards = new EnvironmentalHazards(plugin);

        for (int i = 0; i < amount; i++) {
            EnvironmentalHazards.Hazard hazard;
            do {
                if (!priority.isEmpty() && random.nextInt(100) < 60) {
                    hazard = priority.get(random.nextInt(priority.size()));
                } else {
                    hazard = list.get(random.nextInt(list.size()));
                }
            } while (hazard == null || hazards.hasHazard(world, hazard));
            hazards.addHazard(world, hazard);
        }
    }

    public void genGravity(World world) {
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

        plugin.getConfig().set("system.dim_stats." + world.getName() + ".gravity", gravityModifier);
        plugin.saveConfig();
    }

    public World.Environment getEnvironment() {
        if (random.nextBoolean()) {
            return World.Environment.NORMAL;
        }
        else {
            if (random.nextBoolean()) {
                return World.Environment.NETHER;
            }
            else {
                return World.Environment.THE_END;
            }
        }
    }

    public void genHabitation(World world) {
        int rand = random.nextInt(3);
        boolean mobspawn = false;
        Habitation habitation;
        if (rand == 0) {
            habitation = Habitation.INHABITED;
            mobspawn = true;
        }
        else if (rand == 1) {
            habitation = Habitation.UNINHABITED;
        }
        else {
            habitation = Habitation.REINHABITED;
            mobspawn = true;
        }
        world.setGameRule(GameRule.DO_MOB_SPAWNING, mobspawn);

        plugin.getConfig().set("system.dim_stats." + world.getName() + ".habitation", habitation.name().toLowerCase());
        plugin.saveConfig();
    }


}
