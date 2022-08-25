package me.fullpotato.badlandscaves.WorldGeneration;

import java.util.HashSet;
import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.generator.BlockPopulator;

public class OrePopulator extends BlockPopulator {

    private final Material baseMaterial;
    private final Material oreBlock;
    private final int topBound;
    private final int bottomBound;
    private final int veinHeightDivisor;
    private final int minVeinSize;
    private final int maxVeinSize;

    private final BlockFace[] adjacentFaces = {
        BlockFace.NORTH,
        BlockFace.EAST,
        BlockFace.WEST,
        BlockFace.SOUTH,
        BlockFace.UP,
        BlockFace.DOWN,
    };

    /**
     * Constructor
     * @param baseMaterial The pre-existing block that the ore will replace
     * @param oreBlock The ore block
     * @param topBound Y-level upper bound
     * @param bottomBound Y-level lower bound
     * @param veinHeightDivisor Max # of veins is calculated by (topBound - bottomBound) / veinHeightDivisor. Increase for less veins; decrease for more.
     */
    public OrePopulator(Material baseMaterial, Material oreBlock, int topBound, int bottomBound, int veinHeightDivisor, int minVeinSize, int maxVeinSize) {
        super();

        this.baseMaterial = baseMaterial;
        this.oreBlock = oreBlock;
        this.topBound = topBound;
        this.bottomBound = bottomBound;
        this.veinHeightDivisor = veinHeightDivisor;
        this.minVeinSize = minVeinSize;
        this.maxVeinSize = maxVeinSize;
    }

    @Override
    public void populate(World world, Random random, Chunk source) {
        final int height = topBound - bottomBound;
        if (height / veinHeightDivisor <= 0) return;

        final int veins = random.nextInt(height / veinHeightDivisor);

        for (int i = 0; i < veins; ++i) {
            final Location location = getValidLocation(source, random);
            final int veinSize = random.nextInt(this.maxVeinSize - this.minVeinSize) + this.minVeinSize;

            if (location == null) {
                System.err.println("Failed to find valid location for ore generation.");
                return;
            }

            generateVein(location, random, source, veinSize);
        }

    }

    private Location getValidLocation(Chunk source, Random random) {
        Location location = new Location(source.getWorld(), 0, 0, 0);
        final int CHUNK_SIZE = 16;
      
        int tries = 0;
        //Generate a random location within bounds
        do {
            location.setX(random.nextInt(CHUNK_SIZE) + (source.getX() * CHUNK_SIZE));
            location.setY(random.nextInt(this.topBound - this.bottomBound) + this.bottomBound);
            location.setZ(random.nextInt(CHUNK_SIZE) + (source.getZ() * CHUNK_SIZE));
            
            ++tries;
        } while (tries < 1000 && !isLocationValid(location, source));

        return (tries < 1000) ? location : null;
    }

    private boolean isLocationValid(Location location, Chunk chunk) {
        return (location.getBlock().getType() == this.baseMaterial &&
        location.getChunk().equals(chunk));
    }

    private void generateVein(final Location origin, final Random random, final Chunk chunk, final int blockSize) {
        final HashSet<String> visited = new HashSet<>();
        final Location[] ores = new Location[blockSize];
        
        //Set the origin as the first
        origin.getBlock().setType(this.oreBlock, false);
        ores[0] = origin.getBlock().getLocation();
        visited.add(encodeLocation(ores[0]));

        //Loop starts from 1 because 0 is already populated
        for (int i = 1; i < blockSize; ++i) {
            Location selectedLoc;
            Block neighbour;

            int tries = 0;
            do {
                //Select a location from one of the already visited locations and an adjacent face
                selectedLoc = ores[random.nextInt(i)];

                //Select a random neighbour from this location
                neighbour = selectedLoc.getBlock().getRelative(adjacentFaces[random.nextInt(adjacentFaces.length)]);

                ++tries;

            } while (tries < 1000 && (visited.contains(encodeLocation(neighbour.getLocation())) || !isLocationValid(neighbour.getLocation(), chunk)));

            if (tries >= 1000) {
                return;
            }

            neighbour.setType(this.oreBlock, false);
            ores[i] = neighbour.getLocation();
            visited.add(encodeLocation(ores[i])); 
        }
    }

    private String encodeLocation(Location location) {
        return "(" + location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ() + ")";
    }

    
}
