package me.fullpotato.badlandscaves.badlandscaves;

import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;

import java.util.List;
import java.util.Random;

public class chunkGenerator extends ChunkGenerator {
    //TODO work on this, make it underground somehow. good luck

    @Override
    public List<BlockPopulator> getDefaultPopulators(World world) {
        return null;
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
        return null;
    }
}
