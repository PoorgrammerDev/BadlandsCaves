package me.fullpotato.badlandscaves.badlandscaves.Events;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.badlandscaves.Runnables.makeReincarnationWorld;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitTask;

public class test implements Listener {
    private BadlandsCaves plugin;
    public test (BadlandsCaves bcav) {
        plugin = bcav;
    }

    @EventHandler
    public void bruh (PlayerInteractEvent event) {
        if (event.getAction().equals(Action.RIGHT_CLICK_AIR) && event.getItem() != null && event.getItem().getType().equals(Material.BARRIER)) {
            //TODO remove this after testing
            World world = Bukkit.getWorld("world_reincarnation");
            BukkitTask task = new makeReincarnationWorld(plugin, world).runTask(plugin);
        }
    }
}
