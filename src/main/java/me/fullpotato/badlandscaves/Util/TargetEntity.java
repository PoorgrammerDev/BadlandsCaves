package me.fullpotato.badlandscaves.Util;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class TargetEntity {
    private Location targetLocation;

    /**
     * Finds the group of entities that a location is aiming at.
     * @param location Origin location
     * @param range Maximum range / distance of scouting out from the origin location
     * @param radius Maximum range / distance from the hit location
     * @param excludeEntities Any entities to exclude, aside from spectator players (as they are already automatically excluded)
     * */
    public Collection<Entity> findTargetEntities (Location location, int range, double radius, Entity... excludeEntities) {
        Location clone = location.clone();
        final World world = clone.getWorld();
        final BlockIterator iterator = new BlockIterator(clone);
        final List<Entity> exclude = Arrays.asList(excludeEntities);
        int travelled = 0;


        assert world != null;
        Collection<Entity> entityList = world.getNearbyEntities(clone, radius, radius, radius);
        while (travelled < range && iterator.hasNext()) {
            Block block = iterator.next();
            if (block.isPassable()) {
                clone = block.getLocation().add(0.5, 0.5, 0.5);
                entityList = world.getNearbyEntities(clone, radius, radius, radius);
                entityList.removeIf(entity -> {
                    if (exclude.contains(entity)) return true;
                    if (entity instanceof Player) {
                        Player target = (Player) entity;
                        return target.getGameMode().equals(GameMode.SPECTATOR);
                    }

                    return false;
                });

                if (entityList.isEmpty()) {
                    travelled++;
                }
                else break;
            }
            else break;
        }

        targetLocation = clone;
        return entityList;
    }

    /** Finds the closest entity that a location is aiming at.
     * @param location Origin location
     * @param range Maximum range / distance of scouting out from the origin location
     * @param radius Maximum range / distance from the hit location
     * @param excludeEntities Any entities to exclude, aside from spectator players (as they are already automatically excluded)
     * */
    public Entity findTargetEntity (Location location, int range, double radius, Entity... excludeEntities) {
        Collection<Entity> entities = findTargetEntities(location, range, radius, excludeEntities);
        double shortestDistance = Double.MAX_VALUE;
        Entity target = null;
        for (Entity entity : entities) {
            double distance = entity.getLocation().distanceSquared(targetLocation);
            if (distance < shortestDistance) {
                shortestDistance = distance;
                target = entity;
            }
        }
        return target;
    }

    /**
     * Finds the group of living entities that a location is aiming at.
     * @param location Origin location
     * @param range Maximum range / distance of scouting out from the origin location
     * @param radius Maximum range / distance from the hit location
     * @param excludeEntities Any entities to exclude, aside from spectator players (as they are already automatically excluded)
     * */
    public Collection<LivingEntity> findTargetLivingEntities (Location location, int range, double radius, Entity... excludeEntities) {
        Collection<Entity> entities = findTargetEntities(location, range, radius, excludeEntities);

        Collection<LivingEntity> livingEntities = new ArrayList<>();

        for (Entity entity : entities) {
            if (entity instanceof LivingEntity) {
                livingEntities.add((LivingEntity) entity);
            }
        }
        return livingEntities;
    }

    /** Finds the closest living entity that a location is aiming at.
     * @param location Origin location
     * @param range Maximum range / distance of scouting out from the origin location
     * @param radius Maximum range / distance from the hit location
     * @param excludeEntities Any entities to exclude, aside from spectator players (as they are already automatically excluded)
     * */
    public LivingEntity findTargetLivingEntity (Location location, int range, double radius, Entity... excludeEntities) {
        Collection<LivingEntity> entities = findTargetLivingEntities(location, range, radius, excludeEntities);
        double shortestDistance = Double.MAX_VALUE;
        LivingEntity target = null;
        for (LivingEntity entity : entities) {
            double distance = entity.getLocation().distanceSquared(targetLocation);
            if (distance < shortestDistance) {
                shortestDistance = distance;
                target = entity;
            }
        }
        return target;
    }

    public Location getTargetLocation() {
        return targetLocation;
    }

}
