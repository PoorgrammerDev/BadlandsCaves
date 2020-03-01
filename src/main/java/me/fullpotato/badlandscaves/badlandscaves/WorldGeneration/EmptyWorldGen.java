package me.fullpotato.badlandscaves.badlandscaves.WorldGeneration;

import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EmptyWorldGen extends ChunkGenerator {

    @Override
    public List<BlockPopulator> getDefaultPopulators(World world) {
        return new ArrayList<>();
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
        return createChunkData(world);
    }

}
