package me.fullpotato.badlandscaves.WorldGeneration;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.util.noise.PerlinOctaveGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class PlanetsGeneration extends ChunkGenerator {

    @Override
    public @NotNull ChunkData generateChunkData(@NotNull World world, @NotNull Random random, int chunk_x, int chunk_z, @NotNull BiomeGrid biome) {
        PerlinOctaveGenerator generator = new PerlinOctaveGenerator(random, 128);
        generator.setScale(0.2);
        ChunkData chunk = createChunkData(world);

        int currentHeight;
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                currentHeight = (int) ((((generator.noise((chunk_x * 16) + x, (chunk_z * 16) + z, 0.005, 0.005, true) + 1)
                        * ((Math.max(Math.abs(chunk_x) + Math.abs(chunk_z) - 2, 0))))) + 1);

                if (currentHeight > 0) {
                    chunk.setBlock(x, currentHeight, z, Material.GRASS_BLOCK);

                    for (int y = currentHeight - 1; y > currentHeight - 5 && y >= 0; y--) {
                        chunk.setBlock(x, y, z, Material.DIRT);
                    }

                    for (int y = currentHeight - 5; y > currentHeight - 25 && y >= 0; y--) {
                        chunk.setBlock(x, y, z, Material.STONE);
                    }
                }

            }
        }


        return chunk;
    }
}
