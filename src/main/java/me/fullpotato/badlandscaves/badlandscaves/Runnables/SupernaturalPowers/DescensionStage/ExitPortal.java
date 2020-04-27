package me.fullpotato.badlandscaves.badlandscaves.Runnables.SupernaturalPowers.DescensionStage;

import me.fullpotato.badlandscaves.badlandscaves.Util.ParticleShapes;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ExitPortal extends BukkitRunnable {
    private final World world = Bukkit.getWorld("world_descension");
    private final Location location = new Location(world, 0.5, 82, 0.5);


    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
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
