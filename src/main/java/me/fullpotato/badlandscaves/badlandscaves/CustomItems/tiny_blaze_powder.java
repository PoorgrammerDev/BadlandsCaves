package me.fullpotato.badlandscaves.badlandscaves.CustomItems;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;

public class tiny_blaze_powder implements Listener {
    private BadlandsCaves plugin;
    public tiny_blaze_powder (BadlandsCaves bcav) {
        plugin = bcav;
    }

    public void tiny_blaze_powder_craft () {
        ItemStack tiny_blz = new ItemStack(Material.STRUCTURE_VOID, 9);
        ItemMeta tiny_blz_meta = tiny_blz.getItemMeta();
        tiny_blz_meta.setDisplayName(ChatColor.RESET + "Tiny Pile of Blaze Powder");
        tiny_blz_meta.setCustomModelData(100);
        tiny_blz.setItemMeta(tiny_blz_meta);

        ShapelessRecipe blz_pd_to_small = new ShapelessRecipe(new NamespacedKey(plugin, "tiny_blaze_powder"), tiny_blz);
        blz_pd_to_small.addIngredient(Material.BLAZE_POWDER);

        plugin.getServer().addRecipe(blz_pd_to_small);
    }

    public void back_to_large () {
        ShapelessRecipe tiny_blz_to_big = new ShapelessRecipe(new NamespacedKey(plugin, "large_blaze_powder"), new ItemStack(Material.BLAZE_POWDER, 1));
        tiny_blz_to_big.addIngredient(9, Material.STRUCTURE_VOID);

        plugin.getServer().addRecipe(tiny_blz_to_big);
    }
}
