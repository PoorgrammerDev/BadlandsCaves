package me.fullpotato.badlandscaves.badlandscaves.WorldGeneration;

import org.bukkit.*;

public class EmptyWorld {
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
        world_empty.setDifficulty(Difficulty.PEACEFUL);


    }
}
