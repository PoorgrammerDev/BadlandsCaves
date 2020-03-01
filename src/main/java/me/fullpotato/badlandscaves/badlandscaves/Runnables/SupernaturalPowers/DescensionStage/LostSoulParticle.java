package me.fullpotato.badlandscaves.badlandscaves.Runnables.SupernaturalPowers.DescensionStage;

import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class LostSoulParticle extends BukkitRunnable {
    @Override
    public void run() {
        World world = Bukkit.getWorld("world_descension");
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getWorld().equals(world)) {
                int in_descension = player.getMetadata("in_descension").get(0).asInt();
                if (in_descension == 2) {
                    List<Entity> entities = player.getNearbyEntities(20, 20, 20);
                    for (Entity entity : entities) {
                        if (entity instanceof Zombie) {
                            Zombie zombie = (Zombie) entity;
                            Location entity_location = zombie.getEyeLocation();
                            player.spawnParticle(Particle.REDSTONE, entity_location, 2, new Particle.DustOptions(Color.ORANGE, 2));
                        }
                    }
                }
            }
        }
    }
}
