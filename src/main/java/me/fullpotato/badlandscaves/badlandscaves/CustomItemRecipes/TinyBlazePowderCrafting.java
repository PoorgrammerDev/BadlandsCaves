package me.fullpotato.badlandscaves.badlandscaves.CustomItemRecipes;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.badlandscaves.Events.CustomItems.CustomItem;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;

public class TinyBlazePowderCrafting {
    private BadlandsCaves plugin;
    public TinyBlazePowderCrafting(BadlandsCaves bcav) {
        plugin = bcav;
    }

    public void tiny_blaze_powder_craft () {
        ItemStack tiny_blz = CustomItem.TINY_BLAZE_POWDER.getItem();

        ShapelessRecipe blz_pd_to_small = new ShapelessRecipe(new NamespacedKey(plugin, "tiny_blaze_powder"), tiny_blz);
        blz_pd_to_small.addIngredient(Material.BLAZE_POWDER);

        plugin.getServer().addRecipe(blz_pd_to_small);
    }

    public void back_to_large () {
        ShapelessRecipe tiny_blz_to_big = new ShapelessRecipe(new NamespacedKey(plugin, "large_blaze_powder"), new ItemStack(Material.BLAZE_POWDER, 1));
        tiny_blz_to_big.addIngredient(9, Material.STRUCTURE_BLOCK);

        plugin.getServer().addRecipe(tiny_blz_to_big);
    }
}
