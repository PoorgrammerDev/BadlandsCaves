package me.fullpotato.badlandscaves.badlandscaves.events.Thirst;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class purification implements Listener {
    private BadlandsCaves plugin;
    public purification (BadlandsCaves bcav) {
        plugin = bcav;
    }

    private String cauldron_title = ChatColor.BLUE + "Water Purification";
    public void purification_menu (Player player, Block block) {
        Inventory pure_inv = plugin.getServer().createInventory(null, 27, cauldron_title);

        ItemStack black = new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1);
        ItemMeta black_meta = black.getItemMeta();
        black_meta.setDisplayName(ChatColor.RESET + "");
        black.setItemMeta(black_meta);

        ItemStack blue = new ItemStack(Material.BLUE_STAINED_GLASS_PANE, 1);

        ItemStack d_gray = new ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1);
        ItemMeta d_gray_meta = black.getItemMeta();
        d_gray_meta.setDisplayName(ChatColor.RESET + "");
        d_gray.setItemMeta(d_gray_meta);

        ItemStack l_gray = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE, 1);
        ItemMeta l_gray_meta = black.getItemMeta();
        l_gray_meta.setDisplayName(ChatColor.RESET + "");
        l_gray.setItemMeta(l_gray_meta);

        String cauldron_level = block.getBlockData().getAsString();

        //making the inv
        pure_inv.setItem(1, black);
        pure_inv.setItem(2, d_gray);
        pure_inv.setItem(3, d_gray);
        pure_inv.setItem(4, d_gray);
        pure_inv.setItem(5, d_gray);
        pure_inv.setItem(6, d_gray);
        pure_inv.setItem(7, black);
        pure_inv.setItem(10, black);
        pure_inv.setItem(11, null); //slot for bottle
        pure_inv.setItem(12, d_gray);
        pure_inv.setItem(13, null); //slot for finished prod
        pure_inv.setItem(14, d_gray);
        pure_inv.setItem(15, null); //slot for blaze
        pure_inv.setItem(16, black);
        pure_inv.setItem(19, black);
        pure_inv.setItem(20, d_gray);
        pure_inv.setItem(21, d_gray);
        pure_inv.setItem(22, d_gray);
        pure_inv.setItem(23, d_gray);
        pure_inv.setItem(24, d_gray);
        pure_inv.setItem(25, black);

        if (cauldron_level.equalsIgnoreCase("minecraft:cauldron[level=0]")) {
            ItemStack level_indicator_empty = new ItemStack(Material.GLASS_PANE, 1);
            ItemMeta lvl_indic_em_meta = level_indicator_empty.getItemMeta();

            lvl_indic_em_meta.setDisplayName(ChatColor.BLUE + "Cauldron: 0% Full");

            ArrayList<String> lvl_indic_lore = new ArrayList<String>();
            lvl_indic_lore.add(ChatColor.DARK_AQUA + "Fill to purify water.");
            lvl_indic_em_meta.setLore(lvl_indic_lore);

            level_indicator_empty.setItemMeta(lvl_indic_em_meta);

            pure_inv.setItem(0, level_indicator_empty); //lvl
            pure_inv.setItem(8, level_indicator_empty); //lvl
            pure_inv.setItem(9, level_indicator_empty); //lvl
            pure_inv.setItem(17, level_indicator_empty); //lvl
            pure_inv.setItem(18, level_indicator_empty); //lvl
            pure_inv.setItem(26, level_indicator_empty); //lvl
        }
        else if (cauldron_level.equalsIgnoreCase("minecraft:cauldron[level=1]")) {
            ItemStack level_indicator_empty = new ItemStack(Material.GLASS_PANE, 1);
            ItemStack level_indicator_water = new ItemStack(Material.BLUE_STAINED_GLASS_PANE, 1);
            ItemMeta lvl_indic_em_meta = level_indicator_empty.getItemMeta();
            ItemMeta lvl_indic_wa_meta = level_indicator_water.getItemMeta();

            lvl_indic_em_meta.setDisplayName(ChatColor.BLUE + "Cauldron: 33% Full");
            lvl_indic_wa_meta.setDisplayName(ChatColor.BLUE + "Cauldron: 33% Full");

            ArrayList<String> lvl_indic_lore = new ArrayList<String>();
            lvl_indic_lore.add(ChatColor.DARK_AQUA + "Fill to purify water.");
            lvl_indic_em_meta.setLore(lvl_indic_lore);
            lvl_indic_wa_meta.setLore(lvl_indic_lore);

            level_indicator_empty.setItemMeta(lvl_indic_em_meta);
            level_indicator_water.setItemMeta(lvl_indic_wa_meta);

            pure_inv.setItem(0, level_indicator_empty); //lvl
            pure_inv.setItem(8, level_indicator_empty); //lvl
            pure_inv.setItem(9, level_indicator_empty); //lvl
            pure_inv.setItem(17, level_indicator_empty); //lvl
            pure_inv.setItem(18, level_indicator_water); //lvl
            pure_inv.setItem(26, level_indicator_water); //lvl
        }
        else if (cauldron_level.equalsIgnoreCase("minecraft:cauldron[level=2]")) {
            ItemStack level_indicator_empty = new ItemStack(Material.GLASS_PANE, 1);
            ItemStack level_indicator_water = new ItemStack(Material.BLUE_STAINED_GLASS_PANE, 1);
            ItemMeta lvl_indic_em_meta = level_indicator_empty.getItemMeta();
            ItemMeta lvl_indic_wa_meta = level_indicator_water.getItemMeta();

            lvl_indic_em_meta.setDisplayName(ChatColor.BLUE + "Cauldron: 67% Full");
            lvl_indic_wa_meta.setDisplayName(ChatColor.BLUE + "Cauldron: 67% Full");

            ArrayList<String> lvl_indic_lore = new ArrayList<String>();
            lvl_indic_lore.add(ChatColor.DARK_AQUA + "Fill to purify water.");
            lvl_indic_em_meta.setLore(lvl_indic_lore);
            lvl_indic_wa_meta.setLore(lvl_indic_lore);

            level_indicator_empty.setItemMeta(lvl_indic_em_meta);
            level_indicator_water.setItemMeta(lvl_indic_wa_meta);

            pure_inv.setItem(0, level_indicator_empty); //lvl
            pure_inv.setItem(8, level_indicator_empty); //lvl
            pure_inv.setItem(9, level_indicator_water); //lvl
            pure_inv.setItem(17, level_indicator_water); //lvl
            pure_inv.setItem(18, level_indicator_water); //lvl
            pure_inv.setItem(26, level_indicator_water); //lvl
        }
        else if (cauldron_level.equalsIgnoreCase("minecraft:cauldron[level=3]")) {
            ItemStack level_indicator_empty = new ItemStack(Material.GLASS_PANE, 1);
            ItemStack level_indicator_water = new ItemStack(Material.BLUE_STAINED_GLASS_PANE, 1);
            ItemMeta lvl_indic_em_meta = level_indicator_empty.getItemMeta();
            ItemMeta lvl_indic_wa_meta = level_indicator_water.getItemMeta();

            lvl_indic_em_meta.setDisplayName(ChatColor.BLUE + "Cauldron: 100% Full");
            lvl_indic_wa_meta.setDisplayName(ChatColor.BLUE + "Cauldron: 100% Full");

            ArrayList<String> lvl_indic_lore = new ArrayList<String>();
            lvl_indic_lore.add(ChatColor.AQUA + "Ready to purify water.");
            lvl_indic_em_meta.setLore(lvl_indic_lore);
            lvl_indic_wa_meta.setLore(lvl_indic_lore);

            level_indicator_empty.setItemMeta(lvl_indic_em_meta);
            level_indicator_water.setItemMeta(lvl_indic_wa_meta);

            pure_inv.setItem(0, level_indicator_water); //lvl
            pure_inv.setItem(8, level_indicator_water); //lvl
            pure_inv.setItem(9, level_indicator_water); //lvl
            pure_inv.setItem(17, level_indicator_water); //lvl
            pure_inv.setItem(18, level_indicator_water); //lvl
            pure_inv.setItem(26, level_indicator_water); //lvl
        }

        player.openInventory(pure_inv);
    }

    @EventHandler
    public void cauldron_open (PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            EquipmentSlot eqSlot = event.getHand();
            if (eqSlot.equals(EquipmentSlot.HAND)) {
                if (block.getType().equals(Material.CAULDRON)) {
                    Location cauldron_loc = block.getLocation();
                    Location block_under_loc = new Location(cauldron_loc.getWorld(), cauldron_loc.getX(), cauldron_loc.getY() - 1, cauldron_loc.getZ());
                    Block block_under = block_under_loc.getBlock();
                    if (block_under.getType().equals(Material.FIRE)) {
                        Material holding = player.getInventory().getItemInMainHand().getType();
                        if (holding.equals(Material.WATER_BUCKET) ||
                                holding.equals(Material.GLASS_BOTTLE)||
                                holding.equals(Material.POTION)) {
                            return;
                        }
                        else {
                            purification_menu(player, block);
                        }
                    }
                    else {
                        player.sendMessage(ChatColor.RED + "To purify water, light a fire underneath the cauldron.");
                    }
                }
            }
        }
    }

    @EventHandler
    public void inside_menu (InventoryClickEvent event) {
        Inventory clicked_inv = event.getClickedInventory();
        Inventory target_inv = event.getView().getTopInventory();
        boolean isCauldron = event.getView().getTitle().equalsIgnoreCase(cauldron_title);

        if (isCauldron && clicked_inv.equals(target_inv)) {
            Player player = (Player) event.getWhoClicked();
            ClickType click = event.getClick();
            ItemStack item = event.getCurrentItem();

            if (item == null ||
                    item.getType().equals(Material.BLUE_STAINED_GLASS_PANE) ||
                    item.getType().equals(Material.BLACK_STAINED_GLASS_PANE) ||
                    item.getType().equals(Material.LIGHT_GRAY_STAINED_GLASS_PANE) ||
                    item.getType().equals(Material.GRAY_STAINED_GLASS_PANE) ||
                    item.getType().equals(Material.GLASS_PANE)) {
                event.setCancelled(true);
            }
            else {
                System.out.println("asdf");
            }
        }
    }
}
