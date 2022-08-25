package me.fullpotato.badlandscaves.WorldGeneration;

import java.util.Random;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.util.noise.SimplexOctaveGenerator;
import org.jetbrains.annotations.NotNull;

import me.fullpotato.badlandscaves.BadlandsCaves;

public class DimensionsGen extends ChunkGenerator {
    private final BadlandsCaves plugin;
    private final Biome biome;
    private final int chaos;
    private final Material[] blocks = new Material[2];

    public DimensionsGen(BadlandsCaves plugin, Biome biome, int chaos) {
        this.plugin = plugin;
        this.biome = biome;
        this.chaos = chaos;
        this.populateBlockArray();
    }

    @Override
    public boolean shouldGenerateCaves() {
        return false;
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
        final double persistence = 0.25;                            // Simplex Noise param: how much each octave affects overall shape
        final int variance = random.nextInt(15) + 15;               // "Y-scale" of the noise; how much the terrain's height changes based on noise
        final int center = random.nextInt(60) + 90;                 // Center y-level
        final int layerNoiseOffset = (random.nextInt(5000) + 1000); // Offset x and z values on where to sample noise for y-levels of stone layer beginning and void layer beginning
        final double threshold = (0.2D * random.nextDouble()) + 0.1D; // Threshold value for 3D noise between [0.1, 0.3]
        final double caveThreshold = (0.2D * random.nextDouble()) + 0.5D; // Threshold value for 3D noise between [0.5, 0.7]
        final int inverseSquash = random.nextInt(75) + 75;           //Higher values, less squashing of surface layer; [75,150]

        //Void layer variables
        final double voidThreshold = (0.2D * random.nextDouble()) + 0.4D; // Threshold value for 3D noise between [0.4, 0.6]
        //Y offset for void top layer (based on world's Chaos value)
        final int voidTopLayerOffset = (chaos / 5 > 0) ? Math.min(random.nextInt(chaos / 5), center - variance - 10) : 0;
    
        final SimplexOctaveGenerator generator = new SimplexOctaveGenerator(world.getSeed(), octaves);
        generator.setScale(0.0375D + (random.nextDouble() * 0.0125D));

        //Generating the actual chunk shape
        for (int x = 0; x < 16; ++x) {
            for (int z = 0; z < 16; ++z) {
                // GENERATING 2D HEIGHT ==============================================
                //generate noise value between [-1,1]
                final double heightNoise = generator.noise((chunkX * 16) + x, (chunkZ * 16) + z, lacunarity, persistence, true);
                final double layerNoiseStone = generator.noise((chunkX * 16) + x + layerNoiseOffset, (chunkZ * 16) + z + layerNoiseOffset, lacunarity, persistence, true);
                final double layerNoiseVoid = generator.noise((chunkX * 16) + x - layerNoiseOffset, (chunkZ * 16) + z - layerNoiseOffset, lacunarity, persistence, true);
           
                //middle layer is 120; can go 60 down and 60 up
                final int height = (int) (heightNoise * variance) + center;     // [center - variance, center + variance]
                final int stoneDepth = (int) (layerNoiseStone + 5);             // [4,6]
                final int voidLayerBegins = (int) (layerNoiseVoid * (10 + voidTopLayerOffset)) + (30 + voidTopLayerOffset);   // default [20,40] ; max [40,60]
                // ===================================================================

                //Set the biome
                for (int y = voidLayerBegins; y < world.getMaxHeight(); ++y) {
                    //Set the biome
                    biome.setBiome(x, y, z, this.biome);
                }

                //Generating the top landscape using 3D noise
                for (int y = (height + (stoneDepth * 5)); y >= (height - stoneDepth); --y) {

                    //Noise adjusted by density value to squash blocks down
                    //Density value decreases as Y increases, vice versa. Center point is the 'height' value.
                    double noise = generator.noise((chunkX * 16) + x, y, (chunkZ * 16) + z, lacunarity, persistence, true);
                    noise = (noise + 1.0) / 2.0;    //Transform noise from [-1, 1] -> [0, 1]
                    
                    //Apply density value to it
                    noise -= ((double) y - center) / inverseSquash;

                    if (noise > threshold) {
                        chunk.setBlock(x, y, z, (chunk.getType(x, y + 1, z).isAir()) ? blocks[0] : blocks[1]);
                    }
                }

                //Stone layer ----------

                //Fill in stone layer
                chunk.setRegion(x, voidLayerBegins, z, x + 1, height - stoneDepth, z + 1, Material.STONE);

                //Carve out caves in the stone layer
                for (int y = voidLayerBegins + 3; y < (height - stoneDepth - 3); ++y) {
                    double noise = generator.noise((chunkX * 16) + x, y, (chunkZ * 16) + z, lacunarity, persistence, true);
                    noise = (noise + 1.0) / 2.0;    //Transform noise from [-1, 1] -> [0, 1]

                    if (noise > caveThreshold) {
                        chunk.setBlock(x, y, z, Material.CAVE_AIR);
                    }
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
                    //Unfortuantely this biome cannot be set to THE_VOID (which was the original intention)
                    //because that biome prohibits monster spawns, so this is used as a stand-in
                    biome.setBiome(x, y, z, Biome.GRAVELLY_MOUNTAINS);
                }
            }
        }

        saveStats(world);
        return chunk;
    }

    @Override
    public List<BlockPopulator> getDefaultPopulators(World world) {
        final List<BlockPopulator> populators = super.getDefaultPopulators(world);

        //Add Titanium Ore populator 
        populators.add(new OrePopulator(Material.STONE, Material.DEAD_TUBE_CORAL_BLOCK, 60, 30, 5, 2, 8));

        //Add Energium Ore populator
        populators.add(new OrePopulator(Material.BLACKSTONE, Material.DEAD_BRAIN_CORAL_BLOCK, 29, 1, 5, 1, 4));

        return populators;
    }

    public void populateBlockArray () {
        switch (biome) {
            case DESERT:
                blocks[0] = Material.SAND;
                blocks[1] = Material.SANDSTONE;
                break;
            case MUSHROOM_FIELD_SHORE:
                blocks[0] = Material.MYCELIUM;
                blocks[1] = Material.DIRT;
                break;
            default:
                blocks[0] = Material.GRASS_BLOCK;
                blocks[1] = Material.DIRT;
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
