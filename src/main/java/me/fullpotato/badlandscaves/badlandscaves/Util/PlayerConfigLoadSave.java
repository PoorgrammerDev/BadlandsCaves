package me.fullpotato.badlandscaves.badlandscaves.Util;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerConfigLoadSave extends BukkitRunnable {

    private BadlandsCaves plugin;
    private String[] values;

    public PlayerConfigLoadSave(BadlandsCaves bcav, String[] ply_vals) {
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
        loadOrSavePlayer(player, true);
        if (!silent) {
            plugin.getServer().getConsoleSender().sendMessage("[BadlandsCaves] Saved to Config for Player " + player.getDisplayName() + " (" + player.getUniqueId() + ")");
        }
    }

    public void loadPlayer (Player player) {
        loadOrSavePlayer(player, false);
    }

    /**
     * Main code for loading player's metadata from config, and vice versa.
     *
     * @param player a valid online player
     * @param save if boolean is true, then it'll save. if false, it'll load instead
     **/
    public void loadOrSavePlayer (Player player, boolean save) {
        String type = "double";
        for (String meta: values) {
            if (meta.startsWith("$")) {
                meta = meta.substring(1);
                type = "boolean";
            }

            if (save) {
                if (type.equals("boolean")) {
                    boolean metadata = player.getMetadata(meta).get(0).asBoolean();
                    plugin.getConfig().set("Scores.users." + player.getUniqueId() + "." + meta, metadata);
                }
                else {
                    double metadata = player.getMetadata(meta).get(0).asDouble();
                    plugin.getConfig().set("Scores.users." + player.getUniqueId() + "." + meta, metadata);
                }
                plugin.saveConfig();
            }
            else {
                player.setMetadata(meta, new FixedMetadataValue(plugin, plugin.getConfig().get("Scores.users." + player.getUniqueId() + "." + meta)));
            }
        }
    }

    public void loadPlayers () {
        for (Player player : Bukkit.getOnlinePlayers()) {
            loadPlayer(player);
        }
    }
}
