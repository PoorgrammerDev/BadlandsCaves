package me.fullpotato.badlandscaves.Extraterrestrial;

import me.fullpotato.badlandscaves.BadlandsCaves;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class GravityFallDamage implements Listener {
    private final BadlandsCaves plugin;

    public GravityFallDamage(BadlandsCaves plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void fallDamage (EntityDamageEvent event) {
        if (event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
            Entity entity = event.getEntity();
            if (entity.getWorld().getName().startsWith(plugin.planetPrefixName)) {
                double gravity = plugin.getConfig().getDouble("system.planet_stats." + entity.getWorld().getName() + ".gravity");

                double damage = event.getDamage() * gravity;
                if (damage < 1) {
                    event.setCancelled(true);
                }
                else {
                    event.setDamage(damage);
                }

            }
        }
    }
}
