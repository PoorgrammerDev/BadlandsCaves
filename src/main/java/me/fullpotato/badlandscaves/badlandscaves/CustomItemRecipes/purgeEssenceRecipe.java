package me.fullpotato.badlandscaves.badlandscaves.CustomItemRecipes;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

public class purgeEssenceRecipe implements Listener {
    private BadlandsCaves plugin;
    public purgeEssenceRecipe(BadlandsCaves bcav) {
        plugin = bcav;
    }

    public void purge_essence_craft () {
        ItemStack purge_essence = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.purge_essence").getValues(true));

        ShapedRecipe purge_ess_craft = new ShapedRecipe(new NamespacedKey(plugin, "purge_essence"), purge_essence);

        /*SHAPE:
        *  ###
        *  #@#
        *  ###
        *
        * where # = gold nuggets, @ = tiny pile of blaze powder
        * */
        purge_ess_craft.shape("###","#@#","###");
        purge_ess_craft.setIngredient('#', Material.GOLD_INGOT);
        purge_ess_craft.setIngredient('@', Material.STRUCTURE_BLOCK);

        plugin.getServer().addRecipe(purge_ess_craft);
    }
}
