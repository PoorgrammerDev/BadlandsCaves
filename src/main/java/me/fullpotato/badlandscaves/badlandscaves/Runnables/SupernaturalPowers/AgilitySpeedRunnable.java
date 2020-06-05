package me.fullpotato.badlandscaves.badlandscaves.Runnables.SupernaturalPowers;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.Bukkit;
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
            final boolean has_powers = player.getMetadata("has_supernatural_powers").get(0).asBoolean();
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
