package me.fullpotato.badlandscaves.Extraterrestrial;

import me.fullpotato.badlandscaves.BadlandsCaves;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;

public class GravityRunnable extends BukkitRunnable {
    private final BadlandsCaves plugin;
    private final World world;
    private final double gravity;
    private final HashMap<Entity, Boolean> onGroundMap = new HashMap<>();
    private final HashMap<Entity, Vector> velocityMap = new HashMap<>();
    private final HashMap<Entity, Location> positionMap = new HashMap<>();


    public GravityRunnable(BadlandsCaves plugin, World world) {
        this.plugin = plugin;
        this.world = world;
        this.gravity = plugin.getConfig().getDouble("system.planet_stats." + world.getName() + ".gravity");
    }

    @Override
    public void run() {
        if (gravity == 1.0) return;

        for (Entity entity : world.getEntities()) {
            Vector currentVelocity = entity.getVelocity();

            if (entity instanceof Player) {
                Player player = (Player) entity;
                if (player.getGameMode().equals(GameMode.CREATIVE) || player.getGameMode().equals(GameMode.SPECTATOR)) {
                    continue;
                }
            }


            if (this.velocityMap.containsKey(entity) && this.onGroundMap.containsKey(entity) && !entity.isOnGround() && !entity.isInsideVehicle()) {
                Vector oldVelocity = velocityMap.get(entity);
                Vector position;
                if (!onGroundMap.get(entity)) {
                    position = oldVelocity.clone();
                    position.subtract(currentVelocity);
                    double y = position.getY();
                    if (y > 0 && (Math.abs(currentVelocity.getY()) > 0.01)) {
                        currentVelocity.setY(oldVelocity.getY() - (y * gravity));
                        if (Math.abs(currentVelocity.getX()) > 0.001 && Math.abs(oldVelocity.getX()) > 0.001) {
                            currentVelocity.setX(oldVelocity.getX());
                        }
                        if (Math.abs(currentVelocity.getZ()) > 0.001 && Math.abs(oldVelocity.getZ()) > 0.001) {
                            currentVelocity.setZ(oldVelocity.getZ());
                        }
                    }
                }
                else if (entity instanceof Player && positionMap.containsKey(entity)) {
                    position = entity.getLocation().toVector();
                    Vector oldPosition = this.positionMap.get(entity).toVector();
                    Vector velocity = position.subtract(oldPosition);

                    currentVelocity.setX(velocity.getX() / 1.5);
                    currentVelocity.setZ(velocity.getZ() / 1.5);
                }
                entity.setVelocity(currentVelocity);
            }

            velocityMap.put(entity, currentVelocity.clone());
            onGroundMap.put(entity, entity.isOnGround());
            positionMap.put(entity, entity.getLocation());
        }
    }
}

