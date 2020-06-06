package me.fullpotato.badlandscaves.badlandscaves.Events.CustomItems.Crafting;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.badlandscaves.Events.CustomItems.CustomItem;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

public class HellEssence extends MatchCrafting implements Listener {
    private BadlandsCaves plugin;
    public HellEssence(BadlandsCaves bcav) {
        plugin = bcav;
    }

    @EventHandler
    public void hell_ess (PrepareItemCraftEvent event) {
        if (event.getRecipe() == null || event.getRecipe().getResult() == null) return;

        final ItemStack result = event.getRecipe().getResult();
        final ItemStack hell_essence = CustomItem.HELL_ESSENCE.getItem();
        if (!result.isSimilar(hell_essence)) return;

        final ItemStack pigzombie_soul = CustomItem.PIGZOMBIE_SOUL.getItem();

        if (!isMatching(event.getInventory().getMatrix(), pigzombie_soul)) {
            event.getInventory().setResult(null);
        }

    }


}
