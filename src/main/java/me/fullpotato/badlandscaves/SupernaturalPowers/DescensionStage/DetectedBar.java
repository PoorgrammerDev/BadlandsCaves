package me.fullpotato.badlandscaves.SupernaturalPowers.DescensionStage;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class DetectedBar extends BukkitRunnable {
    private final BadlandsCaves plugin;
    private final NamespacedKey key;
    private final World world;
    public DetectedBar(BadlandsCaves plugin) {
        this.plugin = plugin;
        key = new NamespacedKey(plugin, "descension_detected_bar");
        world = plugin.getServer().getWorld(plugin.getDescensionWorldName());
    }

    @Override
    public void run() {
        //making the bossbar
        KeyedBossBar detected_bar = plugin.getServer().getBossBar(key);
        if (detected_bar == null) {
            String title = ChatColor.RED + "Detection";
            detected_bar = plugin.getServer().createBossBar(key, title, BarColor.RED, BarStyle.SEGMENTED_6);
        }


        for (Player player : world.getEntitiesByClass(Player.class)) {
            int in_descension = ((int) PlayerScore.IN_DESCENSION.getScore(plugin, player));
            if (in_descension == 2) {
                if (!detected_bar.getPlayers().contains(player)) detected_bar.addPlayer(player);
                double detect = ((double) PlayerScore.DESCENSION_DETECT.getScore(plugin, player));
                double detect_max = plugin.getOptionsConfig().getDouble("descension_max_detect");
                double detect_percentage = Math.min(Math.max(detect / detect_max, 0.0), 1.0);

                detected_bar.setProgress(detect_percentage);
            }
        }
    }
}
