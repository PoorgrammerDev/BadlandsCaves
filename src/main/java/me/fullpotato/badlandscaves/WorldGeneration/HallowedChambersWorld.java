package me.fullpotato.badlandscaves.WorldGeneration;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.Util.StructureCopier;
import org.bukkit.*;

public class HallowedChambersWorld {
    private final BadlandsCaves plugin;
    public HallowedChambersWorld(BadlandsCaves bcav) {
        plugin = bcav;
    }

    public void gen_world() {
        WorldCreator worldCreator = new WorldCreator(plugin.chambersWorldName);
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

        StructureCopier.copyStructures(plugin.getServer().getWorld(plugin.mainWorldName), world, "chambers");
    }
}
