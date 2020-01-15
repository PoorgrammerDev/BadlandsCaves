package me.fullpotato.badlandscaves.badlandscaves.Events.SupernaturalPowers;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.badlandscaves.Runnables.displaceAmbientRunnable;
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
        ItemStack displace = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.displace").getValues(true));
        ItemStack withdraw = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.withdraw").getValues(true));

        //switching from item to displace
        if (!offhand_item.getType().equals(Material.KNOWLEDGE_BOOK)) {
            int displace_level = player.getMetadata("displace_level").get(0).asInt();
            if (displace_level >= 1) {
                event.setCancelled(true);
                plugin.getConfig().set("Scores.users." + player.getUniqueId() + ".saved_offhand_item", offhand_item);
                plugin.saveConfig();

                player.getInventory().setItemInOffHand(displace);
                BukkitTask particle_effect = new displaceAmbientRunnable(plugin, player).runTaskTimerAsynchronously(plugin, 0, 0);
                player.setMetadata("displace_particle_id", new FixedMetadataValue(plugin, particle_effect.getTaskId()));
            }
        }
        else {
            //switching from displace to withdraw
            if (offhand_item.isSimilar(displace)) {
                event.setCancelled(true);
                player.getInventory().setItemInOffHand(withdraw);

                int particle_task_id = player.getMetadata("displace_particle_id").get(0).asInt();
                if (particle_task_id != 0 && particle_task_id != 0.0) {
                    Bukkit.getScheduler().cancelTask(particle_task_id);
                    player.setMetadata("displace_particle_id", new FixedMetadataValue(plugin, 0.0));
                }
            }

            else if (offhand_item.isSimilar(withdraw)) {
                event.setCancelled(true);
                ItemStack orig_item = plugin.getConfig().getItemStack("Scores.users." + player.getUniqueId() + ".saved_offhand_item");
                player.getInventory().setItemInOffHand(orig_item);
                plugin.getConfig().set("Scores.users." + player.getUniqueId() + ".saved_offhand_item", null);
                plugin.saveConfig();
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
