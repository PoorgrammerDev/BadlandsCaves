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
import org.bukkit.block.Structure;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootContext;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockVector;

import javax.annotation.Nullable;
import java.util.Random;


public class DimensionStructures {
    public enum PlanetStructure {
        MANABAR(15, true),
        LAB(15, true),
        LAB_ABANDONED(15, false),
        LAB_DESTROYED(0, false),
        JAIL(7, true),
        JAIL_ABANDONED(7, false),
        SHRINE(7, true),
        SHRINE_DESTROYED(7, false),
        CURSED_HOUSE(15, false),
        TENT(7, true),
        HOUSE(9, true),
        HOUSE_ABANDONED(10, false),
        HOUSE_DESTROYED(10, false),
        BUNKER(16, true),
        BUNKER_AB(16, false);

        private final int clearRadius;
        private final boolean inhabited;
        PlanetStructure(int clearRadius, boolean inhabited) {
            this.clearRadius = clearRadius;
            this.inhabited = inhabited;
        }

        public int getClearRadius() {
            return this.clearRadius;
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

    public void generateStructure (World world, DimensionsWorlds.NativeLife habitation, @Nullable Location origin, @Nullable PlanetStructure structure) {
        if (world.getName().startsWith(plugin.getDimensionPrefixName())) {
            if (origin == null || origin.getWorld() == null || !origin.getWorld().equals(world)) {
                int y_start = 200;

                origin = new Location(world, 0, y_start, 0);

                for (int y = y_start; y > 0; y--) {
                    Location iterate = origin.clone();
                    iterate.setY(y);

                    Material type = iterate.getBlock().getType();
                    if (type.isSolid() && !type.name().toUpperCase().contains("LOG") && !type.name().toUpperCase().contains("LEAVES")) {
                        origin.setY(y);
                        break;
                    }
                }
            }

            if (structure == null) {
                structure = PlanetStructure.values()[random.nextInt(PlanetStructure.values().length)];
                if (habitation.equals(DimensionsWorlds.NativeLife.PILLAGERS)) {
                    while (!structure.getInhabited()) {
                        structure = PlanetStructure.values()[random.nextInt(PlanetStructure.values().length)];
                    }
                }
            }

            clearArea(origin, structure.getClearRadius());
            loadStructure(structure, origin, habitation);
        }
    }

    public void clearArea(Location location, int radius) {
        Material groundType = getGroundMaterial(location);

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                for (int y = location.getBlockY() + 1; y < 200; y++) {
                    Location clone = location.clone().add(x, 0, z);
                    clone.setY(y);

                    Block block = clone.getBlock();
                    if (block.getType().name().toUpperCase().contains("LOG") || block.getType().name().toUpperCase().contains("LEAVES") || !block.getType().isSolid() || block.isLiquid()) {
                        block.setType(Material.AIR);
                    }

                }

                for (int y = location.getBlockY() - 4; y <= location.getBlockY(); y++) {
                    Location clone = location.clone().add(x, 0, z);
                    clone.setY(y);

                    Block block = clone.getBlock();
                    if (block.isLiquid() || block.isPassable() || !block.getType().isSolid()) {
                        block.setType(groundType);
                    }
                }
            }
        }

    }

    public Material getGroundMaterial (Location location) {
        for (int x = -10; x <= 10; x++) {
            for (int z = -10; z <= 10; z++) {
                Location clone = location.clone().add(x, 0, z);
                Material type = clone.getBlock().getType();

                if (type.isSolid() && !type.name().toUpperCase().contains("LOG") && !type.name().toUpperCase().contains("LEAVES")) {
                    return type;
                }
            }
        }
        return Material.STONE;
    }

