package me.fullpotato.badlandscaves.badlandscaves.CustomItemRecipes;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.badlandscaves.Events.CustomItems.CustomItem;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

public class PurgeEssenceCrafting {
    private BadlandsCaves plugin;
    public PurgeEssenceCrafting(BadlandsCaves bcav) {
        plugin = bcav;
    }

    public void purge_essence_craft () {
        final ItemStack purge_essence = CustomItem.PURGE_ESSENCE.getItem();
        ShapedRecipe purge_ess_craft = new ShapedRecipe(new NamespacedKey(plugin, "purge_essence"), purge_essence);
        purge_ess_craft.shape("###","#@#","###");

        boolean isHardmode = plugin.getConfig().getBoolean("game_values.hardmode");
        /*SHAPE:
         *  ###
         *  #@#
         *  ###
         */
        if (!isHardmode) {
            purge_ess_craft.setIngredient('#', Material.STRUCTURE_BLOCK); //tiny pile of blaze powder
            purge_ess_craft.setIngredient('@', Material.GOLD_INGOT);
        }
        else {
            purge_ess_craft.setIngredient('#', Material.BLAZE_POWDER);
            purge_ess_craft.setIngredient('@', Material.GOLD_BLOCK);
        }

        plugin.getServer().addRecipe(purge_ess_craft);
    }
}
