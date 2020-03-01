package me.fullpotato.badlandscaves.badlandscaves.CustomItemRecipes;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;

public class MergedSoulsCrafting {
    private BadlandsCaves plugin;
    public MergedSoulsCrafting(BadlandsCaves bcav) {
        plugin = bcav;
    }

    public void merge_souls() {
        ItemStack merged_souls = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.merged_souls").getValues(true));

        ShapelessRecipe merged_souls_recipe = new ShapelessRecipe(new NamespacedKey(plugin, "merged_souls"), merged_souls);
        for (int a = 0; a < 9; a++) {
            merged_souls_recipe.addIngredient(Material.COMMAND_BLOCK);
        }

        plugin.getServer().addRecipe(merged_souls_recipe);
    }
}
