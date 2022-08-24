package me.fullpotato.badlandscaves.Other;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class GravityAttack extends BukkitRunnable {

    private World world;
    private Location location;
    private int lifespan;
    private int ticks;
    private double range;
    private double rangeSquared;
    private double damagePerTick;

    public GravityAttack(Location location, World world, int lifespan, int range, double damagePerTick) {
        this.location = location;
        this.world = world;
        this.lifespan = lifespan;
        this.range = range;
        this.rangeSquared = range * range;
        this.damagePerTick = damagePerTick;
        this.ticks = 0;
    }  


    @Override
    public void run() {
        if (ticks > lifespan) {
            this.cancel();
            return;
        }

        //Sound effect
        if (ticks % 100 == 0) {
            world.playSound(location, Sound.BLOCK_PORTAL_TRIGGER, SoundCategory.HOSTILE, 1, 1);
        }

        //Spawn particles
        world.spawnParticle(Particle.REDSTONE, location, 2, 0.1, 0.1, 0.1, 0, new Particle.DustOptions(Color.BLACK, 2));
        world.spawnParticle(Particle.REDSTONE, location, 10, (range / 2), 0.25, 0.25, 0, new Particle.DustOptions(Color.RED, 1));
        world.spawnParticle(Particle.REDSTONE, location, 10, 0.25, (range / 2), 0.25, 0, new Particle.DustOptions(Color.RED, 1));
        world.spawnParticle(Particle.REDSTONE, location, 10, 0.25, 0.25, (range / 2), 0, new Particle.DustOptions(Color.RED, 1));

        //Pull in nearby players
        for (Entity entity : world.getNearbyEntities(location, 10, 10, 10)) {
            if (entity instanceof Player) {
                Player player = (Player) entity;
                if (player.getWorld().equals(world) && player.getLocation().distanceSquared(location) < this.rangeSquared) {
                    if (player.getLocation().distanceSquared(location) > 1) {
                        player.setVelocity(location.clone().subtract(player.getLocation()).toVector().multiply(0.3));
                    }
                    else if (this.damagePerTick > 0.0) {
                        player.damage(damagePerTick);
                        player.setNoDamageTicks(0);
                    }
                }
            }
        }

        ticks++;
    }
    
}
