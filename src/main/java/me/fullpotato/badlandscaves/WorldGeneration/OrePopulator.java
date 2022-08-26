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
import org.bukkit.util.Vector;

public class OrePopulator extends BlockPopulator {

    private final Material baseMaterial;
    private final Material oreBlock;
    private final int topBound;
    private final int bottomBound;
    private final int veinHeightDivisor;
    private final int minVeinSize;
    private final int maxVeinSize;

    private final Vector[] adjacentVectors = {
        new Vector(1, 0, 0),
        new Vector(-1, 0, 0),
        new Vector(0, 1, 0),
        new Vector(0, -1, 0),
        new Vector(0, 0, 1),
        new Vector(0, 0, -1),
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
        Vector vec = new Vector(0, 0, 0);
        final int CHUNK_SIZE = 16;
      
        int tries = 0;
        //Generate a random location within bounds
        do {
            vec.setX(random.nextInt(CHUNK_SIZE) + (source.getX() * CHUNK_SIZE));
            vec.setY(random.nextInt(this.topBound - this.bottomBound) + this.bottomBound);
            vec.setZ(random.nextInt(CHUNK_SIZE) + (source.getZ() * CHUNK_SIZE));
            
            ++tries;
        } while (tries < 1000 && !isLocationValid(vec, source));

        return (tries < 1000) ? vec.toLocation(source.getWorld()) : null;
    }

    private boolean isLocationValid(Vector vec, Chunk chunk) {
        final int CHUNK_SIZE = 16;

        //Ensure that it's within chunk bounds
        final int minX = chunk.getX() * CHUNK_SIZE;
        final int minZ = chunk.getZ() * CHUNK_SIZE;

        if (vec.getX() < minX || vec.getX() >= (minX + CHUNK_SIZE)) return false;
        if (vec.getZ() < minZ || vec.getZ() >= (minZ + CHUNK_SIZE)) return false;

        //Ensure that the block type matches
        final Location location = vec.toLocation(chunk.getWorld());
        return location.getBlock().getType() == this.baseMaterial;
    }

    private void generateVein(final Location origin, final Random random, final Chunk chunk, final int blockSize) {
        final HashSet<String> visited = new HashSet<>();
        final Vector[] ores = new Vector[blockSize];
        
        //Set the origin as the first
        origin.getBlock().setType(this.oreBlock, false);
        ores[0] = origin.getBlock().getLocation().toVector();
        visited.add(encodeVector(ores[0]));

        //Loop starts from 1 because 0 is already populated
        for (int i = 1; i < blockSize; ++i) {
            Vector selectedLoc;
            Vector neighbour;

            int tries = 0;
            do {
                //Select a location from one of the already visited locations and an adjacent face
                selectedLoc = ores[random.nextInt(i)];

                //Select a random neighbour from this location
                // neighbour = selectedLoc.getRelative(adjacentFaces[random.nextInt(adjacentFaces.length)]);
                neighbour = selectedLoc.add(adjacentVectors[random.nextInt(adjacentVectors.length)]);

                ++tries;

            } while (tries < 1000 && (visited.contains(encodeVector(neighbour)) || !isLocationValid(neighbour, chunk)));

            if (tries >= 1000) {
                return;
            }

            neighbour.toLocation(origin.getWorld()).getBlock().setType(this.oreBlock, false);
            ores[i] = neighbour;
            visited.add(encodeVector(ores[i])); 
        }
    }

    private String encodeVector(Vector vec) {
        return "(" + vec.getBlockX() + "," + vec.getBlockY() + "," + vec.getBlockZ() + ")";
    }

    
}
