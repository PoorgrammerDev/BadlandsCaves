package me.fullpotato.badlandscaves.SupernaturalPowers.DescensionStage;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ForceFixDescensionValues extends BukkitRunnable {
    private BadlandsCaves plugin;
    private World descension_world;

    public ForceFixDescensionValues(BadlandsCaves plugin) {
        this.plugin = plugin;
        descension_world = plugin.getServer().getWorld(plugin.descensionWorldName);
    }

    @Override
    public void run() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if (!player.getWorld().equals(descension_world)) {
                int in_descension = ((int) PlayerScore.IN_DESCENSION.getScore(plugin, player));
                if (in_descension == 1 || in_descension == 2) {
                    PlayerScore.IN_DESCENSION.setScore(plugin, player, 0);
                }
            }
        }
    }
}
