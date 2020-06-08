package me.fullpotato.badlandscaves.SupernaturalPowers.Spells;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import me.fullpotato.badlandscaves.SupernaturalPowers.Spells.Runnables.DisplaceParticleRunnable;
import me.fullpotato.badlandscaves.SupernaturalPowers.Spells.Runnables.ManaBarManager;
import me.fullpotato.badlandscaves.SupernaturalPowers.Spells.Runnables.PossessionIndicatorRunnable;
import me.fullpotato.badlandscaves.SupernaturalPowers.Spells.Runnables.WithdrawIndicatorRunnable;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
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
        final boolean has_powers = (byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == 1;
        if (!has_powers) return;

        ManaBarManager bar = new ManaBarManager(plugin);
        if (player.isSneaking()) {
            PlayerScore.SWAP_WINDOW.setScore(plugin, player, 0);
            bar.clearMessage(player);
        }
        else {
            final boolean doubleshift_window = (PlayerScore.SWAP_DOUBLESHIFT_WINDOW.hasScore(plugin, player)) && ((byte) PlayerScore.SWAP_DOUBLESHIFT_WINDOW.getScore(plugin, player) == 1);
            if (doubleshift_window) {
                PlayerScore.SWAP_WINDOW.setScore(plugin, player, 1);

                bar.displayMessage(player, "§3Scroll to Access Abilities", 2, false);
            }
            else {
                PlayerScore.SWAP_DOUBLESHIFT_WINDOW.setScore(plugin, player, 1);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        PlayerScore.SWAP_DOUBLESHIFT_WINDOW.setScore(plugin, player, 0);
                    }
                }.runTaskLaterAsynchronously(plugin, 20);
            }
        }
    }


    @EventHandler
    public void swap_to_powers (PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        final boolean has_powers = (byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == 1;
        if (!has_powers) return;

        boolean sneaking = player.isSneaking();
        if (!sneaking) return;

        final boolean in_window = (PlayerScore.SWAP_WINDOW.hasScore(plugin, player)) && ((byte) PlayerScore.SWAP_WINDOW.getScore(plugin, player) == 1);
        if (!in_window) return;


        int swap_cd_num = ((int) PlayerScore.SWAP_COOLDOWN.getScore(plugin, player));
        if (swap_cd_num > 0) return;

        ItemStack offhand_item = player.getInventory().getItemInOffHand();
        int swap_slot = ((int) PlayerScore.SWAP_SLOT.getScore(plugin, player));
        int incr = (event.getNewSlot() > event.getPreviousSlot() || (event.getNewSlot() == 0 && event.getPreviousSlot() == 8)) && !(event.getNewSlot() == 8 && event.getPreviousSlot() == 0) ? 1 : -1;

        final String[] names = {
                ChatColor.BOLD.toString() + ChatColor.LIGHT_PURPLE + "Displace",
                ChatColor.BOLD.toString() + ChatColor.BLUE + "Enhanced Eyes",
                ChatColor.BOLD.toString() + ChatColor.GRAY + "Withdraw",
                ChatColor.BOLD.toString() + ChatColor.DARK_GREEN + "Possession",
        };

        final int[] power_levels = {
                (int) PlayerScore.DISPLACE_LEVEL.getScore(plugin, player),
                (int) PlayerScore.EYES_LEVEL.getScore(plugin, player),
                (int) PlayerScore.WITHDRAW_LEVEL.getScore(plugin, player),
                (int) PlayerScore.POSSESS_LEVEL.getScore(plugin, player),
        };

        final ItemStack[] power_items = {
                CustomItem.DISPLACE.getItem(),
                CustomItem.ENHANCED_EYES.getItem(),
                CustomItem.WITHDRAW.getItem(),
                CustomItem.POSSESS.getItem(),
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
                    PlayerScore.SWAP_SLOT.setScore(plugin, player, new_swap_slot);

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
                        PlayerScore.SWAP_SLOT.setScore(plugin, player, new_swap_slot);

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

        PlayerScore.MANA_BAR_ACTIVE_TIMER.setScore(plugin, player, 60);
        PlayerScore.SWAP_COOLDOWN.setScore(plugin, player, 10);
        PlayerScore.SWAP_NAME_TIMER.setScore(plugin, player, 60);
        new BukkitRunnable() {
            @Override
            public void run() {
                int swap_cd_num = ((int) PlayerScore.SWAP_COOLDOWN.getScore(plugin, player));
                int swap_name_timer = ((int) PlayerScore.SWAP_NAME_TIMER.getScore(plugin, player));
                if (swap_cd_num > 0 || swap_name_timer > 0) {
                    swap_cd_num = swap_cd_num > 0 ? swap_cd_num - 1 : swap_cd_num;
                    PlayerScore.SWAP_COOLDOWN.setScore(plugin, player, swap_cd_num);

                    swap_name_timer = swap_name_timer > 0 ? swap_name_timer - 1 : swap_name_timer;
                    PlayerScore.SWAP_NAME_TIMER.setScore(plugin, player, swap_name_timer);
                }
                else {
                    plugin.getServer().getScheduler().cancelTask(this.getTaskId());
                }
            }
        }.runTaskTimerAsynchronously(plugin,0,0);
    }
}
