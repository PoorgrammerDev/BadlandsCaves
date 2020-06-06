package me.fullpotato.badlandscaves.badlandscaves.Events.CustomItems.Crafting;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.badlandscaves.Events.CustomItems.CustomItem;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

public class MagicEssence extends MatchCrafting implements Listener {
    private BadlandsCaves plugin;
    public MagicEssence(BadlandsCaves bcav) {
        plugin = bcav;
    }

    @EventHandler
    public void magic_ess (PrepareItemCraftEvent event) {
        if (event.getRecipe() == null || event.getRecipe().getResult() == null) return;

        final ItemStack result = event.getRecipe().getResult();
        final ItemStack magic_essence = CustomItem.MAGIC_ESSENCE.getItem();
        if (!result.isSimilar(magic_essence)) return;

        final ItemStack witch_soul = CustomItem.WITCH_SOUL.getItem();

        if (!isMatching(event.getInventory().getMatrix(), witch_soul)) {
            event.getInventory().setResult(null);
        }
    }
}
