package me.fullpotato.badlandscaves.badlandscaves.WorldGeneration;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;

import java.util.Random;

public class holePopulator extends BlockPopulator {
    @Override
    public void populate(World world, Random random, Chunk source) {
        if (random.nextBoolean()) {
            int water_amt = random.nextInt(3);
            if (water_amt == 0) {
                int x = random.nextInt(15);
                int z = random.nextInt(15);
                int y_top;
                for (y_top = world.getMaxHeight() - 100; source.getBlock(x, y_top, z).getType().equals(Material.AIR); y_top++);
                for (int y = y_top; y < 215; y++) {
                    source.getBlock(x,y,z).setType(Material.AIR);
                }
            }
        }
    }
}
