package me.fullpotato.badlandscaves.badlandscaves.CustomItemRecipes;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.badlandscaves.Events.CustomItems.CustomItem;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

public class AppleCrafting {
    private BadlandsCaves plugin;
    public AppleCrafting(BadlandsCaves bcav) {
        plugin = bcav;
    }

    public void craftNotchApple() {
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

    public void craftBlessedApple() {
        final ItemStack blessed_apple = CustomItem.BLESSED_APPLE.getItem();

        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "blessed_apple"), blessed_apple);
        recipe.shape("###", "#@#", "###");
        recipe.setIngredient('#', Material.COMMAND_BLOCK);
        recipe.setIngredient('@', Material.GOLDEN_APPLE);

        plugin.getServer().addRecipe(recipe);
    }

    public void craftEnchantedBlessedApple() {
        final ItemStack enchanted_blessed_apple = CustomItem.ENCHANTED_BLESSED_APPLE.getItem();

        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "enchanted_blessed_apple"), enchanted_blessed_apple);


        recipe.shape("###", "#@#", "###");
        recipe.setIngredient('#', Material.COMMAND_BLOCK);
        recipe.setIngredient('@', Material.ENCHANTED_GOLDEN_APPLE);

        plugin.getServer().addRecipe(recipe);
    }
}
