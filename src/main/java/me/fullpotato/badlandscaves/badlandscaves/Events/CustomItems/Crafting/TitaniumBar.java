package me.fullpotato.badlandscaves.badlandscaves.Events.CustomItems.Crafting;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

public class TitaniumBar extends MatchCrafting implements Listener {
    private BadlandsCaves plugin;

    public TitaniumBar(BadlandsCaves plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void craftBar (PrepareItemCraftEvent event) {
        if (event.getRecipe() == null || event.getRecipe().getResult() == null) return;

        final ItemStack result = event.getRecipe().getResult();
        final ItemStack bar = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.titanium_ingot").getValues(true));
        if (!result.isSimilar(bar)) return;

        final ItemStack[] matrix = event.getInventory().getMatrix();
        final ItemStack fragment = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.titanium_fragment").getValues(true));
        if (!isMatching(matrix, fragment)) {
            event.getInventory().setResult(null);
        }
    }
}
