package me.fullpotato.badlandscaves.badlandscaves.WorldGeneration;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.*;

public class descensionWorld {
    private BadlandsCaves plugin;
    public descensionWorld(BadlandsCaves bcav) {
        plugin = bcav;
    }

    public void gen_descension_world() {
        WorldCreator descension = new WorldCreator("world_descensio");
        descension.environment(World.Environment.THE_END)
                .type(WorldType.FLAT)
                .generator(new emptyWorldGen());
        World world_descension = descension.createWorld();
        world_descension.setGameRule(GameRule.DO_INSOMNIA, false);
        world_descension.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        world_descension.setGameRule(GameRule.FALL_DAMAGE, false);
        world_descension.setGameRule(GameRule.DISABLE_RAIDS, true);
        world_descension.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        world_descension.setGameRule(GameRule.MOB_GRIEFING, false);
        world_descension.setDifficulty(Difficulty.HARD);


        //makes it so the portal gens at the top of the world and a barrier cage is made
        Location origin = new Location(world_descension, 0, 120, 0);
        for (int x = -5; x <= 5; x++) {
            for (int z = -5; z <= 5; z++) {
                for (int y = 90; y <= 110; y++) {
                    Location test = new Location(world_descension, x, y, z);
                    if (origin.distanceSquared(test) > 9 && origin.distanceSquared(test) < 25) {
                        test.getBlock().setType(Material.BARRIER);
                    }
                }
            }
        }

    }
}
