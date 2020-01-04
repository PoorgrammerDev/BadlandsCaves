package me.fullpotato.badlandscaves.badlandscaves.events.Thirst;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class toxic_water_bottling implements Listener {
    private BadlandsCaves plugin;
    public toxic_water_bottling (BadlandsCaves bcav) {
        plugin = bcav;
    }
    @EventHandler
    public void bottling (PlayerInteractEvent event) {
        Action clicktype = event.getAction();
        if (!clicktype.equals(Action.RIGHT_CLICK_BLOCK)) {
            return;
        }
        ItemStack item = event.getItem();
        if (!item.getType().equals(Material.GLASS_BOTTLE)) {
            return;
        }

    }
}
