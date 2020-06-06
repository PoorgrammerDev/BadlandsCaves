package me.fullpotato.badlandscaves.badlandscaves.CustomItemRecipes;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.badlandscaves.Events.CustomItems.CustomItem;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;

public class SoulCrystalIncompleteCrafting {
    private BadlandsCaves plugin;
    public SoulCrystalIncompleteCrafting(BadlandsCaves bcav) {
        plugin = bcav;
    }

    public void soul_crystal_incomplete() {
        final ItemStack soul_crystal_incomplete = CustomItem.SOUL_CRYSTAL_INCOMPLETE.getItem();

        ShapelessRecipe soul_crystal_incomplete_recipe = new ShapelessRecipe(new NamespacedKey(plugin, "soul_crystal_incomplete"), soul_crystal_incomplete);
        soul_crystal_incomplete_recipe.addIngredient(Material.DIAMOND);
        soul_crystal_incomplete_recipe.addIngredient(Material.COMMAND_BLOCK);

        plugin.getServer().addRecipe(soul_crystal_incomplete_recipe);
    }


}
