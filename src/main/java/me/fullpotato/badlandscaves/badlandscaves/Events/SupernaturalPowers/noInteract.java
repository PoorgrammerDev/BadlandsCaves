package me.fullpotato.badlandscaves.badlandscaves.Events.SupernaturalPowers;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.List;

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
        ItemStack item_2 = event.getCursor();

        ItemStack displace = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.displace").getValues(true));
        ItemStack withdraw = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.withdraw").getValues(true));
        ItemStack eyes = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.enhanced_eyes").getValues(true));
        ItemStack vanish = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.vanish").getValues(true));


        if (item.isSimilar(displace) ||
                item.isSimilar(withdraw) ||
                item.isSimilar(eyes) ||
                item.isSimilar(vanish) ||
                item_2.isSimilar(displace) ||
                item_2.isSimilar(withdraw) ||
                item_2.isSimilar(eyes) ||
                item_2.isSimilar(vanish)){
                    event.setCancelled(true);
            }

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
        ItemStack withdraw = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.withdraw").getValues(true));
        ItemStack eyes = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.enhanced_eyes").getValues(true));
        ItemStack vanish = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.vanish").getValues(true));

        if (item.isSimilar(displace) ||
                item.isSimilar(withdraw) ||
                item.isSimilar(eyes) ||
                item.isSimilar(vanish) ||
                item_2.isSimilar(displace) ||
                item_2.isSimilar(withdraw) ||
                item_2.isSimilar(eyes) ||
                item_2.isSimilar(vanish)){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void stop_drop (PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        int has_powers = player.getMetadata("has_supernatural_powers").get(0).asInt();
        if (has_powers < 1.0) return;

        ItemStack item = event.getItemDrop().getItemStack();

        ItemStack displace = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.displace").getValues(true));
        ItemStack withdraw = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.withdraw").getValues(true));
        ItemStack eyes = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.enhanced_eyes").getValues(true));
        ItemStack vanish = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.vanish").getValues(true));

        if (item.isSimilar(displace) ||
                item.isSimilar(withdraw) ||
                item.isSimilar(eyes) ||
                item.isSimilar(vanish)){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void death_clear (PlayerDeathEvent event) {
        Player player = event.getEntity();
        int has_powers = player.getMetadata("has_supernatural_powers").get(0).asInt();
        if (has_powers < 1.0) return;

        ItemStack displace = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.displace").getValues(true));
        ItemStack withdraw = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.withdraw").getValues(true));
        ItemStack eyes = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.enhanced_eyes").getValues(true));
        ItemStack vanish = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.vanish").getValues(true));

        List<ItemStack> items = event.getDrops();
        for (int a = 0; a < items.size(); a++) {
            if (items.get(a).isSimilar(displace) ||
                    items.get(a).isSimilar(withdraw) ||
                    items.get(a).isSimilar(eyes) ||
                    items.get(a).isSimilar(vanish)){
                event.getDrops().remove(a);
            }
        }
        player.setMetadata("has_displace_marker", new FixedMetadataValue(plugin, 0));

        int particle_task_id = player.getMetadata("displace_particle_id").get(0).asInt();
        if (particle_task_id != 0 && particle_task_id != 0.0) Bukkit.getScheduler().cancelTask(particle_task_id);
    }
}
