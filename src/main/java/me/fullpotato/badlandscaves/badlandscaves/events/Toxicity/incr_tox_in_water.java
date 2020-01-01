package me.fullpotato.badlandscaves.badlandscaves.events.Toxicity;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffectType;

public class incr_tox_in_water implements Listener {
    private BadlandsCaves plugin;
    public incr_tox_in_water (BadlandsCaves bcav) {
        plugin = bcav;
    }


    //INCREASE TOXICITY IN WATER WHEN MOVING
    @EventHandler
    public void incr_tox_in_water (PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Block block = player.getLocation().getBlock();
        if (player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.ADVENTURE) {
            if (block.getType() == Material.WATER) {
                double current_tox = player.getMetadata("Toxicity").get(0).asDouble();
                int current_tox_slow = player.getMetadata("tox_slow_incr_var").get(0).asInt();

                /* Order of increasing rates
                 *  Boat with wbreath
                 *  Wbreath
                 *  Boat w/o wbreath
                 *  Nothing
                 * */

                if (player.hasPotionEffect(PotionEffectType.WATER_BREATHING) || player.hasPotionEffect(PotionEffectType.CONDUIT_POWER)) {
                    if ((player.getVehicle() instanceof Boat)) {
                        //Boat with wbreath
                        int random_incr = (int) (Math.round(Math.random()));
                        player.setMetadata("tox_slow_incr_var", new FixedMetadataValue(plugin, current_tox_slow + random_incr));
                    }
                    else {
                        //Wbreath no boat
                        int random_incr = (int) ((Math.random() * 5) + 1);
                        player.setMetadata("tox_slow_incr_var", new FixedMetadataValue(plugin, current_tox_slow + random_incr));
                    }
                }
                else if (player.getVehicle() instanceof Boat) {
                    //Boat w/o wbreath
                    int random_incr = (int) ((Math.random() * 5) + 5);
                    player.setMetadata("tox_slow_incr_var", new FixedMetadataValue(plugin, current_tox_slow + random_incr));
                }
                else {
                    //Nothing
                    int random_incr = (int) (Math.random() * 100);
                    player.setMetadata("Toxicity", new FixedMetadataValue(plugin, current_tox + random_incr));
                }

                current_tox = player.getMetadata("Toxicity").get(0).asDouble();
                current_tox_slow = player.getMetadata("tox_slow_incr_var").get(0).asInt();

                if (current_tox_slow >= 1000) {
                    player.setMetadata("tox_slow_incr_var", new FixedMetadataValue(plugin, 0));
                    player.setMetadata("Toxicity", new FixedMetadataValue(plugin, current_tox + 1));
                }

                player.sendMessage("Toxslow:" + player.getMetadata("tox_slow_incr_var").get(0).asInt());
                player.sendMessage("Tox:" + player.getMetadata("Toxicity").get(0).asInt());
            }
        }
    }
}
