package me.fullpotato.badlandscaves.badlandscaves.Runnables;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.noise.SimplexOctaveGenerator;

import java.util.Random;

public class makeReincarnationWorld extends BukkitRunnable {
    private BadlandsCaves plugin;
    private World world;
    public makeReincarnationWorld (BadlandsCaves bcav, World wrld) {
        plugin = bcav;
        world = wrld;
    }

    @Override
    public void run() {
        int clear_lim = 100;
        //clears the area first
        for (int x = -clear_lim; x <= clear_lim; x++) {
            for (int z = -clear_lim; z <= clear_lim; z++) {
                for (int y = -clear_lim; y <= clear_lim; y++) {
                    Location clear = new Location(world, x, y, z);
                    clear.getBlock().setType(Material.AIR);
                }
            }
        }


        Random random = new Random(world.getSeed());
        SimplexOctaveGenerator generator = new SimplexOctaveGenerator(random, 8);
        generator.setScale(0.1D);
        Location origin = new Location(world, 0, 64, 0);

        int currentHeight, rand;
        int inner_lim = 10;
        int upper_lim = 80;
        int lower_lim = 50;

        //generate islands
        for (int x = -upper_lim; x <= upper_lim; x++) {
            for (int z = -upper_lim; z <= upper_lim; z++) {
                Location test = new Location(world, x, 64, z);
                if (test.distance(origin) < inner_lim || (test.distance(origin) > lower_lim && test.distance(origin) < upper_lim)) {
                    currentHeight = (int) ((generator.noise(x, z, 0.5D, 0.05D, true) + 1) *5D + 60D);
                    int thickness =  random.nextInt(10);
                    for (int y = currentHeight; y >= currentHeight - thickness; y--) {
                        Location block_loc = new Location(world, x, y, z);
                        rand = random.nextInt(3);
                        if (rand == 0) block_loc.getBlock().setType(Material.COAL_BLOCK);
                        else if (rand == 1) block_loc.getBlock().setType(Material.BLACK_CONCRETE);
                        else block_loc.getBlock().setType(Material.BLACK_WOOL);
                    }
                }
            }
        }

        /*
        generate shrines at:
        -65 0
        0 65
        0 -65


        the player spawns at 65 0 so no shrine there

         */
    }
}