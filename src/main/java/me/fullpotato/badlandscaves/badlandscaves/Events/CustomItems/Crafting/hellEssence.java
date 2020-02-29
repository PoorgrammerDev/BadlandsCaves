package me.fullpotato.badlandscaves.badlandscaves.Events.CustomItems.Crafting;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

public class hellEssence extends MatchCrafting implements Listener {
    private BadlandsCaves plugin;
    public hellEssence (BadlandsCaves bcav) {
        plugin = bcav;
    }

    @EventHandler
    public void hell_ess (PrepareItemCraftEvent event) {
        if (event.getRecipe() == null || event.getRecipe().getResult() == null) return;

        final ItemStack result = event.getRecipe().getResult();
        final ItemStack hell_essence = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.hell_essence").getValues(true));
        if (!result.isSimilar(hell_essence)) return;

        final ItemStack pigzombie_soul = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.pigzombie_soul").getValues(true));

        if (!isMatching(event.getInventory().getMatrix(), pigzombie_soul)) {
            event.getInventory().setResult(null);
        }

    }


}
