package me.fullpotato.badlandscaves.badlandscaves.CustomItems;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

public class essence_of_purging implements Listener {
    private BadlandsCaves plugin;
    public essence_of_purging (BadlandsCaves bcav) {
        plugin = bcav;
    }

    public void purge_essence_craft () {
        ItemStack purge_essence = new ItemStack(Material.DEBUG_STICK, 1);
        ItemMeta purge_ess_meta = purge_essence.getItemMeta();
        purge_ess_meta.setDisplayName(ChatColor.AQUA + "Essence of Purging");
        purge_ess_meta.setCustomModelData(101);
        purge_essence.setItemMeta(purge_ess_meta);

        ShapedRecipe purge_ess_craft = new ShapedRecipe(new NamespacedKey(plugin, "purge_essence"), purge_essence);

        /*SHAPE:
        *  ###
        *  #@#
        *  ###
        *
        * where # = gold nuggets, @ = tiny pile of blaze powder
        * */
        purge_ess_craft.shape("###","#@#","###");
        purge_ess_craft.setIngredient('#', Material.GOLD_NUGGET);
        purge_ess_craft.setIngredient('@', Material.STRUCTURE_VOID);

        plugin.getServer().addRecipe(purge_ess_craft);
    }
}
