package me.fullpotato.badlandscaves.badlandscaves.CustomItemRecipes;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
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
        ItemStack purge_essence = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.purge_essence").getValues(true));
        ShapedRecipe purge_ess_craft = new ShapedRecipe(new NamespacedKey(plugin, "purge_essence"), purge_essence);
        purge_ess_craft.shape("###","#@#","###");

        boolean isHardmode = plugin.getConfig().getBoolean("game_values.hardmode");
        /*SHAPE:
         *  ###
         *  #@#
         *  ###
         */
        if (!isHardmode) {
            //where # = gold ingots, @ = tiny pile of blaze powder
            purge_ess_craft.setIngredient('#', Material.GOLD_INGOT);
            purge_ess_craft.setIngredient('@', Material.STRUCTURE_BLOCK);
        }
        else {
            //where # = tiny pile of blaze powder, @ = regular golden apple
            purge_ess_craft.setIngredient('#', Material.STRUCTURE_BLOCK);
            purge_ess_craft.setIngredient('@', Material.GOLDEN_APPLE);
        }

        plugin.getServer().addRecipe(purge_ess_craft);
    }
}
