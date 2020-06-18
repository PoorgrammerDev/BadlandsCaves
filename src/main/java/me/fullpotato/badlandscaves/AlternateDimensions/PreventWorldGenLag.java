package me.fullpotato.badlandscaves.AlternateDimensions;

import me.fullpotato.badlandscaves.BadlandsCaves;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldInitEvent;

public class PreventWorldGenLag implements Listener {
    private final BadlandsCaves plugin;

    public PreventWorldGenLag(BadlandsCaves plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void preventPlanetLoading (WorldInitEvent event) {
        if (event.getWorld().getName().startsWith(plugin.dimensionPrefixName)) {
            event.getWorld().setKeepSpawnInMemory(false);
        }
    }

}
