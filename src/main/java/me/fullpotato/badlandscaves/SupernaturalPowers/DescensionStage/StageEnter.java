package me.fullpotato.badlandscaves.SupernaturalPowers.DescensionStage;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class StageEnter extends BukkitRunnable {
    private BadlandsCaves plugin;
    private Player player;

    public StageEnter(BadlandsCaves plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
    }

    @Override
    public void run() {
        final World descension_world = plugin.getServer().getWorld(plugin.descensionWorldName);
        PlayerScore.IN_DESCENSION.setScore(plugin, player, 1);
        if (descension_world != null && descension_world.isChunkLoaded(0, 0)) {
            if (!player.getWorld().equals(descension_world)) {
                Location descension_spawn = new Location(descension_world, 0, 197, 0);
                try {
                    if (descension_spawn.isWorldLoaded()) player.teleport(descension_spawn, PlayerTeleportEvent.TeleportCause.PLUGIN);
                }
                catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }
        if (player.getGameMode().equals(GameMode.SURVIVAL)) player.setGameMode(GameMode.ADVENTURE);
    }
}