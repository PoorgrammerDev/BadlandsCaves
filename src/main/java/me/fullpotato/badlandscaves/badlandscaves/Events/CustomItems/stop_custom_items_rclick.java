package me.fullpotato.badlandscaves.badlandscaves.Events.CustomItems;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class stop_custom_items_rclick implements Listener {
    private BadlandsCaves plugin;
    public  stop_custom_items_rclick(BadlandsCaves bcav) {
        plugin = bcav;
    }

    @EventHandler
    public void r_click (PlayerInteractEvent event) {
        Action action = event.getAction();

        if (action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)) {
            ItemStack item = event.getItem();
            if (item != null) {
                ArrayList<ItemStack> cancelled_items = new ArrayList<>();

                ItemStack tiny_blz = new ItemStack(Material.STRUCTURE_BLOCK, 9);
                ItemMeta tiny_blz_meta = tiny_blz.getItemMeta();
                tiny_blz_meta.setDisplayName(ChatColor.RESET + "Tiny Pile of Blaze Powder");
                tiny_blz_meta.setCustomModelData(100);
                tiny_blz.setItemMeta(tiny_blz_meta);
                cancelled_items.add(tiny_blz);

                ItemStack purge_essence = new ItemStack(Material.COMMAND_BLOCK, 1);
                ItemMeta purge_ess_meta = purge_essence.getItemMeta();
                purge_ess_meta.setDisplayName(ChatColor.AQUA + "Essence of Purging");
                purge_ess_meta.setCustomModelData(101);
                purge_essence.setItemMeta(purge_ess_meta);
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
