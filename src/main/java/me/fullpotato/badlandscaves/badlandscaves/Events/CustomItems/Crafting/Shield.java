package me.fullpotato.badlandscaves.badlandscaves.Events.CustomItems.Crafting;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.badlandscaves.Events.CustomItems.CustomItem;
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
        final ItemStack stoneShield = CustomItem.STONE_SHIELD.getItem();
        final ItemStack ironShield = CustomItem.IRON_SHIELD.getItem();
        final ItemStack diamondShield = CustomItem.DIAMOND_SHIELD.getItem();

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
