package me.fullpotato.badlandscaves.badlandscaves.Events.SupernaturalPowers;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;

public class noInteract implements Listener {
    private BadlandsCaves plugin;
    public noInteract (BadlandsCaves bcav) {
        plugin = bcav;
    }

    @EventHandler
    public void stop_click (InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        int has_powers = player.getMetadata("has_supernatural_powers").get(0).asInt();
        if (has_powers < 1.0) return;

        ItemStack item = event.getCurrentItem();
        if (item == null) return;

        ItemStack displace = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.displace").getValues(true));
        if (item.isSimilar(displace)) event.setCancelled(true);
    }

    @EventHandler
    public void stop_swap (PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        int has_powers = player.getMetadata("has_supernatural_powers").get(0).asInt();
        if (has_powers < 1.0) return;

        ItemStack item = event.getOffHandItem();
        ItemStack item_2 = event.getMainHandItem();
        if (item == null) return;

        ItemStack displace = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.displace").getValues(true));
        if (item.isSimilar(displace)) event.setCancelled(true);
        if (item_2.isSimilar(displace)) event.setCancelled(true);
    }

    @EventHandler
    public void stop_drop (PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        int has_powers = player.getMetadata("has_supernatural_powers").get(0).asInt();
        if (has_powers < 1.0) return;

        ItemStack item = event.getItemDrop().getItemStack();

        ItemStack displace = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.displace").getValues(true));
        if (item.isSimilar(displace)) event.setCancelled(true);
    }

    //TODO the player drops the spell book upon death. somehow prevent this
}
