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

import java.util.List;

public class StopPowersInvInteract implements Listener {
    private BadlandsCaves plugin;
    private ItemStack displace;
    private ItemStack withdraw;
    private ItemStack eyes;
    private ItemStack possess;
    public StopPowersInvInteract(BadlandsCaves bcav) {
        plugin = bcav;

        displace = CustomItem.DISPLACE.getItem();
        withdraw = CustomItem.WITHDRAW.getItem();
        eyes = CustomItem.ENHANCED_EYES.getItem();
        possess = CustomItem.POSSESS.getItem();
    }

    @EventHandler
    public void stop_click (InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        final boolean has_powers = (byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == 1;
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
        final boolean has_powers = (byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == 1;
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
        final boolean has_powers = (byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == 1;
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
        final boolean has_powers = (byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == 1;
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
