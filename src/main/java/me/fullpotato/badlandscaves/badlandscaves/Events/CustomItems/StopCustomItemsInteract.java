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
    private BadlandsCaves plugin;
    private String[] custom_items;
    private String[] cancelled_items_paths = {
            "purge_essence",
            "hell_essence",
            "magic_essence",
            "zombie_soul",
            "creeper_soul",
            "skeleton_soul",
            "spider_soul",
            "pigzombie_soul",
            "ghast_soul",
            "silverfish_soul",
            "witch_soul",
            "phantom_soul",
            "tiny_blaze_powder",
            "merged_souls",
            "voltshock_battery",
            "voltshock_shocker",
            "corrosive_substance",
            "chamber_magma_key",
            "chamber_glowstone_key",
            "chamber_soulsand_key",
    };

    public StopCustomItemsInteract(BadlandsCaves bcav, String[] custom_items) {
        plugin = bcav;
        this.custom_items = custom_items;
    }



    @EventHandler
    public void preventRightClick (PlayerInteractEvent event) {
        Action action = event.getAction();

        if (action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)) {
            ItemStack item = event.getItem();
            if (item != null) {
                ArrayList<ItemStack> cancelled_items = new ArrayList<>();

                for (String path : cancelled_items_paths) {
                    cancelled_items.add(ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items." + path).getValues(true)));
                }

                ItemStack test_ignore_amount = item.clone();
                test_ignore_amount.setAmount(1);

                if (cancelled_items.contains(test_ignore_amount)) {

                    event.setCancelled(true);
                }
            }
        }
    }


    @EventHandler
    public void preventAnvil (PrepareAnvilEvent event) {
        TreasureGear treasureGear = new TreasureGear();
        for (ItemStack item : event.getInventory()) {
            if (item != null) {
                for (String custom_item : custom_items) {
                    if (item.isSimilar(ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items." + custom_item).getValues(true))) || treasureGear.isTreasureGear(item)) {
                        event.setResult(null);
                    }
                }
            }
        }
    }
}
