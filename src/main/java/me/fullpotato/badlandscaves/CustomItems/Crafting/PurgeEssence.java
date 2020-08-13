package me.fullpotato.badlandscaves.CustomItems.Crafting;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

public class PurgeEssence extends MatchCrafting implements Listener {
    private final BadlandsCaves plugin;
    public PurgeEssence(BadlandsCaves bcav) {
        plugin = bcav;
    }

    public void purge_essence_craft () {
        final ItemStack purge_essence = plugin.getCustomItemManager().getItem(CustomItem.PURGE_ESSENCE);
        ShapedRecipe purge_ess_craft = new ShapedRecipe(new NamespacedKey(plugin, "purge_essence"), purge_essence);
        purge_ess_craft.shape("###","#@#","###");

        boolean isHardmode = plugin.getSystemConfig().getBoolean("hardmode");
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

    @EventHandler
    public void purge_ess (PrepareItemCraftEvent event) {
        if (event.getRecipe() == null || event.getRecipe().getResult() == null) return;
        if (plugin.getSystemConfig().getBoolean("hardmode")) return;

        final ItemStack result = event.getRecipe().getResult();
        final ItemStack purge_essence = plugin.getCustomItemManager().getItem(CustomItem.PURGE_ESSENCE);

        if (result.isSimilar(purge_essence)) {
            final ItemStack tiny_blz = plugin.getCustomItemManager().getItem(CustomItem.TINY_BLAZE_POWDER);

            if (!isMatching(event.getInventory().getMatrix(), tiny_blz)) {
                event.getInventory().setResult(null);
            }
        }
    }
}
