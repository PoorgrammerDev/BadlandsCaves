package me.fullpotato.badlandscaves.Thirst;

import me.fullpotato.badlandscaves.BadlandsCaves;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Levelled;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

public class CauldronRunnable extends BukkitRunnable {
    private static final int MAXIMUM_DISTANCE_SQUARED = 25;
    private final BadlandsCaves plugin;
    private final Player player;
    private final Location cauldronLocation;
    private final Inventory inventory;
    private final CauldronMenu cauldronMenu;
    private final BlockFace[] faces = {
            BlockFace.UP,
            BlockFace.NORTH,
            BlockFace.EAST,
            BlockFace.SOUTH,
            BlockFace.WEST,
    };

    public CauldronRunnable(BadlandsCaves plugin, Player player, Location cauldronLocation, Inventory inventory, CauldronMenu cauldronMenu) {
        this.plugin = plugin;
        this.player = player;
        this.cauldronLocation = cauldronLocation;
        this.inventory = inventory;
        this.cauldronMenu = cauldronMenu;
    }

    @Override
    public void run() {
        final Block block = cauldronLocation.getBlock();
        final Block under = block.getRelative(BlockFace.DOWN);
        //cancel checks
        if (!player.isOnline()
                || player.isDead()
                || !block.getType().equals(Material.CAULDRON)
                || !under.getType().equals(Material.FIRE)
                || !player.getWorld().equals(cauldronLocation.getWorld())
                || player.getLocation().distanceSquared(cauldronLocation) > MAXIMUM_DISTANCE_SQUARED
                || !player.getOpenInventory().getTopInventory().equals(inventory)) {
            this.cancel();
            return;
        }


        if (block.getBlockData() instanceof Levelled) {
            final Levelled cauldronLevel = (Levelled) block.getBlockData();
            //water fills in from adjacent blocks
            if (cauldronLevel.getLevel() < cauldronLevel.getMaximumLevel()) {
                for (final BlockFace face : faces) {
                    final Block adjacent = block.getRelative(face);
                    if (adjacent.getType().equals(Material.WATER)) {
                        if (adjacent.getBlockData() instanceof Levelled) {
                            final Levelled waterLevel = (Levelled) adjacent.getBlockData();
                            if (waterLevel.getLevel() == 0) {
                                cauldronLevel.setLevel(cauldronLevel.getLevel() + 1);
                                block.setBlockData(cauldronLevel);

                                adjacent.setType(Material.AIR);
                                adjacent.getState().update(true);
                                break;
                            }
                        }
                    }
                }
            }

            //water level updates in inventory
            cauldronMenu.updateWaterLevel(inventory, cauldronLevel.getLevel());

            //crafting status update
            cauldronMenu.updateCauldronStatus(inventory);
        }

    }

    @Override
    public synchronized void cancel() throws IllegalStateException {
        super.cancel();
        if (player.isOnline() && player.getOpenInventory().getTopInventory().equals(inventory)) {
            player.closeInventory();
        }
    }
}
