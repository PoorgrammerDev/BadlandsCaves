package me.fullpotato.badlandscaves.AlternateDimensions.StructureMechanics;

import java.util.Map;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import me.fullpotato.badlandscaves.BadlandsCaves;

public class CastlePreventModify implements Listener{
    private final int RADIUS_SQUARED = 3600; //RADIUS = 60

    private final BadlandsCaves plugin;

    public CastlePreventModify(BadlandsCaves plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void BreakBlock(BlockBreakEvent event) {
        if (event.isCancelled()) return;
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) return;
        
        if (!isInsideCastle(event.getBlock().getLocation())) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void PlaceBlock (BlockPlaceEvent event) {
        if (event.isCancelled()) return;
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) return;

        if (!isInsideCastle(event.getBlock().getLocation())) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void EntityExplode (EntityExplodeEvent event) {
        if (event.isCancelled()) return;

        if (!isInsideCastle(event.getLocation())) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void BlockExplode (BlockExplodeEvent event) {
        if (event.isCancelled()) return;

        if (!isInsideCastle(event.getBlock().getLocation())) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void PreventMobSpawn(CreatureSpawnEvent event) {
        if (event.isCancelled()) return;

        if (!isInsideCastle(event.getLocation())) return;
        event.setCancelled(true);
    }

    private boolean isInsideCastle(Location location) {
        if (!location.getWorld().getName().startsWith(plugin.getDimensionPrefixName())) return false;

        final World world = location.getWorld();
        final ConfigurationSection section = plugin.getSystemConfig().getConfigurationSection("castle_lectern_locations");
        if (section == null) return false;
        for (Map.Entry<String, Object> entry : section.getValues(false).entrySet()) {
            if (entry.getValue() instanceof Location) {
                final Location deserializedLocation = (Location) entry.getValue();

                if (world.equals(deserializedLocation.getWorld())) {
                    if (deserializedLocation.distanceSquared(location) < RADIUS_SQUARED) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
    
}
