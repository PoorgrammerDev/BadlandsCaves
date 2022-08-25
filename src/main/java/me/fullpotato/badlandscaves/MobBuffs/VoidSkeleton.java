package me.fullpotato.badlandscaves.MobBuffs;

import java.util.Random;

import org.bukkit.Color;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.Other.GravityAttack;

public class VoidSkeleton implements Listener {

    private BadlandsCaves plugin;
    private Random random;
    private NamespacedKey voidKey;
    private NamespacedKey attackKey;
    
    public VoidSkeleton(BadlandsCaves plugin, Random random) {
        this.plugin = plugin;
        this.random = random;
        this.voidKey = new NamespacedKey(plugin, "voidMonster");
        this.attackKey = new NamespacedKey(plugin, "gravityAttackArrow");
    }

    @EventHandler
    public void arrowShoot(EntityShootBowEvent event) {
        if (!(event.getProjectile() instanceof AbstractArrow && event.getEntity() instanceof Skeleton)) return;

        final AbstractArrow arrow = (AbstractArrow) event.getProjectile();
        final Skeleton skeleton = (Skeleton) event.getEntity();

        //Ensure that the skeleton is a Void skeleton
        if (!skeleton.getPersistentDataContainer().has(voidKey, PersistentDataType.BYTE) ||
        skeleton.getPersistentDataContainer().get(voidKey, PersistentDataType.BYTE) != 1) return;

        //Spawn chance
        final int chaos = plugin.getSystemConfig().getInt("chaos_level");
        final int spawnChance = 25 + (chaos / 2);
        if (random.nextInt(100) >= spawnChance) return;

        arrow.getPersistentDataContainer().set(attackKey, PersistentDataType.BYTE, (byte) 1);
        final World world = arrow.getWorld();
        new BukkitRunnable() {
            @Override
            public void run() {
                if (arrow.isDead() || arrow.isInBlock()) {
                    this.cancel();
                }
                else {
                    world.spawnParticle(Particle.REDSTONE, arrow.getLocation(), 3, 0.1, 0.1, 0.1, 0, new Particle.DustOptions(Color.RED, 0.5F));
                }
            }
        }.runTaskTimer(plugin, 0, 0);
    }

    @EventHandler
    public void arrowHit(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof AbstractArrow)) return;
        final AbstractArrow arrow = (AbstractArrow) event.getEntity();
        
        if (!(arrow.getShooter() instanceof Skeleton)) return;
        if (event.getHitEntity() == null || !(event.getHitEntity() instanceof Player)) return;

        if (arrow.getPersistentDataContainer().has(attackKey, PersistentDataType.BYTE)) {
            final Byte result = arrow.getPersistentDataContainer().get(attackKey, PersistentDataType.BYTE);
            if (result != null && result == (byte) 1) {
                final int chaos = plugin.getSystemConfig().getInt("chaos_level");
                final double damage = 0.75 + (chaos / 400.0);

                new GravityAttack(arrow.getLocation(), arrow.getWorld(), 20, 3, damage).runTaskTimer(plugin, 0, 0); 
            }
        }
    }
}
