package me.fullpotato.badlandscaves.SupernaturalPowers.DescensionStage;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class DetectionDecrease extends BukkitRunnable {
    private BadlandsCaves plugin;
    public DetectionDecrease(BadlandsCaves bcav) {
        plugin = bcav;
    }

    @Override
    public void run() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            int in_descension = ((int) PlayerScore.IN_DESCENSION.getScore(plugin, player));
            if (in_descension == 2) {
                double detection = (PlayerScore.DESCENSION_DETECT.hasScore(plugin, player)) ? ((double) PlayerScore.DESCENSION_DETECT.getScore(plugin, player)) : 0;
                if (detection > 0) {
                    double detect_cooldown = (double) PlayerScore.DESCENSION_DETECT_COOLDOWN.getScore(plugin, player);
                    if (detect_cooldown <= 0) {
                        detection -= 0.5;
                        PlayerScore.DESCENSION_DETECT.setScore(plugin, player, detection);
                    }
                    else {
                        detect_cooldown--;
                        PlayerScore.DESCENSION_DETECT_COOLDOWN.setScore(plugin, player, detect_cooldown);
                    }
                }

            }
        }
    }
}
