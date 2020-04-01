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

public class StopPowersInvInteract implements Listener {
    private BadlandsCaves plugin;
    private ItemStack displace;
    private ItemStack withdraw;
    private ItemStack eyes;
    private ItemStack possess;
    public StopPowersInvInteract(BadlandsCaves bcav) {
        plugin = bcav;

        displace = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.displace").getValues(true));
        withdraw = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.withdraw").getValues(true));
        eyes = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.enhanced_eyes").getValues(true));
        possess = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.possess").getValues(true));
    }

    @EventHandler
    public void stop_click (InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        final boolean has_powers = player.getMetadata("has_supernatural_powers").get(0).asBoolean();
        if (!has_powers) return;

        ItemStack item = event.getCurrentItem();

        if (item == null) return;

        if (item.isSimilar(displace) ||
                item.isSimilar(withdraw) ||
                item.isSimilar(eyes) ||
                item.isSimilar(possess)){
                    event.setCancelled(true);
            }

    }

    @EventHandler
    public void stop_swap (PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        final boolean has_powers = player.getMetadata("has_supernatural_powers").get(0).asBoolean();
        if (!has_powers) return;

        ItemStack item = event.getOffHandItem();
        ItemStack item_2 = event.getMainHandItem();
        if (item == null) return;

        assert item_2 != null;
        if (item.isSimilar(displace) ||
                item.isSimilar(withdraw) ||
                item.isSimilar(eyes) ||
                item.isSimilar(possess) ||
                item_2.isSimilar(displace) ||
                item_2.isSimilar(withdraw) ||
                item_2.isSimilar(eyes) ||
                item_2.isSimilar(possess)){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void stop_drop (PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        final boolean has_powers = player.getMetadata("has_supernatural_powers").get(0).asBoolean();
        if (!has_powers) return;

        ItemStack item = event.getItemDrop().getItemStack();

        if (item.isSimilar(displace) ||
                item.isSimilar(withdraw) ||
                item.isSimilar(eyes) ||
                item.isSimilar(possess)){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void death_clear (PlayerDeathEvent event) {
        Player player = event.getEntity();
        final boolean has_powers = player.getMetadata("has_supernatural_powers").get(0).asBoolean();
        if (!has_powers) return;

        List<ItemStack> items = event.getDrops();
        for (int a = items.size() - 1; a >= 0; a--) {
            if (items.get(a).isSimilar(displace) ||
                    items.get(a).isSimilar(withdraw) ||
                    items.get(a).isSimilar(eyes) ||
                    items.get(a).isSimilar(possess)){
                event.getDrops().remove(a);
            }
        }
        player.setMetadata("has_displace_marker", new FixedMetadataValue(plugin, false));

        int particle_task_id = player.getMetadata("displace_particle_id").get(0).asInt();
        if (particle_task_id != 0 && particle_task_id != 0.0) Bukkit.getScheduler().cancelTask(particle_task_id);
    }
}
