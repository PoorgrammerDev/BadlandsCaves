package me.fullpotato.badlandscaves.WorldGeneration;

import me.fullpotato.badlandscaves.BadlandsCaves;
import org.bukkit.*;

public class WithdrawWorld {
    private final BadlandsCaves plugin;

    public WithdrawWorld(BadlandsCaves plugin) {
        this.plugin = plugin;
    }

    public void gen_void_world() {
        WorldCreator worldCreator = new WorldCreator(plugin.getWithdrawWorldName());
        worldCreator.environment(World.Environment.THE_END)
                .type(WorldType.FLAT)
                .generator(new EmptyWorldGen());
        World withdraw = plugin.getServer().createWorld(worldCreator);
        withdraw.setGameRule(GameRule.DO_INSOMNIA, false);
        withdraw.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        withdraw.setGameRule(GameRule.FALL_DAMAGE, false);
        withdraw.setGameRule(GameRule.DISABLE_RAIDS, true);
        withdraw.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        withdraw.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        withdraw.setGameRule(GameRule.MOB_GRIEFING, false);
        withdraw.setGameRule(GameRule.REDUCED_DEBUG_INFO, false);
        withdraw.setDifficulty(Difficulty.HARD);

        PreventDragon.preventDragonSpawn(withdraw);
    }
}
