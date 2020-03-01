package me.fullpotato.badlandscaves.badlandscaves.Events.CustomItems;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.badlandscaves.Util.LoadCustomItems;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class StopCustomItemsInteract implements Listener {
    private BadlandsCaves plugin;
    public StopCustomItemsInteract(BadlandsCaves bcav) {
        plugin = bcav;
    }

    @EventHandler
    public void r_click (PlayerInteractEvent event) {
        Action action = event.getAction();

        if (action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)) {
            ItemStack item = event.getItem();
            if (item != null) {
                ArrayList<ItemStack> cancelled_items = new ArrayList<>();

                String[] cancelled_items_paths = {
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
                };

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
}
