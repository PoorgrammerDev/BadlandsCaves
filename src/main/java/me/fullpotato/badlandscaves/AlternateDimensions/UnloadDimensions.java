package me.fullpotato.badlandscaves.AlternateDimensions;

import me.fullpotato.badlandscaves.BadlandsCaves;

import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

public class UnloadDimensions extends BukkitRunnable {
    private final BadlandsCaves plugin;
    private boolean detectedOnce;

    public UnloadDimensions(BadlandsCaves plugin) {
        this.plugin = plugin;
        this.detectedOnce = false;
    }

    @Override
    public void run() {
        //check if the mechanism has been disabled
        if (!plugin.getOptionsConfig().getBoolean("alternate_dimensions.allow_unload_unused")) {
            this.cancel();
            return;
        }

        //only run this when no players are online twice consecutively
        if (!plugin.getServer().getOnlinePlayers().isEmpty()) {
            this.detectedOnce = false;
            return;
        }

        //first run of no players detected -> set flag to true and return
        if (!detectedOnce) {
            this.detectedOnce = true;
            return;
        }

        //second run -> unload all dimensions
        this.detectedOnce = false;   //reset the flag

        int delay = 0;
        for (World world : plugin.getServer().getWorlds()) {
            if (world.getName().startsWith(plugin.getDimensionPrefixName())) {
                //don't unload every world at once - might overload server
                //instead split between different ticks
                
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        plugin.getLogger().info("Unloading Alternate Dimension " + world.getName() + ".");
                        plugin.getServer().unloadWorld(world, true);
                    }
                }.runTaskLater(plugin, delay);

                delay += 10;
            }
        }

    }
}
