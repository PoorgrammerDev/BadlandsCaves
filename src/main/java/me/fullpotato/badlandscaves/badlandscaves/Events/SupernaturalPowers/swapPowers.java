package me.fullpotato.badlandscaves.badlandscaves.Events.SupernaturalPowers;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;

public class swapPowers implements Listener {
    private BadlandsCaves plugin;
    public swapPowers (BadlandsCaves bcav) {
        plugin = bcav;
    }

    @EventHandler
    public void swap_to_powers (PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        int has_powers = player.getMetadata("has_supernatural_powers").get(0).asInt();
        if (has_powers == 0) return;

        boolean sneaking = player.isSneaking();
        if (!sneaking) return;

        ItemStack offhand_item = player.getInventory().getItemInOffHand();
        if (offhand_item.getType().equals(Material.SHIELD)) {
            //TODO replace their shield with power stuff
        }

    }
}
