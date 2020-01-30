package me.fullpotato.badlandscaves.badlandscaves.Runnables;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
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
        Random random = new Random(world.getSeed());
        SimplexOctaveGenerator generator = new SimplexOctaveGenerator(random, 8);
        generator.setScale(0.05D);
        int lim = 70;
        Location origin = new Location(world, 0, 64, 0);
        int currentHeight;
        for (int x = -lim; x <= lim; x++) {
            for (int z = -lim; z <= lim; z++) {
                Location test = new Location(world, x, 64, z);
                if (test.distance(origin) < 10 || (test.distance(origin) > 50 && test.distance(origin) < lim)) {
                    currentHeight = (int) ((generator.noise(x, z, 0.5D, 0.05D, true) + 1) *5D + 60D);
                    int thickness =  random.nextInt(10);
                    for (int y = currentHeight; y >= currentHeight - thickness; y--) {
                        Location block_loc = new Location(world, x, y, z);
                        Block block = block_loc.getBlock();
                        block.setType(Material.DIAMOND_BLOCK);
                    }

                }
            }
        }
    }
}