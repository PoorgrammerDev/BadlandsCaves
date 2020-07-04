package me.fullpotato.badlandscaves.CustomItems.Using;

import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

public class UseForeverFish implements Listener {

    @EventHandler
    public void eatFish (PlayerItemConsumeEvent event) {
        final ItemStack item = event.getItem();
        final ItemStack forever_fish = CustomItem.FOREVER_FISH.getItem();
        final Player player = event.getPlayer();

        if (player.getGameMode().equals(GameMode.SURVIVAL) || player.getGameMode().equals(GameMode.ADVENTURE)) {
            if (item.isSimilar(forever_fish)) {
                item.setAmount(item.getAmount() + 1);
                event.setItem(item);
                player.updateInventory();
            }
        }
    }
}
