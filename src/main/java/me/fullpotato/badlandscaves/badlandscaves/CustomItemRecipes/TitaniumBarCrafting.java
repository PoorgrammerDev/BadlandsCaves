package me.fullpotato.badlandscaves.badlandscaves.CustomItemRecipes;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.badlandscaves.Events.CustomItems.CustomItem;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

public class TitaniumBarCrafting {
    private BadlandsCaves plugin;

    public TitaniumBarCrafting(BadlandsCaves plugin) {
        this.plugin = plugin;
    }

    public void fragmentIntoBar() {
        ItemStack bar = CustomItem.TITANIUM_INGOT.getItem();
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "titanium_ingot"), bar);

        recipe.shape("##", "##");
        recipe.setIngredient('#', Material.COMMAND_BLOCK);

        plugin.getServer().addRecipe(recipe);
    }

}
