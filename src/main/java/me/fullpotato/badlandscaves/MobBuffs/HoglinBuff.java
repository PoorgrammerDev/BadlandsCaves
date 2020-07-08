package me.fullpotato.badlandscaves.MobBuffs;

import me.fullpotato.badlandscaves.BadlandsCaves;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Hoglin;
import org.bukkit.entity.Piglin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;

import java.util.Random;

public class HoglinBuff implements Listener {
    private final BadlandsCaves plugin;

    public HoglinBuff(BadlandsCaves plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    private void hoglinBuff (CreatureSpawnEvent event) {
        if (!(event.getEntity() instanceof Hoglin)) return;

        final boolean hardmode = plugin.getConfig().getBoolean("system.hardmode");
        if (!hardmode) return;

        final Hoglin hoglin = (Hoglin) event.getEntity();
        final Random random = new Random();
        final int chaos = plugin.getConfig().getInt("system.chaos_level");
        final double chance = Math.pow(1.045, chaos) - 1;

        if (random.nextInt(100) < chance) {
            hoglin.setIsAbleToBeHunted(false);
            Piglin piglin = (Piglin) hoglin.getWorld().spawnEntity(hoglin.getLocation(), EntityType.PIGLIN);
            piglin.setIsAbleToHunt(false);
            if (!hoglin.isAdult()) {
                piglin.setBaby(true);
            }

            hoglin.addPassenger(piglin);
        }
    }

    @EventHandler
    public void preventPigHogDmg (EntityDamageByEntityEvent event) {
        if (plugin.getConfig().getBoolean("system.hardmode")) {
            if (event.getEntity() instanceof Hoglin) {
                Hoglin hoglin = (Hoglin) event.getEntity();
                if (!hoglin.isAbleToBeHunted()) {
                    if (event.getDamager() instanceof Piglin) {
                        event.setCancelled(true);
                    }
                    else if (event.getDamager() instanceof Arrow) {
                        Arrow arrow = (Arrow) event.getDamager();
                        if (arrow.getShooter() instanceof Piglin) {
                            event.setCancelled(true);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void preventPigHogTarget (EntityTargetLivingEntityEvent event) {
        if (plugin.getConfig().getBoolean("system.hardmode")) {
            if (event.getTarget() instanceof Hoglin) {
                Hoglin hoglin = (Hoglin) event.getTarget();
                if (!hoglin.isAbleToBeHunted()) {
                    if (event.getEntity() instanceof Piglin) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
}
