package me.fullpotato.badlandscaves.badlandscaves.Events;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.badlandscaves.Runnables.PlayerSaveToConfig;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerLeave implements Listener {
    private BadlandsCaves plugin;
    private String[] player_values;
    public PlayerLeave(BadlandsCaves bcav, String[] ply_val) {
        plugin = bcav;
        player_values = ply_val;
    }

    @EventHandler
    public void player_leave (PlayerQuitEvent event) {
        Player player = event.getPlayer();
        new PlayerSaveToConfig(plugin, player, player_values, false).runTaskAsynchronously(plugin);
    }
}
