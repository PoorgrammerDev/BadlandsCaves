package me.fullpotato.badlandscaves.Other;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.Effects.PlayerEffects;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class SpawnImmunity implements Listener {
    private final BadlandsCaves plugin;
    private final PlayerEffects playerEffects;

    public SpawnImmunity(BadlandsCaves plugin, PlayerEffects playerEffects) {
        this.plugin = plugin;
        this.playerEffects = playerEffects;
    }

    @EventHandler
    public void enableImmunity (PlayerDeathEvent event) {
        final Player player = event.getEntity();
        protectPlayer(player);
        player.setVelocity(new Vector());
    }

    @EventHandler
    public void hit (PlayerInteractEvent event) {
        attemptUnprotect(event.getPlayer());
    }

    @EventHandler
    public void hitEntity (PlayerInteractAtEntityEvent event) {
        attemptUnprotect(event.getPlayer());
    }

    @EventHandler
    public void startBreaking (BlockDamageEvent event) {
        attemptUnprotect(event.getPlayer());
    }

    @EventHandler
    public void move (PlayerMoveEvent event) {
        attemptUnprotect(event.getPlayer());
    }

    @EventHandler
    public void pickupItem (EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            attemptUnprotect(player);
        }
    }

    @EventHandler
    public void preventTeleport (PlayerTeleportEvent event) {
        if (event.getCause().equals(PlayerTeleportEvent.TeleportCause.SPECTATE)) {
            final Player player = event.getPlayer();
            if ((byte) PlayerScore.SPAWN_IMMUNE.getScore(plugin, player) == 1) {
                event.setCancelled(true);
            }
        }
    }


    public void attemptUnprotect (Player player) {
        if (player.getGameMode().equals(GameMode.SPECTATOR) && (byte) PlayerScore.SPAWN_IMMUNE.getScore(plugin, player) == 1) {
            unprotectPlayer(player);
        }
    }

    public void protectPlayer (Player player) {
        PlayerScore.SPAWN_IMMUNE.setScore(plugin, player, 1);
        player.setGameMode(GameMode.SPECTATOR);
        player.setCollidable(false);
    }

    public void unprotectPlayer (Player player) {
        PlayerScore.SPAWN_IMMUNE.setScore(plugin, player, 0);
        player.setGameMode(GameMode.SURVIVAL);
        player.setCollidable(true);
        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 30, 5, true, true));
        playerEffects.applyEffects(player, true);
    }
}
