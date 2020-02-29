package me.fullpotato.badlandscaves.badlandscaves.CustomItemRecipes;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

public class magicEssenceCrafting {
    private BadlandsCaves plugin;
    public magicEssenceCrafting(BadlandsCaves bcav) {
        plugin = bcav;
    }

    public void magic_essence_craft () {
        ItemStack magic_essence = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.magic_essence").getValues(true));
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "magic_essence"), magic_essence);

        /* SHAPE:
        *  ###
        *  #*#
        *  ###
        * where # = lapis, * = witch soul
         */
        recipe.shape("###", "#*#", "###");
        recipe.setIngredient('#', Material.LAPIS_LAZULI);
        recipe.setIngredient('*', Material.COMMAND_BLOCK);

        plugin.getServer().addRecipe(recipe);
    }
}
