package me.fullpotato.badlandscaves.badlandscaves.Runnables;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
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
        Random random = new Random(world.getSeed());
        SimplexOctaveGenerator noise = new SimplexOctaveGenerator(random, 8);

        //TODO add code to make donut
    }
}
