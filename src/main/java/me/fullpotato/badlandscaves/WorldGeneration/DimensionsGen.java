package me.fullpotato.badlandscaves.WorldGeneration;

import me.fullpotato.badlandscaves.SupernaturalPowers.DescensionStage.MakeDescensionStage;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.util.noise.PerlinOctaveGenerator;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Random;

public class DimensionsGen extends ChunkGenerator {
    private final Biome biome;
    private final double scaleRand;
    private final int raiseFactor;
    private final double frequency;
    private final double amplitude;

    public DimensionsGen(Biome biome) {
        this.biome = biome;

        Random random = new Random();
        scaleRand = (random.nextDouble() / 5.0);
        raiseFactor = random.nextInt(5) + 2;
        frequency = random.nextDouble() / 50;
        amplitude = random.nextDouble() / 50;

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

                double raiser = Math.abs(chunk_x) < 10 && Math.abs(chunk_z) < 10 ? (Math.max((Math.abs((chunk_x * 16.0) + x) + Math.abs((chunk_z * 16.0) + z)) / 16.0, 1) - 2) : Math.max(18 - ((Math.abs(chunk_x) + Math.abs(chunk_z)) / 2.0), 1);
                    currentHeight = (int) (Math.max((generator.noise((chunk_x * 16.0) + x, (chunk_z * 16.0) + z, frequency, amplitude, true) + 1) * raiser * raiseFactor, 0) + 60);

                    if (currentHeight > 0) {
                        Material surface = Material.GRASS_BLOCK;
                        Material subsurface = Material.DIRT;
                        Material under = Material.STONE;

                        if (this.biome.equals(Biome.DESERT)) {
                            surface = Material.SAND;
                            subsurface = Material.SANDSTONE;
                        }
                        else if (this.biome.equals(Biome.ICE_SPIKES)) {
                            surface = Material.WATER;
                            subsurface = Material.PACKED_ICE;
                            under = Material.BLUE_ICE;
                        }
                        else if (this.biome.equals(Biome.MUSHROOM_FIELD_SHORE)) {
                            surface = Material.MYCELIUM;
                        }
                        else if (this.biome.equals(Biome.SNOWY_BEACH)) {
                            surface = Material.SAND;
                            subsurface = surface;
                        }

                        chunk.setBlock(x, currentHeight, z, surface);

                        for (int y = currentHeight - 1; y > currentHeight - 5 && y >= 0; y--) {
                            chunk.setBlock(x, y, z, subsurface);
                        }

                        for (int y = currentHeight - 5; y > 0; y--) {
                            chunk.setBlock(x, y, z, under);
                        }

                        chunk.setBlock(x, 0, z, Material.BEDROCK);
                    }
                }
            }


        return chunk;
    }
}
