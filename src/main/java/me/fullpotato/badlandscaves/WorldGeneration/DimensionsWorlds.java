package me.fullpotato.badlandscaves.WorldGeneration;

import me.fullpotato.badlandscaves.AlternateDimensions.DimensionStructures;
import me.fullpotato.badlandscaves.AlternateDimensions.GravityRunnable;
import me.fullpotato.badlandscaves.AlternateDimensions.Hazards.EnvironmentalHazards;
import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.Util.StructureTrack;
import me.fullpotato.badlandscaves.Util.UnloadedWorld;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Campfire;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Random;

public class DimensionsWorlds {

    private final BadlandsCaves plugin;
    private final Random random = new Random();

    public DimensionsWorlds(BadlandsCaves plugin) {
        this.plugin = plugin;
    }

    private static final Biome[] allBiomes = {
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
            Biome.ICE_SPIKES
    };

    private static final Biome[] habitableBiomes = {
            Biome.PLAINS,
            Biome.FOREST,
            Biome.TAIGA,
            Biome.SWAMP,
            Biome.WOODED_HILLS,
            Biome.MOUNTAIN_EDGE,
            Biome.JUNGLE,
            Biome.JUNGLE_EDGE,
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
    };

    public enum Habitation {
        INHABITED(true),
        UNINHABITED(false),
        REINHABITED(true);

        private final boolean mobSpawn;
        Habitation(boolean mobSpawn) {
            this.mobSpawn = mobSpawn;
        }

        public boolean getMobSpawn() {
            return this.mobSpawn;
        }
    }

    public World generate(String name) {
        UnloadedWorld unloadedWorld = new UnloadedWorld(plugin.getDimensionPrefixName() + name);
        World alreadyExisting = plugin.getServer().getWorld(plugin.getDimensionPrefixName() + name);
        if (plugin.getServer().getWorlds().contains(alreadyExisting)) {
            if (alreadyExisting != null) {
                new GravityRunnable(plugin, alreadyExisting).runTaskTimerAsynchronously(plugin, 0, 0);
            }
            return alreadyExisting;
        }
        else if (unloadedWorld.exists()) {
            unloadedWorld.load(plugin);
            alreadyExisting = plugin.getServer().getWorld(plugin.getDimensionPrefixName() + name);

            if (alreadyExisting != null) {
                new GravityRunnable(plugin, alreadyExisting).runTaskTimerAsynchronously(plugin, 0, 0);
            }

            return alreadyExisting;
        }

        Habitation habitation = Habitation.INHABITED; //getRandomHabitation(); TODO: 7/6/2020 change
        Biome biome;
        if (habitation.equals(Habitation.UNINHABITED)) {
            biome = habitableBiomes[random.nextInt(habitableBiomes.length)];
        }
        else {
            biome = allBiomes[random.nextInt(allBiomes.length)];
        }
        biome = Biome.JUNGLE; // TODO: 7/6/2020 remove

        WorldCreator creator = new WorldCreator(plugin.getDimensionPrefixName() + name);

        World.Environment environment = World.Environment.THE_END; //getEnvironment(); TODO revert
        creator.environment(environment);
        creator.generator(new DimensionsGen(biome));

        World world = plugin.getServer().createWorld(creator);
        assert world != null;
        world.setTime(random.nextInt(24000));
        world.setGameRule(GameRule.DO_INSOMNIA, false);
        world.setGameRule(GameRule.FALL_DAMAGE, true);
        world.setGameRule(GameRule.DISABLE_RAIDS, true);
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        //world.setGameRule(GameRule.REDUCED_DEBUG_INFO, true);
        world.setGameRule(GameRule.DO_FIRE_TICK, false);
        world.setGameRule(GameRule.DO_PATROL_SPAWNING, false);
        world.setGameRule(GameRule.DO_TRADER_SPAWNING, false);
        world.setGameRule(GameRule.RANDOM_TICK_SPEED, 0);
        world.setKeepSpawnInMemory(false);

        WorldBorder border = world.getWorldBorder();
        border.setCenter(0, 0);
        border.setWarningDistance(0);
        border.setSize(1000);

        if (environment.equals(World.Environment.THE_END)) PreventDragon.preventDragonSpawn(world);

        genGravity(world);
        addHabitation(world, habitation);

        final int chaos = plugin.getSystemConfig().getInt("chaos_level");
        final int amount = chaos / 20 > 0 ? 1 + random.nextInt((chaos / 20)) : 1;
        addHazards(world, habitation, environment, biome, amount);

        genSpawnCage(world);

        DimensionStructures structures = new DimensionStructures(plugin);
        new BukkitRunnable() {
            @Override
            public void run() {
                structures.generateStructure(world, habitation, null, null);
            }
        }.runTaskLater(plugin, 5);


        new GravityRunnable(plugin, world).runTaskTimerAsynchronously(plugin, 0, 0);


        Bukkit.broadcastMessage(environment.name());
        Bukkit.broadcastMessage(habitation.name());
        return world;
    }