    public void loadStructure(PlanetStructure queried, Location origin, DimensionsWorlds.NativeLife habitation) {
        //center ground level world origin ~(0, 60, 0)

        //multistructures
        if (queried.equals(PlanetStructure.BUNKER)) {
            final StructureTrack[] bunker = {
                    new StructureTrack(plugin, -6, -9, -14, 0, 1, 0, "badlandscaves:bunker_tophouse", BlockFace.DOWN),
                    new StructureTrack(plugin, -9, -33, -10, 0, 1, 0, "badlandscaves:bunker_intertube", BlockFace.DOWN),
                    new StructureTrack(plugin, -13, -33, 5, 0, 1, 0, "badlandscaves:bunker_foyer", BlockFace.DOWN),
                    new StructureTrack(plugin, -42, -30, 9, 0, 1, 0, "badlandscaves:bunker_bedroom", BlockFace.DOWN),
                    new StructureTrack(plugin, 8, -29, 10, 0, 1, 0, "badlandscaves:bunker_dine", BlockFace.DOWN),
                    new StructureTrack(plugin, 5, -28, 27, -15, 1, 0, "badlandscaves:bunker_farm", BlockFace.UP),
            };

            MultiStructureLoader loader = new MultiStructureLoader(bunker);
            loader.loadAll(origin, true);

            new BukkitRunnable() {
                @Override
                public void run() {
                    for (StructureTrack track : bunker) {
                        fillBarrels(track, origin, habitation);
                    }
                }
            }.runTaskLater(plugin, 5);
        }
        else if (queried.equals(PlanetStructure.BUNKER_AB)) {
            final StructureTrack[] bunker_ab = {
                    new StructureTrack(plugin, -6, -9, -14, 0, 1, 0, "badlandscaves:bunker_tophouse_ab", BlockFace.DOWN),
                    new StructureTrack(plugin, -9, -33, -10, 0, 1, 0, "badlandscaves:bunker_intertube_ab", BlockFace.DOWN),
                    new StructureTrack(plugin, -13, -33, 5, 0, 1, 0, "badlandscaves:bunker_foyer_ab", BlockFace.DOWN),
                    new StructureTrack(plugin, -42, -30, 9, 0, 1, 0, "badlandscaves:bunker_bedroom_ab", BlockFace.DOWN),
                    new StructureTrack(plugin, 8, -29, 10, 0, 1, 0, "badlandscaves:bunker_dine_ab", BlockFace.DOWN),
                    new StructureTrack(plugin, 5, -28, 27, -15, 1, 0, "badlandscaves:bunker_farm_ab", BlockFace.UP),
            };

            MultiStructureLoader loader = new MultiStructureLoader(bunker_ab);
            loader.loadAll(origin, true);

            new BukkitRunnable() {
                @Override
                public void run() {
                    for (StructureTrack track : bunker_ab) {
                        fillBarrels(track, origin, habitation);
                    }
                }
            }.runTaskLater(plugin, 5);
        }
        //single structures
        else {
            final StructureTrack[] structures = {
                    new StructureTrack(plugin, 11, 0, 10, -21, 0, -19, "badlandscaves:" + PlanetStructure.MANABAR.name().toLowerCase(), BlockFace.UP),
                    new StructureTrack(plugin, 11, 0, -15, -22, 0, 1, "badlandscaves:" + PlanetStructure.LAB.name().toLowerCase(), BlockFace.UP),
                    new StructureTrack(plugin, 11, 0, -15, -22, 0, 1, "badlandscaves:" + PlanetStructure.LAB_ABANDONED.name().toLowerCase(), BlockFace.UP),
                    new StructureTrack(plugin, 11, 0, -15, -22, 0, 1, "badlandscaves:" + PlanetStructure.LAB_DESTROYED.name().toLowerCase(), BlockFace.UP),
                    new StructureTrack(plugin, -8, -6, 6, 1, 0, -29, "badlandscaves:" + PlanetStructure.JAIL.name().toLowerCase(), BlockFace.UP),
                    new StructureTrack(plugin, -8, -6, 6, 1, 0, -29, "badlandscaves:" + PlanetStructure.JAIL_ABANDONED.name().toLowerCase(), BlockFace.UP),
                    new StructureTrack(plugin, 6, 0, -6, -11, 0, 1, "badlandscaves:" + PlanetStructure.SHRINE.name().toLowerCase(), BlockFace.UP),
                    new StructureTrack(plugin, 6, 0, -6, -11, 0, 1, "badlandscaves:" + PlanetStructure.SHRINE_DESTROYED.name().toLowerCase(), BlockFace.UP),
                    new StructureTrack(plugin, 10, -1, -11, -19, 0, 1, "badlandscaves:" + PlanetStructure.CURSED_HOUSE.name().toLowerCase(), BlockFace.UP),
                    new StructureTrack(plugin, 6, 0, -6, -11, 0, 1, "badlandscaves:" + PlanetStructure.TENT.name().toLowerCase(), BlockFace.UP),
                    new StructureTrack(plugin, -9, 0, -8, 1, 0, 1, "badlandscaves:" + PlanetStructure.HOUSE.name().toLowerCase(), BlockFace.UP),
                    new StructureTrack(plugin, -9, 0, -8, 1, 0, 1, "badlandscaves:" + PlanetStructure.HOUSE_ABANDONED.name().toLowerCase(), BlockFace.UP),
                    new StructureTrack(plugin, -9, 0, -8, 1, 0, 1, "badlandscaves:" + PlanetStructure.HOUSE_DESTROYED.name().toLowerCase(), BlockFace.UP),

            };

            for (StructureTrack structure : structures) {
                if (queried.name().equalsIgnoreCase(structure.getStructureName().split(":")[1])) {
                    structure.load(origin, true);

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            fillBarrels(structure, origin, habitation);
                        }
                    }.runTaskLater(plugin, 5);
                    return;
                }
            }
        }
    }

    public void fillBarrels(StructureTrack track, Location origin, DimensionsWorlds.NativeLife habitation) {
        final Location clone = origin.clone();
        clone.add(track.getBlockXOffset(), track.getBlockYOffset(), track.getBlockZOffset());

        Block block = clone.getBlock();

        if (block.getType().equals(Material.STRUCTURE_BLOCK)) {
            if (block.getState() instanceof Structure) {
                Structure state = (Structure) block.getState();

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

                                    DimensionStructureTable loot = new DimensionStructureTable(plugin, habitation);
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
