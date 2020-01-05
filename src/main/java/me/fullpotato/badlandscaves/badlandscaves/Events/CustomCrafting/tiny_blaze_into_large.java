package me.fullpotato.badlandscaves.badlandscaves.Events.CustomCrafting;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class tiny_blaze_into_large implements Listener {
    private BadlandsCaves plugin;
    public tiny_blaze_into_large (BadlandsCaves bcav) {
        plugin = bcav;
    }

    @EventHandler
    public void tiny_blaze_into_large (PrepareItemCraftEvent event) {

        if (event.getRecipe() == null) {
            return;
        }

        if (event.getRecipe().getResult() == null) {
            return;
        }


        Material result = event.getRecipe().getResult().getType();
        if (result.equals(Material.BLAZE_POWDER)) {
            String tiny_blz_name = ChatColor.RESET + "Tiny Pile of Blaze Powder";
            boolean found = true;
            for (ItemStack item : event.getInventory().getMatrix()) {

                if (item == null) {
                    found = false;
                }
                else if (!item.hasItemMeta()) {
                    found = false;
                }
                else if (!item.getItemMeta().hasDisplayName()) {
                    found = false;
                }
                else if (!item.getItemMeta().getDisplayName().equals(tiny_blz_name)) {
                    found = false;
                }
            }

            if (!found) {
                event.getInventory().setResult(null);
            }
        }
    }
}
