package me.fullpotato.badlandscaves.WorldGeneration;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.SupernaturalPowers.DescensionStage.MakeDescensionStage;
import me.fullpotato.badlandscaves.Util.UnloadedWorld;
import org.bukkit.*;

import java.util.Random;

public class DescensionWorld {
    private final BadlandsCaves plugin;
    private final MakeDescensionStage makeStage;
    public DescensionWorld(BadlandsCaves plugin, Random random) {
        this.plugin = plugin;
        makeStage = new MakeDescensionStage(plugin, random);
    }

    public void gen_descension_world() {
        UnloadedWorld unloadedWorld = new UnloadedWorld(plugin.getDescensionWorldName());
        boolean alreadyExists = unloadedWorld.exists();

        WorldCreator descension = new WorldCreator(plugin.getDescensionWorldName());
        descension.environment(World.Environment.THE_END)
                .type(WorldType.FLAT)
                .generator(new EmptyWorldGen());
        World world_descension = plugin.getServer().createWorld(descension);
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

        if (!alreadyExists) makeStage.runTaskLater(plugin, 2);
    }
}
