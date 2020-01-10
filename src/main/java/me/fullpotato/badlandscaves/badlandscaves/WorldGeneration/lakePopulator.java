package me.fullpotato.badlandscaves.badlandscaves.WorldGeneration;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.generator.BlockPopulator;

import java.util.Random;

public class lakePopulator extends BlockPopulator {
    @Override
    public void populate(World world, Random random, Chunk chunk) {
        if (random.nextInt(100) < 10) {
            int chunk_x = chunk.getX();
            int chunk_z = chunk.getZ();

            int x = chunk_x * 16 + random.nextInt(15) - 8;
            int z = chunk_z * 16 + random.nextInt(15) - 8;
            int y;

            for (y = world.getMaxHeight() - 100; chunk.getBlock(x, y, z).getType().equals(Material.AIR); y--) ;
            y -= 7;

            Block block = world.getBlockAt(x + 8, y, z + 8);

            if (random.nextInt(100) < 70) block.setType(Material.WATER);
            else block.setType(Material.LAVA);

            boolean[] aboolean = new boolean[2048];
            int i = random.nextInt(4) + 4;

            int j, j1, k1;

            for (j = 0; j < i; ++j) {
                double d0 = random.nextDouble() * 6.0D + 3.0D;
                double d1 = random.nextDouble() * 4.0D + 2.0D;
                double d2 = random.nextDouble() * 6.0D + 3.0D;
                double d3 = random.nextDouble() * (16.0D - d0 - 2.0D) + 1.0D + d0 / 2.0D;
                double d4 = random.nextDouble() * (8.0D - d1 - 4.0D) + 2.0D + d1 / 2.0D;
                double d5 = random.nextDouble() * (16.0D - d2 - 2.0D) + 1.0D + d2 / 2.0D;

                for (int k = 1; k < 15; ++k) {
                    for (int l = 1; l < 15; ++l) {
                        for (int i1 = 1; i1 < 7; ++i1) {
                            double d6 = ((double) k - d3) / (d0 / 2.0D);
                            double d7 = ((double) i1 - d4) / (d1 / 2.0D);
                            double d8 = ((double) l - d5) / (d2 / 2.0D);
                            double d9 = d6 * d6 + d7 * d7 + d8 * d8;

                            if (d9 < 1.0D) {
                                aboolean[(k * 16 + l) * 8 + i1] = true;
                            }
                        }
                    }
                }
            }

            for (j = 0; j < 16; ++j) {
                for (k1 = 0; k1 < 16; ++k1) {
                    for (j1 = 0; j1 < 8; ++j1) {
                        if (aboolean[(j * 16 + k1) * 8 + j1]) {
                            world.getBlockAt(x + j, y + j1, z + k1).setType(j1 > 4 ? Material.AIR : block.getType());
                        }
                    }
                }
            }

            for (j = 0; j < 16; ++j) {
                for (k1 = 0; k1 < 16; ++k1) {
                    for (j1 = 4; j1 < 8; ++j1) {
                        if (aboolean[(j * 16 + k1) * 8 + j1]) {
                            int X1 = x + j;
                            int Y1 = y + j1 - 1;
                            int Z1 = z + k1;
                            if (world.getBlockAt(X1, Y1, Z1).getType() == Material.DIRT) {
                                world.getBlockAt(X1, Y1, Z1).setType(Material.GRASS);
                            }
                        }
                    }
                }
            }
        }
    }
}