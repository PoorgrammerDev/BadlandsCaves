package me.fullpotato.badlandscaves.badlandscaves.Runnables.SupernaturalPowers.DescensionStage;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

public class DetectionDecrease extends BukkitRunnable {
    private BadlandsCaves plugin;
    public DetectionDecrease(BadlandsCaves bcav) {
        plugin = bcav;
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            int in_descension = player.getMetadata("in_descension").get(0).asInt();
            if (in_descension == 2) {
                double detection = player.hasMetadata("descension_detect") ? player.getMetadata("descension_detect").get(0).asDouble() : 0;
                if (detection > 0) {
                    int detect_cooldown = player.getMetadata("descension_detect_cooldown").get(0).asInt();
                    if (detect_cooldown <= 0) {
                        detection -= 0.5;
                        player.setMetadata("descension_detect", new FixedMetadataValue(plugin, detection));
                    }
                    else {
                        detect_cooldown--;
                        player.setMetadata("descension_detect_cooldown", new FixedMetadataValue(plugin, detect_cooldown));
                    }
                }

            }
        }
    }
}
