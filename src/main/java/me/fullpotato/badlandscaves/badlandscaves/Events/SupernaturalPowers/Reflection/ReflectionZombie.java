package me.fullpotato.badlandscaves.badlandscaves.Events.SupernaturalPowers.Reflection;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

public class ReflectionZombie implements Listener {
    private BadlandsCaves plugin;
    private World world = Bukkit.getWorld("world_reflection");
    public ReflectionZombie (BadlandsCaves bcav) {
        plugin = bcav;
    }

    @EventHandler
    public void damagedZombieAvoidPlayer (EntityTargetLivingEntityEvent event) {
        if (event.getEntity() instanceof Zombie &&
                event.getEntity().getWorld().equals(world) &&
                event.getTarget() instanceof Player &&
                event.getTarget().getWorld().equals(world)) {
            final Zombie zombie = (Zombie) event.getEntity();
            final double zombie_health = zombie.getHealth() / zombie.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
            if (zombie_health < 0.3) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void damageZombie (EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Zombie &&
        event.getEntity().getWorld().equals(world) &&
        event.getDamager() instanceof Player &&
        event.getDamager().getWorld().equals(world)) {
            final Zombie zombie = (Zombie) event.getEntity();
            final Player player = (Player) event.getDamager();
            final double damage = event.getFinalDamage();
            final double zombie_old_health = zombie.getHealth() / zombie.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
            final double zombie_new_health = (zombie.getHealth() - damage) / zombie.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
            if (zombie_old_health > 0.3 && zombie_new_health < 0.3) {
                zombie.setMetadata("teleport_cooldown", new FixedMetadataValue(plugin, 0));
            }

            //velocity

            if (damage > 10) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        final double mult = 5;
                        player.spawnParticle(Particle.EXPLOSION_NORMAL, zombie.getLocation(), 20, 0.5, 0.5, 0.5);
                        player.playSound(player.getLocation(), Sound.ENTITY_BLAZE_HURT, 2, 0.5F);
                        player.playSound(player.getLocation(), Sound.BLOCK_CONDUIT_DEACTIVATE, 1.2F, 0.1F);
                        player.playSound(player.getLocation(), Sound.BLOCK_CONDUIT_DEACTIVATE, 1.2F, 0.5F);
                        player.playSound(player.getLocation(), Sound.BLOCK_CONDUIT_DEACTIVATE, 1.2F, 1);
                        player.playSound(player.getLocation(), Sound.BLOCK_CONDUIT_DEACTIVATE, 1.2F, 1.5F);
                        player.playSound(player.getLocation(), Sound.BLOCK_CONDUIT_DEACTIVATE, 1.2F, 2);
                        zombie.setVelocity(zombie.getVelocity().multiply(mult));
                        player.setVelocity(zombie.getVelocity().multiply(-mult / 2));
                    }
                }.runTaskLaterAsynchronously(plugin, 2);
            }
        }
    }
}