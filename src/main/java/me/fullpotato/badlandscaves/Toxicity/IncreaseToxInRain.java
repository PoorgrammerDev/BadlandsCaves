package me.fullpotato.badlandscaves.Toxicity;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.Extraterrestrial.Hazards.EnvironmentalHazards;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

public class IncreaseToxInRain implements Listener {
    private final BadlandsCaves plugin;
    public IncreaseToxInRain(BadlandsCaves bcav) {
        this.plugin = bcav;
    }

    @EventHandler
    public void increase_tox_in_rain (PlayerMoveEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();
        if (!world.hasStorm()) return;

        EnvironmentalHazards planets = new EnvironmentalHazards(plugin);
        if (planets.isPlanet(world) && !planets.hasHazard(world, EnvironmentalHazards.Hazard.ACID_RAIN)) return;

        Location location = player.getLocation();
        double temp = world.getTemperature(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        if (temp > 0.95) return;
        if (player.getGameMode() != GameMode.SURVIVAL && player.getGameMode() != GameMode.ADVENTURE) return;

        for (int y = location.getBlockY(); y < world.getMaxHeight(); y++) {
            location.setY(y);
            if (!location.getBlock().isPassable()) return;
        }

        boolean isHardmode = plugin.getConfig().getBoolean("system.hardmode");
        boolean waterBreathing = (player.hasPotionEffect(PotionEffectType.WATER_BREATHING) || player.hasPotionEffect(PotionEffectType.CONDUIT_POWER));

        final int toxic_sys_var = ((int) PlayerScore.TOX_SLOW_INCR_VAR.getScore(plugin, player));
        final Random random = new Random();

        int base = 50;
        if (isHardmode) base += 50;
        if (waterBreathing) base -= 25;

        final int rand = random.nextInt(base);
        PlayerScore.TOX_SLOW_INCR_VAR.setScore(plugin, player, toxic_sys_var + rand);


        //overflowing tox_slow to tox
        final double current_tox = (double) PlayerScore.TOXICITY.getScore(plugin, player);
        int current_tox_slow = ((int) PlayerScore.TOX_SLOW_INCR_VAR.getScore(plugin, player));
        if (current_tox_slow >= 100) {
            PlayerScore.TOX_SLOW_INCR_VAR.setScore(plugin, player, 0);
            PlayerScore.TOXICITY.setScore(plugin, player, current_tox + 0.1);
        }


    }
}
