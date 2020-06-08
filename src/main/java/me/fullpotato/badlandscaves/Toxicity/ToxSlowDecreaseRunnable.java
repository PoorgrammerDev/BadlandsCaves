package me.fullpotato.badlandscaves.Toxicity;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ToxSlowDecreaseRunnable extends BukkitRunnable {

    private final BadlandsCaves plugin;
    public ToxSlowDecreaseRunnable(BadlandsCaves bcav) {
        plugin = bcav;
    }

    @Override
    public void run() {
        boolean isHardmode = plugin.getConfig().getBoolean("game_values.hardmode");
        if (isHardmode) {
            return;
        }

        for (Player player : plugin.getServer().getOnlinePlayers()) {
            double thirst = (double) PlayerScore.THIRST.getScore(plugin, player);
            double tox = (double) PlayerScore.TOXICITY.getScore(plugin, player);
            if (thirst >= 80) {
                if (tox > 0.1) {
                    PlayerScore.TOXICITY.setScore(plugin, player, tox - 0.1);
                }
            }
        }
    }
}
