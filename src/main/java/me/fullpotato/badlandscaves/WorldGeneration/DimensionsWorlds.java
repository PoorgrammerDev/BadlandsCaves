package me.fullpotato.badlandscaves.WorldGeneration;

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
    private final Random random;
    private final EnvironmentalHazards hazards;

    public DimensionsWorlds(BadlandsCaves plugin, Random random) {
        this.plugin = plugin;
        hazards = new EnvironmentalHazards(plugin, random);
        this.random = random;
    }

    private static final Biome[] allBiomes = {
            Biome.PLAINS,
            Biome.DESERT,
            Biome.FOREST,
            Biome.SWAMP,
            Biome.WOODED_HILLS,
            Biome.JUNGLE,
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
        ILLAGERS,
        UNDEAD,
    }

    public World generate(String name, boolean pregenerate) {
        final String fullName = plugin.getDimensionPrefixName() + name;
        final UnloadedWorld unloadedWorld = new UnloadedWorld(fullName);

        //Check if the world is already loaded. If it is, return it.
        //If it exists but is unloaded, load it and return it.
        //Else, continue with generation
        World alreadyLoaded = plugin.getServer().getWorld(fullName);
        if (plugin.getServer().getWorlds().contains(alreadyLoaded)) {
            return alreadyLoaded;
        }
        else if (unloadedWorld.exists()) {
            alreadyLoaded = loadWorld(fullName);
            return alreadyLoaded;
        }

        //Generate the type of Habitation (what entities live there)
        //and the biome of the world
        final Habitation habitation = getRandomHabitation();
        final Biome biome = allBiomes[random.nextInt(allBiomes.length)];
        final int chaos = plugin.getSystemConfig().getInt("chaos_level");

        //Set environment (dimension type) and generator
        final WorldCreator creator = new WorldCreator(plugin.getDimensionPrefixName() + name);
        creator.environment(World.Environment.NORMAL);
        creator.generator(new DimensionsGen(plugin, biome, chaos)); 

        final World world = plugin.getServer().createWorld(creator);
        assert world != null;

        //Set world settings (gamerules & starting time)
        world.setTime(random.nextInt(24000));
        world.setGameRule(GameRule.DO_INSOMNIA, false);
        world.setGameRule(GameRule.FALL_DAMAGE, true);
        world.setGameRule(GameRule.DISABLE_RAIDS, true);
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        world.setGameRule(GameRule.DO_FIRE_TICK, false);
        world.setGameRule(GameRule.DO_PATROL_SPAWNING, false);
        world.setGameRule(GameRule.DO_TRADER_SPAWNING, false);
        world.setGameRule(GameRule.RANDOM_TICK_SPEED, 0);
        world.setKeepSpawnInMemory(false);

        //Set worldborder
        final WorldBorder border = world.getWorldBorder();
        border.setCenter(0, 0);
        border.setWarningDistance(25);
        border.setSize(random.nextInt(1000) + 1000);

        //This next part is delayed by 5 and 10 ticks
        new BukkitRunnable() {
            @Override
            public void run() {
                genSpawnCage(world);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        plugin.getSystemConfig().set("alternate_dimensions." + fullName + ".accessed", false);
                        plugin.getSystemConfig().set("alternate_dimensions." + fullName + ".structures_generated", false);
                        plugin.saveSystemConfig();
                        addHabitation(world, habitation);
                    }
                }.runTaskLater(plugin, 5);
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

    public void addHazards (World world) {
        final String name = world.getName();
        final String biomeStr = plugin.getSystemConfig().getString("alternate_dimensions." + name + ".generator.biome");

        if (biomeStr != null && !biomeStr.isEmpty()) {
            try {
                final Biome biome = Biome.valueOf(biomeStr.toUpperCase());
                final int chaos = plugin.getSystemConfig().getInt("chaos_level");
                final int amount = chaos / 25 > 0 ? 1 + random.nextInt((chaos / 25)) : 1;

                addHazards(world, biome, amount);
            }
            catch (IllegalArgumentException e){
                e.printStackTrace();
            }
        }
    }

    public void addHazards (World world, Biome biome, int amount) {
        final ArrayList<EnvironmentalHazards.Hazard> list = new ArrayList<>();

        list.add(EnvironmentalHazards.Hazard.FREEZING);
        list.add(EnvironmentalHazards.Hazard.TOXIC_WATER);
        list.add(EnvironmentalHazards.Hazard.METEOR_SHOWERS);
        list.add(EnvironmentalHazards.Hazard.NO_FOOD);
        list.add(EnvironmentalHazards.Hazard.PARANOIA);

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

    public Habitation getRandomHabitation () {
        if (random.nextBoolean()) return Habitation.ILLAGERS;
        return Habitation.UNDEAD;
    }

    public void addHabitation(World world, Habitation habitation) {
        plugin.getSystemConfig().set("alternate_dimensions." + world.getName() + ".habitation", habitation.name().toLowerCase());
        plugin.saveSystemConfig();
    }

    public World loadWorld (String name) {
        final String biomeStr = plugin.getSystemConfig().getString("alternate_dimensions." + name + ".generator.biome");
        final String storedChaosStr = plugin.getSystemConfig().getString("alternate_dimensions." + name + ".generator.chaos");

        if (biomeStr != null && storedChaosStr != null) {
            try {
                final int storedChaos = Integer.parseInt(storedChaosStr);
                final Biome biome = Biome.valueOf(biomeStr);

                final WorldCreator worldCreator = new WorldCreator(name);
                worldCreator.environment(World.Environment.NORMAL).generator(new DimensionsGen(plugin, biome, storedChaos));

                return worldCreator.createWorld();
            }
            catch (IllegalArgumentException ignored) {
            }
        }
        return null;
    }

}
