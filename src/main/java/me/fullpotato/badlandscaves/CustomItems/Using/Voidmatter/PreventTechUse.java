package me.fullpotato.badlandscaves.CustomItems.Using.Voidmatter;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Voidmatter;
import me.fullpotato.badlandscaves.Util.EnchantmentStorage;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PreventTechUse extends BukkitRunnable implements Listener {
    private final BadlandsCaves plugin;
    private final Voidmatter voidmatter;
    private final EnchantmentStorage enchantmentStorage;
    private final List<InventoryAction> topInvActions = Arrays.asList(InventoryAction.MOVE_TO_OTHER_INVENTORY, InventoryAction.HOTBAR_MOVE_AND_READD, InventoryAction.HOTBAR_SWAP);
    private final List<InventoryAction> bottomInvActions = Arrays.asList(InventoryAction.PLACE_SOME, InventoryAction.PLACE_ONE, InventoryAction.PLACE_ALL, InventoryAction.SWAP_WITH_CURSOR);

    public PreventTechUse(BadlandsCaves plugin, Voidmatter voidmatter, EnchantmentStorage enchantmentStorage) {
        this.plugin = plugin;
        this.voidmatter = voidmatter;
        this.enchantmentStorage = enchantmentStorage;
    }


    /**
     * Fixes armor periodically
     * */
    @Override
    public void run() {
        if (plugin.getSystemConfig().getBoolean("hardmode")) {
            final List<Player> players = new ArrayList<>(plugin.getServer().getOnlinePlayers());
            final int[] counter = {0};

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (counter[0] >= players.size()) {
                        this.cancel();
                        return;
                    }

                    final Player player = players.get(counter[0]);
                    final EntityEquipment equipment = player.getEquipment();

                    if (equipment != null) {
                        final ItemStack[] armor = equipment.getArmorContents();
                        for (ItemStack item : armor) {
                            attemptFixItem(player, item);
                        }
                        equipment.setArmorContents(armor);
                    }

                    counter[0]++;
                }
            }.runTaskTimer(plugin, 0, 2);
        }
    }

    /**
     * Fixes item upon pickup
     * */
    @EventHandler
    public void pickupItem (EntityPickupItemEvent event) {
        if (plugin.getSystemConfig().getBoolean("hardmode")) {
            if (event.getEntity() instanceof Player) {
                final Player player = (Player) event.getEntity();
                final Item itemEntity = event.getItem();
                final ItemStack item = itemEntity.getItemStack().clone();
                attemptFixItem(player, item);

                if (!item.isSimilar(itemEntity.getItemStack())) {
                    itemEntity.setItemStack(item);
                    event.setCancelled(true);
                }
            }
        }
    }

    /**
     * Fixes item upon inventory interaction
     * */
    @EventHandler
    public void getItemFromInv (InventoryClickEvent event) {
        if (plugin.getSystemConfig().getBoolean("hardmode")) {
            final Inventory clickedInventory = event.getClickedInventory();
            if (clickedInventory != null) {
                if (event.getWhoClicked() instanceof Player) {
                    final Player player = (Player) event.getWhoClicked();
                    final Inventory inventory = event.getInventory();
                    final InventoryAction action = event.getAction();
                    if (clickedInventory.equals(inventory)) {
                        final ItemStack item = event.getCurrentItem();
                        if (topInvActions.contains(action)) {
                            attemptFixItem(player, item);
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    player.updateInventory();
                                }
                            }.runTaskLater(plugin, 1);
                        }
                    }
                    else {
                        final ItemStack item = event.getCursor();
                        if (bottomInvActions.contains(action)) {
                            attemptFixItem(player, item);
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    player.updateInventory();
                                }
                            }.runTaskLater(plugin, 1);
                        }
                    }
                }
            }
        }
    }

    /**
     * Fixes item upon using
     * */
    @EventHandler
    public void useItem(PlayerInteractEvent event) {
        if (plugin.getSystemConfig().getBoolean("hardmode")) {
            final Player player = event.getPlayer();
            final ItemStack item = event.getItem();
            if (item != null) {
                attemptFixItem(player, item);
            }
        }
    }

    public void attemptFixItem(Player player, ItemStack item) {
        if (item != null && voidmatter.isVoidmatter(item)) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == (byte) 0) {
                    if (meta.hasEnchants()) {
                        enchantmentStorage.disenchantItem(item);
                    }
                }
                else {
                    if (!meta.hasEnchants()) {
                        enchantmentStorage.loadEnchantments(item);
                    }
                }
            }
        }
    }
}
