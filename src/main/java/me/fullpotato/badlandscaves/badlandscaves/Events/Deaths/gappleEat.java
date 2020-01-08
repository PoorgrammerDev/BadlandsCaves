package me.fullpotato.badlandscaves.badlandscaves.Events.Deaths;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

public class gappleEat implements Listener {
    private BadlandsCaves plugin;
    public gappleEat(BadlandsCaves bcav) {
        plugin = bcav;
    }

    @EventHandler
    public void player_eat (PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        int death_count = player.getMetadata("Deaths").get(0).asInt();

        boolean gapple = item.getType().equals(Material.GOLDEN_APPLE);
        boolean ench_gapple = item.getType().equals(Material.ENCHANTED_GOLDEN_APPLE);

        if (gapple || ench_gapple) {
            int decr_by;
            if (gapple) {
                //TODO expand this to hardmode when implemented
                decr_by = plugin.getConfig().getInt("game_values.pre_hardmode_values.death_reverse_gapple");
            }
            else {
                //TODO expand this to hardmode when implemented
                decr_by = plugin.getConfig().getInt("game_values.pre_hardmode_values.death_reverse_ench_gapple");
            }
            player.setMetadata("Deaths", new FixedMetadataValue(plugin, remove_deaths(death_count, decr_by)));
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
