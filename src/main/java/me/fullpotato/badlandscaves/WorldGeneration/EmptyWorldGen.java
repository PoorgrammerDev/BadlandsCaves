package me.fullpotato.badlandscaves.WorldGeneration;

import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EmptyWorldGen extends ChunkGenerator {
    private final Biome biome;

    public EmptyWorldGen() {
        this.biome = null;
    }

    public EmptyWorldGen(Biome biome) {
        this.biome = biome;
    }

    @Override
    public @NotNull List<BlockPopulator> getDefaultPopulators(@NotNull World world) {
        return new ArrayList<>();
    }

    @Override
    public boolean canSpawn (@NotNull World world, int x, int z) {
        return false;
    }

    @Override
    public @NotNull ChunkData generateChunkData(@NotNull World world, @NotNull Random random, int chunkX, int chunkZ, @NotNull BiomeGrid biome) {
        if (this.biome != null) {
            for (int x = 0; x < 16; x++) {
                for (int y = 0; y < world.getMaxHeight(); y++) {
                    for (int z = 0; z < 16; z++) {
                        biome.setBiome(x, y, z, this.biome);
                    }
                }
            }
        }

        return createChunkData(world);
    }

}
