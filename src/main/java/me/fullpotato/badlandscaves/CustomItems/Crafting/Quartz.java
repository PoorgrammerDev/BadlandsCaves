package me.fullpotato.badlandscaves.CustomItems.Crafting;

import me.fullpotato.badlandscaves.BadlandsCaves;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;

public class Quartz {
    private final BadlandsCaves plugin;

    public Quartz(BadlandsCaves plugin) {
        this.plugin = plugin;
    }

    public void craft () {
        ShapelessRecipe recipe = new ShapelessRecipe(new NamespacedKey(plugin, "quartz"), new ItemStack(Material.QUARTZ, 4));
        recipe.addIngredient(Material.QUARTZ_BLOCK);
        plugin.getServer().addRecipe(recipe);
    }
}
