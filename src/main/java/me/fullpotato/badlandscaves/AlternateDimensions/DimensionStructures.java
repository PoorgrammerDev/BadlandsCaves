package me.fullpotato.badlandscaves.AlternateDimensions;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.Util.MultiStructureLoader;
import me.fullpotato.badlandscaves.Util.StructureTrack;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Random;


public class DimensionStructures {
    public enum Structure {
        MANABAR,
        LAB,
        LAB_ABANDONED,
        LAB_DESTROYED,
        JAIL,
        JAIL_ABANDONED,
        SHRINE,
        SHRINE_DESTROYED,
        CURSED_HOUSE,
        TENT,
        HOUSE,
        HOUSE_ABANDONED,
        HOUSE_DESTROYED,
        BUNKER,
        BUNKER_AB,
    }
    
    private final BadlandsCaves plugin;
    private final Random random;
    private final List<Material> blacklistedMats = Arrays.asList(Material.OAK_LOG, Material.ACACIA_LOG, Material.BIRCH_LOG, Material.DARK_OAK_LOG, Material.JUNGLE_LOG, Material.SPRUCE_LOG, Material.OAK_LEAVES, Material.ACACIA_LEAVES, Material.BIRCH_LEAVES, Material.DARK_OAK_LEAVES, Material.JUNGLE_LEAVES, Material.SPRUCE_LEAVES, Material.CRIMSON_NYLIUM, Material.WARPED_NYLIUM, Material.NETHER_WART_BLOCK, Material.WARPED_WART_BLOCK, Material.RED_MUSHROOM_BLOCK, Material.BROWN_MUSHROOM_BLOCK);

    public DimensionStructures(BadlandsCaves plugin, Random random) {
        this.plugin = plugin;
        this.random = random;
    }

    public void generateStructures (World world, @Nullable Location origin, int radius, int count) {
        if (origin == null) origin = new Location(world, 0, 256, 0);

        final int x = origin.getBlockX();
        final int z = origin.getBlockZ();
        final int[] ticker = {0};
        new BukkitRunnable() {
            @Override
            public void run() {
                if (ticker[0] > count) {
                    this.cancel();
                    return;
                }

                //Find randomized location and then find the surface
                final Location location = new Location(world, x + (random.nextInt(radius * 2) - radius), world.getMaxHeight(), z + (random.nextInt(radius * 2) - radius));
                Material type = null;
                do {
                    location.setY(location.getY() - 1);
                    type = location.getBlock().getType();
                } while (!type.isSolid() || blacklistedMats.contains(type));

                generateStructure(location, null, false, false);
                ticker[0]++;
            }
        }.runTaskTimer(plugin, 0, 20);
    }

    public void generateStructure (Location origin, @Nullable Structure structure, boolean leaveStructureBlocks, boolean force) {
        final World world = origin.getWorld();
        if (!force && !world.getName().startsWith(plugin.getDimensionPrefixName())) return;

        if (structure == null) {
            structure = Structure.values()[random.nextInt(Structure.values().length)];
        }

        world.loadChunk(origin.getChunk());
        final Structure finalStructure = structure;
        new BukkitRunnable() {
            @Override
            public void run() {
                loadStructure(finalStructure, origin, leaveStructureBlocks);
            }
        }.runTaskLater(plugin, 20);
    }

