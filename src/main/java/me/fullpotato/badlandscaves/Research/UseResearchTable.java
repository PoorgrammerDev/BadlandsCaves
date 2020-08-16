package me.fullpotato.badlandscaves.Research;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import me.fullpotato.badlandscaves.Util.EmptyItem;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.Lectern;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class UseResearchTable implements Listener {
    private final BadlandsCaves plugin;
    private final ItemStack researchTable;
    private final NamespacedKey key;
    private final ItemStack emptyDarkGray = EmptyItem.getEmptyItem(Material.GRAY_STAINED_GLASS_PANE);
    private final String title = ChatColor.of("#03a1fc") + "Research Table";

    public UseResearchTable(BadlandsCaves plugin) {
        this.plugin = plugin;
        researchTable = plugin.getCustomItemManager().getItem(CustomItem.RESEARCH_TABLE);
        key = new NamespacedKey(plugin, "research_table");
    }

    @EventHandler
    public void placeTable (BlockPlaceEvent event) {
        final ItemStack item = event.getItemInHand();
        if (item.isSimilar(researchTable)) {
            final Player player = event.getPlayer();
            if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == 0) {
                final Block block = event.getBlockPlaced();
                if (block.getState() instanceof Lectern) {
                    final Lectern state = (Lectern) block.getState();
                    state.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte) 1);

                    state.update(true);
                }
            }
            else {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void openTable (PlayerInteractEvent event) {
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            final Block block = event.getClickedBlock();
            if (block != null && isResearchTable(block)) {
                event.setUseInteractedBlock(Event.Result.DENY);

                if (event.getHand() != null && event.getHand().equals(EquipmentSlot.HAND)) {
                    final Player player = event.getPlayer();
                    player.sendMessage(ChatColor.RED + "This block has yet to be implemented."); // TODO: 8/15/2020

                    /*

                    if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == 0) {
                        //openInventory(player);
                    }
                    else {
                        player.sendMessage(ChatColor.RED + "This block cannot be used by Heretics.");
                    }

                     */
                }
            }
        }
    }

    public Inventory openInventory (Player player) {
        final Inventory inventory = plugin.getServer().createInventory(player, 27, title);

        for (int i = 0; i < 27; i++) {
            inventory.setItem(i, emptyDarkGray);
        }
        inventory.setItem(13, null);

        player.openInventory(inventory);
        return inventory;
    }

    @EventHandler
    public void inventoryInteract (InventoryClickEvent event) {
        if (event.getView().getTitle().equals(title)) {
            if (event.getClickedInventory() != null) {
                final InventoryAction action = event.getAction();
                final Inventory menu = event.getInventory();
                if (event.getClickedInventory().equals(menu)) {
                    final int slot = event.getSlot();
                    if (slot == 13) {
                        updateMenu(menu);
                        return;
                    }
                    event.setCancelled(true);
                }
                else {
                    updateMenu(menu);
                }
            }
        }
    }

    public void updateMenu (Inventory inventory) {
        final ItemStack item = inventory.getItem(13);
        if (item != null) {

        }
    }

    @EventHandler
    public void breakTable (BlockBreakEvent event) {
        final Block block = event.getBlock();
        if (isResearchTable(block)) {
            if (!event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
                event.setDropItems(false);
                block.getWorld().dropItemNaturally(block.getLocation(), researchTable);
            }
        }
    }

    public boolean isResearchTable(Block block) {
        if (block.getType().equals(Material.LECTERN)) {
            if (block.getState() instanceof Lectern) {
                final Lectern state = (Lectern) block.getState();
                final PersistentDataContainer container = state.getPersistentDataContainer();
                if (container.has(key, PersistentDataType.BYTE)) {
                    Byte result = container.get(key, PersistentDataType.BYTE);
                    if (result != null) {
                        return result == (byte) 1;
                    }
                }
            }
        }
        return false;
    }
}
