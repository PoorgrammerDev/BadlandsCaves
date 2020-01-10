package me.fullpotato.badlandscaves.badlandscaves.WorldGeneration;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;

import java.util.Random;

public class pillarPopulator extends BlockPopulator {
    @Override
    public void populate(World world, Random random, Chunk source) {
        if (random.nextBoolean()) {
            int pillar_amt = random.nextInt(30);
            if (pillar_amt == 10) {
                int x = random.nextInt(15);
                int z = random.nextInt(15);
                int y_bottom;
                int y_top;

                for (y_bottom = world.getMaxHeight() - 80; source.getBlock(x, y_bottom, z).getType().equals(Material.AIR); y_bottom--);
                for (y_top = world.getMaxHeight() - 80; source.getBlock(x, y_top, z).getType().equals(Material.AIR); y_top++);

                for (int y = y_bottom; y < y_top; y++) {
                    source.getBlock(x,y,z).setType(Material.BEDROCK);
                }
            }
        }
    }
}
