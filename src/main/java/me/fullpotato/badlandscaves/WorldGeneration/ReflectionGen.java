package me.fullpotato.badlandscaves.WorldGeneration;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.util.noise.SimplexOctaveGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class ReflectionGen extends ChunkGenerator {

    @Override
    public @NotNull ChunkData generateChunkData(World world, Random random, int chunkX, int chunkZ, @NotNull BiomeGrid biome) {
        SimplexOctaveGenerator generator = new SimplexOctaveGenerator(new Random(world.getSeed()), 8);
        ChunkData chunk = createChunkData(world);
        generator.setScale(random.nextDouble() / 15);

        final int offsets = 20;
        final int chunk_offset = random.nextInt(offsets);
        final int constant = 200 - offsets;


        for (int X = 0; X < 16; X++) {
            for (int Z = 0; Z < 16; Z++) {
                int currentHeight = (int) ((generator.noise(chunkX * 16 + X, chunkZ * 16 + Z, 0.5D, 0.05D, true) + 1) * 15D + chunk_offset + constant);

                //top ceiling
                for (int Y = currentHeight; Y >= currentHeight - 5; Y--) {
                    chunk.setBlock(X, Y, Z, randomIceVariant(random, true));
                }

                //bottom ceiling
                for (int Y = currentHeight - 6; Y >= currentHeight - 10; Y--) {
                    chunk.setBlock(X, Y, Z, randomIceVariant(random, false));
                }

                //top floor
                for (int Y = currentHeight - 80 ; Y >= currentHeight - 90; Y--) {
                    chunk.setBlock(X, Y, Z, randomIceVariant(random, false));
                }

                //bottom floor
                for (int Y = currentHeight - 91 ; Y >= currentHeight - 100; Y--) {
                    chunk.setBlock(X, Y, Z, randomIceVariant(random, true));
                }

                for (int Y = 0; Y < 255; Y++) {
                    biome.setBiome(X, Y, Z, Biome.THE_VOID);
                }
            }
        }
        return chunk;
    }

    public Material randomIceVariant (Random random, boolean snow) {
        int rand = random.nextInt(102);

        if (rand <= 25) {
            return Material.ICE;
        }
        else if (rand <= 50) {
            return Material.BLUE_ICE;
        }
        else if (rand <= 75) {
            return Material.PACKED_ICE;
        }
        else if (rand <= 100) {
            rand = snow ? random.nextInt(4) : random.nextInt(5);
            if (rand == 0) return Material.LIGHT_BLUE_CONCRETE;
            else if (rand == 1) return Material.LIGHT_BLUE_GLAZED_TERRACOTTA;
            else if (rand == 2) return Material.LIGHT_BLUE_STAINED_GLASS;
            else if (rand == 3) return Material.LIGHT_BLUE_WOOL;
            else return Material.LIGHT_BLUE_CONCRETE_POWDER;
        }
        else {
            return snow ? Material.SNOW_BLOCK : Material.SEA_LANTERN;
        }
    }
}
