package me.fullpotato.badlandscaves.Util;

import me.fullpotato.badlandscaves.BadlandsCaves;
import org.bukkit.entity.Player;

public class InitializePlayer {
    private final BadlandsCaves plugin;

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
