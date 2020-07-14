package me.fullpotato.badlandscaves.CustomItems.Using.Starlight.Nebulites;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Starlight.StarlightCharge;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import me.fullpotato.badlandscaves.Util.EmptyItem;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;

public class NebuliteInstaller implements Listener {
    private final BadlandsCaves plugin;
    private final String title = ChatColor.of("#0081fa") + "Nebulite Installer";

    public NebuliteInstaller(BadlandsCaves plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void rightClickItem(PlayerInteractEvent event) {
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_AIR)) {
            final ItemStack item = event.getItem();
            if (item != null) {
                if (item.isSimilar(CustomItem.NEBULITE_INSTALLER.getItem())) {
                    event.setCancelled(true);
                    if (plugin.getSystemConfig().getBoolean("hardmode")) {
                        final Player player = event.getPlayer();
                        if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == (byte) 0) {
                            openInventory(player);
                        }
                    }
                }
            }
        }
    }

    public void openInventory (Player player) {
        final Inventory inventory = plugin.getServer().createInventory(player, 27, title);
        final ItemStack background = EmptyItem.getEmptyItem(Material.BLUE_STAINED_GLASS_PANE);
        final ItemStack emptyNebuliteFiller = EmptyItem.getEmptyItem(Material.GRAY_STAINED_GLASS_PANE);

        for (int i = 0; i < 27; i++) {
            inventory.setItem(i, background);
        }

        inventory.setItem(10, null);
        inventory.setItem(14, emptyNebuliteFiller);
        inventory.setItem(15, emptyNebuliteFiller);
        inventory.setItem(16, emptyNebuliteFiller);

        player.openInventory(inventory);
    }

    @EventHandler
    public void useMenu (InventoryClickEvent event) {
        if (event.getView().getTitle().equals(title)) {
            if (event.getClickedInventory() != null) {
                final Player player = (Player) event.getWhoClicked();
                final Inventory inventory = event.getClickedInventory();
                final StarlightCharge starlightCharge = new StarlightCharge(plugin);
                final ItemStack item = event.getCurrentItem();
                final ItemStack cursor = event.getCursor();
                final InventoryAction action = event.getAction();
                if (inventory.equals(event.getInventory())) {
                    if (action.equals(InventoryAction.PLACE_ALL) ||
                            action.equals(InventoryAction.PLACE_ONE) ||
                            action.equals(InventoryAction.PLACE_SOME) ||
                            action.equals(InventoryAction.SWAP_WITH_CURSOR)) {
                        if (cursor != null) {
                            if (starlightCharge.isStarlight(cursor)) {
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        displayNebulites(player, inventory, cursor);
                                    }
                                }.runTaskLater(plugin, 1);
                            }
                            else {
                                event.setCancelled(true);
                                return;
                            }
                        }
                    }

                    if (item != null) {
                        if (item.getType().equals(Material.BLUE_STAINED_GLASS_PANE) ||
                        item.getType().equals(Material.GRAY_STAINED_GLASS_PANE)) {
                            event.setCancelled(true);
                        }
                    }
                }
                else {
                   if (item != null) {
                       if (item.isSimilar(CustomItem.NEBULITE_INSTALLER.getItem())) {
                           event.setCancelled(true);
                       }
                       else if (action.equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)) {
                           if (starlightCharge.isStarlight(item)) {
                               new BukkitRunnable() {
                                   @Override
                                   public void run() {
                                       displayNebulites(player, inventory, item);
                                   }
                               }.runTaskLater(plugin, 1);
                           }
                           else {
                               event.setCancelled(true);
                           }
                       }
                   }
                }
            }
        }
    }

    public void displayNebulites (Player player, Inventory inventory, ItemStack starlight) {
        NebuliteManager nebuliteManager = new NebuliteManager(plugin);
        final Nebulite[] nebulites = nebuliteManager.getNebulites(starlight.clone());

        final ItemStack empty = new ItemStack(Material.AIR);
        if (nebulites == null) {
            inventory.setItem(14, empty);
            inventory.setItem(15, empty);
            inventory.setItem(16, empty);
        }
        else {
            for (int i = 0; i < 3; i++) {
                if (nebulites.length > i && nebulites[i] != null) {
                    inventory.setItem(14 + i, nebulites[i].getNebuliteItem().getItem());
                }
                else {
                    inventory.setItem(14 + i, empty);
                }
            }
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                player.updateInventory();
            }
        }.runTaskLater(plugin, 2);
    }

    @EventHandler
    public void preventDrag(InventoryDragEvent event) {
        if (event.getView().getTitle().equals(title)) {
            for (int slot : event.getRawSlots()) {
                if (slot >= 0 && slot <= 26) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }
}
