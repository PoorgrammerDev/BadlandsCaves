package me.fullpotato.badlandscaves.SupernaturalPowers.DescensionStage;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.Util.ParticleShapes;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ExitPortal extends BukkitRunnable {
    private BadlandsCaves plugin;
    private final World world;
    private final Location location;

    public ExitPortal(BadlandsCaves plugin) {
        this.plugin = plugin;
        world = plugin.getServer().getWorld(plugin.descensionWorldName);
        location = new Location(world, 0.5, 82, 0.5);
    }


    @Override
    public void run() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            int in_descension = player.getMetadata("in_descension").get(0).asInt();
            if (in_descension == 2) {
                int capped = player.getMetadata("descension_shrines_capped").get(0).asInt();
                if (capped == 4) {
                    ParticleShapes.particleSphere(null, Particle.SPELL_WITCH, location, 5, -1, null);
                    return;
                }
            }
        }
    }
}
