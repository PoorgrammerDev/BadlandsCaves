package me.fullpotato.badlandscaves.badlandscaves.Runnables;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class playerSaveToConfig extends BukkitRunnable {

    private BadlandsCaves plugin;
    private Player player;
    private String[] values;

    public playerSaveToConfig(BadlandsCaves bcav, Player ply, String[] ply_vals) {
        plugin = bcav;
        player = ply;
        values = ply_vals;
    }

    @Override
    public void run() {

        for (String meta : values) {
            String filtered;
            String dot_meta;
            if (meta.contains("#") || meta.contains("*")) {
                filtered = meta.substring(1);
            }
            else {
                filtered = meta;
            }
            dot_meta = "." + filtered;
            plugin.getConfig().set("Scores.users." + player.getUniqueId() + dot_meta, player.getMetadata(filtered).get(0).asDouble());
            plugin.saveConfig();
        }

        plugin.getServer().getConsoleSender().sendMessage("[BadlandsCaves] Saved to Config for Player " + player.getDisplayName() + " (" + player.getUniqueId() + ")");
    }
}
