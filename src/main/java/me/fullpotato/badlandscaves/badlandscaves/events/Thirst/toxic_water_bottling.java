package me.fullpotato.badlandscaves.badlandscaves.events.Thirst;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.badlandscaves.Runnables.tox_bottling_runnable;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

public class toxic_water_bottling implements Listener {
    private BadlandsCaves plugin;
    public toxic_water_bottling (BadlandsCaves bcav) {
        plugin = bcav;
    }

    @EventHandler
    public void bottleWater (PlayerInteractEvent event) {
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (event.getHand().equals(EquipmentSlot.HAND)) {
                if (event.getItem().getType().equals(Material.GLASS_BOTTLE)) {
                    BukkitTask bottling = new tox_bottling_runnable(plugin, event.getPlayer()).runTaskLaterAsynchronously(plugin, 1);
                }
            }
        }
    }
}