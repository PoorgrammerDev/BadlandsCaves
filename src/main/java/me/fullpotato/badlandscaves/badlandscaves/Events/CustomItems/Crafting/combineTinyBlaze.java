package me.fullpotato.badlandscaves.badlandscaves.Events.CustomItems.Crafting;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

public class combineTinyBlaze implements Listener {
    private BadlandsCaves plugin;

    public combineTinyBlaze(BadlandsCaves bcav) {
        plugin = bcav;
    }

    @EventHandler
    public void tiny_blaze_into_large(PrepareItemCraftEvent event) {

        if (event.getRecipe() == null) {
            return;
        }

        if (event.getRecipe().getResult() == null) {
            return;
        }


        Material result = event.getRecipe().getResult().getType();
        if (result.equals(Material.BLAZE_POWDER)) {
            ItemStack tiny_blz = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("item.tiny_blaze_powder").getValues(true));
            boolean found = true;
            for (ItemStack item : event.getInventory().getMatrix()) {
                if (!item.isSimilar(tiny_blz)) {
                    found = false;
                }

                if (!found) {
                    event.getInventory().setResult(null);
                }
            }
        }
    }
}
