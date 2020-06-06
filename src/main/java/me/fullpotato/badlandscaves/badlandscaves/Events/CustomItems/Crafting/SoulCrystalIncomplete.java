package me.fullpotato.badlandscaves.badlandscaves.Events.CustomItems.Crafting;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.badlandscaves.Events.CustomItems.CustomItem;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

public class SoulCrystalIncomplete extends MatchCrafting implements Listener {
    private BadlandsCaves plugin;
    public SoulCrystalIncomplete (BadlandsCaves bcav) {
        plugin = bcav;
    }

    @EventHandler
    public void soul_crystal (PrepareItemCraftEvent event) {
        if (event.getRecipe() == null || event.getRecipe().getResult() == null) return;

        final ItemStack result = event.getRecipe().getResult();
        final ItemStack soul_crystal_incomplete = CustomItem.SOUL_CRYSTAL_INCOMPLETE.getItem();

        if (result.isSimilar(soul_crystal_incomplete)) {
            final ItemStack merged_souls = CustomItem.MERGED_SOULS.getItem();

            if (!isMatching(event.getInventory().getMatrix(), merged_souls)) {
                event.getInventory().setResult(null);
            }
        }
    }
}
