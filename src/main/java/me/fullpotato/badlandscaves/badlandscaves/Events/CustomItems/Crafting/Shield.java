package me.fullpotato.badlandscaves.badlandscaves.Events.CustomItems.Crafting;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

public class Shield implements Listener {
    private BadlandsCaves plugin;

    public Shield(BadlandsCaves plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void craftShield(PrepareItemCraftEvent event) {
        if (event.getRecipe() == null || event.getRecipe().getResult() == null) return;

        final ItemStack result = event.getRecipe().getResult();
        final ItemStack stoneShield = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.stone_shield").getValues(true));
        final ItemStack ironShield = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.iron_shield").getValues(true));
        final ItemStack diamondShield = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.diamond_shield").getValues(true));

        if (result.isSimilar(stoneShield) || result.isSimilar(ironShield) || result.isSimilar(diamondShield)) {
            for (HumanEntity humanEntity : event.getViewers()) {
                Player player = (Player) humanEntity;
                if (player.getMetadata("has_supernatural_powers").get(0).asBoolean()) {
                    event.getInventory().setResult(null);
                    return;
                }
            }
        }
    }
}
