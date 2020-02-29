package me.fullpotato.badlandscaves.badlandscaves.CustomItemRecipes;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

public class notchAppleCrafting {
    private BadlandsCaves plugin;
    public notchAppleCrafting (BadlandsCaves bcav) {
        plugin = bcav;
    }

    public void crafting_notch_apple () {
        ItemStack notch_apple = new ItemStack(Material.ENCHANTED_GOLDEN_APPLE);

        ShapedRecipe notch_apple_recipe = new ShapedRecipe(new NamespacedKey(plugin, "notch_apple"), notch_apple);

        /*SHAPE:
         *  ###
         *  #@#
         *  ###
         *
         * where # = gold blocks, @ = apple
         * */
        notch_apple_recipe.shape("###","#@#","###");
        notch_apple_recipe.setIngredient('#', Material.GOLD_BLOCK);
        notch_apple_recipe.setIngredient('@', Material.APPLE);

        plugin.getServer().addRecipe(notch_apple_recipe);
    }
}
