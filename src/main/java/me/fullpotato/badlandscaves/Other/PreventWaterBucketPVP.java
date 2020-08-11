package me.fullpotato.badlandscaves.Other;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketEmptyEvent;

import java.util.Collection;

public class PreventWaterBucketPVP implements Listener {
    private static final int RANGE = 2;

    @EventHandler
    public void emptyWater (PlayerBucketEmptyEvent event) {
        if (event.getBucket().equals(Material.WATER_BUCKET)) {
            final Player player = event.getPlayer();
            final Collection<Entity> nearbyPlayers = player.getWorld().getNearbyEntities(event.getBlock().getLocation(), RANGE, RANGE, RANGE, entity -> (!entity.equals(player) && entity instanceof Player));
            if (!nearbyPlayers.isEmpty()) {
                event.setCancelled(true);
            }
        }
    }
}
