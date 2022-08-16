package me.fullpotato.badlandscaves.SupernaturalPowers.Spells;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.Util.ParticleShapes;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Agility extends UsePowers implements Listener {
    private final ParticleShapes particleShapes;
    public Agility(BadlandsCaves bcav, ParticleShapes particleShapes) {
        super(bcav, particleShapes);
        this.particleShapes = particleShapes;
    }

    //detecting if the player is jumping
    @EventHandler
    public void firstJump(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!player.getGameMode().equals(GameMode.SURVIVAL) && !player.getGameMode().equals(GameMode.ADVENTURE)) return;

        final boolean has_powers = (byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == 1;
        int agility_level = (int) PlayerScore.AGILITY_LEVEL.getScore(plugin, player);
        if ((!has_powers || agility_level < 1.0) && player.getAllowFlight()) {
            player.setAllowFlight(false);
        }
        else if (((LivingEntity) player).isOnGround()) {
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
        event.setCancelled(true);
        player.setFlying(false);
        player.setAllowFlight(false);

        final boolean has_powers = (byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == 1;
        if (!has_powers) return;

        int agility_level = (int) PlayerScore.AGILITY_LEVEL.getScore(plugin, player);
        if (agility_level < 1.0) return;

        if (((int) PlayerScore.SPELLS_SILENCED_TIMER.getScore(plugin, player) > 0)) return;
        if (attemptSilence(player)) return;

        double mana = ((double) PlayerScore.MANA.getScore(plugin, player));
        int cost = plugin.getOptionsConfig().getInt("spell_costs.agility_mana_cost");

        if (mana >= cost) {
            PlayerScore.MANA_REGEN_DELAY_TIMER.setScore(plugin, player, Math.max(
                    ((int) PlayerScore.MANA_REGEN_DELAY_TIMER.getScore(plugin, player)),
                    plugin.getOptionsConfig().getInt("mana_regen_cooldown") / 5
            ));
            PlayerScore.MANA_BAR_ACTIVE_TIMER.setScore(plugin, player, 60);
            PlayerScore.MANA.setScore(plugin, player, mana - cost);

            player.playSound(player.getLocation(), Sound.ENTITY_GHAST_SHOOT, SoundCategory.PLAYERS, 0.5F, 1);
            for (Entity entity : player.getNearbyEntities(10, 10, 10)) {
                if (entity instanceof Player) {
                    Player powered = (Player) entity;
                    if (!powered.equals(player) && (byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, powered) == 1 && powered.getWorld().equals(player.getWorld()) && powered.getLocation().distanceSquared(player.getLocation()) < 100) {
                        powered.playSound(player.getLocation(), Sound.ENTITY_GHAST_SHOOT, SoundCategory.PLAYERS, 0.3F, 1);
                    }
                }
            }

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
                    if (((LivingEntity) player).isOnGround() || running[0] >= max) {
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
                if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, powered) == 1) powered.spawnParticle(Particle.CLOUD, player_loc, 5, 0.1, 0.1, 0.1);
            }
        }
    }
}
