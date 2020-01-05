package me.fullpotato.badlandscaves.badlandscaves.Events.CustomCrafting;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

public class purge_ess implements Listener {
    private BadlandsCaves plugin;
    public purge_ess (BadlandsCaves bcav) {
        plugin = bcav;
    }

    @EventHandler
    public void purge_ess (PrepareItemCraftEvent event) {
        if (event.getRecipe() == null) {
            return;
        }

        if (event.getRecipe().getResult() == null) {
            return;
        }

        if (!event.getRecipe().getResult().hasItemMeta()) {
            return;
        }

        if (!event.getRecipe().getResult().getItemMeta().hasDisplayName()) {
            return;
        }

        Material result = event.getRecipe().getResult().getType();
        if (result.equals(Material.DEBUG_STICK)) {
            String result_name = event.getRecipe().getResult().getItemMeta().getDisplayName();
            String tiny_blz_name = ChatColor.RESET + "Tiny Pile of Blaze Powder";
            String prg_ess_name = ChatColor.AQUA + "Essence of Purging";

            if (!result_name.equals(prg_ess_name)) {
                return;
            }

            boolean matching = false;

            if (event.getInventory().getItem(5) == null) {
            }
            else if (!event.getInventory().getItem(5).hasItemMeta()) {
            }
            else if (!event.getInventory().getItem(5).getItemMeta().hasDisplayName()) {
            }
            else if (event.getInventory().getItem(5).getItemMeta().getDisplayName().equals(tiny_blz_name)) {
                matching = true;
            }

            if (!matching) {
                event.getInventory().setResult(null);
            }
        }
    }
}
