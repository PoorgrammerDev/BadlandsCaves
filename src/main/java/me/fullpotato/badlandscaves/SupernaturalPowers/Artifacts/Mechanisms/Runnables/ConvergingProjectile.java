package me.fullpotato.badlandscaves.SupernaturalPowers.Artifacts.Mechanisms.Runnables;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.Util.PlayerScore;

import org.bukkit.event.player.PlayerTeleportEvent;

public class ConvergingProjectile extends BukkitRunnable {
   
    private BadlandsCaves plugin;
    private LivingEntity sourceEntity;
    private Location front;
    private double range;
    private int times;

    private World world;
    private Location scout;
    private int ran;    

    public ConvergingProjectile(BadlandsCaves plugin, LivingEntity sourceEntity, Location front, double range, int times) {
        super();

        this.plugin = plugin;
        this.sourceEntity = sourceEntity;
        this.front = front;
        this.range = range;
        this.times = times;

        this.world = sourceEntity.getWorld();
        this.scout = front.clone();
        this.ran = 0;
    }


    @Override
    public void run() {
        if (ran > times || !scout.getBlock().isPassable()) {
            this.cancel();
            return;
        }

        scout = scout.add(front.getDirection().normalize());
        world.spawnParticle(Particle.REDSTONE, scout, 1, 0, 0, 0, 0, new Particle.DustOptions(Color.fromRGB(75, 0, 145), 1));

        for (Entity target : world.getNearbyEntities(scout, range, range, range)) {
            if (target instanceof LivingEntity) {
                final LivingEntity livingTarget = (LivingEntity) target;
                if (!livingTarget.equals(sourceEntity)) {
                    //Exception - if livingTarget is a player and a sorcerer
                    if (livingTarget instanceof Player) {
                        final Player playerTarget = (Player) livingTarget;

                        if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, playerTarget) == 1) {
                            continue;
                        }

                    }

                    
                    this.cancel();

                    //Teleport the target to the position
                    front.setYaw(livingTarget.getLocation().getYaw());
                    front.setPitch(livingTarget.getLocation().getPitch());
                    livingTarget.teleport(front, PlayerTeleportEvent.TeleportCause.PLUGIN);

                    //Effects
                    world.spawnParticle(Particle.SPELL_WITCH, scout, 25, 0.5, 1, 0.5);
                    world.spawnParticle(Particle.SPELL_WITCH, front, 25, 0.5, 1, 0.5);
                    
                    //More effects
                    world.playSound(front, "custom.supernatural.displace.warp", SoundCategory.HOSTILE, 1, 1);
                    world.spawnParticle(Particle.SPELL_WITCH, front, 5, 0.1, 0.1, 0.1, 1);
                }
            }
        }

        ran++;
    }
}
