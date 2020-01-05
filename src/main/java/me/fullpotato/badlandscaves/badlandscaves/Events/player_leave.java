package me.fullpotato.badlandscaves.badlandscaves.Events;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.badlandscaves.Runnables.player_save_to_config;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class player_leave implements Listener {
    private BadlandsCaves plugin;
    public player_leave (BadlandsCaves bcav) {
        plugin = bcav;
    }

    @EventHandler
    public void player_leave (PlayerQuitEvent event) {
        Player player = event.getPlayer();
        new player_save_to_config(plugin, player).run();
    }
}
