package me.fullpotato.badlandscaves.badlandscaves.WorldGeneration;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.*;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

public class descensionWorld {
    private BadlandsCaves plugin;
    public descensionWorld(BadlandsCaves bcav) {
        plugin = bcav;
    }

    public void gen_descension_world() {
        WorldCreator descension = new WorldCreator("world_descension");
        descension.environment(World.Environment.THE_END)
                .type(WorldType.FLAT)
                .generator(new emptyWorldGen());
        World world_descension = descension.createWorld();
        world_descension.setSpawnLocation(0, 197, 0);
        world_descension.setGameRule(GameRule.DO_INSOMNIA, false);
        world_descension.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        world_descension.setGameRule(GameRule.FALL_DAMAGE, false);
        world_descension.setGameRule(GameRule.DISABLE_RAIDS, true);
        world_descension.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        world_descension.setGameRule(GameRule.MOB_GRIEFING, false);
        world_descension.setDifficulty(Difficulty.HARD);

        world_descension.getBlockAt(0,250, 0).setType(Material.BARRIER);
        world_descension.getBlockAt(0,196, 0).setType(Material.BARRIER);

    }
}
