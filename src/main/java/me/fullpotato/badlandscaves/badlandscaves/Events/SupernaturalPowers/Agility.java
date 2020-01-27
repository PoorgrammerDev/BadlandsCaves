package me.fullpotato.badlandscaves.badlandscaves.Events.SupernaturalPowers;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.badlandscaves.Runnables.agilityJumpRunnable;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

public class Agility implements Listener {
    private BadlandsCaves plugin;
    public Agility (BadlandsCaves bcav) {
        plugin = bcav;
    }

    //detecting if the player is jumping
    @EventHandler
    public void firstJump (PlayerMoveEvent event) {
        Player player = event.getPlayer();
        int has_powers = player.getMetadata("has_supernatural_powers").get(0).asInt();
        if (has_powers < 1.0) return;

        int agility_level = player.getMetadata("agility_level").get(0).asInt();
        if (agility_level < 1.0) return;

        Vector velocity = player.getVelocity();
        Location location = player.getLocation();
        Block block = location.getBlock();

        if (velocity.getY() > 0 && !player.isFlying() && !player.isOnGround() && !player.isSwimming() && !block.getType().equals(Material.LADDER) && !block.getType().equals(Material.VINE)) {
            player.setAllowFlight(true);
            player.setMetadata("agility_jump_timer", new FixedMetadataValue(plugin, 50));
            int run_id = player.getMetadata("agility_jump_id").get(0).asInt();
            if (run_id == 0) {
                BukkitTask flight_cancel = new agilityJumpRunnable(plugin, player).runTaskTimerAsynchronously(plugin, 0, 1);
                player.setMetadata("agility_jump_id", new FixedMetadataValue(plugin, flight_cancel.getTaskId()));
            }

        }
    }

    @EventHandler
    public void multiJump (PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();
        if (!player.getGameMode().equals(GameMode.SURVIVAL) && !player.getGameMode().equals(GameMode.ADVENTURE)) return;

        int has_powers = player.getMetadata("has_supernatural_powers").get(0).asInt();
        if (has_powers < 1.0) return;

        int agility_level = player.getMetadata("agility_level").get(0).asInt();
        if (agility_level < 1.0) return;
        player.setMetadata("agility_jump_timer", new FixedMetadataValue(plugin , 0));
        event.setCancelled(true);

        Vector velocity = player.getVelocity();
        if (agility_level == 1) {
            velocity.multiply(1).setY(0.8);
        }
        else {
            //TODO change these values around a bit to make it better
            velocity.multiply(3).setY(1.5);
        }

        player.setVelocity(velocity);

    }
}
