package me.fullpotato.badlandscaves.badlandscaves.Events.CustomItems;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class stopCustomItemsRClick implements Listener {
    private BadlandsCaves plugin;
    public stopCustomItemsRClick(BadlandsCaves bcav) {
        plugin = bcav;
    }

    @EventHandler
    public void r_click (PlayerInteractEvent event) {
        Action action = event.getAction();

        if (action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)) {
            ItemStack item = event.getItem();
            if (item != null) {
                ArrayList<ItemStack> cancelled_items = new ArrayList<>();

                ItemStack tiny_blz = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.tiny_blaze_powder").getValues(true));
                cancelled_items.add(tiny_blz);

                ItemStack purge_essence = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.purge_essence").getValues(true));
                cancelled_items.add(purge_essence);

                //keep adding more custom items here...

                boolean found = false;
                for (int a = 0; a < cancelled_items.size(); a++) {
                    if (item.isSimilar(cancelled_items.get(a))) {
                        found = true;
                    }
                }

                if (found) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
