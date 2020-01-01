package me.fullpotato.badlandscaves.badlandscaves.events.Deaths;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

public class gapple_eat implements Listener {
    private BadlandsCaves plugin;
    public gapple_eat (BadlandsCaves bcav) {
        plugin = bcav;
    }

    @EventHandler
    public void player_eat (PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        int death_count = player.getMetadata("Deaths").get(0).asInt();

        if (item.getType() == Material.GOLDEN_APPLE) {
            player.setMetadata("Deaths", new FixedMetadataValue(plugin, remove_deaths(death_count, 1)));
        }
        else if (item.getType() == Material.ENCHANTED_GOLDEN_APPLE) {
            player.setMetadata("Deaths", new FixedMetadataValue(plugin, remove_deaths(death_count, 10)));
        }
    }

    public int remove_deaths(int current_deaths, int decrease_by) {
        if (current_deaths - decrease_by >= 0) {
            return current_deaths-decrease_by;
        }
        else {
            return 0;
        }
    }
}
