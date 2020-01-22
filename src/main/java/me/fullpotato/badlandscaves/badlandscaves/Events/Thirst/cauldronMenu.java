package me.fullpotato.badlandscaves.badlandscaves.Events.Thirst;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.badlandscaves.Runnables.cauldronRunnable;
import org.bukkit.Bukkit;
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
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;

public class cauldronMenu implements Listener {
    private BadlandsCaves plugin;
    public cauldronMenu(BadlandsCaves bcav) {
        plugin = bcav;
    }

    private String cauldron_title = ChatColor.DARK_GRAY + "Cauldron";
    private int cdr_lvl = 0;
    private boolean ready;
    private Inventory cauldron_inv;
    private int refresh_id;
    private Block cauldron_block;
    private Location loc_of_cauld;


    public void purification_menu(Player player, Block block, String cauldron_title) {
        Inventory pure_inv = plugin.getServer().createInventory(null, 27, cauldron_title);
        cauldron_inv = pure_inv;

        ItemStack black = new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1);
        ItemMeta black_meta = black.getItemMeta();
        assert black_meta != null;
        black_meta.setDisplayName(ChatColor.RESET + "");
        black.setItemMeta(black_meta);

        ItemStack blue = new ItemStack(Material.BLUE_STAINED_GLASS_PANE, 1);

