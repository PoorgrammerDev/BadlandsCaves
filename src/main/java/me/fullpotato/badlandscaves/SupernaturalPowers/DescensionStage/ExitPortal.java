package me.fullpotato.badlandscaves.SupernaturalPowers.DescensionStage;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.Util.ParticleShapes;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;

public class ExitPortal extends BukkitRunnable {
    private final BadlandsCaves plugin;
    private final World world;
    private final Location location;
    private final ParticleShapes particleShapes;

    public ExitPortal(BadlandsCaves plugin, ParticleShapes particleShapes) {
        this.plugin = plugin;
        this.world = plugin.getServer().getWorld(plugin.getDescensionWorldName());
        this.particleShapes = particleShapes;
        location = new Location(world, 0.5, 82, 0.5);
    }


    @Override
    public void run() {
        final Collection<? extends Player> players = world.getEntitiesByClass(Player.class);
        if (players.isEmpty()) {
            this.cancel();
            return;
        }

        for (Player player : players) {
            int in_descension = ((int) PlayerScore.IN_DESCENSION.getScore(plugin, player));
            if (in_descension == 2) {
                int capped = ((int) PlayerScore.DESCENSION_SHRINES_CAPPED.getScore(plugin, player));
                if (capped == 4) {
                    particleShapes.sphere(null, Particle.SPELL_WITCH, location, 5, -1, null);
                    return;
                }
            }
        }
    }
}
