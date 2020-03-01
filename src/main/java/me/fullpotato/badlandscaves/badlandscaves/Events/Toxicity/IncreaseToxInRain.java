package me.fullpotato.badlandscaves.badlandscaves.Events.Toxicity;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

public class IncreaseToxInRain implements Listener {
    private BadlandsCaves plugin;
    public IncreaseToxInRain(BadlandsCaves bcav) {
        this.plugin = bcav;
    }

    @EventHandler
    public void increase_tox_in_rain (PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location location = player.getLocation();
        World world = location.getWorld();
        boolean isHardmode = plugin.getConfig().getBoolean("game_values.hardmode");
        if (!isHardmode) return;

        if (world == null || !world.hasStorm()) return;
        double temp = world.getTemperature(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        if (temp > 0.95) return;
        if (player.hasPotionEffect(PotionEffectType.WATER_BREATHING) || player.hasPotionEffect(PotionEffectType.CONDUIT_POWER)) return;
        if (player.getGameMode() != GameMode.SURVIVAL && player.getGameMode() != GameMode.ADVENTURE) return;

        for (int y = location.getBlockY(); y < world.getMaxHeight(); y++) {
            Location iterate = location;
            iterate.setY(y);

            Block block = iterate.getBlock();
            if (!block.getType().isAir()) {
                return;
            }
        }

        int toxic_sys_var = player.getMetadata("tox_slow_incr_var").get(0).asInt();
        Random random = new Random();
        int rand = random.nextInt(100);
        player.setMetadata("tox_slow_incr_var", new FixedMetadataValue(plugin, toxic_sys_var + rand));


        //overflowing tox_slow to tox
        double current_tox = player.getMetadata("Toxicity").get(0).asDouble();
        int current_tox_slow = player.getMetadata("tox_slow_incr_var").get(0).asInt();
        if (current_tox_slow >= 100) {
            player.setMetadata("tox_slow_incr_var", new FixedMetadataValue(plugin, 0));
            player.setMetadata("Toxicity", new FixedMetadataValue(plugin, current_tox + 0.1));
        }


    }
}
