package me.fullpotato.badlandscaves.badlandscaves.Runnables;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class CauldronRunnable extends BukkitRunnable {

    private BadlandsCaves plugin;
    private Inventory inventory;
    private Integer cauldron_level;
    private Location location;
    private Location location_under;
    private Player player;

    public CauldronRunnable(BadlandsCaves bcav, Inventory inv, Location loc, Location bl_under, Player ply) {
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
            player.sendMessage(ChatColor.RED + "Light a fire underneath the Cauldron to use it.");
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
                lvl_indic_lore.add(ChatColor.DARK_GRAY + "Fill to use Cauldron.");
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
                ItemStack level_indicator_water = new ItemStack(Material.GREEN_STAINED_GLASS_PANE, 1);
                ItemMeta lvl_indic_em_meta = level_indicator_empty.getItemMeta();
                ItemMeta lvl_indic_wa_meta = level_indicator_water.getItemMeta();

                assert lvl_indic_em_meta != null;
                lvl_indic_em_meta.setDisplayName(ChatColor.GRAY + "Cauldron: " + ChatColor.DARK_GREEN + "33% Full");
                assert lvl_indic_wa_meta != null;
                lvl_indic_wa_meta.setDisplayName(ChatColor.GRAY + "Cauldron: " + ChatColor.DARK_GREEN + "33% Full");

                ArrayList<String> lvl_indic_lore = new ArrayList<String>();
                lvl_indic_lore.add(ChatColor.DARK_GREEN + "Fill to use Cauldron.");
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
                ItemStack level_indicator_water = new ItemStack(Material.GREEN_STAINED_GLASS_PANE, 1);
                ItemMeta lvl_indic_em_meta = level_indicator_empty.getItemMeta();
                ItemMeta lvl_indic_wa_meta = level_indicator_water.getItemMeta();

                assert lvl_indic_em_meta != null;
                lvl_indic_em_meta.setDisplayName(ChatColor.GRAY + "Cauldron: " + ChatColor.DARK_GREEN + "67% Full");
                assert lvl_indic_wa_meta != null;
                lvl_indic_wa_meta.setDisplayName(ChatColor.GRAY + "Cauldron: " + ChatColor.DARK_GREEN + "67% Full");

                ArrayList<String> lvl_indic_lore = new ArrayList<String>();
                lvl_indic_lore.add(ChatColor.DARK_GREEN + "Fill to use Cauldron.");
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
                ItemStack level_indicator_water = new ItemStack(Material.GREEN_STAINED_GLASS_PANE, 1);
                ItemMeta lvl_indic_em_meta = level_indicator_empty.getItemMeta();
                ItemMeta lvl_indic_wa_meta = level_indicator_water.getItemMeta();

                assert lvl_indic_em_meta != null;
                lvl_indic_em_meta.setDisplayName(ChatColor.GRAY + "Cauldron: " + ChatColor.GREEN + "100% Full");
                assert lvl_indic_wa_meta != null;
                lvl_indic_wa_meta.setDisplayName(ChatColor.GRAY + "Cauldron: " + ChatColor.GREEN + "100% Full");

                ArrayList<String> lvl_indic_lore = new ArrayList<String>();
                lvl_indic_lore.add(ChatColor.GREEN + "There's enough water.");
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

            ArrayList<Material> in_slots = new ArrayList<>(2);
            if (inventory.getItem(11) != null) {
                in_slots.add(0, inventory.getItem(11).getType());
            }
            else {
                in_slots.add(0, Material.AIR);
            }

            if (inventory.getItem(15) != null) {
                in_slots.add(1, inventory.getItem(15).getType());
            }
            else {
                in_slots.add(1, Material.AIR);
            }


            //PROCESS READY INDICATOR
            if ((cauldron_level == 3) && (inventory.getItem(11) != null && inventory.getItem(15) != null)) {
                boolean is_ready = false;
                ItemStack green = new ItemStack(Material.LIME_STAINED_GLASS_PANE, 1);
                ItemMeta green_meta = green.getItemMeta();
                assert green_meta != null;
                ArrayList<String> green_lore = new ArrayList<String>();

                if (in_slots.contains(Material.GLASS_BOTTLE)) {
                    boolean isHardmode = plugin.getConfig().getBoolean("game_values.hardmode");
                    if (!isHardmode && in_slots.contains(Material.BLAZE_POWDER)) {
                        green_meta.setDisplayName(ChatColor.GREEN + "Purification Process Ready");
                        green_lore.add(ChatColor.DARK_GREEN + "Click to purify water.");
                        is_ready = true;
                    }
                    else if (in_slots.contains(Material.COMMAND_BLOCK)) {
                        int slotPRG = in_slots.indexOf(Material.COMMAND_BLOCK);
                        int slot;

                        if (slotPRG == 0) {
                            slot = 11;
                        }
                        else {
                            slot = 15;
                        }

                        ItemStack purge_ess = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.purge_essence").getValues(true));
                        ItemStack hell_ess = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.hell_essence").getValues(true));
                        ItemStack magic_ess = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.magic_essence").getValues(true));

                        if (inventory.getItem(slot).isSimilar(purge_ess)) {
                            green_meta.setDisplayName(ChatColor.GREEN + "Antidote Ready");
                            green_lore.add(ChatColor.DARK_GREEN + "Click to create antidote.");
                            is_ready = true;
                        }

                        else if (inventory.getItem(slot).isSimilar(hell_ess)) {
                            green_meta.setDisplayName(ChatColor.GREEN + "Purification Process Ready");
                            green_lore.add(ChatColor.DARK_GREEN + "Click to purify water.");
                            is_ready = true;
                        }
                        else if (inventory.getItem(slot).isSimilar(magic_ess)) {
                            green_meta.setDisplayName(ChatColor.GREEN + "Mana Potion Ready");
                            green_lore.add(ChatColor.DARK_GREEN + "Click to create a Mana Potion.");
                            is_ready = true;
                        }

                    }
                }
                else if (in_slots.contains(Material.SUGAR) && (in_slots.contains(Material.BONE_MEAL))) {
                    green_meta.setDisplayName(ChatColor.GREEN + "Tainting Ready");
                    green_lore.add(ChatColor.DARK_GREEN + "Click to taint the powder.");
                    is_ready = true;
                }

                if (is_ready) {
                    green_meta.setLore(green_lore);
                    green.setItemMeta(green_meta);
                    inventory.setItem(13, green);
                }
            }
            else {
                ItemStack red = new ItemStack(Material.RED_STAINED_GLASS_PANE, 1);
                ItemMeta red_meta = red.getItemMeta();
                assert red_meta != null;
                red_meta.setDisplayName(ChatColor.RED + "Not Ready");
                ArrayList<String> red_lore = new ArrayList<String>();
                if (cauldron_level < 3) {
                    red_lore.add(ChatColor.DARK_RED + "Requires full tank of water.");
                }
                else {
                    if (inventory.getItem(11) == null && inventory.getItem(15) == null) {
                        red_lore.add(ChatColor.DARK_RED + "Input items in slots to use cauldron.");
                    }

                    else if (!in_slots.contains(Material.GLASS_BOTTLE) && !in_slots.contains(Material.SUGAR)) {
                        red_lore.add(ChatColor.DARK_RED + "Requires correct primary ingredient.");
                    }

                    else if (!in_slots.contains(Material.BLAZE_POWDER) && !in_slots.contains(Material.COMMAND_BLOCK)) {
                        red_lore.add(ChatColor.DARK_RED + "Requires correct secondary ingredient.");
                    }
                }

                red_meta.setLore(red_lore);
                red.setItemMeta(red_meta);

                inventory.setItem(13, red);
            }
        }

    }
}
