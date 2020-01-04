package me.fullpotato.badlandscaves.badlandscaves.events.Thirst;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

public class water_drinking implements Listener {
    private BadlandsCaves plugin;
    public water_drinking (BadlandsCaves bcav) {
        plugin = bcav;
    }
    @EventHandler
    public void drink (PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();
        if (!item.getType().equals(Material.POTION)) {
            return;
        }

        if (item.getItemMeta().getLore().get(0).equalsIgnoreCase(ChatColor.GRAY + "Light and refreshing.")) {

        }
    }
}
