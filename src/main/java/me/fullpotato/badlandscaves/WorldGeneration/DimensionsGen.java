package me.fullpotato.badlandscaves.WorldGeneration;

import me.fullpotato.badlandscaves.BadlandsCaves;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.util.noise.PerlinOctaveGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class DimensionsGen extends ChunkGenerator {
    private final BadlandsCaves plugin;
    private final Biome biome;
    private final double scale;
    private final double frequency;
    private final double amplitude;
    private final Material[] blocks = new Material[3];
    private final boolean firstGen;
    private final int chaos;
    private final int middle = 60;

    public DimensionsGen(BadlandsCaves plugin, Biome biome) {
        this.plugin = plugin;
        this.biome = biome;
        final Random random = new Random();
        this.scale = (random.nextInt(32) + 32);
        this.frequency = (random.nextDouble()) + 0.25;
        this.amplitude = (random.nextDouble()) + 0.25;
        this.firstGen = true;
        this.chaos = plugin.getSystemConfig().getInt("chaos_level");
        this.setBlocks();
    }

    public DimensionsGen(BadlandsCaves plugin, Biome biome, double scale, double frequency, double amplitude, int chaos) {
        this.plugin = plugin;
        this.biome = biome;
        this.scale = scale;
        this.frequency = frequency;
        this.amplitude = amplitude;
        this.firstGen = false;
        this.chaos = chaos;
        this.setBlocks();
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
        final ChunkData chunk = createChunkData(world);
        final PerlinOctaveGenerator generator = new PerlinOctaveGenerator(world.getSeed(), 8);
        generator.setScale(1 / scale);
        int chunkVerticalShift = 0;
        if (chaos / 10 > 0 && random.nextInt(100) < chaos) {
            chunkVerticalShift = random.nextInt(chaos / 10) - (chaos / 20);
        }


        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                //biome setting
                for (int y = 0; y < world.getMaxHeight(); y++) {
                    biome.setBiome(x, y, z, this.biome);
                }

                final double noise = generator.noise((chunk_x * 16) + x, (chunk_z * 16) + z, frequency, amplitude);
                final int currentHeight = (int) ((middle + (noise) * middle / 3) + chunkVerticalShift);
                if (currentHeight > 0) {
                    //base world generation
                    chunk.setBlock(x, currentHeight, z, blocks[0]);
                    chunk.setRegion(x, currentHeight - 4, z, x + 1, currentHeight, z + 1, blocks[1]);
                    chunk.setRegion(x, 1, z, x + 1, currentHeight - 4, z + 1, blocks[2]);

                    final int blackstoneHeight = (int) ((Math.max((noise * chaos) + (random.nextInt(4) - 2) + (chaos / 3.0), 0)));
                    chunk.setRegion(x, 0, z, x + 1, blackstoneHeight, z + 1, Material.BLACKSTONE);
                }
            }
        }
        if (firstGen) saveStats(world);
        return chunk;
    }

    public void setBlocks () {
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

    public void saveStats(World world) {
        plugin.getSystemConfig().set("dim_stats." + world.getName() + ".generator.scale", scale);
        plugin.getSystemConfig().set("dim_stats." + world.getName() + ".generator.frequency", frequency);
        plugin.getSystemConfig().set("dim_stats." + world.getName() + ".generator.amplitude", amplitude);
        plugin.getSystemConfig().set("dim_stats." + world.getName() + ".generator.biome", biome.name());
        plugin.getSystemConfig().set("dim_stats." + world.getName() + ".generator.chaos", chaos);
        plugin.saveSystemConfig();
    }
}
