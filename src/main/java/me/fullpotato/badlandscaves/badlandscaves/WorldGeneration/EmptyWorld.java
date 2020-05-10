package me.fullpotato.badlandscaves.badlandscaves.WorldGeneration;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.*;

public class EmptyWorld {
    private BadlandsCaves plugin;

    public EmptyWorld(BadlandsCaves plugin) {
        this.plugin = plugin;
    }

    public void gen_void_world() {
        WorldCreator emptyworld = new WorldCreator("world_empty");
        emptyworld.environment(World.Environment.THE_END)
                .type(WorldType.FLAT)
                .generator(new EmptyWorldGen());
        World world_empty = emptyworld.createWorld();
        world_empty.setGameRule(GameRule.DO_INSOMNIA, false);
        world_empty.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        world_empty.setGameRule(GameRule.FALL_DAMAGE, false);
        world_empty.setGameRule(GameRule.DISABLE_RAIDS, true);
        world_empty.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        world_empty.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        world_empty.setGameRule(GameRule.MOB_GRIEFING, false);
        world_empty.setGameRule(GameRule.REDUCED_DEBUG_INFO, false);
        world_empty.setDifficulty(Difficulty.HARD);

        PreventDragon.preventDragonSpawn(world_empty);
    }
}
