package me.fullpotato.badlandscaves.SupernaturalPowers.DescensionStage;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.Util.ParticleShapes;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ExitPortal extends BukkitRunnable {
    private final BadlandsCaves plugin;
    private final Location location;

    public ExitPortal(BadlandsCaves plugin) {
        this.plugin = plugin;
        World world = plugin.getServer().getWorld(plugin.getDescensionWorldName());
        location = new Location(world, 0.5, 82, 0.5);
    }


    @Override
    public void run() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            int in_descension = ((int) PlayerScore.IN_DESCENSION.getScore(plugin, player));
            if (in_descension == 2) {
                int capped = ((int) PlayerScore.DESCENSION_SHRINES_CAPPED.getScore(plugin, player));
                if (capped == 4) {
                    ParticleShapes.particleSphere(null, Particle.SPELL_WITCH, location, 5, -1, null);
                    return;
                }
            }
        }
    }
}
