package me.fullpotato.badlandscaves.badlandscaves.Runnables;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class playerSaveToConfig extends BukkitRunnable {

    private BadlandsCaves plugin;
    private Player plyr;
    private String[] values;
    private boolean silent;

    public playerSaveToConfig(BadlandsCaves bcav, Player ply, String[] ply_vals, boolean slnt) {
        plugin = bcav;
        plyr = ply;
        values = ply_vals;
        silent = slnt;
    }

    @Override
    public void run() {
        if (this.plyr == null) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                saveToConfig(player);
            }
        }
        else {
            saveToConfig(plyr);
        }
    }

    public void saveToConfig (Player player) {
        for (String meta : values) {
            String filtered;
            String dot_meta;
            if (meta.contains("!")) {
                continue;
            }
            else if (meta.contains("#") || meta.contains("*")) {
                filtered = meta.substring(1);
            }
            else {
                filtered = meta;
            }
            dot_meta = "." + filtered;
            plugin.getConfig().set("Scores.users." + player.getUniqueId() + dot_meta, player.getMetadata(filtered).get(0).asDouble());
            plugin.saveConfig();
        }

        if (!silent) {
            plugin.getServer().getConsoleSender().sendMessage("[BadlandsCaves] Saved to Config for Player " + player.getDisplayName() + " (" + player.getUniqueId() + ")");
        }
    }
}
