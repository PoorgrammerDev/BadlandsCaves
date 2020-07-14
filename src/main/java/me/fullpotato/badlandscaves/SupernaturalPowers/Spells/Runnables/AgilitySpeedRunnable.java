package me.fullpotato.badlandscaves.SupernaturalPowers.Spells.Runnables;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class AgilitySpeedRunnable extends BukkitRunnable {
    private final BadlandsCaves plugin;
    public AgilitySpeedRunnable(BadlandsCaves bcav) {
        plugin = bcav;
    }

    @Override
    public void run() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            final boolean has_powers = (byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == 1;
            if (has_powers) {
                int agility_level = (int) PlayerScore.AGILITY_LEVEL.getScore(plugin, player);
                PlayerScore.AGILITY_BUFF_SPEED_LVL.setScore(plugin, player, agility_level);
            }
            else {
                PlayerScore.AGILITY_BUFF_SPEED_LVL.setScore(plugin, player, 0);
            }
        }
    }
}
