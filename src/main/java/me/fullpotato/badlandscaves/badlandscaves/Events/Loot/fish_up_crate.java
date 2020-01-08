package me.fullpotato.badlandscaves.badlandscaves.Events.Loot;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;

public class fish_up_crate implements Listener {
    @EventHandler
    public void fishing (PlayerFishEvent event) {
        System.out.println(event.getCaught().getType());
    }

}
