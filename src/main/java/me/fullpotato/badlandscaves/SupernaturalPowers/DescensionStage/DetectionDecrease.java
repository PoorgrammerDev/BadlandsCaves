package me.fullpotato.badlandscaves.SupernaturalPowers.DescensionStage;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;

public class DetectionDecrease extends BukkitRunnable {
    private final BadlandsCaves plugin;
    private final World world;
    public DetectionDecrease(BadlandsCaves bcav) {
        plugin = bcav;
        world = plugin.getServer().getWorld(plugin.getDescensionWorldName());
    }

    @Override
    public void run() {
        final Collection<? extends Player> players = world.getEntitiesByClass(Player.class);
        if (players.isEmpty()) {
            this.cancel();
            return;
        }

        for (Player player : players) {
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
