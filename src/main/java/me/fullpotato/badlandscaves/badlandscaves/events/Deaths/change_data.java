package me.fullpotato.badlandscaves.badlandscaves.events.Deaths;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class change_data implements Listener {

    private BadlandsCaves plugin;
    public change_data (BadlandsCaves bcav) {
        plugin = bcav;
    }

    @EventHandler
    public void change_data(PlayerDeathEvent event) {
        Player player = event.getEntity();
        int death_count = player.getMetadata("Deaths").get(0).asInt();

        //resetting thirst/tox values on death
        player.setMetadata("Thirst", new FixedMetadataValue(plugin, 100.0));
        player.setMetadata("Toxicity", new FixedMetadataValue(plugin, 0.0));
        player.setMetadata("thirst_sys_var", new FixedMetadataValue(plugin, 0.0));
        player.setMetadata("tox_nat_decr_var", new FixedMetadataValue(plugin, 0.0));
        player.setMetadata("tox_slow_incr_var", new FixedMetadataValue(plugin, 0.0));

        player.setMetadata("Deaths", new FixedMetadataValue(plugin, death_count + 1));
    }
}
