package me.fullpotato.badlandscaves.badlandscaves.Runnables;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class purification_runnable extends BukkitRunnable {

    private BadlandsCaves plugin;
    private Inventory inventory;
    private Integer cauldron_level;
    private Location location;
    private Location location_under;
    private Player player;

    public purification_runnable (BadlandsCaves bcav, Inventory inv, Location loc, Location bl_under, Player ply) {
        plugin = bcav;
        inventory = inv;
        location = loc;
        player = ply;
        location_under = bl_under;
    }

    @Override
    public void run() {
        String block_desc = location.getBlock().getBlockData().getAsString();
        if (!location.getBlock().getType().equals(Material.CAULDRON)) {
            player.closeInventory();
            return;
        }
        else if (!location_under.getBlock().getType().equals(Material.FIRE)) {
            player.closeInventory();
            player.sendMessage(ChatColor.RED + "To purify water, light a fire underneath the cauldron.");
            return;
        }
        else {
            //WATER LEVEL INDICATOR
            if (block_desc.equalsIgnoreCase("minecraft:cauldron[level=0]")) {
                cauldron_level = 0;
                ItemStack level_indicator_empty = new ItemStack(Material.GLASS_PANE, 1);
                ItemMeta lvl_indic_em_meta = level_indicator_empty.getItemMeta();

                assert lvl_indic_em_meta != null;
                lvl_indic_em_meta.setDisplayName(ChatColor.GRAY + "Cauldron: " + ChatColor.DARK_GRAY + "0% Full");

                ArrayList<String> lvl_indic_lore = new ArrayList<String>();
                lvl_indic_lore.add(ChatColor.DARK_GRAY + "Fill to purify water.");
                lvl_indic_em_meta.setLore(lvl_indic_lore);

                level_indicator_empty.setItemMeta(lvl_indic_em_meta);

                inventory.setItem(0, level_indicator_empty); //lvl
                inventory.setItem(8, level_indicator_empty); //lvl
                inventory.setItem(9, level_indicator_empty); //lvl
                inventory.setItem(17, level_indicator_empty); //lvl
                inventory.setItem(18, level_indicator_empty); //lvl
                inventory.setItem(26, level_indicator_empty); //lvl
            }
            else if (block_desc.equalsIgnoreCase("minecraft:cauldron[level=1]")) {
                cauldron_level = 1;
                ItemStack level_indicator_empty = new ItemStack(Material.GLASS_PANE, 1);
                ItemStack level_indicator_water = new ItemStack(Material.BLUE_STAINED_GLASS_PANE, 1);
                ItemMeta lvl_indic_em_meta = level_indicator_empty.getItemMeta();
                ItemMeta lvl_indic_wa_meta = level_indicator_water.getItemMeta();

                assert lvl_indic_em_meta != null;
                lvl_indic_em_meta.setDisplayName(ChatColor.BLUE + "Cauldron: " + ChatColor.DARK_AQUA + "33% Full");
                assert lvl_indic_wa_meta != null;
                lvl_indic_wa_meta.setDisplayName(ChatColor.BLUE + "Cauldron: " + ChatColor.DARK_AQUA + "33% Full");

                ArrayList<String> lvl_indic_lore = new ArrayList<String>();
                lvl_indic_lore.add(ChatColor.DARK_AQUA + "Fill to purify water.");
                lvl_indic_em_meta.setLore(lvl_indic_lore);
                lvl_indic_wa_meta.setLore(lvl_indic_lore);

                level_indicator_empty.setItemMeta(lvl_indic_em_meta);
                level_indicator_water.setItemMeta(lvl_indic_wa_meta);

                inventory.setItem(0, level_indicator_empty); //lvl
                inventory.setItem(8, level_indicator_empty); //lvl
                inventory.setItem(9, level_indicator_empty); //lvl
                inventory.setItem(17, level_indicator_empty); //lvl
                inventory.setItem(18, level_indicator_water); //lvl
                inventory.setItem(26, level_indicator_water); //lvl
            }
            else if (block_desc.equalsIgnoreCase("minecraft:cauldron[level=2]")) {
                cauldron_level = 2;
                ItemStack level_indicator_empty = new ItemStack(Material.GLASS_PANE, 1);
                ItemStack level_indicator_water = new ItemStack(Material.BLUE_STAINED_GLASS_PANE, 1);
                ItemMeta lvl_indic_em_meta = level_indicator_empty.getItemMeta();
                ItemMeta lvl_indic_wa_meta = level_indicator_water.getItemMeta();

                assert lvl_indic_em_meta != null;
                lvl_indic_em_meta.setDisplayName(ChatColor.BLUE + "Cauldron: " + ChatColor.DARK_AQUA + "67% Full");
                assert lvl_indic_wa_meta != null;
                lvl_indic_wa_meta.setDisplayName(ChatColor.BLUE + "Cauldron: " + ChatColor.DARK_AQUA + "67% Full");

                ArrayList<String> lvl_indic_lore = new ArrayList<String>();
                lvl_indic_lore.add(ChatColor.DARK_AQUA + "Fill to purify water.");
                lvl_indic_em_meta.setLore(lvl_indic_lore);
                lvl_indic_wa_meta.setLore(lvl_indic_lore);

                level_indicator_empty.setItemMeta(lvl_indic_em_meta);
                level_indicator_water.setItemMeta(lvl_indic_wa_meta);

                inventory.setItem(0, level_indicator_empty); //lvl
                inventory.setItem(8, level_indicator_empty); //lvl
                inventory.setItem(9, level_indicator_water); //lvl
                inventory.setItem(17, level_indicator_water); //lvl
                inventory.setItem(18, level_indicator_water); //lvl
                inventory.setItem(26, level_indicator_water); //lvl
            }
            else if (block_desc.equalsIgnoreCase("minecraft:cauldron[level=3]")) {
                cauldron_level = 3;
                ItemStack level_indicator_empty = new ItemStack(Material.GLASS_PANE, 1);
                ItemStack level_indicator_water = new ItemStack(Material.BLUE_STAINED_GLASS_PANE, 1);
                ItemMeta lvl_indic_em_meta = level_indicator_empty.getItemMeta();
                ItemMeta lvl_indic_wa_meta = level_indicator_water.getItemMeta();

                assert lvl_indic_em_meta != null;
                lvl_indic_em_meta.setDisplayName(ChatColor.BLUE + "Cauldron: " + ChatColor.AQUA + "100% Full");
                assert lvl_indic_wa_meta != null;
                lvl_indic_wa_meta.setDisplayName(ChatColor.BLUE + "Cauldron: " + ChatColor.AQUA + "100% Full");

                ArrayList<String> lvl_indic_lore = new ArrayList<String>();
                lvl_indic_lore.add(ChatColor.AQUA + "There's enough water to purify.");
                lvl_indic_em_meta.setLore(lvl_indic_lore);
                lvl_indic_wa_meta.setLore(lvl_indic_lore);

                level_indicator_empty.setItemMeta(lvl_indic_em_meta);
                level_indicator_water.setItemMeta(lvl_indic_wa_meta);

                inventory.setItem(0, level_indicator_water); //lvl
                inventory.setItem(8, level_indicator_water); //lvl
                inventory.setItem(9, level_indicator_water); //lvl
                inventory.setItem(17, level_indicator_water); //lvl
                inventory.setItem(18, level_indicator_water); //lvl
                inventory.setItem(26, level_indicator_water); //lvl
            }

            //PROCESS READY INDICATOR
            if ((cauldron_level == 3) && (inventory.getItem(11) != null && inventory.getItem(15) != null) && (inventory.getItem(11).getType().equals(Material.GLASS_BOTTLE) && inventory.getItem(15).getType().equals(Material.BLAZE_POWDER) || (inventory.getItem(11).getType().equals(Material.BLAZE_POWDER) && inventory.getItem(15).getType().equals(Material.GLASS_BOTTLE)))) {
                ItemStack green = new ItemStack(Material.LIME_STAINED_GLASS_PANE, 1);
                ItemMeta green_meta = green.getItemMeta();
                assert green_meta != null;
                green_meta.setDisplayName(ChatColor.GREEN + "Purification Process Ready");
                ArrayList<String> green_lore = new ArrayList<String>();
                green_lore.add(ChatColor.DARK_GREEN + "Click to purify water.");
                green_meta.setLore(green_lore);
                green.setItemMeta(green_meta);

                inventory.setItem(13, green);
            }
            else {
                ItemStack red = new ItemStack(Material.RED_STAINED_GLASS_PANE, 1);
                ItemMeta red_meta = red.getItemMeta();
                assert red_meta != null;
                red_meta.setDisplayName(ChatColor.RED + "Purification Process Not Ready");
                ArrayList<String> red_lore = new ArrayList<String>();
                if (cauldron_level < 3) {
                    red_lore.add(ChatColor.DARK_RED + "Requires full tank of water.");
                }
                else {
                    if (inventory.getItem(11) == null && inventory.getItem(15) == null) {
                        red_lore.add(ChatColor.DARK_RED + "Input items in slots to purify water.");
                    }

                    else if (inventory.getItem(11) == null) {
                        if (inventory.getItem(15).getType().equals(Material.GLASS_BOTTLE)) {
                            red_lore.add(ChatColor.DARK_RED + "Requires blaze powder.");
                        }
                        else if (inventory.getItem(15).getType().equals(Material.BLAZE_POWDER)) {
                            red_lore.add(ChatColor.DARK_RED + "Requires empty bottle.");
                        }
                        else {
                            red_lore.add(ChatColor.DARK_RED + "Requires blaze powder and empty bottle.");
                        }
                    }

                    else if (inventory.getItem(15) == null) {
                        if (inventory.getItem(11).getType().equals(Material.GLASS_BOTTLE)) {
                            red_lore.add(ChatColor.DARK_RED + "Requires blaze powder.");
                        }
                        else if (inventory.getItem(11).getType().equals(Material.BLAZE_POWDER)) {
                            red_lore.add(ChatColor.DARK_RED + "Requires empty bottle.");
                        }
                        else {
                            red_lore.add(ChatColor.DARK_RED + "Requires blaze powder and empty bottle.");
                        }
                    }
                    else {
                        if (inventory.getItem(11).getType().equals(Material.GLASS_BOTTLE) || inventory.getItem(15).getType().equals(Material.GLASS_BOTTLE)) {
                            red_lore.add(ChatColor.DARK_RED + "Requires blaze powder.");
                        }
                        else if (inventory.getItem(11).getType().equals(Material.BLAZE_POWDER) || inventory.getItem(15).getType().equals(Material.BLAZE_POWDER)) {
                            red_lore.add(ChatColor.DARK_RED + "Requires empty bottle.");
                        }
                        else {
                            red_lore.add(ChatColor.DARK_RED + "Requires blaze powder and empty bottle.");
                        }
                    }
                }

                red_meta.setLore(red_lore);
                red.setItemMeta(red_meta);

                inventory.setItem(13, red);
            }
        }

    }
}