    public void loadStructure(Structure queried, Location origin, boolean leaveStructureBlocks) {
        //center ground level world origin ~(0, 60, 0)

        //multistructures
        if (queried.equals(Structure.BUNKER)) {
            final StructureTrack[] bunker = {
                    new StructureTrack(plugin, -6, -9, -14, 0, 1, 0, "badlandscaves:bunker_tophouse", BlockFace.DOWN),
                    new StructureTrack(plugin, -9, -33, -10, 0, 1, 0, "badlandscaves:bunker_intertube", BlockFace.DOWN),
                    new StructureTrack(plugin, -13, -33, 5, 0, 1, 0, "badlandscaves:bunker_foyer", BlockFace.DOWN),
                    new StructureTrack(plugin, -42, -30, 9, 0, 1, 0, "badlandscaves:bunker_bedroom", BlockFace.DOWN),
                    new StructureTrack(plugin, 8, -29, 10, 0, 1, 0, "badlandscaves:bunker_dine", BlockFace.DOWN),
                    new StructureTrack(plugin, 5, -28, 27, -15, 1, 0, "badlandscaves:bunker_farm", BlockFace.UP),
            };

            MultiStructureLoader loader = new MultiStructureLoader(bunker);
            loader.loadAll(origin, leaveStructureBlocks);
        }
        else if (queried.equals(Structure.BUNKER_AB)) {
            final StructureTrack[] bunker_ab = {
                    new StructureTrack(plugin, -6, -9, -14, 0, 1, 0, "badlandscaves:bunker_tophouse_ab", BlockFace.DOWN),
                    new StructureTrack(plugin, -9, -33, -10, 0, 1, 0, "badlandscaves:bunker_intertube_ab", BlockFace.DOWN),
                    new StructureTrack(plugin, -13, -33, 5, 0, 1, 0, "badlandscaves:bunker_foyer_ab", BlockFace.DOWN),
                    new StructureTrack(plugin, -42, -30, 9, 0, 1, 0, "badlandscaves:bunker_bedroom_ab", BlockFace.DOWN),
                    new StructureTrack(plugin, 8, -29, 10, 0, 1, 0, "badlandscaves:bunker_dine_ab", BlockFace.DOWN),
                    new StructureTrack(plugin, 5, -28, 27, -15, 1, 0, "badlandscaves:bunker_farm_ab", BlockFace.UP),
            };

            MultiStructureLoader loader = new MultiStructureLoader(bunker_ab);
            loader.loadAll(origin, leaveStructureBlocks);
        }
        //single structures
        else {
            final StructureTrack[] structures = {
                    new StructureTrack(plugin, 11, 0, 10, -21, 0, -19, "badlandscaves:" + Structure.MANABAR.name().toLowerCase(), BlockFace.UP),
                    new StructureTrack(plugin, 11, 0, -15, -22, 0, 1, "badlandscaves:" + Structure.LAB.name().toLowerCase(), BlockFace.UP),
                    new StructureTrack(plugin, 11, 0, -15, -22, 0, 1, "badlandscaves:" + Structure.LAB_ABANDONED.name().toLowerCase(), BlockFace.UP),
                    new StructureTrack(plugin, 11, 0, -15, -22, 0, 1, "badlandscaves:" + Structure.LAB_DESTROYED.name().toLowerCase(), BlockFace.UP),
                    new StructureTrack(plugin, -8, -6, 6, 1, 0, -29, "badlandscaves:" + Structure.JAIL.name().toLowerCase(), BlockFace.UP),
                    new StructureTrack(plugin, -8, -6, 6, 1, 0, -29, "badlandscaves:" + Structure.JAIL_ABANDONED.name().toLowerCase(), BlockFace.UP),
                    new StructureTrack(plugin, 6, 0, -6, -11, 0, 1, "badlandscaves:" + Structure.SHRINE.name().toLowerCase(), BlockFace.UP),
                    new StructureTrack(plugin, 6, 0, -6, -11, 0, 1, "badlandscaves:" + Structure.SHRINE_DESTROYED.name().toLowerCase(), BlockFace.UP),
                    new StructureTrack(plugin, 10, -1, -11, -19, 0, 1, "badlandscaves:" + Structure.CURSED_HOUSE.name().toLowerCase(), BlockFace.UP),
                    new StructureTrack(plugin, 6, 0, -6, -11, 0, 1, "badlandscaves:" + Structure.TENT.name().toLowerCase(), BlockFace.UP),
                    new StructureTrack(plugin, -9, 0, -8, 1, 0, 1, "badlandscaves:" + Structure.HOUSE.name().toLowerCase(), BlockFace.UP),
                    new StructureTrack(plugin, -9, 0, -8, 1, 0, 1, "badlandscaves:" + Structure.HOUSE_ABANDONED.name().toLowerCase(), BlockFace.UP),
                    new StructureTrack(plugin, -9, 0, -8, 1, 0, 1, "badlandscaves:" + Structure.HOUSE_DESTROYED.name().toLowerCase(), BlockFace.UP),

            };

            for (StructureTrack structure : structures) {
                if (queried.name().equalsIgnoreCase(structure.getStructureName().split(":")[1])) {
                    structure.load(origin, leaveStructureBlocks);
                    return;
                }
            }
        }
    }
}
