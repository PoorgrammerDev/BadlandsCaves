package me.fullpotato.badlandscaves.badlandscaves.Events.SupernaturalPowers;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.badlandscaves.Runnables.SupernaturalPowers.AgilityJumpRunnable;
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

        if (!player.getGameMode().equals(GameMode.SURVIVAL) && !player.getGameMode().equals(GameMode.ADVENTURE)) return;

        final boolean has_powers = player.getMetadata("has_supernatural_powers").get(0).asBoolean();
        int agility_level = player.getMetadata("agility_level").get(0).asInt();
        if (!has_powers || agility_level < 1.0) {
            player.setAllowFlight(false);
            return;
        }

        double mana = player.getMetadata("Mana").get(0).asDouble();
        int agility_jump_mana_cost = plugin.getConfig().getInt("game_values.agility_jump_mana_cost");

        if (mana >= agility_jump_mana_cost) {
            Vector velocity = player.getVelocity();
            Location location = player.getLocation();
            Block block = location.getBlock();

            Location below = location.clone();
            below.subtract(0, 1, 0);
            Block block_below = below.getBlock();

            Location above = location.clone();
            above.add(0, 2, 0);
            Block block_above = above.getBlock();

            if (velocity.getY() > 0 && !player.isFlying() && !player.isOnGround() && !player.isSwimming() && !block.getType().equals(Material.LADDER) && !block.getType().equals(Material.VINE) && !block_below.isPassable() && block_above.isPassable()) {
                player.setAllowFlight(true);
                player.setMetadata("agility_jump_timer", new FixedMetadataValue(plugin, 30));
                int run_id = player.getMetadata("agility_jump_id").get(0).asInt();
                if (run_id == 0) {
                    BukkitTask flight_cancel = new AgilityJumpRunnable(plugin, player).runTaskTimerAsynchronously(plugin, 0, 1);
                    player.setMetadata("agility_jump_id", new FixedMetadataValue(plugin, flight_cancel.getTaskId()));
                }

            }
        }
    }

    @EventHandler
    public void multiJump (PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();
        if (!player.getGameMode().equals(GameMode.SURVIVAL) && !player.getGameMode().equals(GameMode.ADVENTURE)) return;

        final boolean has_powers = player.getMetadata("has_supernatural_powers").get(0).asBoolean();
        if (!has_powers) return;

        int agility_level = player.getMetadata("agility_level").get(0).asInt();
        if (agility_level < 1.0) return;

        double mana = player.getMetadata("Mana").get(0).asDouble();
        int agility_jump_mana_cost = plugin.getConfig().getInt("game_values.agility_jump_mana_cost");

        player.setMetadata("mana_bar_active_timer", new FixedMetadataValue(plugin, 60));
        if (mana >= agility_jump_mana_cost) {
            int agility_jump_timer = player.getMetadata("agility_jump_timer").get(0).asInt();
            if (agility_jump_timer > 0) {
                double new_mana = mana - (double) (agility_jump_mana_cost);
                player.setMetadata("Mana", new FixedMetadataValue(plugin, new_mana));
                player.setMetadata("mana_regen_delay_timer", new FixedMetadataValue(plugin, 30));

                player.setMetadata("agility_jump_timer", new FixedMetadataValue(plugin , 0));
                event.setCancelled(true);
                player.setFlying(false);

                Vector velocity = player.getVelocity();
                if (agility_level == 1) {
                    velocity.multiply(1).setY(0.8);
                }
                else {
                    velocity.multiply(4).setY(1.2);
                }

                player.setVelocity(velocity);
            }
        }
        else {
            player.setMetadata("agility_jump_timer", new FixedMetadataValue(plugin , 0));
            player.setMetadata("mana_needed_timer", new FixedMetadataValue(plugin, 5));
        }
    }
}
