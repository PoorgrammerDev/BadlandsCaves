package me.fullpotato.badlandscaves.AlternateDimensions;

import me.fullpotato.badlandscaves.BadlandsCaves;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class UnloadDimensions implements Listener {
    private final BadlandsCaves plugin;

    public UnloadDimensions(BadlandsCaves plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void exitDimension(PlayerChangedWorldEvent event) {
        final World world = event.getFrom();
        if (world.getName().startsWith(plugin.getDimensionPrefixName())) {
            if (world.getEntitiesByClass(Player.class).isEmpty()) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (world.getEntitiesByClass(Player.class).isEmpty()) {
                            plugin.getLogger().info("Unloading Alternate Dimension " + world.getName() + ".");
                            plugin.getServer().unloadWorld(world, true);
                        }
                    }
                }.runTaskLater(plugin, 1200L);
            }
        }
    }
}
