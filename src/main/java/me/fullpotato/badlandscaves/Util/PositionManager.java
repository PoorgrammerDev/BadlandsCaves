package me.fullpotato.badlandscaves.Util;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.MainHand;
import org.bukkit.util.Vector;

public class PositionManager {
    public Location getRightSide(Location location, double distance) {
        float angle = location.getYaw() / 60;
        return location.clone().subtract(new Vector(Math.cos(angle), 0, Math.sin(angle)).normalize().multiply(distance));
    }

    public Location getLeftSide(Location location, double distance) {
        float angle = location.getYaw() / 60;
        return location.clone().add(new Vector(Math.cos(angle), 0, Math.sin(angle)).normalize().multiply(distance));
    }

    public Location getMainSide (Player player) {
        return player.getMainHand().equals(MainHand.LEFT) ? getLeftSide(player.getLocation().add(0, 1, 0), 1) : getRightSide(player.getLocation().add(0, 1, 0), 1);
    }

    public Location getOffSide (Player player) {
        return player.getMainHand().equals(MainHand.RIGHT) ? getLeftSide(player.getLocation().add(0, 1, 0), 1) : getRightSide(player.getLocation().add(0, 1, 0), 1);
    }
}
