package me.fullpotato.badlandscaves.badlandscaves.Events.CustomItems.Crafting;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

public class PurgeEssence extends MatchCrafting implements Listener {
    private BadlandsCaves plugin;
    public PurgeEssence(BadlandsCaves bcav) {
        plugin = bcav;
    }

    @EventHandler
    public void purge_ess (PrepareItemCraftEvent event) {
        if (event.getRecipe() == null || event.getRecipe().getResult() == null) return;
        if (plugin.getConfig().getBoolean("game_values.hardmode")) return;

        final ItemStack result = event.getRecipe().getResult();
        final ItemStack purge_essence = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.purge_essence").getValues(true));

        if (result.isSimilar(purge_essence)) {
            final ItemStack tiny_blz = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.tiny_blaze_powder").getValues(true));

            if (!isMatching(event.getInventory().getMatrix(), tiny_blz)) {
                event.getInventory().setResult(null);
            }
        }
    }
}
