package me.fullpotato.badlandscaves.CustomItems.Using;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public abstract class LimitedUseItems {
    public boolean checkMatchIgnoreUses(final ItemStack item, final ItemStack check, final int lore_line) {
        final ItemStack clone = item.clone();

        final ItemMeta item_meta = clone.getItemMeta();
        if (item_meta != null && item_meta.hasLore() && item_meta.getLore().size() > lore_line) {
            List<String> clone_lore = item_meta.getLore();
            clone_lore.remove(lore_line);
            item_meta.setLore(clone_lore);
            clone.setItemMeta(item_meta);
        }

        final ItemMeta check_meta = check.getItemMeta();
        if (check_meta != null && check_meta.hasLore() && check_meta.getLore().size() > lore_line) {
            List<String> check_lore = check_meta.getLore();
            check_lore.remove(lore_line);
            check_meta.setLore(check_lore);
            check.setItemMeta(check_meta);
        }

        return (clone.isSimilar(check));
    }

    public void depleteUse(final ItemStack item, final int uses_line) {
        final ItemMeta meta = item.getItemMeta();
        if (meta != null && meta.hasLore()) {
            ArrayList<String> lore = (ArrayList<String>) meta.getLore();
            if (lore != null) {
                String uses = lore.size() > uses_line? lore.get(uses_line) : null;
                if (uses != null) {
                    String[] split = uses.split(":");
                    try {
                        int uses_left = Integer.parseInt(split[1].substring(1));
                        uses_left--;
                        if (uses_left == 0) {
                            item.setAmount(0);
                        }
                        else {
                            lore.set(uses_line, split[0] + ": " + uses_left);
                            meta.setLore(lore);
                            item.setItemMeta(meta);
                        }
                    }
                    catch (NumberFormatException ignore) {
                        //System.out.println(split[1].substring(1));
                    }
                }
            }
        }
    }
}
