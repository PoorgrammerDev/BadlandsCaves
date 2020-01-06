package me.fullpotato.badlandscaves.badlandscaves.Events.CustomCrafting;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class stop_custom_items_rclick implements Listener {
    private BadlandsCaves plugin;
    public  stop_custom_items_rclick(BadlandsCaves bcav) {
        plugin = bcav;
    }

    @EventHandler
    public void r_click (PlayerInteractEvent event) {
        Action action = event.getAction();

        if (action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)) {
            ItemStack item = event.getItem();
            if (item != null) {
                if (item.getType().equals(Material.STRUCTURE_VOID) || item.getType().equals(Material.DEBUG_STICK)) {
                    Player player = event.getPlayer();
                    if (player.getGameMode().equals(GameMode.SURVIVAL) || player.getGameMode().equals(GameMode.ADVENTURE)) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
}
