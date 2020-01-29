package me.fullpotato.badlandscaves.badlandscaves.WorldGeneration;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.badlandscaves.Runnables.makeReincarnationWorld;
import org.bukkit.*;

public class reincarnationWorld {
    private BadlandsCaves plugin;
    public reincarnationWorld (BadlandsCaves bcav) {
        plugin = bcav;
    }

    public void gen_reincarnation_world() {
        WorldCreator reincarnation = new WorldCreator("world_reincarnation");
        reincarnation.environment(World.Environment.THE_END)
                .type(WorldType.FLAT)
                .generator(new emptyWorldGen())
                .generateStructures(false);
        World world_reincarnation = reincarnation.createWorld();
        world_reincarnation.setGameRule(GameRule.DO_INSOMNIA, false);
        world_reincarnation.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        world_reincarnation.setGameRule(GameRule.FALL_DAMAGE, false);
        world_reincarnation.setGameRule(GameRule.DISABLE_RAIDS, true);
        world_reincarnation.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        world_reincarnation.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        world_reincarnation.setGameRule(GameRule.MOB_GRIEFING, false);
        world_reincarnation.setDifficulty(Difficulty.HARD);

        new makeReincarnationWorld(plugin, world_reincarnation).runTaskLater(plugin, 20);
    }
}
