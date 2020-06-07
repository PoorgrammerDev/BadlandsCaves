package me.fullpotato.badlandscaves.SupernaturalPowers.Spells.Runnables;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

public class AgilitySpeedRunnable extends BukkitRunnable {
    private BadlandsCaves plugin;
    public AgilitySpeedRunnable(BadlandsCaves bcav) {
        plugin = bcav;
    }

    @Override
    public void run() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            final boolean has_powers = (byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == 1;
            if (has_powers) {
                int agility_level = player.getMetadata("agility_level").get(0).asInt();
                player.setMetadata("agility_buff_speed_lvl", new FixedMetadataValue(plugin, agility_level));
            }
            else {
                player.setMetadata("agility_buff_speed_lvl", new FixedMetadataValue(plugin, 0));
            }
        }
    }
}
