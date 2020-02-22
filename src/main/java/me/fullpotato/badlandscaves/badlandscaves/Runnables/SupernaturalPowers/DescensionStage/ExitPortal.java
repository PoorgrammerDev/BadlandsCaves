package me.fullpotato.badlandscaves.badlandscaves.Runnables.SupernaturalPowers.DescensionStage;

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
        boolean run = false;
        for (Player player : Bukkit.getOnlinePlayers()) {
            int in_descension = player.getMetadata("in_descension").get(0).asInt();
            if (in_descension == 2) {
                int capped = player.getMetadata("descension_shrines_capped").get(0).asInt();
                if (capped == 4) {
                    run = true;
                    break;
                }
            }
        }

        if (!run) {
            return;
        }

        double phi = 0;
        while (phi <= Math.PI) {
            phi += Math.PI / 10;

            for (double theta = 0; theta <= 2 * Math.PI; theta += Math.PI / 40) {
                double radius = 5;
                double x = radius * Math.cos(theta) * Math.sin(phi);
                double y = radius * Math.cos(phi) + 1.5;
                double z = radius * Math.sin(theta) * Math.sin(phi);

                location.add(x, y, z);
                world.spawnParticle(Particle.SPELL_WITCH, location, 1);
                location.subtract(x, y, z);

            }
        }
    }
}
