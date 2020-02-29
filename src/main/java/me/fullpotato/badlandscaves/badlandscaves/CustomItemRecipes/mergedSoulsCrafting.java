package me.fullpotato.badlandscaves.badlandscaves.CustomItemRecipes;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;

public class mergedSoulsCrafting {
    private BadlandsCaves plugin;
    public mergedSoulsCrafting (BadlandsCaves bcav) {
        plugin = bcav;
    }

    public void merge_souls() {
        //TODO implement an actual merged soul item
        ItemStack merged_soul = new ItemStack(Material.DIRT);

        ShapelessRecipe merged_souls_recipe = new ShapelessRecipe(new NamespacedKey(plugin, "merged_soul"), merged_soul);
        for (int a = 0; a < 9; a++) {
            merged_souls_recipe.addIngredient(Material.COMMAND_BLOCK);
        }

        plugin.getServer().addRecipe(merged_souls_recipe);
    }
}
