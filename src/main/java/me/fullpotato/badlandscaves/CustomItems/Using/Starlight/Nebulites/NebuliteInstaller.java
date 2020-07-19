package me.fullpotato.badlandscaves.CustomItems.Using.Starlight.Nebulites;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Starlight.StarlightCharge;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import me.fullpotato.badlandscaves.Util.EmptyItem;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class NebuliteInstaller implements Listener {
    private final BadlandsCaves plugin;
    private final StarlightCharge starlightCharge;
    private final NebuliteManager nebuliteManager;
    private final NebuliteStatChanges statChanger;
    final ItemStack emptyNebuliteFiller = EmptyItem.getEmptyItem(Material.GRAY_STAINED_GLASS_PANE);
    private final String title = ChatColor.of("#0081fa") + "Nebulite Installer";

    public NebuliteInstaller(BadlandsCaves plugin) {
        this.plugin = plugin;
        starlightCharge = new StarlightCharge(plugin);
        nebuliteManager = new NebuliteManager(plugin);
        statChanger = new NebuliteStatChanges(plugin);
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
                final Inventory inventory = event.getInventory();
                final ItemStack item = event.getCurrentItem();
                final ItemStack cursor = event.getCursor();
                final InventoryAction action = event.getAction();
                final int slot = event.getSlot();

                if (action.equals(InventoryAction.HOTBAR_MOVE_AND_READD) || action.equals(InventoryAction.HOTBAR_SWAP)) {
                    event.setCancelled(true);
                    return;
                }

                //INSTALLER INVENTORY
                if (inventory.equals(event.getClickedInventory())) {
                    if (action.equals(InventoryAction.PLACE_ALL) ||
                            action.equals(InventoryAction.PLACE_ONE) ||
                            action.equals(InventoryAction.PLACE_SOME) ||
                            action.equals(InventoryAction.SWAP_WITH_CURSOR)) {
                        if (cursor != null) {
                            //ADDING STARLIGHT ITEM
                            if (slot == 10) {
                                if (starlightCharge.isStarlight(cursor) && (int) (starlightCharge.getCharge(cursor) * 0.99) > 0) {
                                    displayNebulites(player, inventory);
                                }
                                //PREVENT ADD OTHER ITEMS
                                else {
                                    event.setCancelled(true);
                                    return;
                                }
                            }

                            //ADDING NEBULITE
                            else if (nebuliteManager.isNebulite(cursor)) {
                                ItemStack starlight = inventory.getItem(10);
                                Nebulite nebulite = nebuliteManager.getNebulite(cursor);
                                if (starlight != null && nebulite != null) {
                                    if (nebuliteManager.isSuitable(starlight, nebulite)) {
                                        if (!nebuliteManager.isConflicting(starlight, nebulite)) {
                                            updateNebulites(player, inventory, true);
                                        }
                                        else {
                                            event.setCancelled(true);
                                        }
                                    }
                                    else {
                                        event.setCancelled(true);
                                        return;
                                    }
                                }
                                else {
                                    event.setCancelled(true);
                                    return;
                                }
                            }
                            //PREVENT ADD OTHER ITEMS
                            else {
                                event.setCancelled(true);
                                return;
                            }
                        }
                    }

                    //ANY INTERACTION WITH NEBULITES
                    if (slot >= 14 && slot <= 16) {
                        updateNebulites(player, inventory, inventory.getItem(slot) != null);
                    }


                    //REMOVING STARLIGHT ITEM
                    if (slot == 10 && (action.equals(InventoryAction.PICKUP_ALL) ||
                            action.equals(InventoryAction.PICKUP_HALF) ||
                            action.equals(InventoryAction.PICKUP_ONE) ||
                            action.equals(InventoryAction.PICKUP_SOME) ||
                            action.equals(InventoryAction.MOVE_TO_OTHER_INVENTORY))) {

                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                for (int i = 0; i < 3; i++) {
                                    inventory.setItem(14 + i, emptyNebuliteFiller);
                                }
                                player.updateInventory();
                            }
                        }.runTaskLater(plugin, 1);
                    }

                    //PREVENT INTERACT WITH BACKGROUND ELEMENTS
                    if (item != null) {
                        if (item.getType().equals(Material.BLUE_STAINED_GLASS_PANE) ||
                        item.getType().equals(Material.GRAY_STAINED_GLASS_PANE)) {
                            event.setCancelled(true);
                        }
                    }
                }
                //PLAYER INVENTORY
                else {
                   if (item != null) {
                       //PREVENT INTERACT WITH INSTALLER
                       if (item.isSimilar(CustomItem.NEBULITE_INSTALLER.getItem())) {
                           event.setCancelled(true);
                       }
                       else if (action.equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)) {
                           if (inventory.getItem(10) == null || inventory.getItem(10).getType().equals(Material.AIR)) {
                               //QUICK-ADD STARLIGHT ITEM
                               if (starlightCharge.isStarlight(item) && (int) (starlightCharge.getCharge(item) * 0.99) > 0) {
                                   displayNebulites(player, inventory);
                               }
                               //PREVENT QUICK-ADD OTHER ITEMS
                               else {
                                   event.setCancelled(true);
                               }
                           }

                           //QUICK-ADD NEBULITE
                           else if (nebuliteManager.isNebulite(item)) {
                               ItemStack starlight = inventory.getItem(10);
                               Nebulite nebulite = nebuliteManager.getNebulite(item);
                               if (starlight != null && nebulite != null) {
                                   if (nebuliteManager.isSuitable(starlight, nebulite)) {
                                       if (!nebuliteManager.isConflicting(starlight, nebulite)) {
                                           updateNebulites(player, inventory, true);
                                       }
                                       else {
                                           event.setCancelled(true);
                                       }
                                   }
                                   else {
                                       event.setCancelled(true);
                                   }
                               }
                               else {
                                   event.setCancelled(true);
                               }
                           }
                           //PREVENT QUICK-ADD OTHER ITEMS
                           else {
                               event.setCancelled(true);
                           }
                       }
                   }
                }
            }
        }
    }

    public void displayNebulites (Player player, Inventory inventory) {
        new BukkitRunnable() {
            @Override
            public void run() {
                final ItemStack starlight = inventory.getItem(10);
                if (starlight != null) {
                    final Nebulite[] nebulites = nebuliteManager.getNebulites(starlight.clone());

                    if (nebulites == null) {
                        inventory.setItem(14, null);
                        inventory.setItem(15, null);
                        inventory.setItem(16, null);
                    }
                    else {
                        for (int i = 0; i < 3; i++) {
                            if (nebulites.length > i && nebulites[i] != null) {
                                inventory.setItem(14 + i, nebulites[i].getNebuliteItem().getItem());
                            }
                            else {
                                inventory.setItem(14 + i, null);
                            }
                        }
                    }
                    player.updateInventory();
                }
            }
        }.runTaskLater(plugin, 1);
    }

    public void updateNebulites(Player player, Inventory inventory, boolean active) {
        new BukkitRunnable() {
            @Override
            public void run() {
                final ItemStack starlight = inventory.getItem(10);
                final ItemStack[] nebuliteItems = {
                        inventory.getItem(14),
                        inventory.getItem(15),
                        inventory.getItem(16),
                };

                if (starlight != null && starlightCharge.isStarlight(starlight)) {
                    final Nebulite[] nebulites = new Nebulite[3];
                    for (int i = 0; i < nebuliteItems.length; i++) {
                        if (nebuliteItems[i] != null && nebuliteManager.isNebulite(nebuliteItems[i])) {
                            nebulites[i] = nebuliteManager.getNebulite(nebuliteItems[i]);
                        }
                    }

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            nebuliteManager.setNebulites(starlight, nebulites);
                            statChanger.updateStats(starlight);
                        }
                    }.runTaskLater(plugin, 1);


                    if (active) {
                        player.playSound(player.getLocation(), "custom.nebulite_installer_use", SoundCategory.PLAYERS, 1, 1.5F);

                        int newCharge = (int) (starlightCharge.getCharge(starlight) * 0.99);
                        starlightCharge.setCharge(starlight, newCharge);

                        if ((int) (newCharge * 0.99) <= 0) {
                            player.closeInventory();
                            return;
                        }
                    }
                }
                player.updateInventory();
            }
        }.runTaskLater(plugin, 1);
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

    @EventHandler
    public void closeReturnsItem (InventoryCloseEvent event) {
        if (event.getView().getTitle().equals(title)) {
            ItemStack item = event.getInventory().getItem(10);
            if (item != null) {
                Player player = (Player) event.getViewers().get(0);
                if (player.getInventory().firstEmpty() == -1) {
                    player.getWorld().dropItemNaturally(player.getLocation(), item);
                }
                else {
                    player.getInventory().addItem(item);
                }
            }
        }
    }
}
