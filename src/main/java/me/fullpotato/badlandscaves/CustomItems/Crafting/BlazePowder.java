package me.fullpotato.badlandscaves.CustomItems.Crafting;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;

public class BlazePowder extends MatchCrafting implements Listener {
    private BadlandsCaves plugin;

    public BlazePowder(BadlandsCaves bcav) {
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

    @EventHandler
    public void tiny_blaze_into_large(PrepareItemCraftEvent event) {
        if (event.getRecipe() == null || event.getRecipe().getResult() == null) return;

        final Material result = event.getRecipe().getResult().getType();
        if (result.equals(Material.BLAZE_POWDER)) {
            final ItemStack tiny_blz = CustomItem.TINY_BLAZE_POWDER.getItem();
            if (!isMatching(event.getInventory().getMatrix(), tiny_blz)) {
                event.getInventory().setResult(null);
            }
        }
    }
}
