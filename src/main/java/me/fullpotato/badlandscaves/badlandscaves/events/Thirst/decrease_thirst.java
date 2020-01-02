package me.fullpotato.badlandscaves.badlandscaves.events.Thirst;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class decrease_thirst implements Listener {
    private BadlandsCaves plugin;
    public decrease_thirst (BadlandsCaves bcav) {
        plugin = bcav;
    }


    @EventHandler
    public void decrease_thirst (PlayerMoveEvent event) {
        Player player = event.getPlayer();

        boolean moved_x = (Math.abs(event.getTo().getX() - event.getFrom().getX()) > 0);
        boolean moved_y = (Math.abs(event.getTo().getY() - event.getFrom().getY()) > 0);
        boolean moved_z = (Math.abs(event.getTo().getZ() - event.getFrom().getZ()) > 0);
        boolean moved = moved_x || moved_y || moved_z;
        boolean sprint = player.isSprinting();
        boolean sneak = player.isSneaking();
        boolean climb = moved_y && player.getLocation().getBlock().getType() == Material.LADDER;
        double current_thirst_sys = player.getMetadata("thirst_sys_var").get(0).asDouble();

        if (moved) {
            if (climb) {
                double new_thirst_sys = current_thirst_sys + 3;
                double rounded = Math.round(new_thirst_sys * 100.0) / 100.0;
                player.setMetadata("thirst_sys_var", new FixedMetadataValue(plugin, rounded));
            }
            else if (sprint) {
                double new_thirst_sys = current_thirst_sys + 1.5;
                double rounded = Math.round(new_thirst_sys * 100.0) / 100.0;
                player.setMetadata("thirst_sys_var", new FixedMetadataValue(plugin, rounded));
            }
            else if (sneak) {
                double new_thirst_sys = current_thirst_sys + 0.5;
                double rounded = Math.round(new_thirst_sys * 100.0) / 100.0;
                player.setMetadata("thirst_sys_var", new FixedMetadataValue(plugin, rounded));
            }
            else {
                double new_thirst_sys = current_thirst_sys + 1;
                double rounded = Math.round(new_thirst_sys * 100.0) / 100.0;
                player.setMetadata("thirst_sys_var", new FixedMetadataValue(plugin, rounded));
            }
            //player.sendMessage("thirstsys level: " + player.getMetadata("thirst_sys_var").get(0).asDouble());
        }

        if (player.getMetadata("thirst_sys_var").get(0).asDouble() >= 500) {
            player.setMetadata("thirst_sys_var", new FixedMetadataValue(plugin, 0));

            double current_thirst = player.getMetadata("Thirst").get(0).asDouble();

            double new_thirst = current_thirst - 1;
            double thirst_rounded = Math.round(new_thirst * 100.0) / 100.0;
            player.setMetadata("Thirst" , new FixedMetadataValue(plugin, thirst_rounded));
        }
    }

}
