package me.fullpotato.badlandscaves.WorldGeneration;

import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EmptyWorldGen extends ChunkGenerator {

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
        return createChunkData(world);
    }

}
