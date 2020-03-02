package me.fullpotato.badlandscaves.badlandscaves.WorldGeneration;

import org.bukkit.*;

public class ReflectionWorld {
    public void gen_refl_world() {
        WorldCreator reflectionworld = new WorldCreator("world_reflection");
        reflectionworld.environment(World.Environment.NORMAL)
                .generator(new ReflectionGen());
        World world_reflection = reflectionworld.createWorld();
        world_reflection.setGameRule(GameRule.DO_INSOMNIA, false);
        world_reflection.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        world_reflection.setGameRule(GameRule.FALL_DAMAGE, false);
        world_reflection.setGameRule(GameRule.DISABLE_RAIDS, true);
        world_reflection.setGameRule(GameRule.KEEP_INVENTORY, true);
        world_reflection.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        world_reflection.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        world_reflection.setGameRule(GameRule.MOB_GRIEFING, false);
        world_reflection.setGameRule(GameRule.RANDOM_TICK_SPEED, 0);
        world_reflection.setDifficulty(Difficulty.HARD);
    }
}
