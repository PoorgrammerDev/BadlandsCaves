package me.fullpotato.badlandscaves.SupernaturalPowers.Spells;

import me.fullpotato.badlandscaves.BadlandsCaves;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Agility extends UsePowers implements Listener {
    public Agility (BadlandsCaves bcav) {
        super(bcav);
    }

    //detecting if the player is jumping
    @EventHandler
    public void firstJump(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!player.getGameMode().equals(GameMode.SURVIVAL) && !player.getGameMode().equals(GameMode.ADVENTURE)) return;

        final boolean has_powers = player.getMetadata("has_supernatural_powers").get(0).asBoolean();
        int agility_level = player.getMetadata("agility_level").get(0).asInt();
        if ((!has_powers || agility_level < 1.0) && player.getAllowFlight()) {
            player.setAllowFlight(false);
        }
        else if (player.isOnGround()) {
            player.setAllowFlight(false);
            new BukkitRunnable() {
                @Override
                public void run() {
                    player.setAllowFlight(true);
                }
            }.runTaskLaterAsynchronously(plugin, 1);
        }
    }

    @EventHandler
    public void multiJump(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();
        if (!player.getGameMode().equals(GameMode.SURVIVAL) && !player.getGameMode().equals(GameMode.ADVENTURE)) return;

        final boolean has_powers = player.getMetadata("has_supernatural_powers").get(0).asBoolean();
        if (!has_powers) return;

        int agility_level = player.getMetadata("agility_level").get(0).asInt();
        if (agility_level < 1.0) return;

        double mana = player.getMetadata("Mana").get(0).asDouble();
        int cost = plugin.getConfig().getInt("game_values.agility_mana_cost");

        if (mana >= cost) {
            player.setMetadata("mana_regen_delay_timer", new FixedMetadataValue(plugin, Math.max(player.getMetadata("mana_regen_delay_timer").get(0).asInt(), 3)));
            player.setMetadata("mana_bar_active_timer", new FixedMetadataValue(plugin, 60));
            player.setMetadata("Mana", new FixedMetadataValue(plugin, mana - cost));

            player.playSound(player.getLocation(), Sound.ENTITY_GHAST_SHOOT, SoundCategory.PLAYERS, 0.5F, 1);
            for (Entity entity : player.getNearbyEntities(10, 10, 10)) {
                if (entity instanceof Player) {
                    Player powered = (Player) entity;
                    if (!powered.equals(player) && powered.getMetadata("has_supernatural_powers").get(0).asBoolean() && powered.getWorld().equals(player.getWorld()) && powered.getLocation().distanceSquared(player.getLocation()) < 100) {
                        powered.playSound(player.getLocation(), Sound.ENTITY_GHAST_SHOOT, SoundCategory.PLAYERS, 0.3F, 1);
                    }
                }
            }

            event.setCancelled(true);
            player.setFlying(false);
            player.setAllowFlight(false);

            Vector velocity = player.getVelocity();
            if (agility_level == 1) {
                velocity.multiply(1.1).setY(0.8);
            } else {
                velocity.multiply(3.2).setY(1.2);
            }

            player.setVelocity(velocity);
            jumpParticle(player);

            int max = agility_level > 1 ? 9 : 5;
            int[] running = {0};
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (player.isOnGround() || running[0] >= max) {
                        this.cancel();
                    }
                    else {
                        float fallDist = player.getFallDistance();
                        if (fallDist >= 1) {
                            player.setFallDistance(Math.max(fallDist - 1, 0));
                            running[0]++;
                        }
                    }
                }
            }.runTaskTimerAsynchronously(plugin, 0, 0);
        }
        else {
            player.setFlying(false);
            player.setAllowFlight(false);

            notEnoughMana(player);
        }
    }

    public void jumpParticle (Player player) {
        Vector opposite = player.getVelocity().multiply(-0.3);
        Location player_loc = player.getLocation();

        for (int i = 0; i < 5; i++) {
            player_loc.add(opposite);

            for (Player powered : plugin.getServer().getOnlinePlayers()) {
                if (powered.getMetadata("has_supernatural_powers").get(0).asBoolean()) powered.spawnParticle(Particle.CLOUD, player_loc, 5, 0.1, 0.1, 0.1);
            }
        }
    }
}
