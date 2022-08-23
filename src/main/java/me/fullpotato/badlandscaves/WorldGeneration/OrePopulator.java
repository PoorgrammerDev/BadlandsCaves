package me.fullpotato.badlandscaves.WorldGeneration;

import java.util.Random;
import java.util.HashSet;
import java.util.Queue;
import java.util.Stack;
import java.util.LinkedList;

import org.bukkit.Bukkit;
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
    private boolean forceExpose;

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
     * @param base The pre-existing block that the ore will replace
     * @param oreBlock The ore block
     * @param topBound Y-level upper bound
     * @param bottomBound Y-level lower bound
     * @param veinHeightDivisor Max # of veins is calculated by (topBound - bottomBound) / veinHeightDivisor. Increase for less veins; decrease for more.
     */
    public OrePopulator(Material baseMaterial, Material oreBlock, int topBound, int bottomBound, int veinHeightDivisor, int minVeinSize, int maxVeinSize, boolean forceExpose) {
        super();

        this.baseMaterial = baseMaterial;
        this.oreBlock = oreBlock;
        this.topBound = topBound;
        this.bottomBound = bottomBound;
        this.veinHeightDivisor = veinHeightDivisor;
        this.minVeinSize = minVeinSize;
        this.maxVeinSize = maxVeinSize;
        this.forceExpose = forceExpose;
    }

    @Override
    public void populate(World world, Random random, Chunk source) {
        random.setSeed(world.getSeed());
        
        final int height = topBound - bottomBound;
        if (height / veinHeightDivisor <= 0) return;

        final int veins = random.nextInt(height / veinHeightDivisor);

        System.out.println("Generating " + veins + " veins.");

        for (int i = 0; i < veins; ++i) {
            final Location location = getValidLocation(source, random);
            final int veinSize = random.nextInt(this.maxVeinSize - this.minVeinSize) + this.minVeinSize;

            generateVein(location, random, source, veinSize);
            // location.getBlock().setType(this.oreBlock, false);
        }

    }

    private Location getValidLocation(Chunk source, Random random) {
        Location location = new Location(source.getWorld(), 0, 0, 0);
        final int CHUNK_SIZE = 16;
       
        int count = 0;
        //Generate a random location within bounds
        do {
            location.setX(random.nextInt(CHUNK_SIZE) + (source.getX() * CHUNK_SIZE));
            location.setY(random.nextInt(this.topBound - this.bottomBound) + this.bottomBound);
            location.setZ(random.nextInt(CHUNK_SIZE) + (source.getZ() * CHUNK_SIZE));

            count++;

        } while (!isLocationValid(location, source));

        // System.out.println("Valid location found after " + count + " tries.");

        //Expose the ore location if requested
        // if (this.forceExpose) {
        //     final Location airLoc = findExposedLocation(location, source);

        //     if (airLoc != null) {
        //         for (BlockFace adj : this.adjacentFaces) {
        //             Location relative = airLoc.getBlock().getRelative(adj).getLocation();
        //             if (isLocationValid(relative, source)) {
        //                 return relative;
        //             }
        //         }
        //     }
        // }

        return location;
    }

    private boolean isLocationValid(Location location, Chunk chunk) {
        return (location.getBlock().getType() == this.baseMaterial && location.getChunk().equals(chunk));
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
                System.out.println("Vein failed to generate");
                return;
            }

            neighbour.setType(this.oreBlock, false);
            ores[i] = neighbour.getLocation();
            visited.add(encodeLocation(ores[i])); 
        }
    }

    private Location findExposedLocation(final Location origin, final Chunk chunk) {
        Queue<Location> queue = new LinkedList<>();
        HashSet<String> visited = new HashSet<>();
        String encoded;

        Block block = origin.getBlock();
        Location location;

        //Add neighbors of center to queue
        for (BlockFace adj : this.adjacentFaces) {
            queue.add(block.getRelative(adj).getLocation());
        }

        while (!queue.isEmpty()) {
            location = queue.poll();
            encoded = encodeLocation(location);
            if (visited.contains(encoded)) continue;

            block = location.getBlock();

            //Add location to visited
            visited.add(encoded);

            //If any neighbor is air: return
            if (block.getType().isAir()) {
                return location;
            }

            //Add all neighbours
            for (BlockFace adj : this.adjacentFaces) {
                Location adjLoc = block.getRelative(adj).getLocation();
                if (location.getChunk().equals(chunk) && !visited.contains(encodeLocation(adjLoc))) queue.add(adjLoc);
            }
        }
        return null;
        
        //Using iterative DFS
        // final HashSet<String> visited = new HashSet<>();
        // final Stack<Location> stack = new Stack<>();

        // stack.push(origin.getBlock().getLocation());
        // visited.add(encodeLocation(origin.getBlock().getLocation()));

        // while (!stack.isEmpty()) {
        //     final Location location = stack.pop();
        //     visited.add(encodeLocation(location));

        //     final Block block = location.getBlock();
        //     if (block.getChunk().equals(chunk) && block.getType().isAir()) {
        //         return location;
        //     }

        //     for (final BlockFace adj : this.adjacentFaces) {
        //         final Location adjacent = origin.getBlock().getRelative(adj).getLocation();
        //         if (!visited.contains(encodeLocation(adjacent)) && adjacent.getChunk().equals(chunk)) {
        //             stack.push(adjacent);
        //         }
        //     }
        // }
        // return null;
    }

    private String encodeLocation(Location location) {
        return "(" + location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ() + ")";
    }

    
}
