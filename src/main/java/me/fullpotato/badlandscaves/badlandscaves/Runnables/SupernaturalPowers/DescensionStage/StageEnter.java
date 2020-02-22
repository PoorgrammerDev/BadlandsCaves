package me.fullpotato.badlandscaves.badlandscaves.Runnables.SupernaturalPowers.DescensionStage;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

public class StageEnter extends BukkitRunnable {
    private BadlandsCaves plugin;
    public StageEnter(BadlandsCaves bcav) {
        plugin = bcav;
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.getGameMode().equals(GameMode.SURVIVAL) && !player.getGameMode().equals(GameMode.ADVENTURE)) continue;

            int deaths = player.getMetadata("Deaths").get(0).asInt();
            if (deaths >= 50) {
                int hasPowers = player.getMetadata("has_supernatural_powers").get(0).asInt();
                int in_descension = player.getMetadata("in_descension").get(0).asInt();
                if (in_descension != 3 && hasPowers < 0.5) {
                    if (in_descension == 0) {
                        player.setMetadata("in_descension", new FixedMetadataValue(plugin, 1));
                    }
                    World descension_world = Bukkit.getWorld("world_descension");
                    if (descension_world != null && descension_world.isChunkLoaded(0, 0)) {
                        if (!player.getWorld().equals(descension_world)) {
                            Location descension_spawn = new Location(descension_world, 0, 197, 0);
                            try {
                                if (descension_spawn != null && descension_spawn.isWorldLoaded()) player.teleport(descension_spawn);
                            }
                            catch (NullPointerException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }
}
