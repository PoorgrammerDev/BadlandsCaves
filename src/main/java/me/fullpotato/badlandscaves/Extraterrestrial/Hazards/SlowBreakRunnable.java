package me.fullpotato.badlandscaves.Extraterrestrial.Hazards;

import me.fullpotato.badlandscaves.BadlandsCaves;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class SlowBreakRunnable extends BukkitRunnable {
    private final BadlandsCaves plugin;
    private final EnvironmentalHazards planets;

    public SlowBreakRunnable(BadlandsCaves plugin) {
        this.plugin = plugin;
        this.planets = new EnvironmentalHazards(this.plugin);
    }

    @Override
    public void run() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if (player.getGameMode().equals(GameMode.SURVIVAL) || player.getGameMode().equals(GameMode.ADVENTURE)) {
                World world = player.getWorld();
                if (planets.isPlanet(world) && planets.hasHazard(world, EnvironmentalHazards.Hazard.SLOW_BREAK)) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 40, 1, false, false));
                }
            }
        }
    }
}
