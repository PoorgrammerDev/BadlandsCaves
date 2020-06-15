package me.fullpotato.badlandscaves.NMS.LineOfSight;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface LineOfSightNMS {
    boolean hasLineOfSight(Player player, Location location);
}
