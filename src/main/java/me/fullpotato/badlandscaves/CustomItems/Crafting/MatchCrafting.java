package me.fullpotato.badlandscaves.CustomItems.Crafting;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class MatchCrafting {

    public boolean isMatching(ItemStack[] matrix, ItemStack matches) {
        for (ItemStack item : matrix) {
            if (item != null) {
                if (item.getType().equals(matches.getType()) && !item.isSimilar(matches)) return false;
            }
        }
        return true;
    }


    public boolean isMatching(ItemStack[] matrix, ArrayList<ItemStack> matches) {
        ArrayList<ItemStack> matches_clone = new ArrayList<>();
        for (ItemStack item : matches) {
            matches_clone.add(item);
            item.setAmount(1);
        }

        ArrayList<Material> materials = new ArrayList<>();
        for (ItemStack match : matches_clone) {
            materials.add(match.getType());
        }

        for (ItemStack item : matrix) {
            if (item != null) {
                if (materials.contains(item.getType()) && !matches_clone.contains(item))  {
                    return false;
                }
            }
        }

        return true;
    }

    public boolean isMatching(ItemStack[] matrix, HashMap<ItemStack, Integer> matches) {

        for (ItemStack item : matrix) {
            if (item != null) {
                boolean pass = false;
                for (ItemStack match : matches.keySet()) {
                    if (match.getType().equals(item.getType()) && match.isSimilar(item)) {
                        pass = true;
                        break;
                    }
                }

                if (!pass) {
                    return false;
                }
            }
        }

        int matching;
        for (int a = 0; a < matrix.length; a++) {
            matching = 0;

            for (int b = a; b < matrix.length; b++) {
                if (matrix[a].isSimilar(matrix[b])) matching++;
            }

            int max_amount = 0;
            for (ItemStack match : matches.keySet()) {
                if (matrix[a].isSimilar(match)) {
                    max_amount = matches.get(match);
                }
            }

            if (max_amount > 0) {
                if (matching > max_amount) {
                    return false;
                }
            }

        }
        return true;
    }

    public boolean isMatching (ItemStack[] matrix, ItemStack matches, int slot) {
        return (matrix[slot].isSimilar(matches));
    }

}
