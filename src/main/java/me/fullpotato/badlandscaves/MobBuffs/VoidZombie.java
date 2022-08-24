package me.fullpotato.badlandscaves.MobBuffs;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.SupernaturalPowers.Artifacts.Mechanisms.Runnables.ConvergingProjectile;

public class VoidZombie implements Listener {
    private final BadlandsCaves plugin;
    private final NamespacedKey voidKey;

    private final int COOLDOWN = 20;    // runnable is run every 5 ticks -> 100 ticks
    private final double MAX_PROJECTILE_DISTANCE_SQUARED = 100; //10 blocks
    private final double MELEE_DISTANCE_THRESHOLD_SQUARED = 16; //4 blocks

    public VoidZombie(BadlandsCaves plugin) {
        this.plugin = plugin;
        this.voidKey = new NamespacedKey(plugin, "voidMonster");
    }

    @EventHandler
    public void targetPlayer(EntityTargetLivingEntityEvent event) {
        if (event.isCancelled()) return;
        if (!(event.getEntity() instanceof Zombie)) return;

        final Zombie zombie = (Zombie) event.getEntity();
        final LivingEntity target = event.getTarget();
        

        //Ensure that the zombie is a Void zombie
        if (!zombie.getPersistentDataContainer().has(voidKey, PersistentDataType.BYTE) ||
        zombie.getPersistentDataContainer().get(voidKey, PersistentDataType.BYTE) != 1) return;

        //Runnable - cast projectile to teleport target closer
        //Similar to Artifact Converging Swings
        new BukkitRunnable() {

            @Override
            public void run() {
                //If zombie or target is dead, or if target changed, end this runnable
                if (zombie == null || target == null || zombie.isDead() || target.isDead() || zombie.getTarget() == null || !zombie.getTarget().equals(target)) {
                    this.cancel();
                    return;
                }

                //Check if on cooldown - if so, decrement and return
                final int cooldown = (zombie.hasMetadata("convergingCooldown")) ? zombie.getMetadata("convergingCooldown").get(0).asInt() : 0;
                if (cooldown > 0) {
                    zombie.setMetadata("convergingCooldown", new FixedMetadataValue(plugin, cooldown - 1));
                    return;
                }

                //Check the distance between the zombie and its target
                final double distanceSq = zombie.getLocation().distanceSquared(target.getLocation());

                if (distanceSq < MAX_PROJECTILE_DISTANCE_SQUARED && distanceSq > MELEE_DISTANCE_THRESHOLD_SQUARED) {
                    //The direction for the zombie to face its target
                    final Vector direction = target.getLocation().toVector().subtract(zombie.getLocation().toVector());
                    Location front = zombie.getEyeLocation().add(direction.normalize());
                    front.setDirection(direction); 

                    new ConvergingProjectile(plugin, zombie, front, 0.25, 25).runTaskTimer(plugin, 0, 1);
                    zombie.setMetadata("convergingCooldown", new FixedMetadataValue(plugin, COOLDOWN));
                }
            }

        }.runTaskTimer(plugin, 0, 5); 


    }
    
}
