package me.fullpotato.badlandscaves.badlandscaves.WorldGeneration;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.noise.SimplexOctaveGenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class chunkGenerator extends ChunkGenerator {
    //TODO work on this, make it underground somehow. good luck
    int currentHeight = 50;

    @Override
    public List<BlockPopulator> getDefaultPopulators(World world) {
        List<BlockPopulator> populators = new ArrayList<>();
        //populators.add(new coalPopulator());
        populators.add(new holePopulator());
        //populators.add(new lakePopulator());

        return populators;
    }

    @Override
    public boolean canSpawn (World world, int x, int z) {
        return false;
    }

    public int coordtoByte (int x, int y, int z) {
        return (x * 16 + z) * 128 + y;
    }

    @Override
    public ChunkData generateChunkData(World world, Random random, int chunkX, int chunkZ, BiomeGrid biome) {
        SimplexOctaveGenerator generator = new SimplexOctaveGenerator(new Random(world.getSeed()), 8);
        ChunkData chunk = createChunkData(world);
        generator.setScale(0.3D);

        for (int X = 0; X < 16; X++) {
            for (int Z = 0; Z < 16; Z++) {
                currentHeight = (int) ((generator.noise(chunkX*16+X, chunkZ*16+Z, 0.5D, 0.05D, true) + 1) *15D + 170D);
                int random_terracotta_color = new Random().nextInt(16);

                /*
                //water
                for (int Y = 255; Y >= currentHeight + 1; Y--) {
                    chunk.setBlock(X, Y, Z, Material.WATER);
                }
                 */

                //terracotta for top blocks
                for (int Y = currentHeight; Y >= currentHeight - 4; Y--) {
                    chunk.setBlock(X, Y, Z, Material.TERRACOTTA);
                }

               //stone ceilings
                for (int Y = currentHeight - 5; Y >= currentHeight - 30; Y--) {
                    chunk.setBlock(X, Y, Z, Material.STONE);
                }

                //stone floors
                if (currentHeight - 180 > 0) {
                    for (int Y = currentHeight - 180; Y >= 0; Y--) {
                        chunk.setBlock(X, Y, Z, Material.STONE);
                    }
                }

                for (int Y = 0; Y < 255; Y++) {
                    biome.setBiome(X, Y, Z, Biome.MODIFIED_WOODED_BADLANDS_PLATEAU);
                }




                //the middle part stuff

                int real_x = X + chunkX * 16;
                int real_z = Z + chunkZ * 16;

                for (int y = 64; y < 128; y++) {
                    double density = generator.noise(real_x, y, real_z, 0.5, 0.5);
                    double threshold = 0.0;

                }



            }
        }
        return chunk;
    }


}
