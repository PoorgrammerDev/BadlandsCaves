package me.fullpotato.badlandscaves.badlandscaves.Events.SupernaturalPowers;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.badlandscaves.Runnables.displaceParticleRunnable;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class swapPowers implements Listener {
    private BadlandsCaves plugin;
    public swapPowers (BadlandsCaves bcav) {
        plugin = bcav;
    }

    @EventHandler
    public void swap_to_powers (PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        int has_powers = player.getMetadata("has_supernatural_powers").get(0).asInt();
        if (has_powers < 1.0) return;

        boolean sneaking = player.isSneaking();
        if (!sneaking) return;

        int swap_cd_num = player.getMetadata("swap_cooldown").get(0).asInt();
        if (swap_cd_num > 0) return;

        ItemStack offhand_item = player.getInventory().getItemInOffHand();
        int swap_slot = player.getMetadata("swap_slot").get(0).asInt();
        int incr = (event.getNewSlot() > event.getPreviousSlot() || (event.getNewSlot() == 0 && event.getPreviousSlot() == 8)) && !(event.getNewSlot() == 8 && event.getPreviousSlot() == 0) ? 1 : -1;

        final int[] power_levels = {
                player.getMetadata("displace_level").get(0).asInt(),
                player.getMetadata("eyes_level").get(0).asInt(),
                player.getMetadata("withdraw_level").get(0).asInt(),
                player.getMetadata("vanish_level").get(0).asInt(),
        };

        final ItemStack[] power_items = {
                ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.displace").getValues(true)),
                ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.enhanced_eyes").getValues(true)),
                ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.withdraw").getValues(true)),
                ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.vanish").getValues(true)),
        };

        final BukkitTask[] power_runnables = {
                new displaceParticleRunnable(plugin, player).runTaskTimerAsynchronously(plugin, 0, 1),
                null,
                null,
                null,
        };

        int new_swap_slot = swap_slot + incr;
        if (new_swap_slot >= power_items.length) {
            new_swap_slot = -1;
        }
        else if (new_swap_slot < -1) {
            new_swap_slot = power_items.length - 1;
        }
            while (true) {
                if (new_swap_slot == -1) {
                    event.setCancelled(true);
                    ItemStack orig_item = plugin.getConfig().getItemStack("Scores.users." + player.getUniqueId() + ".saved_offhand_item");
                    player.getInventory().setItemInOffHand(orig_item);
                    plugin.getConfig().set("Scores.users." + player.getUniqueId() + ".saved_offhand_item", null);
                    plugin.saveConfig();
                    player.setMetadata("swap_slot", new FixedMetadataValue(plugin, new_swap_slot));
                    break;
                }
                else {
                    event.setCancelled(true);
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

        player.setMetadata("swap_cooldown", new FixedMetadataValue(plugin, 10));
        BukkitTask decrement = new BukkitRunnable() {
            @Override
            public void run() {
                int swap_cd_num = player.getMetadata("swap_cooldown").get(0).asInt();
                if (swap_cd_num > 0) {
                    swap_cd_num--;
                    player.setMetadata("swap_cooldown", new FixedMetadataValue(plugin, swap_cd_num));
                }
                else {
                    Bukkit.getScheduler().cancelTask(this.getTaskId());
                }
            }
        }.runTaskTimerAsynchronously(plugin,0,0);
    }
}
