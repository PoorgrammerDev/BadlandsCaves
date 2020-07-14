package me.fullpotato.badlandscaves.Util;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class EmptyItem {
    public static ItemStack getEmptyItem(Material material) {
        final ItemStack empty = new ItemStack(material);
        final ItemMeta meta = empty.getItemMeta();
        meta.setDisplayName(ChatColor.RESET.toString());
        empty.setItemMeta(meta);

        return empty;
    }
}
