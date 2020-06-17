package me.fullpotato.badlandscaves.Extraterrestrial.Hazards;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.SupernaturalPowers.ReflectionStage.ZombieBossBehavior;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;


public class BewildermentRunnable extends BukkitRunnable {
    private final BadlandsCaves plugin;
    private final EnvironmentalHazards planets;
    private final ZombieBossBehavior locationFinder;
    private final Random random = new Random();

    public BewildermentRunnable(BadlandsCaves plugin) {
        this.plugin = plugin;
        this.planets = new EnvironmentalHazards(plugin);
        this.locationFinder = new ZombieBossBehavior(plugin);
    }

    @Override
    public void run() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            World world = player.getWorld();
            if (planets.isPlanet(world) && planets.hasHazard(world, EnvironmentalHazards.Hazard.BEWILDERMENT)) {
                if (player.getGameMode().equals(GameMode.SURVIVAL) || player.getGameMode().equals(GameMode.ADVENTURE)) {
                    locationFinder.warpPlayer(player, random);
                    player.playSound(player.getLocation(), Sound.BLOCK_BELL_USE, SoundCategory.HOSTILE, 0.5F, 0.2F);

                    if (player.isOnGround()) {
                        player.setMetadata("bewilder_velocity_cancel", new FixedMetadataValue(plugin, true));
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                player.setMetadata("bewilder_velocity_cancel", new FixedMetadataValue(plugin, false));
                            }
                        }.runTaskLaterAsynchronously(plugin, 20);
                    }
                }
            }
        }
    }
}
