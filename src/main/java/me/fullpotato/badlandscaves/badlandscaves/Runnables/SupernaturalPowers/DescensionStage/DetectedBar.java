package me.fullpotato.badlandscaves.badlandscaves.Runnables.SupernaturalPowers.DescensionStage;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class DetectedBar extends BukkitRunnable {
    private BadlandsCaves plugin;
    public DetectedBar(BadlandsCaves bcav) {
        plugin = bcav;
    }

    @Override
    public void run() {
        //making the bossbar
        NamespacedKey key = new NamespacedKey(plugin, "descension_detected_bar");
        KeyedBossBar detected_bar = plugin.getServer().getBossBar(key);
        if (detected_bar == null) {
            String title = ChatColor.RED + "Detection";
            detected_bar = plugin.getServer().createBossBar(key, title, BarColor.RED, BarStyle.SEGMENTED_6);
        }

        World world = plugin.getServer().getWorld(plugin.descensionWorldName);
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            int in_descension = player.getMetadata("in_descension").get(0).asInt();
            if (in_descension == 2 && player.getWorld().equals(world)) {
                if (!detected_bar.getPlayers().contains(player)) detected_bar.addPlayer(player);
                double detect = player.getMetadata("descension_detect").get(0).asDouble();
                double detect_max = plugin.getConfig().getDouble("game_values.descension_max_detect");
                double detect_percentage = Math.min(Math.max(detect / detect_max, 0.0), 1.0);

                detected_bar.setProgress(detect_percentage);
            }
            else {
                detected_bar.removePlayer(player);
            }
        }
    }
}
