package me.fullpotato.badlandscaves.Thirst;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Levelled;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class CauldronRunnable extends BukkitRunnable {

    private final BadlandsCaves plugin;
    private final Inventory inventory;
    private final Location location;
    private final Location location_under;
    private final Player player;

    public CauldronRunnable(BadlandsCaves bcav, Inventory inv, Location loc, Location bl_under, Player ply) {
        plugin = bcav;
        inventory = inv;
        location = loc;
        player = ply;
        location_under = bl_under;
    }

    @Override
    public void run() {
        int cauldron_level = ((Levelled) (location.getBlock().getBlockData())).getLevel();

        if (!location.getBlock().getType().equals(Material.CAULDRON)) {
            player.closeInventory();
        }
        else if (!location_under.getBlock().getType().equals(Material.FIRE)) {
            player.closeInventory();
            player.sendMessage(ChatColor.RED + "Light a fire underneath the Cauldron to use it.");
        }
        else {
            waterLevelIndicator(cauldron_level);
            //--------------------------------------------------------------
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
            //--------------------------------------------------------------
            processReadyIndicator(cauldron_level, in_slots);
            regenWater(cauldron_level);
        }

    }

    public void waterLevelIndicator (int cauldron_level) {
        if (cauldron_level == 0) {
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
        else if (cauldron_level == 1) {
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
        else if (cauldron_level == 2) {
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
        else if (cauldron_level == 3) {
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
    }

    public void processReadyIndicator (int cauldron_level, ArrayList<Material> in_slots) {
        boolean emptyOutput = (inventory.getItem(22) == null || inventory.getItem(22).getType().equals(Material.AIR) || inventory.getItem(22).getType().equals(Material.GRAY_STAINED_GLASS_PANE));

        //READY INDICATOR----------------------------
        if ((cauldron_level == 3) && (inventory.getItem(11) != null && inventory.getItem(15) != null)) {
            boolean is_ready = false;
            ItemStack green = new ItemStack(Material.LIME_STAINED_GLASS_PANE, 1);
            ItemMeta green_meta = green.getItemMeta();
            assert green_meta != null;
            ArrayList<String> green_lore = new ArrayList<String>();

            if (in_slots.contains(Material.GLASS_BOTTLE)) {
                final boolean isHardmode = plugin.getSystemConfig().getBoolean("hardmode");

                if (!isHardmode && in_slots.contains(Material.BLAZE_POWDER) && emptyOutput) {
                    green_meta.setDisplayName(ChatColor.GREEN + "Purification Process Ready");
                    green_lore.add(ChatColor.DARK_GREEN + "Click to purify water.");
                    is_ready = true;
                }
                else if (in_slots.contains(Material.COMMAND_BLOCK)) {
                    int slot = in_slots.indexOf(Material.COMMAND_BLOCK) == 0 ? 11 : 15;

                    final ItemStack purge_ess = CustomItem.PURGE_ESSENCE.getItem();
                    final ItemStack hell_ess = CustomItem.HELL_ESSENCE.getItem();
                    final ItemStack magic_ess = CustomItem.MAGIC_ESSENCE.getItem();

                    if (inventory.getItem(slot) != null && inventory.getItem(slot).isSimilar(purge_ess) && emptyOutput) {
                        green_meta.setDisplayName(ChatColor.GREEN + "Antidote Ready");
                        green_lore.add(ChatColor.DARK_GREEN + "Click to create antidote.");
                        is_ready = true;
                    }

                    else if (inventory.getItem(slot) != null && inventory.getItem(slot).isSimilar(hell_ess) && emptyOutput) {
                        green_meta.setDisplayName(ChatColor.GREEN + "Purification Process Ready");
                        green_lore.add(ChatColor.DARK_GREEN + "Click to purify water.");
                        is_ready = true;
                    }
                    else if (inventory.getItem(slot) != null && inventory.getItem(slot).isSimilar(magic_ess) && emptyOutput) {
                        green_meta.setDisplayName(ChatColor.GREEN + "Mana Potion Ready");
                        green_lore.add(ChatColor.DARK_GREEN + "Click to create a Mana Potion.");
                        is_ready = true;
                    }

                }
            }
            else if (in_slots.contains(Material.SUGAR) && (in_slots.contains(Material.BONE_MEAL))) {
                ItemStack tainted_powder = CustomItem.TAINTED_POWDER.getItem();
                boolean outputMatches = (inventory.getItem(22) != null && inventory.getItem(22).isSimilar(tainted_powder) && inventory.getItem(22).getAmount() + tainted_powder.getAmount() <= 64);
                if (emptyOutput || outputMatches) {
                    green_meta.setDisplayName(ChatColor.GREEN + "Tainting Ready");
                    green_lore.add(ChatColor.DARK_GREEN + "Click to taint the powder.");
                    is_ready = true;
                }
            }

            if (is_ready) {
                green_meta.setLore(green_lore);
                green.setItemMeta(green_meta);
                inventory.setItem(13, green);
                return;
            }
        }

        //ERROR INDICATOR-------------------------
        ItemStack red = new ItemStack(Material.RED_STAINED_GLASS_PANE, 1);
        ItemMeta red_meta = red.getItemMeta();
        assert red_meta != null;
        red_meta.setDisplayName(ChatColor.RED + "Not Ready");
        ArrayList<String> red_lore = new ArrayList<>();
        if (cauldron_level < 3) {
            red_lore.add(ChatColor.DARK_RED + "Requires full tank of water.");
        }
        else {
            ItemStack tainted_powder = CustomItem.TAINTED_POWDER.getItem();
            if (inventory.getItem(11) == null && inventory.getItem(15) == null) {
                red_lore.add(ChatColor.DARK_RED + "Input items in slots to use cauldron.");
            }

            else if (!in_slots.contains(Material.GLASS_BOTTLE) && !in_slots.contains(Material.SUGAR)) {
                red_lore.add(ChatColor.DARK_RED + "Requires correct primary ingredient.");
            }

            else if (!in_slots.contains(Material.BLAZE_POWDER) && !in_slots.contains(Material.COMMAND_BLOCK)) {
                red_lore.add(ChatColor.DARK_RED + "Requires correct secondary ingredient.");
            }
            else if (!emptyOutput || (inventory.getItem(22) != null && inventory.getItem(22).isSimilar(tainted_powder) && inventory.getItem(22).getAmount() + tainted_powder.getAmount() <= 64)) {
                red_lore.add(ChatColor.DARK_RED + "Output slot is full.");
            }
            else {
                red_lore.add(ChatColor.DARK_RED + "An unexpected error has occurred.");
            }
        }

        red_meta.setLore(red_lore);
        red.setItemMeta(red_meta);

        inventory.setItem(13, red);
    }

    public void regenWater (int cauldron_level) {
        final BlockFace[] faces = {
                BlockFace.UP,
                BlockFace.NORTH,
                BlockFace.EAST,
                BlockFace.SOUTH,
                BlockFace.WEST,
        };

        if (cauldron_level < 3) {
            final Block cauldron = location.getBlock();
            for (BlockFace face : faces) {
                final Block adjacent = cauldron.getRelative(face);
                if (adjacent.getType().equals(Material.WATER) && ((Levelled) adjacent.getBlockData()).getLevel() == 0) {
                    Levelled level = ((Levelled) cauldron.getBlockData());
                    level.setLevel(cauldron_level + 1);
                    cauldron.setBlockData(level);
                    adjacent.setType(Material.AIR);
                }
            }
        }
    }
}
