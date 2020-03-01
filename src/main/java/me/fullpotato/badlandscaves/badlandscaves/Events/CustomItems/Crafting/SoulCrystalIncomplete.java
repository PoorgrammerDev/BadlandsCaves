package me.fullpotato.badlandscaves.badlandscaves.Events.CustomItems.Crafting;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

public class SoulCrystalIncomplete extends MatchCrafting implements Listener {
    private BadlandsCaves plugin;
    public SoulCrystalIncomplete (BadlandsCaves bcav) {
        plugin = bcav;
    }

    @EventHandler
    public void soul_crystal (PrepareItemCraftEvent event) {
        if (event.getRecipe() == null || event.getRecipe().getResult() == null) return;

        final ItemStack result = event.getRecipe().getResult();
        final ItemStack soul_crystal_incomplete = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.soul_crystal_incomplete").getValues(true));

        if (result.isSimilar(soul_crystal_incomplete)) {
            final ItemStack merged_souls = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.merged_souls").getValues(true));

            if (!isMatching(event.getInventory().getMatrix(), merged_souls)) {
                event.getInventory().setResult(null);
            }
        }
    }
}
