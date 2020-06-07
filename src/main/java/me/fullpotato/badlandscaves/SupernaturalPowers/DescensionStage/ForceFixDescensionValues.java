package me.fullpotato.badlandscaves.SupernaturalPowers.DescensionStage;

import me.fullpotato.badlandscaves.BadlandsCaves;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
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
                int in_descension = player.getMetadata("in_descension").get(0).asInt();
                if (in_descension == 1 || in_descension == 2) {
                    player.setMetadata("in_descension", new FixedMetadataValue(plugin, 0));
                }
            }
        }
    }
}
