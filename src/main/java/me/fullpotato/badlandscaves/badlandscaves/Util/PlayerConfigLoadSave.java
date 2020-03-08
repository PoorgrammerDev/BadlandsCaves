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
        playerValuesManager(player, true, false);
        if (!silent) {
            plugin.getServer().getConsoleSender().sendMessage("[BadlandsCaves] Saved to Config for Player " + player.getDisplayName() + " (" + player.getUniqueId() + ")");
        }
    }

    public void loadPlayers () {
        for (Player player : Bukkit.getOnlinePlayers()) {
            loadPlayer(player);
        }
    }

    public void loadPlayer (Player player) {
        playerValuesManager(player, false, false);
    }

    public void saveDefault (Player player) {
        playerValuesManager(player, true, true);
    }

    /**
     * Main code for loading player's metadata from config, and vice versa.
     *
     * @param player a valid online player
     * @param save if boolean is true, then it'll save. if false, it'll load instead
     * @param def only works if save is also true. saves the default values
     **/
    public void playerValuesManager (Player player, boolean save, boolean def) {
        for (String meta: values) {
            double default_val = 0;
            String type = "double";
            if (meta.startsWith("$")) {
                meta = meta.substring(1);
                type = "boolean";
            }
            else if (meta.startsWith("%")) {
                meta = meta.substring(1);
                default_val = 100;
            }

            if (save) {
                if (type.equals("boolean")) {
                    boolean metadata = def ? default_val != 0 : player.getMetadata(meta).get(0).asBoolean();
                    plugin.getConfig().set("Scores.users." + player.getUniqueId() + "." + meta, metadata);
                }
                else {
                    double metadata = def ? default_val : player.getMetadata(meta).get(0).asDouble();
                    plugin.getConfig().set("Scores.users." + player.getUniqueId() + "." + meta, metadata);
                }
                plugin.saveConfig();
            }
            else {
                player.setMetadata(meta, new FixedMetadataValue(plugin, plugin.getConfig().get("Scores.users." + player.getUniqueId() + "." + meta)));
            }
        }
        plugin.saveConfig();
    }
}
