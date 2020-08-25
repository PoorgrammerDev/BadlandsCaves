package me.fullpotato.badlandscaves.CustomItems.Crafting;

import me.fullpotato.badlandscaves.BadlandsCaves;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
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
        red_sand_recipe.addIngredient(new RecipeChoice.MaterialChoice(Material.TERRACOTTA, Material.WHITE_TERRACOTTA, Material.ORANGE_TERRACOTTA, Material.MAGENTA_TERRACOTTA, Material.LIGHT_BLUE_TERRACOTTA, Material.YELLOW_TERRACOTTA, Material.LIME_TERRACOTTA, Material.PINK_TERRACOTTA, Material.GRAY_TERRACOTTA, Material.LIGHT_GRAY_TERRACOTTA, Material.CYAN_TERRACOTTA, Material.PURPLE_TERRACOTTA, Material.BLUE_TERRACOTTA, Material.BROWN_TERRACOTTA, Material.GREEN_TERRACOTTA, Material.RED_TERRACOTTA, Material.BLACK_TERRACOTTA));

        plugin.getServer().addRecipe(red_sand_recipe);
    }
}
