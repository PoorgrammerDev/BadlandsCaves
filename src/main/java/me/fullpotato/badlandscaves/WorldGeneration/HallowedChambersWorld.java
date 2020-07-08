package me.fullpotato.badlandscaves.WorldGeneration;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.SupernaturalPowers.DescensionStage.MakeDescensionStage;
import me.fullpotato.badlandscaves.Util.MultiStructureLoader;
import me.fullpotato.badlandscaves.Util.StructureTrack;
import org.bukkit.*;
import org.bukkit.block.BlockFace;

import java.util.Random;

public class HallowedChambersWorld {
    private final BadlandsCaves plugin;
    public HallowedChambersWorld(BadlandsCaves bcav) {
        plugin = bcav;
    }

    public void gen_world() {
        WorldCreator worldCreator = new WorldCreator(plugin.getChambersWorldName());
        worldCreator.environment(World.Environment.THE_END)
                .type(WorldType.FLAT)
                .generator(new EmptyWorldGen());
        World world = plugin.getServer().createWorld(worldCreator);
        world.setSpawnLocation(0, 128, 0);
        world.setGameRule(GameRule.DO_INSOMNIA, false);
        world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        world.setGameRule(GameRule.FALL_DAMAGE, false);
        world.setGameRule(GameRule.DISABLE_RAIDS, true);
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        world.setGameRule(GameRule.MOB_GRIEFING, false);
        world.setGameRule(GameRule.KEEP_INVENTORY, true);
        world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
        world.setGameRule(GameRule.REDUCED_DEBUG_INFO, true);
        world.setGameRule(GameRule.DO_FIRE_TICK, false);
        world.setGameRule(GameRule.DO_TILE_DROPS, false);
        world.setGameRule(GameRule.DO_PATROL_SPAWNING, false);
        world.setGameRule(GameRule.DO_TRADER_SPAWNING, false);
        world.setGameRule(GameRule.SPECTATORS_GENERATE_CHUNKS, false);
        world.setDifficulty(Difficulty.HARD);

        PreventDragon.preventDragonSpawn(world);

        spawnInStructure(null);
    }

