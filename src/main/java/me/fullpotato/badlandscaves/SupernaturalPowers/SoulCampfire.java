package me.fullpotato.badlandscaves.SupernaturalPowers;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import me.fullpotato.badlandscaves.SupernaturalPowers.Spells.ActivePowers;
import me.fullpotato.badlandscaves.SupernaturalPowers.Spells.SwapPowers;
import me.fullpotato.badlandscaves.Util.EmptyItem;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class SoulCampfire implements Listener {
    private final BadlandsCaves plugin;
    private final SwapPowers swapPowers;
    private final ItemStack empty = EmptyItem.getEmptyItem(Material.WHITE_STAINED_GLASS_PANE);
    private final ItemStack selectedIndicator = getSelectedIndicator();
    private final ItemStack orderChangeExplanation = getOrderChangeExplanation();
    private final ItemStack doubleShiftIconOn = getDoubleShiftIcon(true);
    private final ItemStack doubleShiftIconOff = getDoubleShiftIcon(false);
    private final ItemStack swapCooldownIconOn = getSwapCooldownIcon(true);
    private final ItemStack swapCooldownIconOff = getSwapCooldownIcon(false);
    private final String title = ChatColor.of("#03969a") + "Soul Campfire";

    public SoulCampfire(BadlandsCaves plugin) {
        this.plugin = plugin;
        this.swapPowers = new SwapPowers(plugin);
    }

    @EventHandler
    public void campfireInteract (PlayerInteractEvent event) {
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            final Block block = event.getClickedBlock();
            if (block != null && block.getType().equals(Material.SOUL_CAMPFIRE)) {
                final Player player = event.getPlayer();
                if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == 1) {
                    if (PlayerScore.SWAP_WINDOW.hasScore(plugin, player) && (byte) PlayerScore.SWAP_WINDOW.getScore(plugin, player) != 0) {
                        event.setCancelled(true);
                        openInventory(player);
                    }
                }
            }
        }
    }

    public void openInventory (Player player) {
        final Inventory inventory = plugin.getServer().createInventory(player, 54, title);
        for (int i = 0; i < 54; i++) {
            inventory.setItem(i, empty);
        }

        inventory.setItem(4, orderChangeExplanation);

        //Power Swap Order
        final ActivePowers[] order = swapPowers.getSwapOrder(player);
        for (int i = 0; i < order.length; i++) {
            inventory.setItem(10 + (2 * i), order[i].getItem().getItem());
        }

        //Add more things here (Utilize slots 37, 39, 41, 43)
        inventory.setItem(37, (byte) PlayerScore.SWAP_DOUBLESHIFT_OPTION.getScore(plugin, player) == 1 ? doubleShiftIconOn : doubleShiftIconOff);
        inventory.setItem(39, (byte) PlayerScore.SWAP_COOLDOWN_OPTION.getScore(plugin, player) == 1 ? swapCooldownIconOn : swapCooldownIconOff);

        player.openInventory(inventory);
    }

    @EventHandler
    public void menuInteract (InventoryClickEvent event) {
        if (event.getView().getTitle().equals(title)) {
            final Inventory inventory = event.getClickedInventory();
            final Player player = (Player) event.getWhoClicked();
            final ItemStack cursor = event.getCursor();
            final ItemStack currentItem = event.getCurrentItem();
            final InventoryAction action = event.getAction();
            final int slot = event.getSlot();
            if (inventory != null) {
                if (inventory.equals(event.getView().getTopInventory())) {
                    event.setCancelled(true);
                    //Select Spells
                    if (currentItem != null) {
                        if (isSpellItem(currentItem)) {
                            for (int i = 0; i < 9; i++) {
                                ItemStack iter = inventory.getItem(i);
                                if (iter != null && iter.isSimilar(selectedIndicator)) {
                                    final ItemStack otherSpell = inventory.getItem(i + 9);

                                    //swap spells
                                    inventory.setItem(i + 9, currentItem);
                                    inventory.setItem(slot, otherSpell);

                                    //clear indicators
                                    inventory.setItem(i, empty);
                                    inventory.setItem(slot - 9, empty);
                                    return;
                                }
                            }

                            inventory.setItem(slot - 9, selectedIndicator);
                        }
                        //Double Shift Option
                        else if (currentItem.isSimilar(doubleShiftIconOn)) {
                            inventory.setItem(slot, doubleShiftIconOff);
                        }
                        else if (currentItem.isSimilar(doubleShiftIconOff)) {
                            inventory.setItem(slot, doubleShiftIconOn);
                        }

                        //Swap Cooldown Option
                        else if (currentItem.isSimilar(swapCooldownIconOn)) {
                            inventory.setItem(slot, swapCooldownIconOff);
                        }
                        else if (currentItem.isSimilar(swapCooldownIconOff)) {
                            inventory.setItem(slot, swapCooldownIconOn);
                        }
                    }


                }
            }
        }
    }

    @EventHandler
    public void closeInventory (InventoryCloseEvent event) {
        if (event.getView().getTitle().equals(title)) {
            final Inventory inventory = event.getInventory();
            final Player player = (Player) event.getPlayer();

            //UPDATE SWAP POWER ORDER
            final ItemStack[] powerItemOrder = {
                    inventory.getItem(10),
                    inventory.getItem(12),
                    inventory.getItem(14),
                    inventory.getItem(16),
            };

            final ActivePowers[] values = ActivePowers.values();
            final ActivePowers[] powerOrder = new ActivePowers[4];
            for (int i = 0; i < powerItemOrder.length; i++) {
                for (ActivePowers value : values) {
                    if (powerItemOrder[i] != null && powerItemOrder[i].isSimilar(value.getItem().getItem())) {
                        powerOrder[i] = value;
                    }
                }
            }
            swapPowers.setSwapOrder(player, powerOrder);

            //UPDATE DOUBLESHIFT OPTION
            final ItemStack doubleshift = inventory.getItem(37);
            if (doubleshift != null) {
                if (doubleshift.isSimilar(doubleShiftIconOn)) {
                    PlayerScore.SWAP_DOUBLESHIFT_OPTION.setScore(plugin, player, 1);
                }
                else if (doubleshift.isSimilar(doubleShiftIconOff)) {
                    PlayerScore.SWAP_DOUBLESHIFT_OPTION.setScore(plugin, player, 0);
                }
            }

            final ItemStack swapCooldown = inventory.getItem(39);
            if (swapCooldown != null) {
                if (swapCooldown.isSimilar(swapCooldownIconOn)) {
                    PlayerScore.SWAP_COOLDOWN_OPTION.setScore(plugin, player, 1);
                }
                else if (swapCooldown.isSimilar(swapCooldownIconOff)) {
                    PlayerScore.SWAP_COOLDOWN_OPTION.setScore(plugin, player, 0);
                }
            }

        }
    }

    public boolean isSpellItem (ItemStack item) {
        for (ActivePowers value : ActivePowers.values()) {
            if (item.isSimilar(value.getItem().getItem())) return true;
        }
        return false;
    }

    public ItemStack getOrderChangeExplanation() {
        final ItemStack item = CustomItem.SOUL_CRYSTAL.getItem();
        final ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD + "Change Swap Order");

        final ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Change the order of swapping between powers here.");
        meta.setLore(lore);

        item.setItemMeta(meta);
        return item;
    }

    public ItemStack getSelectedIndicator () {
        final ItemStack item = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        final ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN.toString() + ChatColor.BOLD + "Selected");

        final ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "This power has been selected.");
        lore.add(ChatColor.GRAY + "Select another power to swap places.");
        meta.setLore(lore);

        item.setItemMeta(meta);
        return item;
    }

    public ItemStack getDoubleShiftIcon(boolean activated) {
        final ItemStack item = new ItemStack(Material.KNOWLEDGE_BOOK);
        final ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.BLUE.toString() + ChatColor.BOLD + "Doubleshift");
        meta.setCustomModelData(223);

        final ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "This is activated by default.");
        lore.add(ChatColor.GRAY + "Shift / sneak twice to access powers.");
        lore.add(ChatColor.GRAY + "When this is disabled, you only have to shift / sneak once.");

        if (activated) {
            lore.add(ChatColor.GREEN.toString() + ChatColor.BOLD + "ACTIVATED");
        }
        else {
            lore.add(ChatColor.RED.toString() + ChatColor.BOLD + "DEACTIVATED");
        }

        meta.setLore(lore);

        item.setItemMeta(meta);
        return item;
    }

    public ItemStack getSwapCooldownIcon(boolean activated) {
        final ItemStack item = new ItemStack(Material.KNOWLEDGE_BOOK);
        final ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.BLUE.toString() + ChatColor.BOLD + "Swap Cooldown");
        meta.setCustomModelData(224);

        final ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "This is activated by default.");
        lore.add(ChatColor.GRAY + "A very short cooldown prevents you from");
        lore.add(ChatColor.GRAY + "switching between multiple powers quickly.");
        lore.add(ChatColor.GRAY + "This is meant to prevent accidental over-switching.");

        if (activated) {
            lore.add(ChatColor.GREEN.toString() + ChatColor.BOLD + "ACTIVATED");
        }
        else {
            lore.add(ChatColor.RED.toString() + ChatColor.BOLD + "DEACTIVATED");
        }

        meta.setLore(lore);

        item.setItemMeta(meta);
        return item;
    }
}
