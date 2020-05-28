package me.fullpotato.badlandscaves.badlandscaves.Events.SupernaturalPowers;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.badlandscaves.Runnables.SupernaturalPowers.DisplaceParticleRunnable;
import me.fullpotato.badlandscaves.badlandscaves.Runnables.SupernaturalPowers.ManaBarManager;
import me.fullpotato.badlandscaves.badlandscaves.Runnables.SupernaturalPowers.PossessionIndicatorRunnable;
import me.fullpotato.badlandscaves.badlandscaves.Runnables.SupernaturalPowers.WithdrawIndicatorRunnable;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class SwapPowers implements Listener {
    private BadlandsCaves plugin;
    public SwapPowers(BadlandsCaves bcav) {
        plugin = bcav;
    }

    @EventHandler
    public void doubleShift (PlayerToggleSneakEvent event) {
        final Player player = event.getPlayer();
        final boolean has_powers = player.getMetadata("has_supernatural_powers").get(0).asBoolean();
        if (!has_powers) return;

        ManaBarManager bar = new ManaBarManager(plugin);
        if (player.isSneaking()) {
            player.setMetadata("swap_window", new FixedMetadataValue(plugin, false));
            bar.clearMessage(player);
        }
        else {
            final boolean doubleshift_window = player.hasMetadata("swap_doubleshift_window") && player.getMetadata("swap_doubleshift_window").get(0).asBoolean();
            if (doubleshift_window) {
                player.setMetadata("swap_window", new FixedMetadataValue(plugin, true));

                bar.displayMessage(player, "§3Scroll to Access Abilities", 2, false);
            }
            else {
                player.setMetadata("swap_doubleshift_window", new FixedMetadataValue(plugin, true));
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.setMetadata("swap_doubleshift_window", new FixedMetadataValue(plugin, false));
                    }
                }.runTaskLaterAsynchronously(plugin, 20);
            }
        }
    }


    @EventHandler
    public void swap_to_powers (PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        final boolean has_powers = player.getMetadata("has_supernatural_powers").get(0).asBoolean();
        if (!has_powers) return;

        boolean sneaking = player.isSneaking();
        if (!sneaking) return;

        final boolean in_window = player.hasMetadata("swap_window") && player.getMetadata("swap_window").get(0).asBoolean();
        if (!in_window) return;


        int swap_cd_num = player.getMetadata("swap_cooldown").get(0).asInt();
        if (swap_cd_num > 0) return;

        ItemStack offhand_item = player.getInventory().getItemInOffHand();
        int swap_slot = player.getMetadata("swap_slot").get(0).asInt();
        int incr = (event.getNewSlot() > event.getPreviousSlot() || (event.getNewSlot() == 0 && event.getPreviousSlot() == 8)) && !(event.getNewSlot() == 8 && event.getPreviousSlot() == 0) ? 1 : -1;

        final String[] names = {
                ChatColor.BOLD.toString() + ChatColor.LIGHT_PURPLE + "Displace",
                ChatColor.BOLD.toString() + ChatColor.BLUE + "Enhanced Eyes",
                ChatColor.BOLD.toString() + ChatColor.GRAY + "Withdraw",
                ChatColor.BOLD.toString() + ChatColor.DARK_GREEN + "Possession",
        };

        final int[] power_levels = {
                player.getMetadata("displace_level").get(0).asInt(),
                player.getMetadata("eyes_level").get(0).asInt(),
                player.getMetadata("withdraw_level").get(0).asInt(),
                player.getMetadata("possess_level").get(0).asInt(),
        };

        final ItemStack[] power_items = {
                ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.displace").getValues(true)),
                ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.enhanced_eyes").getValues(true)),
                ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.withdraw").getValues(true)),
                ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.possess").getValues(true)),
        };

        final BukkitTask[] power_runnables = {
                new DisplaceParticleRunnable(plugin, player).runTaskTimerAsynchronously(plugin, 0, 0),
                null,
                new WithdrawIndicatorRunnable(plugin, player).runTaskTimerAsynchronously(plugin, 0, 0),
                new PossessionIndicatorRunnable(plugin, player).runTaskTimer(plugin, 0, 0),
        };

        int new_swap_slot = swap_slot + incr;
        if (new_swap_slot >= power_items.length) {
            new_swap_slot = -1;
        }
        else if (new_swap_slot < -1) {
            new_swap_slot = power_items.length - 1;
        }

        event.setCancelled(true);

        ManaBarManager bar = new ManaBarManager(plugin);
            while (true) {
                if (new_swap_slot == -1) {
                    ItemStack orig_item = plugin.getConfig().getItemStack("Scores.users." + player.getUniqueId() + ".saved_offhand_item");
                    player.getInventory().setItemInOffHand(orig_item);
                    plugin.getConfig().set("Scores.users." + player.getUniqueId() + ".saved_offhand_item", null);
                    plugin.saveConfig();
                    player.setMetadata("swap_slot", new FixedMetadataValue(plugin, new_swap_slot));

                    bar.clearMessage(player);
                    break;
                }
                else {
                    if (power_levels[new_swap_slot] > 0) {
                        if (!offhand_item.getType().equals(Material.KNOWLEDGE_BOOK)) {
                            plugin.getConfig().set("Scores.users." + player.getUniqueId() + ".saved_offhand_item", offhand_item);
                            plugin.saveConfig();
                        }
                        player.getInventory().setItemInOffHand(power_items[new_swap_slot]);

                        if (power_runnables[new_swap_slot] != null) {
                            BukkitTask task = power_runnables[new_swap_slot];
                        }
                        player.setMetadata("swap_slot", new FixedMetadataValue(plugin, new_swap_slot));

                        bar.displayMessage(player, names[new_swap_slot], 2, true);
                        break;
                    }

                    new_swap_slot += incr;
                    if (new_swap_slot >= power_items.length) {
                        new_swap_slot = -1;
                    }
                    else if (new_swap_slot < -1) {
                        new_swap_slot = power_items.length - 1;
                    }
                }
            }

        player.setMetadata("mana_bar_active_timer", new FixedMetadataValue(plugin, 60));
        player.setMetadata("swap_cooldown", new FixedMetadataValue(plugin, 10));
        player.setMetadata("swap_name_timer", new FixedMetadataValue(plugin, 60));
        new BukkitRunnable() {
            @Override
            public void run() {
                int swap_cd_num = player.getMetadata("swap_cooldown").get(0).asInt();
                int swap_name_timer = player.getMetadata("swap_name_timer").get(0).asInt();
                if (swap_cd_num > 0 || swap_name_timer > 0) {
                    swap_cd_num = swap_cd_num > 0 ? swap_cd_num - 1 : swap_cd_num;
                    player.setMetadata("swap_cooldown", new FixedMetadataValue(plugin, swap_cd_num));

                    swap_name_timer = swap_name_timer > 0 ? swap_name_timer - 1 : swap_name_timer;
                    player.setMetadata("swap_name_timer", new FixedMetadataValue(plugin, swap_name_timer));
                }
                else {
                    Bukkit.getScheduler().cancelTask(this.getTaskId());
                }
            }
        }.runTaskTimerAsynchronously(plugin,0,0);
    }
}
