package me.fullpotato.badlandscaves.WorldGeneration;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.util.noise.SimplexOctaveGenerator;
import org.jetbrains.annotations.NotNull;

import me.fullpotato.badlandscaves.BadlandsCaves;

public class DimensionsGen extends ChunkGenerator {
    private final BadlandsCaves plugin;
    private final Biome biome;
    private final int chaos;
    private final Material[] blocks = new Material[3];

    public DimensionsGen(BadlandsCaves plugin, Biome biome, int chaos) {
        this.plugin = plugin;
        this.biome = biome;
        this.chaos = chaos;
        this.populateBlockArray();
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
    public @NotNull ChunkData generateChunkData(@NotNull World world, @NotNull Random random, int chunkX, int chunkZ, @NotNull BiomeGrid biome) {
        final ChunkData chunk = createChunkData(world);
        random.setSeed(world.getSeed());

        //Values for generation
        final int octaves = 8;                                      // Simplex Noise param: octaves to generate (more octaves produces better detail)
        final double lacunarity = 2.0;                              // Simplex Noise param: how much detail later octaves add to the surface (<1 smoother; 1 same impact; >1 more detail)
        final double persistence = 0.25;                             // Simplex Noise param: how much each octave affects overall shape
        final int variance = random.nextInt(15) + 15;               // "Y-scale" of the noise; how much the terrain's height changes based on noise
        final int center = random.nextInt(90) + 90;                 // Center y-level
        final int layerNoiseOffset = (random.nextInt(5000) + 1000); // Offset x and z values on where to sample noise for y-levels of stone layer beginning and void layer beginning
        final double voidThreshold = (0.2D * random.nextDouble()) + 0.4D; // Threshold value for 3D noise between [0.4, 0.6]
        final int voidTopLayerOffset = Math.min(random.nextInt(chaos / 5), center - variance - 5);

        final SimplexOctaveGenerator generator = new SimplexOctaveGenerator(world.getSeed(), octaves);
        generator.setScale(0.0375D + (random.nextDouble() * 0.0125D));

        //Generating the actual chunk shape
        for (int x = 0; x < 16; ++x) {
            for (int z = 0; z < 16; ++z) {
                //generate noise value between [-1,1]
                final double noise = generator.noise((chunkX * 16) + x, (chunkZ * 16) + z, lacunarity, persistence, true);
                final double layerNoiseStone = generator.noise((chunkX * 16) + x + layerNoiseOffset, (chunkZ * 16) + z + layerNoiseOffset, lacunarity, persistence, true);
                final double layerNoiseVoid = generator.noise((chunkX * 16) + x - layerNoiseOffset, (chunkZ * 16) + z - layerNoiseOffset, lacunarity, persistence, true);
           
                //middle layer is 120; can go 60 down and 60 up
                final int height = (int) (noise * variance) + center;           // [center - variance, center + variance]
                final int stoneDepth = (int) layerNoiseStone + 5;               // [4, 6]
                final int voidLayerBegins = (int) (layerNoiseVoid * (10 + voidTopLayerOffset)) + (30 + voidTopLayerOffset);   // default [20,40] ; max [40,60]

                //Fill in the blocks
                chunk.setBlock(x, height, z, blocks[0]);                                                //Set top block
                chunk.setRegion(x, height - stoneDepth, z, x + 1, height, z + 1, blocks[1]);            //Set blocks between top and {stone}
                chunk.setRegion(x, voidLayerBegins, z, x + 1, height - stoneDepth, z + 1, blocks[2]);   //Fill in stone blocks
                
                //Set the biome
                for (int y = voidLayerBegins; y < world.getMaxHeight(); ++y) {
                    biome.setBiome(x, y, z, this.biome);
                }

                //Void layer --------

                //Fill the top of the layer
                chunk.setRegion(x, voidLayerBegins - stoneDepth, z, x + 1, voidLayerBegins + 1, z + 1, Material.BLACKSTONE);

                for (int y = 0; y < voidLayerBegins; ++y) {
                    //Using 3D noise
                    double voidNoise = generator.noise((chunkX * 16) + x, y, (chunkZ * 16) + z, lacunarity, persistence, true);
                    
                    //Translate voidNoise from [-1, 1] to [0, 1]
                    voidNoise = (voidNoise + 1.0) / 2.0;
                    
                    if (voidNoise > voidThreshold) {
                        chunk.setBlock(x, y, z, Material.BLACKSTONE);
                    }

                    //Set the biome
                    biome.setBiome(x, y, z, Biome.THE_VOID);
                }
            }
        }

        saveStats(world);
        return chunk;
    }

    public void populateBlockArray () {
        switch (biome) {
            case DESERT:
                blocks[0] = Material.SAND;
                blocks[1] = Material.SANDSTONE;
                blocks[2] = Material.STONE;
                break;
            case MUSHROOM_FIELD_SHORE:
                blocks[0] = Material.MYCELIUM;
                blocks[1] = Material.DIRT;
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
        final String prefix = "alternate_dimensions." + world.getName() + ".generator";
        if (plugin.getSystemConfig().contains(prefix + ".biome")) return;

        plugin.getSystemConfig().set(prefix + ".biome", biome.name());
        plugin.getSystemConfig().set(prefix + ".chaos", chaos);
        plugin.saveSystemConfig();
    }
}