        ItemStack d_gray = new ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1);
        ItemMeta d_gray_meta = d_gray.getItemMeta();
        assert d_gray_meta != null;
        d_gray_meta.setDisplayName(ChatColor.RESET + "");
        d_gray.setItemMeta(d_gray_meta);

        ItemStack l_gray = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE, 1);
        ItemMeta l_gray_meta = l_gray.getItemMeta();
        assert l_gray_meta != null;
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
        pure_inv.setItem(12, d_gray);
        pure_inv.setItem(14, d_gray);
        pure_inv.setItem(16, black);
        pure_inv.setItem(19, black);
        pure_inv.setItem(20, d_gray);
        pure_inv.setItem(21, d_gray);
        pure_inv.setItem(22, d_gray);
        pure_inv.setItem(23, d_gray);
        pure_inv.setItem(24, d_gray);
        pure_inv.setItem(25, black);
        player.openInventory(pure_inv);
    }

    @EventHandler
    public void cauldron_open (PlayerInteractEvent event) {
        Player player = event.getPlayer();
        cauldron_block = event.getClickedBlock();

        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            EquipmentSlot eqSlot = event.getHand();
            assert eqSlot != null;
            if (eqSlot.equals(EquipmentSlot.HAND)) {
                assert cauldron_block != null;
                if (cauldron_block.getType().equals(Material.CAULDRON)) {
                    loc_of_cauld = cauldron_block.getLocation();
                    Location block_under_loc = new Location(loc_of_cauld.getWorld(), loc_of_cauld.getX(), loc_of_cauld.getY() - 1, loc_of_cauld.getZ());
                    Block block_under = block_under_loc.getBlock();
                    Material holding = player.getInventory().getItemInMainHand().getType();
                    boolean otherAction = (player.isSneaking() || holding.equals(Material.WATER_BUCKET) || holding.equals(Material.POTION) || holding.equals(Material.BUCKET) || holding.equals(Material.GLASS_BOTTLE));
                    if (block_under.getType().equals(Material.FIRE)) {
                        if (!otherAction) {
                            event.setCancelled(true);
                            purification_menu(player, cauldron_block, cauldron_title);
                            BukkitTask inv_refresh = new cauldronRunnable(plugin, cauldron_inv, loc_of_cauld, block_under_loc, player).runTaskTimer(plugin, 0, 10);
                            refresh_id = inv_refresh.getTaskId();
                        }
                    }
                    else if (!otherAction) {
                        player.sendMessage(ChatColor.RED + "Light a fire underneath the Cauldron to use it.");
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
        boolean isHardmode = plugin.getConfig().getBoolean("game_values.hardmode");

        if (clicked_inv != null) {
            if (isCauldron && clicked_inv.equals(target_inv)) {
                cauldron_inv = target_inv;
                Player player = (Player) event.getWhoClicked();
                ClickType click = event.getClick();
                ItemStack item = event.getCurrentItem();

                if (cauldron_block.getBlockData().getAsString().contains("0")) {
                    cdr_lvl = 0;
                }
                else if (cauldron_block.getBlockData().getAsString().contains("1")) {
                    cdr_lvl = 1;
                }
                else if (cauldron_block.getBlockData().getAsString().contains("2")) {
                    cdr_lvl = 2;
                }
                else if (cauldron_block.getBlockData().getAsString().contains("3")) {
                    cdr_lvl = 3;
                }


                boolean hasIng = false;
                boolean isFull = cdr_lvl == 3;

                ArrayList<Material> in_slots = new ArrayList<>();
                ArrayList<ItemStack> itemstacks_slots = new ArrayList<>();
                if (cauldron_inv.getItem(11) != null) {
                    in_slots.add(cauldron_inv.getItem(11).getType());
                    itemstacks_slots.add(cauldron_inv.getItem(11));
                }
                if (cauldron_inv.getItem(15) != null) {
                    in_slots.add(cauldron_inv.getItem(15).getType());
                    itemstacks_slots.add(cauldron_inv.getItem(15));
                }

                if (target_inv.getItem(11) == null || target_inv.getItem(15) == null) {
                    hasIng = false;
                }
                else if (in_slots.contains(Material.GLASS_BOTTLE) && ((in_slots.contains(Material.BLAZE_POWDER) ))) {
                    hasIng = true;
                }
                else if (in_slots.contains(Material.GLASS_BOTTLE) && in_slots.contains(Material.COMMAND_BLOCK)) {
                    hasIng = true;
                }
                else if (in_slots.contains(Material.SUGAR) && in_slots.contains(Material.BONE_MEAL)) {
                    hasIng = true;
                }

                ready = (hasIng && isFull);

                if (item != null) {
                    if (item.getType().equals(Material.GREEN_STAINED_GLASS_PANE) ||
                            item.getType().equals(Material.BLACK_STAINED_GLASS_PANE) ||
                            item.getType().equals(Material.LIGHT_GRAY_STAINED_GLASS_PANE) ||
                            item.getType().equals(Material.GRAY_STAINED_GLASS_PANE) ||
                            item.getType().equals(Material.GLASS_PANE) ||
                            item.getType().equals(Material.RED_STAINED_GLASS_PANE)) {
                        event.setCancelled(true);
                    }
                    else if (item.getType().equals(Material.LIME_STAINED_GLASS_PANE)) {
                        event.setCancelled(true);
                        if (ready) {
                            int item_1_amt = cauldron_inv.getItem(11).getAmount();
                            int item_2_amt = cauldron_inv.getItem(15).getAmount();

                            cauldron_inv.getItem(11).setAmount(item_1_amt - 1);
                            cauldron_inv.getItem(15).setAmount(item_2_amt - 1);
                            cauldron_block.setType(Material.CAULDRON);

                            ItemStack purified_water = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.purified_water").getValues(true));
                            //PURIFIED WATER
                            if (!isHardmode && in_slots.contains(Material.BLAZE_POWDER)) {
                                cauldron_inv.setItem(22, purified_water);
                            }
                            //ANTIDOTE
                            else if (in_slots.contains(Material.COMMAND_BLOCK)) {
                                int slot = in_slots.indexOf(Material.COMMAND_BLOCK);

                                ItemStack purge_ess = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.purge_essence").getValues(true));
                                ItemStack hell_ess = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.hell_essence").getValues(true));

                                if (itemstacks_slots.get(slot).hasItemMeta()) {
                                    if (itemstacks_slots.get(slot).getItemMeta().hasDisplayName()) {
                                        if (itemstacks_slots.get(slot).getItemMeta().getDisplayName().equalsIgnoreCase(purge_ess.getItemMeta().getDisplayName())) {
                                            ItemStack antidote = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.antidote").getValues(true));
                                            cauldron_inv.setItem(22, antidote);
                                        }
                                        else if (isHardmode && itemstacks_slots.get(slot).getItemMeta().getDisplayName().equalsIgnoreCase(hell_ess.getItemMeta().getDisplayName())) {
                                            cauldron_inv.setItem(22, purified_water);
                                        }
                                    }
                                }

                            }
                            //TAINTED POWDER
                            else if (in_slots.contains(Material.SUGAR) && in_slots.contains(Material.BONE_MEAL)) {
                                ItemStack tainted_powder = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.tainted_powder").getValues(true));
                                cauldron_inv.setItem(22, tainted_powder);
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void closeCDR (InventoryCloseEvent event) {
        Inventory inv = event.getInventory();
        Player player = (Player) event.getPlayer();
        if (inv.equals(cauldron_inv)) {
            Bukkit.getScheduler().cancelTask(refresh_id);

            if (cauldron_inv.getItem(11) != null) {
                player.getInventory().addItem(cauldron_inv.getItem(11));
            }

            if (cauldron_inv.getItem(15) != null) {
                player.getInventory().addItem(cauldron_inv.getItem(15));
            }

            if (cauldron_inv.getItem(22) != null && !cauldron_inv.getItem(22).getType().equals(Material.GRAY_STAINED_GLASS_PANE)) {
                player.getInventory().addItem(cauldron_inv.getItem(22));
            }
        }
    }
}
