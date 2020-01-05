package me.fullpotato.badlandscaves.badlandscaves.Runnables;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

public class tox_decr_slowly extends BukkitRunnable {

    private BadlandsCaves plugin;
    private Player player;
    public tox_decr_slowly (BadlandsCaves bcav, Player ply) {
        plugin = bcav;
        player = ply;
    }

    @Override
    public void run() {
        double thirst = player.getMetadata("Thirst").get(0).asDouble();
        double tox = player.getMetadata("Toxicity").get(0).asDouble();
        if (thirst >= 80) {
            if (tox > 0.1) {
                player.setMetadata("Toxicity", new FixedMetadataValue(plugin, tox - 0.1));
            }
        }
    }
}
