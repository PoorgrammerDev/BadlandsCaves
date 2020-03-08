package me.fullpotato.badlandscaves.badlandscaves.Events.SupernaturalPowers.Reflection;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class PlayerUnderSht implements Listener {
    private World world = Bukkit.getWorld("world_reflection");

    @EventHandler
    public void activateUnderSht (EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player &&
                event.getEntity().getWorld().equals(world) &&
                event.getDamager() instanceof Zombie &&
                event.getDamager().getWorld().equals(world)) {
            final Player player = (Player) event.getEntity();
            final Zombie boss = (Zombie) event.getDamager();

            final double hp_before_atk = player.getHealth();
            final double hp_after_atk = hp_before_atk - event.getFinalDamage();

            if (hp_before_atk > 2 && hp_after_atk < 0) {
                event.setDamage(player.getHealth() - 1);
                player.spawnParticle(Particle.TOTEM, player.getEyeLocation(), 1);
                player.playSound(player.getEyeLocation(), Sound.ITEM_TOTEM_USE, 1, 1);
            }
        }
    }
}
