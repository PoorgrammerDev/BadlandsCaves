package me.fullpotato.badlandscaves.badlandscaves.Runnables.SupernaturalPowers;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

public class agilitySpeedRunnable extends BukkitRunnable {
    private BadlandsCaves plugin;
    public agilitySpeedRunnable (BadlandsCaves bcav) {
        plugin = bcav;
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            int has_powers = player.getMetadata("has_supernatural_powers").get(0).asInt();
            if (has_powers < 1.0) return;

            int agility_level = player.getMetadata("agility_level").get(0).asInt();
            if (agility_level >= 1) player.setMetadata("agility_buff_speed_lvl", new FixedMetadataValue(plugin, agility_level));
        }
    }
}