    public void spawnInStructure (StructureTrack[] structures) {
        World world = plugin.getServer().getWorld(plugin.getChambersWorldName());
        boolean initialSpawn = structures == null || structures.length <= 0;
        if (initialSpawn && plugin.getSystemConfig().getBoolean("chambers_spawned")) return;

        if (initialSpawn) {
            structures = new StructureTrack[]{
                    new StructureTrack(plugin, new Location(world, 17, 120, -18),0, 0, 0, -32, -5, 1, "badlandscaves:chambers_entrance_lobby", BlockFace.UP),
                    new StructureTrack(plugin, new Location(world, -15, 113, -19),0, 0, 0, -31, 1, 2, "badlandscaves:chambers_intersection", BlockFace.UP),
                    new StructureTrack(plugin, new Location(world, -22, 133, 22),0, 0, 0, -5, -6, -7, "badlandscaves:chambers_magma_hallway", BlockFace.UP),
                    new StructureTrack(plugin, new Location(world, -8, 126, 21),0, 0, 0, -32, 1, 1, "badlandscaves:chambers_magma_maze", BlockFace.UP),
                    new StructureTrack(plugin, new Location(world, -46, 134, 16),0, 0, 0, -31, -7, -31, "badlandscaves:chambers_glowstone_maze", BlockFace.UP),
                    new StructureTrack(plugin, new Location(world, -28, 133, -22),0, 0, 0, 1, -6, 1, "badlandscaves:chambers_soulsand_hallway", BlockFace.UP),
                    new StructureTrack(plugin, new Location(world, -41, 134, -21),0, 0, 0, 1, -7, -31, "badlandscaves:chambers_soulsand_maze", BlockFace.UP),
                    new StructureTrack(plugin, new Location(world, -25, 160, 0),0, 0, 0, -2, -32, -2, "badlandscaves:chambers_keyholder_platform", BlockFace.UP),
                    new StructureTrack(plugin, new Location(world, -5, 159, 1),0, 0, 0, 0, -31, -2, "badlandscaves:chambers_start_barrier", BlockFace.UP),
                    new StructureTrack(plugin, new Location(world, -22, 113, -3),0, 0, 0, -5, -31, 1, "badlandscaves:chambers_tunnel_1", BlockFace.UP),
                    new StructureTrack(plugin, new Location(world, -22, 81, -3),0, 0, 0, -5, -31, 1, "badlandscaves:chambers_tunnel_2", BlockFace.UP),
                    new StructureTrack(plugin, new Location(world, -22, 49, -3),0, 0, 0, -5, -31, 1, "badlandscaves:chambers_tunnel_3", BlockFace.UP),
                    new StructureTrack(plugin, new Location(world, -22, 17, -3),0, 0, 0, -5, -17, 1, "badlandscaves:chambers_tunnel_4", BlockFace.UP),
                    new StructureTrack(plugin, new Location(world, 250, 246, -50),0, 0, 0, 0, -32, 0, "badlandscaves:chambers_boss_stage_1a", BlockFace.UP),
                    new StructureTrack(plugin, new Location(world, 250, 246, -18),0, 0, 0, 0, -32, 0, "badlandscaves:chambers_boss_stage_2a", BlockFace.UP),
                    new StructureTrack(plugin, new Location(world, 250, 246, 14),0, 0, 0, 0, -32, 0, "badlandscaves:chambers_boss_stage_3a", BlockFace.UP),
                    new StructureTrack(plugin, new Location(world, 250, 246, 46),0, 0, 0, 0, -32, 0, "badlandscaves:chambers_boss_stage_4a", BlockFace.UP),
                    new StructureTrack(plugin, new Location(world, 282, 246, -50),0, 0, 0, 0, -32, 0, "badlandscaves:chambers_boss_stage_5a", BlockFace.UP),
                    new StructureTrack(plugin, new Location(world, 282, 246, -18),0, 0, 0, 0, -32, 0, "badlandscaves:chambers_boss_stage_6a", BlockFace.UP),
                    new StructureTrack(plugin, new Location(world, 282, 246, 14),0, 0, 0, 0, -32, 0, "badlandscaves:chambers_boss_stage_7a", BlockFace.UP),
                    new StructureTrack(plugin, new Location(world, 282, 246, 46),0, 0, 0, 0, -32, 0, "badlandscaves:chambers_boss_stage_8a", BlockFace.UP),
                    new StructureTrack(plugin, new Location(world, 314, 246, -50),0, 0, 0, 0, -32, 0, "badlandscaves:chambers_boss_stage_9a", BlockFace.UP),
                    new StructureTrack(plugin, new Location(world, 314, 246, -18),0, 0, 0, 0, -32, 0, "badlandscaves:chambers_boss_stage_10a", BlockFace.UP),
                    new StructureTrack(plugin, new Location(world, 314, 246, 14),0, 0, 0, 0, -32, 0, "badlandscaves:chambers_boss_stage_11a", BlockFace.UP),
                    new StructureTrack(plugin, new Location(world, 314, 246, 46),0, 0, 0, 0, -32, 0, "badlandscaves:chambers_boss_stage_12a", BlockFace.UP),
                    new StructureTrack(plugin, new Location(world, 346, 246, -50),0, 0, 0, 0, -32, 0, "badlandscaves:chambers_boss_stage_13a", BlockFace.UP),
                    new StructureTrack(plugin, new Location(world, 346, 246, -18),0, 0, 0, 0, -32, 0, "badlandscaves:chambers_boss_stage_14a", BlockFace.UP),
                    new StructureTrack(plugin, new Location(world, 346, 246, 14),0, 0, 0, 0, -32, 0, "badlandscaves:chambers_boss_stage_15a", BlockFace.UP),
                    new StructureTrack(plugin, new Location(world, 346, 246, 46),0, 0, 0, 0, -32, 0, "badlandscaves:chambers_boss_stage_16a", BlockFace.UP),
                    new StructureTrack(plugin, new Location(world, 250, 181, -50),0, 0, 0, 0, 1, 0, "badlandscaves:chambers_boss_stage_1b", BlockFace.UP),
                    new StructureTrack(plugin, new Location(world, 250, 181, -18),0, 0, 0, 0, 1, 0, "badlandscaves:chambers_boss_stage_2b", BlockFace.UP),
                    new StructureTrack(plugin, new Location(world, 250, 181, 14),0, 0, 0, 0, 1, 0, "badlandscaves:chambers_boss_stage_3b", BlockFace.UP),
                    new StructureTrack(plugin, new Location(world, 250, 181, 46),0, 0, 0, 0, 1, 0, "badlandscaves:chambers_boss_stage_4b", BlockFace.UP),
                    new StructureTrack(plugin, new Location(world, 282, 181, -50),0, 0, 0, 0, 1, 0, "badlandscaves:chambers_boss_stage_5b", BlockFace.UP),
                    new StructureTrack(plugin, new Location(world, 282, 181, -18),0, 0, 0, 0, 1, 0, "badlandscaves:chambers_boss_stage_6b", BlockFace.UP),
                    new StructureTrack(plugin, new Location(world, 282, 181, 14),0, 0, 0, 0, 1, 0, "badlandscaves:chambers_boss_stage_7b", BlockFace.UP),
                    new StructureTrack(plugin, new Location(world, 282, 181, 46),0, 0, 0, 0, 1, 0, "badlandscaves:chambers_boss_stage_8b", BlockFace.UP),
                    new StructureTrack(plugin, new Location(world, 314, 181, -50),0, 0, 0, 0, 1, 0, "badlandscaves:chambers_boss_stage_9b", BlockFace.UP),
                    new StructureTrack(plugin, new Location(world, 314, 181, -18),0, 0, 0, 0, 1, 0, "badlandscaves:chambers_boss_stage_10b", BlockFace.UP),
                    new StructureTrack(plugin, new Location(world, 314, 181, 14),0, 0, 0, 0, 1, 0, "badlandscaves:chambers_boss_stage_11b", BlockFace.UP),
                    new StructureTrack(plugin, new Location(world, 314, 181, 46),0, 0, 0, 0, 1, 0, "badlandscaves:chambers_boss_stage_12b", BlockFace.UP),
                    new StructureTrack(plugin, new Location(world, 346, 181, -50),0, 0, 0, 0, 1, 0, "badlandscaves:chambers_boss_stage_13b", BlockFace.UP),
                    new StructureTrack(plugin, new Location(world, 346, 181, -18),0, 0, 0, 0, 1, 0, "badlandscaves:chambers_boss_stage_14b", BlockFace.UP),
                    new StructureTrack(plugin, new Location(world, 346, 181, 14),0, 0, 0, 0, 1, 0, "badlandscaves:chambers_boss_stage_15b", BlockFace.UP),
                    new StructureTrack(plugin, new Location(world, 346, 181, 46),0, 0, 0, 0, 1, 0, "badlandscaves:chambers_boss_stage_16b", BlockFace.UP),
                    new StructureTrack(plugin, new Location(world, 303, 254, 3),0, 0, 0, -5, -8, -5, "badlandscaves:chambers_wither_boss_stage_entry_tunnel", BlockFace.UP),
            };
        }


        MultiStructureLoader multiStructureLoader = new MultiStructureLoader(structures);
        multiStructureLoader.loadAll();

        Random random = new Random();
        for (int x = -27; x <= -23; x++) {
            for (int z = -2; z <= 2; z++) {
                for (int y = 0; y <= 127; y++) {
                    if (world.getBlockAt(x, y, z).getType().equals(Material.STONE)) {
                        world.getBlockAt(x, y, z).setType(MakeDescensionStage.getVoidMat(random));
                    }
                }
            }
        }

        for (int x = 298; x <= 302; x++) {
            for (int z = -2; z <= 2; z++) {
                for (int y = 221; y <= 255; y++) {
                    if (world.getBlockAt(x, y, z).getType().equals(Material.STONE)) {
                        world.getBlockAt(x, y, z).setType(MakeDescensionStage.getVoidMat(random));
                    }
                }
            }
        }

        if (initialSpawn) {
            plugin.getSystemConfig().set("chambers_spawned", true);
            plugin.saveSystemConfig();
        }
    }
}
