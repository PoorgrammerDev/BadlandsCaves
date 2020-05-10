package me.fullpotato.badlandscaves.badlandscaves.Runnables.SupernaturalPowers.DescensionStage;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.metadata.FixedMetadataValue;
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
        final World descension_world = Bukkit.getWorld("world_descension");
        player.setMetadata("in_descension", new FixedMetadataValue(plugin, 1));
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