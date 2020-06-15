package me.fullpotato.badlandscaves.NMS.EnhancedEyes;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface EnhancedEyesNMS {
    int spawnIndicator (Player player, Location location);
    int spawnIndicator (Player player, Location location, ChatColor color);
    void highlightEntity (Player player, org.bukkit.entity.Entity entity);
    void highlightEntity (Player player, org.bukkit.entity.Entity ent, ChatColor color);
    void removeIndicator (Player player, int id);
}