    public void genSpawnCage (World world) {
        Location location = new Location(world, random.nextInt(200) - 100, random.nextInt(100) + 20, random.nextInt(200) - 100);
        world.setSpawnLocation(location);

        StructureTrack track = new StructureTrack(plugin, location, -4, -1, -4, 0, 0, 0, "badlandscaves:dungeon", BlockFace.DOWN);
        track.load();

        Block campfire = location.getBlock();
        campfire.setType(Material.CAMPFIRE);

        if (campfire.getState() instanceof Campfire) {
            Campfire state = (Campfire) campfire.getState();

            state.getPersistentDataContainer().set(new NamespacedKey(plugin, "is_return_portal"), PersistentDataType.BYTE, (byte) 1);
            state.update(true);
        }
    }

    public void addHazards (World world, Habitation habitation, World.Environment environment, Biome biome, int amount) {
        ArrayList<EnvironmentalHazards.Hazard> list = new ArrayList<>();
        ArrayList<EnvironmentalHazards.Hazard> priority = new ArrayList<>();

        //EXCLUSIVE-----------------------------------------------
        if (environment.equals(World.Environment.NORMAL)) list.add(EnvironmentalHazards.Hazard.ACID_RAIN);
        if (biome.equals(Biome.SNOWY_BEACH) || biome.equals(Biome.SNOWY_TAIGA) || biome.equals(Biome.ICE_SPIKES))
            list.add(EnvironmentalHazards.Hazard.FREEZING);

        //SWAMP----------------------------------------------------
        if (biome.equals(Biome.SWAMP)) {
            priority.add(EnvironmentalHazards.Hazard.TOXIC_WATER);
        } else {
            list.add(EnvironmentalHazards.Hazard.TOXIC_WATER);
        }

        //NETHER---------------------------------------------------
        if (!habitation.equals(Habitation.INHABITED)) {
            if (environment.equals(World.Environment.NETHER)) {
                priority.add(EnvironmentalHazards.Hazard.METEOR_SHOWERS);
                priority.add(EnvironmentalHazards.Hazard.LAVA_FLOOR);
            } else {
                list.add(EnvironmentalHazards.Hazard.METEOR_SHOWERS);
                list.add(EnvironmentalHazards.Hazard.LAVA_FLOOR);
            }
        }

        //END-------------------------------------------------------
        if (!habitation.equals(Habitation.INHABITED)) {
            if (environment.equals(World.Environment.THE_END)) {
                priority.add(EnvironmentalHazards.Hazard.NO_OXYGEN);
            } else {
                list.add(EnvironmentalHazards.Hazard.NO_OXYGEN);
            }
        }

        //OTHER UNINHABITABLE
        if (!habitation.equals(Habitation.INHABITED)) {
            list.add(EnvironmentalHazards.Hazard.NO_FLOOR);
            list.add(EnvironmentalHazards.Hazard.NO_FOOD);
        }

        //NONSPECIFIC-----------------------------------------------
        list.add(EnvironmentalHazards.Hazard.SLOW_BREAK);
        list.add(EnvironmentalHazards.Hazard.PARANOIA);
        list.add(EnvironmentalHazards.Hazard.BEWILDERMENT);


        //RETURNING----------------------------------------------------------------------------
        EnvironmentalHazards hazards = new EnvironmentalHazards(plugin);
        hazards.addHazard(world, EnvironmentalHazards.Hazard.NO_OXYGEN);

        for (int i = 0; i < amount; i++) {
            EnvironmentalHazards.Hazard hazard;
            int count = 0;
            do {
                if (!priority.isEmpty() && random.nextInt(100) < 60) {
                    hazard = priority.get(random.nextInt(priority.size()));
                } else {
                    hazard = list.get(random.nextInt(list.size()));
                }
                count++;
            } while ((hazard == null || hazards.hasHazard(world, hazard)) && count < 100);
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

        plugin.getSystemConfig().set("dim_stats." + world.getName() + ".gravity", gravityModifier);
        plugin.saveSystemConfig();
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

    public Habitation getRandomHabitation () {
        int rand = random.nextInt(3);
        if (rand == 0) {
            return Habitation.INHABITED;
        }
        else if (rand == 1) {
            return Habitation.UNINHABITED;
        }
        else {
            return Habitation.REINHABITED;
        }
    }

    public void addHabitation(World world, Habitation habitation) {
        world.setGameRule(GameRule.DO_MOB_SPAWNING, habitation.getMobSpawn());
        plugin.getSystemConfig().set("dim_stats." + world.getName() + ".habitation", habitation.name().toLowerCase());
        plugin.saveSystemConfig();
    }


}
