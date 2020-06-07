package me.fullpotato.badlandscaves.CustomItems.Crafting;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ExpBottleEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class EXPBottle implements Listener {

    @EventHandler
    public void enchantBottle (PlayerInteractEvent event) {
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (event.getItem() != null) {
                final ItemStack item = event.getItem();
                if (item.getType().equals(Material.GLASS_BOTTLE) || item.getType().equals(Material.EXPERIENCE_BOTTLE)) {
                    if (event.getClickedBlock() != null && event.getClickedBlock().getType().equals(Material.ENCHANTING_TABLE)) {
                        final Player player = event.getPlayer();
                        if (player.getLevel() >= 1) {
                            event.setCancelled(true);
                            player.setLevel(player.getLevel() - 1);

                            player.playSound(event.getClickedBlock().getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.BLOCKS, 1, 1);
                            int amount = player.getExpToLevel();

                            if (item.getType().equals(Material.GLASS_BOTTLE)) {
                                item.setAmount(item.getAmount() - 1);
                                ItemStack exp_bottle = new ItemStack(Material.EXPERIENCE_BOTTLE);
                                ItemMeta meta = exp_bottle.getItemMeta();
                                ArrayList<String> lore = new ArrayList<>();
                                lore.add("§a" + amount + " §7Experience Points");
                                meta.setLore(lore);
                                exp_bottle.setItemMeta(meta);

                                if (player.getInventory().firstEmpty() == -1) {
                                    player.getWorld().dropItemNaturally(player.getLocation(), exp_bottle);
                                }
                                else {
                                    player.getInventory().addItem(exp_bottle);
                                }
                            }
                            else {
                                if (item.getAmount() == 1) {
                                    ItemMeta meta = item.getItemMeta();
                                    if (meta != null) {
                                        List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
                                        if (lore != null) {
                                            try {
                                                int points = lore.size() >= 1 ? Integer.parseInt(lore.get(0).split(" ")[0].substring(2)) : 0;
                                                if (lore.size() >= 1) {
                                                    lore.set(0, "§a" + (Math.min(points + amount, 999999999)) + " §7Experience Points");
                                                }
                                                else {
                                                    lore.add("§a" + (amount) + " §7Experience Points");
                                                }

                                                meta.setLore(lore);
                                                item.setItemMeta(meta);
                                            }
                                            catch (NumberFormatException ignore) {
                                            }
                                        }
                                    }
                                }
                                else {
                                    item.setAmount(item.getAmount() - 1);
                                    ItemStack exp_bottle = new ItemStack(Material.EXPERIENCE_BOTTLE);
                                    ItemMeta meta = exp_bottle.getItemMeta();
                                    List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
                                    if (lore != null) {
                                        try {
                                            int points = lore.size() >= 1 ? Integer.parseInt(lore.get(0).split(" ")[0].substring(2)) : 0;
                                            if (lore.size() >= 1) {
                                                lore.set(0, "§a" + (Math.min(points + amount, 999999999)) + " §7Experience Points");
                                            }
                                            else {
                                                lore.add("§a" + (amount) + " §7Experience Points");
                                            }

                                            meta.setLore(lore);
                                            exp_bottle.setItemMeta(meta);
                                        }
                                        catch (NumberFormatException ignore) {
                                        }
                                    }
                                    if (player.getInventory().firstEmpty() == -1) {
                                        player.getWorld().dropItemNaturally(player.getLocation(), exp_bottle);
                                    }
                                    else {
                                        player.getInventory().addItem(exp_bottle);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void useEXPBottle (ExpBottleEvent event) {
        final ItemStack item = event.getEntity().getItem();
        if (item.hasItemMeta() && item.getItemMeta() != null && item.getItemMeta().hasLore()) {
            List<String> lore = item.getItemMeta().getLore();
            if (lore != null && lore.size() >= 1) {
                try {
                    int points = Integer.parseInt(lore.get(0).split(" ")[0].substring(2));
                    event.setExperience(points);
                }
                catch (NumberFormatException ignore) {
                }
            }
        }
    }
}
