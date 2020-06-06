package me.fullpotato.badlandscaves.badlandscaves.Events.CustomItems;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.badlandscaves.Events.Loot.TreasureGear;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class StopCustomItemsInteract implements Listener {
    private final BadlandsCaves plugin;

    public StopCustomItemsInteract(BadlandsCaves bcav) {
        plugin = bcav;
    }


    @EventHandler
    public void preventRightClick (PlayerInteractEvent event) {
        Action action = event.getAction();

        if (action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)) {
            ItemStack item = event.getItem();
            if (item != null) {
                for (CustomItem customItem : CustomItem.values()) {
                    if (item.isSimilar(customItem.getItem()) && customItem.getPreventUse()) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }


    @EventHandler
    public void preventAnvil (PrepareAnvilEvent event) {
        TreasureGear treasureGear = new TreasureGear();
        for (ItemStack item : event.getInventory()) {
            if (item != null) {
                for (CustomItem customItem : CustomItem.values()) {
                    if (item.isSimilar(customItem.getItem()) || treasureGear.isTreasureGear(item)) {
                        event.setResult(null);
                    }
                }
            }
        }
    }
}
