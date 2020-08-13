package me.fullpotato.badlandscaves.CustomItems;

import me.fullpotato.badlandscaves.Loot.TreasureGear;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class StopCustomItemsInteract implements Listener {
    private final CustomItemManager customItemManager;

    public StopCustomItemsInteract(CustomItemManager customItemManager) {
        this.customItemManager = customItemManager;
    }

    @EventHandler
    public void preventRightClick (PlayerInteractEvent event) {
        Action action = event.getAction();

        if (action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)) {
            ItemStack item = event.getItem();
            if (item != null) {
                if (item.getType().equals(Material.KNOWLEDGE_BOOK)) {
                    event.setCancelled(true);
                    return;
                }

                for (CustomItem customItem : CustomItem.values()) {
                    if (item.isSimilar(customItemManager.getItem(customItem)) && customItem.getPreventUse()) {
                        event.setCancelled(true);
                        return;
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
                    if (item.isSimilar(customItemManager.getItem(customItem)) || treasureGear.isTreasureGear(item)) {
                        event.setResult(null);
                        return;
                    }
                }
            }
        }
    }
}
