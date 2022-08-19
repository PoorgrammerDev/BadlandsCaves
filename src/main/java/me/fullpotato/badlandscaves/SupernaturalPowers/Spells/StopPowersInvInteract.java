package me.fullpotato.badlandscaves.SupernaturalPowers.Spells;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class StopPowersInvInteract implements Listener {
    private final BadlandsCaves plugin;
    private final List<ItemStack> activePowers;
    public StopPowersInvInteract(BadlandsCaves bcav) {
        plugin = bcav;

        activePowers = new ArrayList<>();
        for (ActivePowers value : ActivePowers.values()) {
            activePowers.add(plugin.getCustomItemManager().getItem(value.getItem()));
        }
        activePowers.add(plugin.getCustomItemManager().getItem(CustomItem.ECLIPSED_SHADOWS));
    }

    @EventHandler
    public void stop_click (InventoryClickEvent event) {
        final ItemStack item = event.getCurrentItem();
        if (item == null) return;

        for (ItemStack activePower : activePowers) {
            if (item.isSimilar(activePower)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void stop_swap (PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        final boolean has_powers = (byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == 1;
        if (!has_powers) return;

        final ItemStack offhand = event.getOffHandItem();
        final ItemStack mainhand = event.getMainHandItem();

        for (ItemStack activePower : activePowers) {
            if ((offhand != null && offhand.isSimilar(activePower)) || (mainhand != null && mainhand.isSimilar(activePower))) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void stop_drop (PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        final boolean has_powers = (byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == 1;
        if (!has_powers) return;

        final ItemStack item = event.getItemDrop().getItemStack();
        for (ItemStack activePower : activePowers) {
            if (item.isSimilar(activePower)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void death_clear (PlayerDeathEvent event) {
        Player player = event.getEntity();
        final boolean has_powers = (byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == 1;
        if (!has_powers) return;

        List<ItemStack> items = event.getDrops();
        for (int a = items.size() - 1; a >= 0; a--) {
            for (ItemStack activePower : activePowers) {
                if (items.get(a).isSimilar(activePower)) {
                    event.getDrops().remove(a);
                    break;
                }
            }
        }

        String sectionname = "player_info." + player.getUniqueId() + ".saved_offhand_item";
        ItemStack item = plugin.getSystemConfig().getItemStack(sectionname);
        if (item != null && !item.getType().isAir()) {
            player.getWorld().dropItemNaturally(player.getLocation(), item);
            plugin.getSystemConfig().set(sectionname, null);
            plugin.saveSystemConfig();
        }


        PlayerScore.HAS_DISPLACE_MARKER.setScore(plugin, player, 0);
    }
}
