package me.fullpotato.badlandscaves.badlandscaves.Events.CustomItems.Crafting;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

public class purgeEssence implements Listener {
    private BadlandsCaves plugin;
    public purgeEssence(BadlandsCaves bcav) {
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

        ItemStack result = event.getRecipe().getResult();
        ItemStack prg_ess = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("item.purge_essence").getValues(true));
        if (result.isSimilar(prg_ess)) {
            ItemStack tiny_blz = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("item.tiny_blaze_powder").getValues(true));
            boolean matching = false;

            if (event.getInventory().getItem(5) == null) {
            }
            else if (event.getInventory().getItem(5).isSimilar(tiny_blz)) {
                matching = true;
            }

            if (!matching) {
                event.getInventory().setResult(null);
            }
        }
    }
}
