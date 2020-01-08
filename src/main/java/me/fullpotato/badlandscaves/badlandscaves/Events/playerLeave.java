package me.fullpotato.badlandscaves.badlandscaves.Events;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.badlandscaves.Runnables.playerSaveToConfig;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class playerLeave implements Listener {
    private BadlandsCaves plugin;
    private String[] player_values;
    public playerLeave(BadlandsCaves bcav, String[] ply_val) {
        plugin = bcav;
        player_values = ply_val;
    }

    @EventHandler
    public void player_leave (PlayerQuitEvent event) {
        Player player = event.getPlayer();
        new playerSaveToConfig(plugin, player, player_values).run();
    }
}
