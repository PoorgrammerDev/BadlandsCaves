package me.fullpotato.badlandscaves.CustomItems.Using;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import me.fullpotato.badlandscaves.CustomItems.CustomItemManager;
import me.fullpotato.badlandscaves.Util.EmptyItem;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class UseRune implements Listener {
    private final BadlandsCaves plugin;
    private final CustomItemManager customItemManager;
    private final String title = "§8Rune";
    private int task_id;

    public UseRune(BadlandsCaves plugin) {
        this.plugin = plugin;
        customItemManager = plugin.getCustomItemManager();
    }

    @EventHandler
    public void useRune (PlayerInteractEvent event) {
        final Action action = event.getAction();
        if (!action.equals(Action.RIGHT_CLICK_BLOCK) && !action.equals(Action.RIGHT_CLICK_AIR)) return;
        if (event.getItem() == null) return;

        final ItemStack current = event.getItem();
        if (!checkRuneItem(current)) return;

        final Player player = event.getPlayer();
        event.setCancelled(true);

        if (event.getHand() == null || event.getHand().equals(EquipmentSlot.OFF_HAND)) return;
        if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) != 1) {
            player.sendMessage(ChatColor.RED + "This item can only be used by Heretics.");
            return;
        }

        openInputGUI(player);
        player.playSound(player.getLocation(), Sound.BLOCK_ENDER_CHEST_OPEN, SoundCategory.PLAYERS, 0.5F, 1);
    }

    public boolean checkRuneItem(final ItemStack input) {
        final ItemStack current = input.clone();
        final ItemStack rune = customItemManager.getItem(CustomItem.RUNE);

        if (current.hasItemMeta()) {
            ItemMeta current_meta = current.getItemMeta();
            if (current_meta != null && current_meta.hasLore()) {
                List<String> current_lore = current_meta.getLore();
                if (current_lore != null && current_lore.size() == 4) {
                    current_lore.remove(3);
                    current_lore.remove(2);
                    current_meta.setLore(current_lore);
                    current.setItemMeta(current_meta);

                    //does the same thing for the rune item
                    ItemMeta rune_meta = rune.getItemMeta();
                    List<String> rune_lore = rune_meta.getLore();
                    rune_lore.remove(3);
                    rune_lore.remove(2);
                    rune_meta.setLore(rune_lore);
                    rune.setItemMeta(rune_meta);

                    return current.isSimilar(rune);
                }
            }
        }
        return false;
    }

    public void openInputGUI (final Player player) {
        final Inventory inventory = plugin.getServer().createInventory(player, InventoryType.HOPPER, title);
        final ItemStack empty = EmptyItem.getEmptyItem(Material.BLACK_STAINED_GLASS_PANE);

        inventory.setItem(0, empty);
        inventory.setItem(1, empty);
        inventory.setItem(3, empty);
        inventory.setItem(4, empty);

        player.openInventory(inventory);

        ItemStack current = player.getInventory().getItemInMainHand();
        final ItemStack magic_essence = customItemManager.getItem(CustomItem.MAGIC_ESSENCE);
        final ItemStack merged_souls = customItemManager.getItem(CustomItem.MERGED_SOULS);
        new BukkitRunnable() {
            @Override
            public void run() {
                task_id = this.getTaskId();
                try {
                    int souls_level = Integer.parseInt(current.getItemMeta().getLore().get(2).substring(2).split(" ")[0]);
                    int magic_essence_level = Integer.parseInt(current.getItemMeta().getLore().get(3).substring(2).split(" ")[0]);

                    if (souls_level < 1 || magic_essence_level < 4) {
                        ItemStack offering = inventory.getItem(2);
                        if (offering != null) {
                            if (souls_level < 1 && offering.isSimilar(merged_souls)) {
                                souls_level++;
                                ItemMeta current_meta = current.getItemMeta();
                                List<String> current_lore = current_meta.getLore();
                                current_lore.set(2, "§7" + souls_level + " / 1 §dMerged Souls");
                                current_meta.setLore(current_lore);
                                current.setItemMeta(current_meta);
                                offering.setAmount(offering.getAmount() - 1);
                                player.playSound(player.getLocation(), Sound.BLOCK_DISPENSER_DISPENSE, SoundCategory.PLAYERS, 0.5F, 1);
                            }
                            else if (magic_essence_level < 4 && offering.isSimilar(magic_essence)) {
                                magic_essence_level++;
                                ItemMeta current_meta = current.getItemMeta();
                                List<String> current_lore = current_meta.getLore();
                                current_lore.set(3, "§7" + magic_essence_level + " / 4 §9Essences of Magic");
                                current_meta.setLore(current_lore);
                                current.setItemMeta(current_meta);
                                offering.setAmount(offering.getAmount() - 1);
                                player.playSound(player.getLocation(), Sound.BLOCK_DISPENSER_DISPENSE, SoundCategory.PLAYERS, 0.5F, 1);
                            }
                        }
                    }
                    else {
                        final ItemStack charged_rune = customItemManager.getItem(CustomItem.CHARGED_RUNE);
                        player.getInventory().setItemInMainHand(charged_rune);
                        player.closeInventory();
                        player.playSound(player.getLocation(), Sound.BLOCK_CONDUIT_ACTIVATE, SoundCategory.PLAYERS, 0.5F, 1);
                        this.cancel();
                    }
                }
                catch (NumberFormatException ignored) {
                }
            }
        }.runTaskTimer(plugin, 0, 20);
    }

    @EventHandler
    public void insideMenu (InventoryClickEvent event) {
        final Inventory clicked_inv = event.getClickedInventory();
        if (clicked_inv != null) {
            if (event.getView().getTitle().equalsIgnoreCase(title)) {
                final Inventory target_inv = event.getView().getTopInventory();
                final Inventory player_inv = event.getView().getBottomInventory();
                final InventoryAction action = event.getAction();
                final ItemStack magic_essence = customItemManager.getItem(CustomItem.MAGIC_ESSENCE);
                final ItemStack merged_souls = customItemManager.getItem(CustomItem.MERGED_SOULS);
                final int slot = event.getSlot();

                if (clicked_inv.equals(target_inv)) {
                    final Player player = (Player) (event.getWhoClicked());
                    if (slot != 2) {
                        event.setCancelled(true);
                    }
                    else {
                        if (action.equals(InventoryAction.PLACE_ALL) || action.equals(InventoryAction.PLACE_ONE) || action.equals(InventoryAction.PLACE_SOME) || action.equals(InventoryAction.SWAP_WITH_CURSOR)) {
                            player.getItemOnCursor();
                            if (!player.getItemOnCursor().isSimilar(merged_souls) && !player.getItemOnCursor().isSimilar(magic_essence)) {
                                event.setCancelled(true);
                            }
                        }
                    }
                }
                else if (clicked_inv.equals(player_inv)) {
                    if (event.getCurrentItem() != null && checkRuneItem(event.getCurrentItem())) {
                        event.setCancelled(true);
                    }
                    if (action.equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)) {
                        if (event.getCurrentItem() != null && !event.getCurrentItem().isSimilar(merged_souls) && !event.getCurrentItem().isSimilar(magic_essence)) {
                            event.setCancelled(true);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void closeMenu (InventoryCloseEvent event) {
        final Inventory inventory = event.getInventory();
        final InventoryView view = event.getView();
        final Player player = (Player) event.getPlayer();

        if (view.getTitle().equals(title) && view.getTopInventory().equals(inventory)) {
            plugin.getServer().getScheduler().cancelTask(task_id);
            if (inventory.getItem(2) != null) {
                if (player.getInventory().firstEmpty() != -1) {
                    player.getInventory().addItem(inventory.getItem(2));
                }
                else {
                    player.getWorld().dropItemNaturally(player.getEyeLocation(), inventory.getItem(2));
                }
            }
            player.playSound(player.getLocation(), Sound.BLOCK_ENDER_CHEST_CLOSE, SoundCategory.PLAYERS, 0.5F, 1);
        }
    }

}
