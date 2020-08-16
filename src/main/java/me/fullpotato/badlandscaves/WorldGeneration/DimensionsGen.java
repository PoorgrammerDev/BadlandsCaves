package me.fullpotato.badlandscaves.WorldGeneration;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.util.noise.PerlinOctaveGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class DimensionsGen extends ChunkGenerator {
    private final Biome biome;
    private final double scaleRand;
    private final double frequency;
    private final double amplitude;
    private final Material[] blocks = new Material[3];

    public DimensionsGen(Biome biome) {
        this.biome = biome;

        final Random random = new Random();
        scaleRand = (random.nextDouble() / 5.0);
        frequency = random.nextDouble() / 50;
        amplitude = random.nextDouble() / 50;

        switch (biome) {
            case DESERT:
                blocks[0] = Material.SAND;
                blocks[1] = Material.SANDSTONE;
                blocks[2] = Material.STONE;
                break;
            case ICE_SPIKES:
                blocks[0] = Material.WATER;
                blocks[1] = Material.PACKED_ICE;
                blocks[2] = Material.BLUE_ICE;
                break;
            case MUSHROOM_FIELD_SHORE:
                blocks[0] = Material.MYCELIUM;
                blocks[1] = Material.DIRT;
                blocks[2] = Material.STONE;
                break;
            case SNOWY_BEACH:
                blocks[0] = Material.SAND;
                blocks[1] = blocks[0];
                blocks[2] = Material.STONE;
                break;
            default:
                blocks[0] = Material.GRASS_BLOCK;
                blocks[1] = Material.DIRT;
                blocks[2] = Material.STONE;
                break;
        }
    }

    @Override
    public boolean shouldGenerateCaves() {
        return true;
    }

    @Override
    public boolean shouldGenerateDecorations() {
        return true;
    }

    @Override
    public @NotNull ChunkData generateChunkData(@NotNull World world, @NotNull Random random, int chunk_x, int chunk_z, @NotNull BiomeGrid biome) {
        ChunkData chunk = createChunkData(world);

        PerlinOctaveGenerator generator = new PerlinOctaveGenerator(random, 128);
        generator.setScale(0.2 + scaleRand);

        int currentHeight;
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                //biome setting
                for (int y = 0; y < world.getMaxHeight(); y++) {
                    biome.setBiome(x, y, z, this.biome);
                }

                currentHeight = (int) (Math.max((generator.noise((chunk_x * 16.0) + x, (chunk_z * 16.0) + z, frequency, amplitude, true) + 1), 0) + 60);
                if (currentHeight > 0) {
                    chunk.setBlock(x, currentHeight, z, blocks[0]);

                    for (int y = currentHeight - 1; y > currentHeight - 5 && y >= 0; y--) {
                        chunk.setBlock(x, y, z, blocks[1]);
                    }

                    for (int y = currentHeight - 5; y > 0; y--) {
                        chunk.setBlock(x, y, z, blocks[2]);
                    }

                    chunk.setBlock(x, 0, z, Material.BEDROCK);
                }
            }
        }
        return chunk;
    }
}
