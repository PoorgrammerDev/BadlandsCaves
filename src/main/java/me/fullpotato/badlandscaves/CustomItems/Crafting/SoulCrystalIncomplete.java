package me.fullpotato.badlandscaves.CustomItems.Crafting;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;

public class SoulCrystalIncomplete extends MatchCrafting implements Listener {
    private final BadlandsCaves plugin;
    public SoulCrystalIncomplete (BadlandsCaves bcav) {
        plugin = bcav;
    }

    public void soul_crystal_incomplete() {
        final ItemStack soul_crystal_incomplete = plugin.getCustomItemManager().getItem(CustomItem.SOUL_CRYSTAL_INCOMPLETE);

        ShapelessRecipe soul_crystal_incomplete_recipe = new ShapelessRecipe(new NamespacedKey(plugin, "soul_crystal_incomplete"), soul_crystal_incomplete);
        soul_crystal_incomplete_recipe.addIngredient(Material.DIAMOND);
        soul_crystal_incomplete_recipe.addIngredient(Material.COMMAND_BLOCK);

        plugin.getServer().addRecipe(soul_crystal_incomplete_recipe);
    }

    @EventHandler
    public void soul_crystal (PrepareItemCraftEvent event) {
        if (event.getRecipe() == null || event.getRecipe().getResult() == null) return;

        final ItemStack result = event.getRecipe().getResult();
        final ItemStack soul_crystal_incomplete = plugin.getCustomItemManager().getItem(CustomItem.SOUL_CRYSTAL_INCOMPLETE);

        if (result.isSimilar(soul_crystal_incomplete)) {
            final ItemStack merged_souls = plugin.getCustomItemManager().getItem(CustomItem.MERGED_SOULS);

            if (!isMatching(event.getInventory().getMatrix(), merged_souls)) {
                event.getInventory().setResult(null);
            }
        }
    }
}
