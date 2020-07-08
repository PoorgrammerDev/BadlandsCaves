package me.fullpotato.badlandscaves.CustomItems.Using;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class UseSilencer implements Listener {
    private final BadlandsCaves plugin;

    public UseSilencer(BadlandsCaves plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void placeSilencer (PlayerInteractEvent event) {
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            final ItemStack item = event.getItem();
            if (item != null && item.isSimilar(CustomItem.SILENCER.getItem())) {
                // TODO: 7/7/2020
            }
        }
    }
}
