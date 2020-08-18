package me.fullpotato.badlandscaves.AlternateDimensions;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.Loot.DimensionStructureTable;
import me.fullpotato.badlandscaves.Util.MultiStructureLoader;
import me.fullpotato.badlandscaves.Util.StructureTrack;
import me.fullpotato.badlandscaves.WorldGeneration.DimensionsWorlds;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Barrel;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootContext;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockVector;

import javax.annotation.Nullable;
import java.util.Random;


public class DimensionStructures {
    public enum Structure {
        MANABAR(true),
        LAB(true),
        LAB_ABANDONED(false),
        LAB_DESTROYED(false),
        JAIL(true),
        JAIL_ABANDONED(false),
        SHRINE(true),
        SHRINE_DESTROYED(false),
        CURSED_HOUSE(false),
        TENT(true),
        HOUSE(true),
        HOUSE_ABANDONED(false),
        HOUSE_DESTROYED(false),
        BUNKER(true),
        BUNKER_AB(false);

        private final boolean inhabited;
        Structure(boolean inhabited) {
            this.inhabited = inhabited;
        }

        public boolean getInhabited() {
            return inhabited;
        }
    }
    
    private final BadlandsCaves plugin;
    private final Random random = new Random();

    public DimensionStructures(BadlandsCaves plugin) {
        this.plugin = plugin;
    }

    public void generateStructures (World world, DimensionsWorlds.Habitation habitation, @Nullable Location origin, int radius, int count) {
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
                final Location location = new Location(world, x + (random.nextInt(radius * 2) - radius), 256, z + (random.nextInt(radius * 2) - radius));
                generateStructure(world, habitation, location, null);
                ticker[0]++;
            }
        }.runTaskTimer(plugin, 0, 20);
    }

    public void generateStructure (World world, DimensionsWorlds.Habitation habitation, Location origin, @Nullable Structure structure) {
        if (world.getName().startsWith(plugin.getDimensionPrefixName())) {
            for (int y = world.getMaxHeight(); y > 0; y--) {
                Location iterate = origin.clone();
                iterate.setY(y);

                final Material type = iterate.getBlock().getType();
                final String name = type.name().toUpperCase();
                if (type.isSolid() && !name.contains("LOG") && !name.contains("LEAVES") && !name.contains("MUSHROOM")) {
                    origin.setY(y);
                    break;
                }
            }

            if (structure == null) {
                structure = Structure.values()[random.nextInt(Structure.values().length)];
                if (habitation.equals(DimensionsWorlds.Habitation.ILLAGERS)) {
                    while (!structure.getInhabited()) {
                        structure = Structure.values()[random.nextInt(Structure.values().length)];
                    }
                }
            }

            world.loadChunk(origin.getChunk());
            final Structure finalStructure = structure;
            new BukkitRunnable() {
                @Override
                public void run() {
                    loadStructure(finalStructure, origin);
                }
            }.runTaskLater(plugin, 20);
        }
    }

    public void loadStructure(Structure queried, Location origin) {
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
            loader.loadAll(origin, false);

            new BukkitRunnable() {
                @Override
                public void run() {
                    for (StructureTrack track : bunker) {
                        fillBarrels(track, origin);
                    }
                }
            }.runTaskLater(plugin, 5);
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
            loader.loadAll(origin, false);

            new BukkitRunnable() {
                @Override
                public void run() {
                    for (StructureTrack track : bunker_ab) {
                        fillBarrels(track, origin);
                    }
                }
            }.runTaskLater(plugin, 5);
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
                    structure.load(origin, false);

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            fillBarrels(structure, origin);
                        }
                    }.runTaskLater(plugin, 5);
                    return;
                }
            }
        }
    }

    public void fillBarrels(StructureTrack track, Location origin) {
        final Location clone = origin.clone();
        clone.add(track.getBlockXOffset(), track.getBlockYOffset(), track.getBlockZOffset());

        Block block = clone.getBlock();

        if (block.getType().equals(Material.STRUCTURE_BLOCK)) {
            if (block.getState() instanceof org.bukkit.block.Structure) {
                org.bukkit.block.Structure state = (org.bukkit.block.Structure) block.getState();

                BlockVector size = state.getStructureSize();
                BlockVector offset = state.getRelativePosition();

                Location negative = block.getLocation().add(offset.getBlockX(), offset.getBlockY(), offset.getBlockZ());
                Location positive = negative.clone().add(size.getBlockX(), size.getBlockY(), size.getBlockZ());

                for (int x = negative.getBlockX(); x < positive.getBlockX(); x++) {
                    for (int y = negative.getBlockY(); y < positive.getBlockY(); y++) {
                        for (int z = negative.getBlockZ(); z < positive.getBlockZ(); z++) {
                            Location iter = new Location(negative.getWorld(), x, y, z);

                            if (iter.getBlock().getType().equals(Material.BARREL)) {
                                Block barrel = iter.getBlock();
                                if (barrel.getState() instanceof Barrel) {
                                    Barrel barrelState = (Barrel) barrel.getState();
                                    Inventory inventory = barrelState.getInventory();

                                    DimensionStructureTable loot = new DimensionStructureTable(plugin);
                                    LootContext.Builder builder = new LootContext.Builder(block.getLocation());

                                    for (ItemStack item : loot.populateLoot(random, builder.build())) {
                                        int slot;
                                        do {
                                            slot = random.nextInt(inventory.getSize());
                                        } while (inventory.getItem(slot) != null);

                                        inventory.setItem(slot, item);
                                    }

                                }
                            }
                        }
                    }
                }
            }
            block.setType(Material.AIR);
        }
    }
}
