package me.fullpotato.badlandscaves.badlandscaves.Runnables;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerSaveToConfig extends BukkitRunnable {

    private BadlandsCaves plugin;
    private String[] values;

    public PlayerSaveToConfig(BadlandsCaves bcav, String[] ply_vals) {
        plugin = bcav;
        values = ply_vals;
    }

    //autosave
    @Override
    public void run() {
        saveToConfig(true);
    }


    public void saveToConfig (boolean silent) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            saveToConfig(player, silent);
        }
    }

    public void saveToConfig (Player player, boolean silent) {
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
