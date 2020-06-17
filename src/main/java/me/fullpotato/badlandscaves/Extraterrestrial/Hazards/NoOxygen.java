package me.fullpotato.badlandscaves.Extraterrestrial.Hazards;

import me.fullpotato.badlandscaves.BadlandsCaves;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityAirChangeEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class NoOxygen extends BukkitRunnable implements Listener {
    private final BadlandsCaves plugin;
    private final EnvironmentalHazards planets;

    public NoOxygen(BadlandsCaves plugin) {
        this.plugin = plugin;
        this.planets = new EnvironmentalHazards(plugin);
    }

    @EventHandler
    public void cancelAirRegen(EntityAirChangeEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            World world = player.getWorld();
            if (planets.isPlanet(world) && planets.hasHazard(world, EnvironmentalHazards.Hazard.NO_OXYGEN)) {
                if (player.getGameMode().equals(GameMode.SURVIVAL) || player.getGameMode().equals(GameMode.ADVENTURE)) {
                    if (event.getAmount() > player.getRemainingAir()) {
                        if (event.getAmount() < 0) {
                            player.setHealth(player.getHealth() - 0.75);
                            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_HURT_DROWN, SoundCategory.PLAYERS, 1, 1);
                            player.setNoDamageTicks(0);
                            event.setAmount(0);
                        }
                        else {
                            event.setAmount(player.getRemainingAir() - 1);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void run() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            World world = player.getWorld();
            if (planets.isPlanet(world) && planets.hasHazard(world, EnvironmentalHazards.Hazard.NO_OXYGEN)) {
                if (player.getGameMode().equals(GameMode.SURVIVAL) || player.getGameMode().equals(GameMode.ADVENTURE)) {
                    if (player.getRemainingAir() == 300) {
                        player.setRemainingAir(299);
                    }
                }
            }
        }
    }
}
