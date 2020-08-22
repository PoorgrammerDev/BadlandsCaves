package me.fullpotato.badlandscaves.WorldGeneration;

import me.fullpotato.badlandscaves.BadlandsCaves;
import org.bukkit.*;

import java.util.Random;

public class Backrooms {
    private final BadlandsCaves plugin;
    private final Random random;

    public Backrooms(BadlandsCaves plugin, Random random) {
        this.plugin = plugin;
        this.random = random;
    }

    public void gen_backrooms() {
        WorldCreator backrooms = new WorldCreator(plugin.getBackroomsWorldName());
        backrooms.environment(World.Environment.NORMAL)
                .type(WorldType.FLAT)
                .generator(new BackroomsGen(random));
        World world_backrooms = plugin.getServer().createWorld(backrooms);
        world_backrooms.setSpawnLocation(3, 64, 3);
        world_backrooms.setGameRule(GameRule.DO_INSOMNIA, false);
        world_backrooms.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        world_backrooms.setGameRule(GameRule.FALL_DAMAGE, false);
        world_backrooms.setGameRule(GameRule.DISABLE_RAIDS, true);
        world_backrooms.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        world_backrooms.setGameRule(GameRule.MOB_GRIEFING, false);
        world_backrooms.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        world_backrooms.setGameRule(GameRule.DO_TILE_DROPS, false);
        world_backrooms.setGameRule(GameRule.RANDOM_TICK_SPEED, 0);
        world_backrooms.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
        world_backrooms.setGameRule(GameRule.DO_FIRE_TICK, false);
        world_backrooms.setGameRule(GameRule.REDUCED_DEBUG_INFO, true);
        world_backrooms.setDifficulty(Difficulty.HARD);
    }
}
