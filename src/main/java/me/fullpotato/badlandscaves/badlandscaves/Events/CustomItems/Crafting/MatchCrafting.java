package me.fullpotato.badlandscaves.badlandscaves.Events.CustomItems.Crafting;

import org.bukkit.inventory.ItemStack;

public abstract class MatchCrafting {

    public boolean isMatching(ItemStack[] matrix, ItemStack matches) {
        for (ItemStack item : matrix) {
            if (item.getType().equals(matches.getType()) && !item.isSimilar(matches)) return false;
        }
        return true;
    }

}
