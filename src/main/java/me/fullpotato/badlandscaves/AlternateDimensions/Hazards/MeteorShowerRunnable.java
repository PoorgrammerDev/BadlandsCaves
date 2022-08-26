package me.fullpotato.badlandscaves.AlternateDimensions.Hazards;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.SupernaturalPowers.ReflectionStage.ZombieBossBehavior;
import net.minecraft.server.v1_16_R2.Material;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Random;

public class MeteorShowerRunnable extends BukkitRunnable{
    private final BadlandsCaves plugin;
    private final EnvironmentalHazards dims;
    private final Random random;

    public MeteorShowerRunnable(BadlandsCaves plugin, Random random) {
        this.plugin = plugin;
        this.dims = new EnvironmentalHazards(plugin, random);
        this.random = random;
    }


    @Override
    public void run() {
        final int chaos = plugin.getSystemConfig().getInt("chaos_level");
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            final World world = player.getWorld();
            //Must be alt. dimension, player is in survival/adv, player is exposed to the sky
            if (!dims.isDimension(world) || !dims.hasHazard(world, EnvironmentalHazards.Hazard.METEOR_SHOWERS)) continue;
            if (!player.getGameMode().equals(GameMode.SURVIVAL) && !player.getGameMode().equals(GameMode.ADVENTURE)) continue;
            if (player.getLocation().getBlock().getLightFromSky() == 0) continue;

            for (int i = 0; i < (chaos / 20) + 5; i++) {
                if (random.nextInt(100) >= Math.max(chaos / 1.25, 25)) continue;

                //Find nearby location
                Location nearby = player.getLocation().add(random.nextInt(20) - 10, 0, random.nextInt(20) - 10);
                if (nearby == null) continue;

                //Send location into the sky
                nearby.add(0, 50, 0);

                //Point direction at the player or at the ground
                if (random.nextInt(100) < Math.max(chaos / 2, 10) && random.nextInt(255) < player.getLocation().getY()) {
                    nearby.setDirection(player.getLocation().subtract(nearby.clone()).toVector().multiply(5));
                }
                else {
                    nearby.setDirection(new Vector(random.nextDouble() / 2.0, -1, random.nextDouble() / 2.0).normalize().multiply(5));
                }

                spawnMeteor(nearby, (dims.hasHazard(world, EnvironmentalHazards.Hazard.FREEZING)));
            }
        }
    }

    public void spawnMeteor(Location location, boolean ice) {
        Location clone = location.clone();
        Vector dir = clone.getDirection();
        World world = clone.getWorld();
        if (world != null) {
            final int limit = 1000;
            int[] times = {0};

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!clone.getBlock().isPassable() || times[0] > limit) {
                        world.createExplosion(clone, 5, !ice, true);
                        this.cancel();
                    }
                    else {
                        if (ice) {
                            world.spawnParticle(Particle.ITEM_CRACK, clone, 2, 0, 0, 0, 0, Material.ICE);
                        }
                        else {
                            world.spawnParticle(Particle.LAVA, clone, 2, 0, 0, 0, 0);
                        }

                        clone.add(dir);
                        times[0]++;
                    }
                }
            }.runTaskTimer(plugin, 0, 0);
        }
    }
}
