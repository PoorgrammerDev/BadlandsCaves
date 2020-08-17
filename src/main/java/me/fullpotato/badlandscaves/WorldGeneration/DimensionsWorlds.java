package me.fullpotato.badlandscaves.WorldGeneration;

import me.fullpotato.badlandscaves.AlternateDimensions.DimensionStructures;
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
    private final EnvironmentalHazards hazards;
    private final DimensionStructures structures;

    public DimensionsWorlds(BadlandsCaves plugin) {
        this.plugin = plugin;
        hazards = new EnvironmentalHazards(plugin);
        structures = new DimensionStructures(plugin);
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

    public enum NativeLife {
        ILLAGERS,
        UNDEAD,
    }

    public World generate(String name) {
        final String fullName = plugin.getDimensionPrefixName() + name;
        final UnloadedWorld unloadedWorld = new UnloadedWorld(fullName);
        World alreadyExisting = plugin.getServer().getWorld(fullName);
        if (plugin.getServer().getWorlds().contains(alreadyExisting)) {
            return alreadyExisting;
        }
        else if (unloadedWorld.exists()) {
            alreadyExisting = loadWorld(fullName);
            return alreadyExisting;
        }

        final NativeLife habitation = getRandomHabitation();
        final Biome biome = allBiomes[random.nextInt(allBiomes.length)];

        final WorldCreator creator = new WorldCreator(plugin.getDimensionPrefixName() + name);
        creator.environment(World.Environment.NORMAL);
        creator.generator(new DimensionsGen(plugin, biome));

        final World world = plugin.getServer().createWorld(creator);
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

        final WorldBorder border = world.getWorldBorder();
        border.setCenter(0, 0);
        border.setWarningDistance(25);

        final int worldBorderSize = random.nextInt(4000) + 1000;
        border.setSize(worldBorderSize);

        addHabitation(world, habitation);

        final int chaos = plugin.getSystemConfig().getInt("chaos_level");
        final int amount = chaos / 20 > 0 ? 1 + random.nextInt((chaos / 20)) : 1;
        addHazards(world, habitation, biome, amount);
        genSpawnCage(world);

        new BukkitRunnable() {
            @Override
            public void run() {
                structures.generateStructures(world, habitation, null, worldBorderSize / 2, random.nextInt((chaos / 5) + 20) + 5);
            }
        }.runTaskLater(plugin, 5);

        return world;
    }

    public void genSpawnCage (World world) {
        final Location location = new Location(world, random.nextInt(200) - 100, random.nextInt(100) + 20, random.nextInt(200) - 100);
        world.setSpawnLocation(location);

        final StructureTrack track = new StructureTrack(plugin, location, -7, -1, -7, 0, 0, 0, "badlandscaves:dungeon", BlockFace.DOWN);
        track.load();

        Block campfire = location.getBlock();
        campfire.setType(Material.CAMPFIRE);

        if (campfire.getState() instanceof Campfire) {
            Campfire state = (Campfire) campfire.getState();

            state.getPersistentDataContainer().set(new NamespacedKey(plugin, "is_return_portal"), PersistentDataType.BYTE, (byte) 1);
            state.update(true);
        }
    }

    public void addHazards (World world, NativeLife habitation, Biome biome, int amount) {
        final ArrayList<EnvironmentalHazards.Hazard> list = new ArrayList<>();

        //EXCLUSIVE-----------------------------------------------
        if (biome.equals(Biome.SNOWY_BEACH) || biome.equals(Biome.SNOWY_TAIGA) || biome.equals(Biome.ICE_SPIKES)) {
            list.add(EnvironmentalHazards.Hazard.FREEZING);
        }
        else if (biome.equals(Biome.SWAMP)) {
            list.add(EnvironmentalHazards.Hazard.ACID_RAIN);
            list.add(EnvironmentalHazards.Hazard.TOXIC_WATER);
        }

        //UNINHABITABLE-------------------------------------------
        if (!habitation.equals(NativeLife.ILLAGERS)) {
            list.add(EnvironmentalHazards.Hazard.METEOR_SHOWERS);
            list.add(EnvironmentalHazards.Hazard.LAVA_FLOOR);
            list.add(EnvironmentalHazards.Hazard.NO_OXYGEN);
            list.add(EnvironmentalHazards.Hazard.NO_FLOOR);
            list.add(EnvironmentalHazards.Hazard.NO_FOOD);
        }

        //NONSPECIFIC-----------------------------------------------
        list.add(EnvironmentalHazards.Hazard.SLOW_BREAK);
        list.add(EnvironmentalHazards.Hazard.PARANOIA);
        list.add(EnvironmentalHazards.Hazard.BEWILDERMENT);


        //RETURNING----------------------------------------------------------------------------
        for (int i = 0; i < amount; i++) {
            EnvironmentalHazards.Hazard hazard;
            int tries = 0;
            do {
                hazard = list.get(random.nextInt(list.size()));
                tries++;
            } while ((hazard == null || hazards.hasHazard(world, hazard)) && tries < 100);

            if (!hazards.hasHazard(world, hazard)) {
                hazards.addHazard(world, hazard);
            }
        }
    }

    public NativeLife getRandomHabitation () {
        if (random.nextBoolean()) return NativeLife.ILLAGERS;
        return NativeLife.UNDEAD;
    }

    public void addHabitation(World world, NativeLife habitation) {
        plugin.getSystemConfig().set("dim_stats." + world.getName() + ".habitation", habitation.name().toLowerCase());
        plugin.saveSystemConfig();
    }

    public World loadWorld (String name) {
        final String scaleRandStr = plugin.getSystemConfig().getString("dim_stats." + name + ".generator.scale");
        final String freqStr = plugin.getSystemConfig().getString("dim_stats." + name + ".generator.frequency");
        final String amplitudeStr = plugin.getSystemConfig().getString("dim_stats." + name + ".generator.amplitude");
        final String biomeStr = plugin.getSystemConfig().getString("dim_stats." + name + ".generator.biome");
        final String storedChaosStr = plugin.getSystemConfig().getString("dim_stats." + name + ".generator.chaos");

        if (scaleRandStr != null && freqStr != null && amplitudeStr != null && biomeStr != null) {
            try {
                final double scaleRand = Double.parseDouble(scaleRandStr);
                final double frequency = Double.parseDouble(freqStr);
                final double amplitude = Double.parseDouble(amplitudeStr);
                final int storedChaos = Integer.parseInt(storedChaosStr);
                final Biome biome = Biome.valueOf(biomeStr);

                final WorldCreator worldCreator = new WorldCreator(name);
                worldCreator.environment(World.Environment.NORMAL).generator(new DimensionsGen(plugin, biome, scaleRand, frequency, amplitude, storedChaos));

                return worldCreator.createWorld();
            }
            catch (IllegalArgumentException ignored) {
            }
        }
        return null;
    }

}
