package me.fullpotato.badlandscaves.WorldGeneration;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.Util.MultiStructureLoader;
import me.fullpotato.badlandscaves.Util.StructureCopier;
import me.fullpotato.badlandscaves.Util.StructureTrack;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Random;

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

    public enum Habitation {
        INHABITED,
        UNINHABITED,
        REINHABITED
    }

    public enum PlanetStructure {
        MANABAR(15),
        LAB(15),
        LAB_ABANDONED(15),
        LAB_DESTROYED(0),
        JAIL(7),
        JAIL_ABANDONED(7),
        SHRINE(7),
        SHRINE_DESTROYED(7),
        CURSED_HOUSE(15),
        TENT(7),
        HOUSE(9),
        HOUSE_ABANDONED(10),
        HOUSE_DESTROYED(10),
        BUNKER(16),
        BUNKER_AB(16);

        private final int clearRadius;
        PlanetStructure(int clearRadius) {
            this.clearRadius = clearRadius;
        }

        public int getClearRadius() {
            return this.clearRadius;
        }
    }

    public World generate(String name) {
        return generate(name, biomes[random.nextInt(biomes.length)]);
    }

    public World generate(String name, Biome biome) {
        WorldCreator creator = new WorldCreator(plugin.planetPrefixName + name);

        World.Environment environment = getEnvironment();
        creator.environment(environment);
        creator.generator(new PlanetsGeneration(biome));
        World world = plugin.getServer().createWorld(creator);
        world.setSpawnLocation(0, 127, 0);
        world.setGameRule(GameRule.DO_INSOMNIA, false);
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

        if (environment.equals(World.Environment.THE_END)) PreventDragon.preventDragonSpawn(world);
        gravity(world);
        habitation(world);

        StructureCopier.copyStructures(plugin.getServer().getWorld(plugin.mainWorldName), world, "planet_structures");

        new BukkitRunnable() {
            @Override
            public void run() {
                generateStructure(world, null, PlanetStructure.BUNKER);
            }
        }.runTaskLater(plugin, 5);

        return world;
    }

    public void gravity (World world) {
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

    public void habitation(World world) {
        int rand = random.nextInt(3);
        Habitation habitation;
        if (rand == 0) {
            habitation = Habitation.INHABITED;
            world.setGameRule(GameRule.DO_MOB_SPAWNING, true);
        }
        else if (rand == 1) {
            habitation = Habitation.UNINHABITED;
            world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        }
        else {
            habitation = Habitation.REINHABITED;
            world.setGameRule(GameRule.DO_MOB_SPAWNING, true);
        }

        plugin.getConfig().set("system.planet_stats." + world.getName() + ".habitation", habitation.name().toLowerCase());
        plugin.saveConfig();
    }

    public void generateStructure (World world, @Nullable Location origin, @Nullable PlanetStructure structure) {
        if (world.getName().startsWith(plugin.planetPrefixName)) {
            if (origin == null || origin.getWorld() == null || !origin.getWorld().equals(world)) {
                int y_start = 200;

                origin = new Location(world, 0, y_start, 0);

                for (int y = y_start; y > 0; y--) {
                    Location iterate = origin.clone();
                    iterate.setY(y);

                    Material type = iterate.getBlock().getType();
                    if (type.isSolid() && !type.name().toUpperCase().contains("LOG") && !type.name().toUpperCase().contains("LEAVES")) {
                        origin.setY(y);
                        break;
                    }
                }
            }

            if (structure == null) structure = PlanetStructure.values()[random.nextInt(PlanetStructure.values().length)];

            clearArea(origin, structure.getClearRadius());
            loadStructure(structure, origin);
        }
    }

    public void clearArea(Location location, int radius) {
        Material groundType = getGroundMaterial(location);

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                for (int y = location.getBlockY() + 1; y < 200; y++) {
                    Location clone = location.clone().add(x, 0, z);
                    clone.setY(y);

                    Block block = clone.getBlock();
                    if (block.getType().name().toUpperCase().contains("LOG") || block.getType().name().toUpperCase().contains("LEAVES") || !block.getType().isSolid() || block.isLiquid()) {
                        block.setType(Material.AIR);
                    }

                }

                for (int y = location.getBlockY() - 4; y <= location.getBlockY(); y++) {
                    Location clone = location.clone().add(x, 0, z);
                    clone.setY(y);

                    Block block = clone.getBlock();
                    if (block.isLiquid() || block.isPassable() || !block.getType().isSolid()) {
                        block.setType(groundType);
                    }
                }
            }
        }

    }

    public Material getGroundMaterial (Location location) {
        for (int x = -10; x <= 10; x++) {
            for (int z = -10; z <= 10; z++) {
                Location clone = location.clone().add(x, 0, z);
                Material type = clone.getBlock().getType();

                if (type.isSolid() && !type.name().toUpperCase().contains("LOG") && !type.name().toUpperCase().contains("LEAVES")) {
                    return type;
                }
            }
        }
        return Material.STONE;
    }

    public void loadStructure(@NotNull PlanetStructure queried, Location origin) {
        //center ground level world origin ~(0, 60, 0)

        //multistructures
        if (queried.equals(PlanetStructure.BUNKER)) {
            final StructureTrack[] bunker = {
                    new StructureTrack(plugin, -6, -9, -14, 0, 1, 0, "badlandscaves:bunker_tophouse", BlockFace.DOWN),
                    new StructureTrack(plugin, -9, -33, -10, 0, 1, 0, "badlandscaves:bunker_intertube", BlockFace.DOWN),
                    new StructureTrack(plugin, -13, -33, 5, 0, 1, 0, "badlandscaves:bunker_foyer", BlockFace.DOWN),
                    new StructureTrack(plugin, -42, -30, 9, 0, 1, 0, "badlandscaves:bunker_bedroom", BlockFace.DOWN),
                    new StructureTrack(plugin, 8, -29, 10, 0, 1, 0, "badlandscaves:bunker_dine", BlockFace.DOWN),
                    new StructureTrack(plugin, 5, -28, 27, -15, 1, 0, "badlandscaves:bunker_farm", BlockFace.UP),
            };

            MultiStructureLoader loader = new MultiStructureLoader(bunker);
            loader.loadAll(origin);

            new BukkitRunnable() {
                @Override
                public void run() {
                    new StructureTrack(plugin, 5, -28, 27, -15, 1, 0, "badlandscaves:bunker_farm", BlockFace.UP).load(origin);
                }
            }.runTaskLater(plugin, 5);
        }
        else if (queried.equals(PlanetStructure.BUNKER_AB)) {
            final StructureTrack[] bunker_ab = {
                    new StructureTrack(plugin, -6, -9, -14, 0, 1, 0, "badlandscaves:bunker_tophouse_ab", BlockFace.DOWN),
                    new StructureTrack(plugin, -9, -33, -10, 0, 1, 0, "badlandscaves:bunker_intertube_ab", BlockFace.DOWN),
                    new StructureTrack(plugin, -13, -33, 5, 0, 1, 0, "badlandscaves:bunker_foyer_ab", BlockFace.DOWN),
                    new StructureTrack(plugin, -42, -30, 9, 0, 1, 0, "badlandscaves:bunker_bedroom_ab", BlockFace.DOWN),
                    new StructureTrack(plugin, 8, -29, 10, 0, 1, 0, "badlandscaves:bunker_dine_ab", BlockFace.DOWN),
                    new StructureTrack(plugin, 5, -28, 27, -15, 1, 0, "badlandscaves:bunker_farm_ab", BlockFace.UP),
            };

            MultiStructureLoader loader = new MultiStructureLoader(bunker_ab);
            loader.loadAll(origin);
        }
        //single structures
        else {
            final StructureTrack[] structures = {
                    new StructureTrack(plugin, 11, 0, 10, -21, 0, -19, "badlandscaves:" + PlanetStructure.MANABAR.name().toLowerCase(), BlockFace.UP),
                    new StructureTrack(plugin, 11, 0, -15, -22, 0, 1, "badlandscaves:" + PlanetStructure.LAB.name().toLowerCase(), BlockFace.UP),
                    new StructureTrack(plugin, 11, 0, -15, -22, 0, 1, "badlandscaves:" + PlanetStructure.LAB_ABANDONED.name().toLowerCase(), BlockFace.UP),
                    new StructureTrack(plugin, 11, 0, -15, -22, 0, 1, "badlandscaves:" + PlanetStructure.LAB_DESTROYED.name().toLowerCase(), BlockFace.UP),
                    new StructureTrack(plugin, -8, -6, 6, 1, 0, -29, "badlandscaves:" + PlanetStructure.JAIL.name().toLowerCase(), BlockFace.UP),
                    new StructureTrack(plugin, -8, -6, 6, 1, 0, -29, "badlandscaves:" + PlanetStructure.JAIL_ABANDONED.name().toLowerCase(), BlockFace.UP),
                    new StructureTrack(plugin, 6, 0, -6, -11, 0, 1, "badlandscaves:" + PlanetStructure.SHRINE.name().toLowerCase(), BlockFace.UP),
                    new StructureTrack(plugin, 6, 0, -6, -11, 0, 1, "badlandscaves:" + PlanetStructure.SHRINE_DESTROYED.name().toLowerCase(), BlockFace.UP),
                    new StructureTrack(plugin, 10, -1, -11, -19, 0, 1, "badlandscaves:" + PlanetStructure.CURSED_HOUSE.name().toLowerCase(), BlockFace.UP),
                    new StructureTrack(plugin, 6, 0, -6, -11, 0, 1, "badlandscaves:" + PlanetStructure.TENT.name().toLowerCase(), BlockFace.UP),
                    new StructureTrack(plugin, -9, 0, -8, 1, 0, 1, "badlandscaves:" + PlanetStructure.HOUSE.name().toLowerCase(), BlockFace.UP),
                    new StructureTrack(plugin, -9, 0, -8, 1, 0, 1, "badlandscaves:" + PlanetStructure.HOUSE_ABANDONED.name().toLowerCase(), BlockFace.UP),
                    new StructureTrack(plugin, -9, 0, -8, 1, 0, 1, "badlandscaves:" + PlanetStructure.HOUSE_DESTROYED.name().toLowerCase(), BlockFace.UP),

            };

            for (StructureTrack structure : structures) {
                if (queried.name().equalsIgnoreCase(structure.getStructureName().split(":")[1])) {
                    structure.load(origin);
                    return;
                }
            }
        }
    }
}
