package me.fullpotato.badlandscaves.badlandscaves.Events.CustomItems.Crafting;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

public class combineTinyBlaze extends MatchCrafting implements Listener {
    private BadlandsCaves plugin;

    public combineTinyBlaze(BadlandsCaves bcav) {
        plugin = bcav;
    }

    @EventHandler
    public void tiny_blaze_into_large(PrepareItemCraftEvent event) {
        if (event.getRecipe() == null || event.getRecipe().getResult() == null) return;

        final Material result = event.getRecipe().getResult().getType();
        if (result.equals(Material.BLAZE_POWDER)) {
            final ItemStack tiny_blz = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.tiny_blaze_powder").getValues(true));
            if (!isMatching(event.getInventory().getMatrix(), tiny_blz)) {
                event.getInventory().setResult(null);
            }
        }
    }
}
