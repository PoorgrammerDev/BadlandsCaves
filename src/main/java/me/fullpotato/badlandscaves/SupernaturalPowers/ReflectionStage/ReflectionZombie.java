package me.fullpotato.badlandscaves.SupernaturalPowers.ReflectionStage;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.NMS.FakePlayer.FakePlayerNMS;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

public class ReflectionZombie implements Listener {
    private final BadlandsCaves plugin;
    private final World world;
    public ReflectionZombie (BadlandsCaves bcav) {
        plugin = bcav;
        world = plugin.getServer().getWorld(plugin.getReflectionWorldName());
    }

    @EventHandler
    public void damageZombie (EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Zombie && event.getEntity().getWorld().equals(world)) {
            if (event.getDamager() instanceof Player && event.getDamager().getWorld().equals(world)) {
                FakePlayerNMS nms = plugin.getFakePlayerNMS();
                nms.damage(ZombieBossBehavior.fakePlayer, null, true);

                final Zombie zombie = (Zombie) event.getEntity();
                zombie.getWorld().playSound(zombie.getLocation(), Sound.ENTITY_PLAYER_HURT, SoundCategory.HOSTILE, 1, 1);

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
                            player.playSound(player.getLocation(), Sound.ENTITY_BLAZE_HURT, SoundCategory.HOSTILE, 2, 0.5F);
                            player.playSound(player.getLocation(), Sound.BLOCK_CONDUIT_DEACTIVATE, SoundCategory.HOSTILE, 1.2F, 0.1F);
                            player.playSound(player.getLocation(), Sound.BLOCK_CONDUIT_DEACTIVATE, SoundCategory.HOSTILE, 1.2F, 0.5F);
                            player.playSound(player.getLocation(), Sound.BLOCK_CONDUIT_DEACTIVATE, SoundCategory.HOSTILE, 1.2F, 1);
                            player.playSound(player.getLocation(), Sound.BLOCK_CONDUIT_DEACTIVATE, SoundCategory.HOSTILE, 1.2F, 1.5F);
                            player.playSound(player.getLocation(), Sound.BLOCK_CONDUIT_DEACTIVATE, SoundCategory.HOSTILE, 1.2F, 2);
                            zombie.setVelocity(zombie.getVelocity().multiply(mult));
                            player.setVelocity(zombie.getVelocity().multiply(-mult / 2));
                        }
                    }.runTaskLaterAsynchronously(plugin, 2);
                }
            }
        }
    }

    @EventHandler
    public void MeleeOnly (EntityDamageEvent event) {
        if (event.getEntity() instanceof Zombie && event.getEntity().getWorld().equals(world)) {
            if (!event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)) {
                event.setCancelled(true);
            }
        }
    }

}
