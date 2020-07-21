package me.fullpotato.badlandscaves.AlternateDimensions.Hazards;

import me.fullpotato.badlandscaves.BadlandsCaves;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class NoFloorRunnable extends BukkitRunnable {
    private final BadlandsCaves plugin;
    private final EnvironmentalHazards dims;

    public NoFloorRunnable(BadlandsCaves plugin) {
        this.plugin = plugin;
        this.dims = new EnvironmentalHazards(plugin);
    }

    @Override
    public void run() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            World world = player.getWorld();
            if (dims.isDimension(world) && dims.hasHazard(world, EnvironmentalHazards.Hazard.NO_FLOOR)) {
                if (player.getGameMode().equals(GameMode.SURVIVAL) || player.getGameMode().equals(GameMode.ADVENTURE)) {
                    if (((LivingEntity) player).isOnGround()) {
                        player.teleport(player.getLocation().subtract(0, 0.05, 0), PlayerTeleportEvent.TeleportCause.PLUGIN);
                    }
                }
            }
        }
    }
}
