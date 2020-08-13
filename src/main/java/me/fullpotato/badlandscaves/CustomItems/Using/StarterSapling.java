package me.fullpotato.badlandscaves.CustomItems.Using;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import org.bukkit.Location;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class StarterSapling implements Listener {
    private final BadlandsCaves plugin;
    private final CustomItem sapling = CustomItem.STARTER_SAPLING;

    public StarterSapling(BadlandsCaves plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void useSapling (BlockPlaceEvent event) {
        final ItemStack item = event.getItemInHand();
        if (item.isSimilar(plugin.getCustomItemManager().getItem(sapling))) {
            event.setCancelled(true);
            final Location location = event.getBlockPlaced().getLocation();
            final World world = location.getWorld();
            if (world != null) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (world.generateTree(location, TreeType.TREE)) {
                            item.setAmount(item.getAmount() - 1);
                        }
                    }
                }.runTaskLater(plugin, 1L);
            }
        }
    }
}