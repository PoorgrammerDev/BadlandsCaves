package me.fullpotato.badlandscaves.badlandscaves.WorldGeneration;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;

import java.util.Random;

public class coalPopulator extends BlockPopulator {
    @Override
    public void populate(World world, Random random, Chunk chunk) {
        int X, Y, Z;
        boolean isStone;
        for (int i = 1; i < 30; i++) {  // Number of tries
            if (random.nextInt(100) < 60) {  // The chance of spawning
                X = random.nextInt(15);
                Z = random.nextInt(15);
                Y = random.nextInt(40);  // Get randomized coordinates
                if (chunk.getBlock(X, Y, Z).getType().equals(Material.STONE)) {
                    isStone = true;
                    while (isStone) {
                        chunk.getBlock(X, Y, Z).setType(Material.COAL_ORE);
                        if (random.nextInt(100) < 40)  {   // The chance of continuing the vein
                            switch (random.nextInt(5)) {  // The direction chooser
                                case 0: X++; break;
                                case 1: Y++; break;
                                case 2: Z++; break;
                                case 3: X--; break;
                                case 4: Y--; break;
                                case 5: Z--; break;
                            }
                            isStone = ((chunk.getBlock(X, Y, Z).getType().equals(Material.STONE)) && (!(chunk.getBlock(X, Y, Z).getType().equals(Material.COAL_ORE))));
                        } else isStone = false;
                    }
                }
            }
        }
    }
}
