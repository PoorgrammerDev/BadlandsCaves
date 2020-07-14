package me.fullpotato.badlandscaves.CustomItems.Crafting;

import me.fullpotato.badlandscaves.BadlandsCaves;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;

public class Sand {
    private final BadlandsCaves plugin;
    public Sand(BadlandsCaves bcav) {
        plugin = bcav;
    }

    public void craft_sand () {
        ItemStack red_sand = new ItemStack(Material.RED_SAND, 2);

        ShapelessRecipe red_sand_recipe = new ShapelessRecipe(new NamespacedKey(plugin, "red_sand"), red_sand);
        red_sand_recipe.addIngredient(Material.DIRT);
        red_sand_recipe.addIngredient(Material.TERRACOTTA);

        plugin.getServer().addRecipe(red_sand_recipe);
    }
}
