package me.fullpotato.badlandscaves.CustomItems.Crafting;

import me.fullpotato.badlandscaves.BadlandsCaves;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

public class Reeds {
    private final BadlandsCaves plugin;
    public Reeds(BadlandsCaves bcav) {
        plugin = bcav;
    }

    public void craft_reeds () {
        ItemStack reeds = new ItemStack(Material.SUGAR_CANE);

        ShapedRecipe reeds_craft = new ShapedRecipe(new NamespacedKey(plugin, "reeds"), reeds);

        /*SHAPE:
         *  *#%
         *  #@#
         *  %#*
         *
         * where # = seeds, @ = diamond block, * = saplings, % = lily pads
         * */
        reeds_craft.shape("*#%","#@#","%#*");
        reeds_craft.setIngredient('#', Material.WHEAT_SEEDS);
        reeds_craft.setIngredient('@', Material.DIAMOND_BLOCK);
        reeds_craft.setIngredient('*', Material.OAK_SAPLING);
        reeds_craft.setIngredient('%', Material.LILY_PAD);

        plugin.getServer().addRecipe(reeds_craft);
    }

}
