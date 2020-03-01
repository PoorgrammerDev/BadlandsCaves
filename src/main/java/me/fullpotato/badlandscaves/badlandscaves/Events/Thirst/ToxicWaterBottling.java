package me.fullpotato.badlandscaves.badlandscaves.Events.Thirst;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.badlandscaves.Runnables.Toxicity.ToxBottlingRunnable;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.scheduler.BukkitTask;

public class ToxicWaterBottling implements Listener {
    private BadlandsCaves plugin;
    public ToxicWaterBottling(BadlandsCaves bcav) {
        plugin = bcav;
    }

    @EventHandler
    public void bottleWater (PlayerInteractEvent event) {
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_AIR)) {
            if (event.getHand() != null) {
                if (event.getHand().equals(EquipmentSlot.HAND)) {
                    if (event.getItem() != null) {
                        if (event.getItem().getType().equals(Material.GLASS_BOTTLE)) {
                            BukkitTask bottling = new ToxBottlingRunnable(plugin, event.getPlayer()).runTaskLaterAsynchronously(plugin, 1);
                        }
                    }
                }
            }
        }
    }
}