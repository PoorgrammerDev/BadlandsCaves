package me.fullpotato.badlandscaves.Util;

import me.fullpotato.badlandscaves.BadlandsCaves;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

public class InitializePlayer {
    private BadlandsCaves plugin;

    public InitializePlayer(BadlandsCaves plugin) {
        this.plugin = plugin;
    }

    public void initializePlayer(Player player) {
        for (PlayerScore score : PlayerScore.values()) {
            score.setScore(plugin, player, score.getDefaultScore());
        }

        PlayerScore.INITIALIZED.setScore(plugin, player, (byte) 1);
    }
}
