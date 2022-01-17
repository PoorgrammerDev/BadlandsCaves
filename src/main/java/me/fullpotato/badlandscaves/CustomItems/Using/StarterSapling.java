package me.fullpotato.badlandscaves.CustomItems.Using;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
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
            final Block block = event.getBlockPlaced();
            final Material saplingMat = plugin.getCustomItemManager().getItem(sapling).getType();
            new BukkitRunnable() {
                @Override
                public void run() {
                    int counter = 0;
                    while (block.getType() == saplingMat && counter < 64) {
                        block.applyBoneMeal(BlockFace.UP);
                        ++counter;
                    }
                }
            }.runTaskLater(plugin, 5);
        }
    }
}