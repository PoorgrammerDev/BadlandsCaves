package me.fullpotato.badlandscaves.WorldGeneration;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class BackroomsGen extends ChunkGenerator {
    private final Random random;

    public BackroomsGen(Random random) {
        this.random = random;
    }

    @Override
    public @NotNull ChunkData generateChunkData(@NotNull World world, @NotNull Random random, int chunkX, int chunkZ, @NotNull BiomeGrid biome) {
        ChunkData chunk = createChunkData(world);
        genAt(chunk, 63, Material.SMOOTH_SANDSTONE, Material.GREEN_WOOL, Material.SEA_LANTERN, true);
        genAt(chunk, 43, Material.BLACK_CONCRETE, Material.BLACK_CONCRETE, Material.BLACK_CONCRETE, false);

        return chunk;
    }

    private void genAt(ChunkData chunk, int y, Material wall, Material floor, Material lights, boolean randomlyBlocked) {
        //floor and roof
        chunk.setRegion(0, y, 0, 16, y + 1, 16, floor);
        chunk.setRegion(0, y + 5, 0, 16, y + 6, 16, wall);

        //four surrounding walls of chunk
        chunk.setRegion(0, y + 1, 0, 16, y + 5, 1, wall);
        chunk.setRegion(0, y + 1, 0, 1, y + 5, 16, wall);
        chunk.setRegion(0, y + 1, 15, 16, y + 5, 16, wall);
        chunk.setRegion(15, y + 1, 0, 16, y + 5, 16, wall);

        //middle walls
        chunk.setRegion(7, y + 1, 1, 9, y + 5, 15, wall);
        chunk.setRegion(1,  y + 1, 7, 15, y + 5, 9, wall);

        //doors
        chunk.setRegion(3, y + 1, 0, 5, y + 4, 16, Material.AIR);
        chunk.setRegion(0, y + 1, 3, 16, y + 4, 5, Material.AIR);
        chunk.setRegion(11, y + 1, 0, 13, y + 4, 16, Material.AIR);
        chunk.setRegion(0, y + 1, 11, 16, y + 4, 13, Material.AIR);

        //lights
        chunk.setRegion(2, y + 5, 3, 6, y + 6, 5, lights);
        chunk.setRegion(10, y + 5, 3, 14, y + 6, 5, lights);
        chunk.setRegion(2, y + 5, 11, 6, y + 6, 13, lights);
        chunk.setRegion(10, y + 5, 11, 14, y + 6, 13, lights);

        if (randomlyBlocked) {
            for (int i = 0; i < random.nextInt(2) + 1; i++) {
                int rand = random.nextInt(4);

                if (rand == 0) chunk.setRegion(7, y + 1, 3, 9, y + 4, 5, wall);
                else if (rand == 1) chunk.setRegion(3, y + 1, 7, 5, y + 4, 9, wall);
                else if (rand == 2) chunk.setRegion(7, y + 1, 11, 9, y + 4, 13, wall);
                else chunk.setRegion(11, y + 1, 7, 13, y + 4, 9, wall);
            }
        }
    }
}
